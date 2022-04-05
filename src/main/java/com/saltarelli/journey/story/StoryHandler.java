/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.story;

import com.saltarelli.journey.Game;
import com.saltarelli.journey.json.Story;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Room;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class StoryHandler {

    private final Game game;
    private final Collection<Story> stories;

    public StoryHandler(Game game, Collection<Story> stories) {
        this.game = game;
        this.stories = stories;
    }

    public StoryHandlerResponse processOutput(ParserOutput output) {
        Story story = stories.stream()
                .filter(g -> g.match(output, game.getInventory()))
                .findFirst()
                .orElse(null);
        
        if (isStoryRequiredForCommand(output.getCommand()) && (story == null)) {
            return StoryHandlerResponse.newMessage(
                    Optional.ofNullable(customMessageResponse(output)).orElse(handleLookAtCommand(output)),
                    0,
                    false);
        }

        String message = updateGame(output);

        StoryHandlerResponse response = handleStory(story, false, false);

        if (response != null) {
            switch (response.getType()) {
                case MESSAGE:
                    if (message != null && !message.isEmpty()) {
                        message = message + "\n\n" + ((StoryHandlerMessage) response).getMessage();
                    } else {
                        message = ((StoryHandlerMessage) response).getMessage();
                    }

                    ((StoryHandlerMessage) response).setMessage(message);
                    return response;
                case QUESTION:
                    if (message != null && !message.isEmpty()) {
                        message = message + "\n\n" + ((StoryHandlerQuestion) response).getQuestion();
                    } else {
                        message = ((StoryHandlerQuestion) response).getQuestion();
                    }

                    ((StoryHandlerQuestion) response).setQuestion(message);
                    return response;
                default:
                    throw new AssertionError(response.getType().name());
            }
        } else if (message != null && !message.isEmpty()) {
            return StoryHandlerResponse.newMessage(message, 0, false);
        }

        switch (output.getCommand()) {
            case END:
            case RESTART:
            case INVENTORY:
            case SCORE:
            case HELP:
                return null;
            default:
                return StoryHandlerResponse.newMessage(game.getUnknownOutput(), 0, false);
        }
    }

    public StoryHandlerResponse processQuestionAnswer(Boolean yesAnswer, ParserOutput output) {
        Story story = fetchStoryFrom(output);
        StoryHandlerResponse response = handleStory(story, true, yesAnswer);
        if (response != null) {
            return response;
        } else {
            return StoryHandlerResponse.newMessage(game.getUnknownOutput(), 0, false);
        }
    }

    private String updateGame(ParserOutput output) {
        String message = null;

        switch (output.getCommand()) {
            case END:
            case RESTART:
            case INVENTORY:
            case SCORE:
            case HELP:
                return null;
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case WALK_TO:
                
                message = handleDirectionOutput(output);
                
                break;
            case OPEN:
                
                    output.getObjects().stream()
                            .forEach(o -> o.setIsOpen(true));
                
                break;
            case CLOSE:
                
                    output.getObjects().stream()
                            .forEach(o -> o.setIsOpen(false));
                
                break;
            case PUSH:
                
                    output.getObjects().stream()
                            .forEach(o -> o.setIsPush(true));
                
                break;
            case PULL:
                
                    output.getObjects().stream()
                            .forEach(o -> o.setIsPush(false));
                
                break;
            case PICK_UP:
                
                    output.getObjects().stream()
                            .forEach(o -> {
                                game.getCurrentRoom().getObjects().remove(o);
                                game.getInventory().add(o);
                            });
                
                break;
            case GIVE:
                
                    output.getObjects().stream()
                            .forEach(o -> {
                                game.getInventory().remove(o);
                            });
                
                break;
            case LOOK_AT:
                message = handleLookAtCommand(output);
                break;
            case READ:
            case SPEAK:
                message = Optional
                        .ofNullable(customMessageResponse(output))
                        .orElse(handleLookAtCommand(output));
                break;
            case COMBINE:
                message = Optional
                        .ofNullable(customMessageResponse(output))
                        .orElse(game.getUselessCombineCommand());
                break;
            case SING:
                break;
            default:
                throw new AssertionError(output.getCommand().name());
        }

        return message;
    }

    private String handleDirectionOutput(ParserOutput output) {
        game.setCurrentRoom(output.getRoom());
        return game.getCurrentRoom().getName() + "\n" + game.getCurrentRoom().getDescription();
    }

    private String handleLookAtCommand(ParserOutput output) {
        String message;

        if (output.getRoom() != null) {
            message = output.getRoom().getDescription();
        } else if (output.getPerson() != null) {
            message = output.getPerson().getDescription();
        } else if (!output.getObjects().isEmpty()) {
            if (output.getObjects().size() == 1) {
                message = output.getObjects().get(0).getDescription();
            } else {
                List<String> messages = output.getObjects().stream()
                        .map(o -> o.getName() + ": " + o.getDescription())
                        .collect(Collectors.toList());

                message = String.join("\n", messages);
            }
        } else {
            message = game.getUnknownOutput();
        }

        return message;
    }

    private String customMessageResponse(ParserOutput output) {
        Command.Name command = output.getCommand();

        if (output.getPerson() != null && output.getPerson().customMessageForCommand(command) != null && !output.getPerson().customMessageForCommand(command).isEmpty()) {
            return output.getPerson().customMessageForCommand(command);
        } else if (!output.getObjects().isEmpty()) {
            List<String> customMessages = output.getObjects().stream()
                    .map(o -> o.customMessageForCommand(command))
                    .distinct()
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.toList());

            if (!customMessages.isEmpty()) {
                return String.join("\n", customMessages);
            }
        }

        return null;
    }

    private Story fetchStoryFrom(ParserOutput output) {
        Command.Name command = output.getCommand();

        return stories.stream()
                .filter(gp -> {
                    return gp.getInput().getCommand() == command
                            && (gp.getInput().getPerson() == null || gp.getInput().getPerson() == output.getPerson().getId())
                            && (gp.getInput().getObjects() == null
                            || gp.getInput().getObjects().isEmpty()
                            || gp.getInput().getObjects().equals(output.getObjects().stream()
                                    .map(o -> o.getId())
                                    .collect(Collectors.toSet())));
                })
                .findFirst()
                .orElse(null);
    }

    private StoryHandlerResponse handleStory(Story story, Boolean checkAnswer, Boolean yesAnswer) {
        if (story == null) {
            return null;
        }

        // Editing game
        if (story.getOutput().getEditing() != null) {
            handleEditing(story.getOutput().getEditing());
        }

        // Editing game with answer
        if (story.getOutput().getQuestion() != null && checkAnswer) {
            if (yesAnswer) {
                if (story.getOutput().getQuestion().getYesAnswer().getEditing() != null) {
                    handleEditing(story.getOutput().getQuestion().getYesAnswer().getEditing());
                }
            } else {
                if (story.getOutput().getQuestion().getNoAnswer().getEditing() != null) {
                    handleEditing(story.getOutput().getQuestion().getNoAnswer().getEditing());
                }
            }
        }

        if (story.getDelete() != null && story.getDelete()
                || (checkAnswer && yesAnswer && story.getOutput().getQuestion().getYesAnswer().getDelete())
                || (checkAnswer && !yesAnswer && story.getOutput().getQuestion().getNoAnswer().getDelete())) {
            this.stories.remove(story);
        }

        // create story response
        if (checkAnswer) {
            if (yesAnswer) {
                return StoryHandlerResponse.newMessage(story.getOutput().getQuestion().getYesAnswer().getMessage(),
                        story.getOutput().getQuestion().getYesAnswer().getScore(),
                        story.getOutput().getQuestion().getYesAnswer().getIsLast());
            } else {
                return StoryHandlerResponse.newMessage(story.getOutput().getQuestion().getNoAnswer().getMessage(),
                        story.getOutput().getQuestion().getNoAnswer().getScore(),
                        story.getOutput().getQuestion().getNoAnswer().getIsLast());
            }
        } else {
            if (story.getOutput().getQuestion() != null) {
                return StoryHandlerResponse.newQuestion(story.getOutput().getMessage(),
                        story.getOutput().getQuestion().getYesAnswer().getMessage(),
                        story.getOutput().getQuestion().getNoAnswer().getMessage());
            } else if (story.getOutput().getMessage() != null && !story.getOutput().getMessage().isEmpty()) {
                return StoryHandlerResponse.newMessage(story.getOutput().getMessage(),
                        Optional.ofNullable(story.getScore()).orElse(0),
                        Optional.ofNullable(story.getIsLast()).orElse(false));
            }
        }

        return null;
    }

    private void handleEditing(Story.Editing editing) {
        if (editing.getObjects() != null && !editing.getObjects().isEmpty()) {
            editing.getObjects().stream()
                    .forEach(this::editObject);
        }

        if (editing.getPeople() != null && !editing.getPeople().isEmpty()) {
            editing.getPeople().stream()
                    .forEach(this::editPerson);
        }

        if (editing.getRooms() != null && !editing.getRooms().isEmpty()) {
            editing.getRooms().stream()
                    .forEach(r -> getRoom(r.getId()).setDescription(r.getDescription()));
        }
    }

    private void editObject(Story.Editing.AdvObject editingObject) {
        AdvObject object = getGameObject(editingObject.getId());
        Room room = getRoomOfObject(object.getId());

        if (editingObject.getCanClose() != null) {
            object.setCanClose(editingObject.getCanClose());
        }

        if (editingObject.getCanOpen() != null) {
            object.setCanOpen(editingObject.getCanOpen());
        }

        if (editingObject.getCanPull() != null) {
            object.setCanPull(editingObject.getCanPull());
        }

        if (editingObject.getCanPush() != null) {
            object.setCanPush(editingObject.getCanPush());
        }

        if (editingObject.getCanTake() != null) {
            object.setCanTake(editingObject.getCanTake());
        }

        if (editingObject.getIsOpen() != null) {
            object.setIsOpen(editingObject.getIsOpen());
        }

        if (editingObject.getIsPush() != null) {
            object.setIsPush(editingObject.getIsPush());
        }

        if (editingObject.getMoveToInventory() != null) {
            if (editingObject.getMoveToInventory()) {
                if (!game.getInventory().contains(object)) {
                    if (room != null && room.getObjects().contains(object)) {
                        room.getObjects().remove(object);
                    } else if (game.getInvisibleObjects().contains(object)) {
                        game.getInvisibleObjects().remove(object);
                    }

                    game.getInventory().add(object);
                }
            } else if (game.getInventory().contains(object)) {
                game.getInventory().remove(object);
            }
        }

        if (editingObject.getCustomCommandMessages() != null && !editingObject.getCustomCommandMessages().isEmpty()) {
            editingObject.getCustomCommandMessages().stream()
                    .forEach(m -> {
                        if (object.getCustomCommandMessages().contains(m)) {
                            object.getCustomCommandMessages().remove(m);
                        }
                        object.getCustomCommandMessages().add(m);
                    });
        }

        if (editingObject.getDescription() != null && !editingObject.getDescription().isEmpty()) {
            object.setDescription(editingObject.getDescription());
        }

        if (editingObject.getMoveToRoomID() != null) {
            if (room != null && room.getObjects().contains(object)) {
                room.getObjects().remove(object);
            }

            if (editingObject.getMoveToRoomID() != -1) {
                if (game.getInventory().contains(object)) {
                    game.getInventory().remove(object);
                }

                Room nextRoom = getRoom(editingObject.getMoveToRoomID());
                if (!nextRoom.getObjects().contains(object)) {
                    nextRoom.getObjects().add(object);
                }
            }
        }
    }

    private void editPerson(Story.Editing.Person editingPerson) {
        Person person = getPerson(editingPerson.getId());
        Room room = getRoomOfPerson(person.getId());

        if (editingPerson.getMoveToRoomID() != null) {
            if (room != null) {
                room.getPeople().remove(person);
            }

            if (editingPerson.getMoveToRoomID() != -1) {
                Room nextRoom = getRoom(editingPerson.getMoveToRoomID());
                if (!nextRoom.getPeople().contains(person)) {
                    nextRoom.getPeople().add(person);
                }
            }
        }

        if (editingPerson.getDescription() != null && !editingPerson.getDescription().isEmpty()) {
            person.setDescription(editingPerson.getDescription());
        }

        if (editingPerson.getCustomCommandMessages() != null && !editingPerson.getCustomCommandMessages().isEmpty()) {
            editingPerson.getCustomCommandMessages().stream()
                    .forEach(m -> {
                        if (person.getCustomCommandMessages().contains(m)) {
                            person.getCustomCommandMessages().remove(m);
                        }
                        person.getCustomCommandMessages().add(m);
                    });
        }
    }

    private Room getRoom(int roomId) {
        return game.getRooms().stream()
                .filter(r -> r.getId() == roomId)
                .findFirst()
                .orElse(null);
    }

    private Room getRoomOfObject(int objectId) {
        return game.getRooms().stream()
                .filter(r -> {
                    return !r.getObjects().stream()
                            .filter(o -> o.getId() == objectId)
                            .collect(Collectors.toList())
                            .isEmpty();
                })
                .findFirst()
                .orElse(null);
    }

    private AdvObject getGameObject(int objectId) {
        AdvObject object = game.getInventory().stream()
                .filter(o -> o.getId() == objectId)
                .findFirst()
                .orElse(null);

        if (object != null) {
            return object;
        }

        object = game.getInvisibleObjects().stream()
                .filter(o -> o.getId() == objectId)
                .findFirst()
                .orElse(null);

        if (object != null) {
            return object;
        }

        return game.getRooms().stream()
                .map(r -> r.getObjects())
                .flatMap(o -> o.stream())
                .filter(o -> o.getId() == objectId)
                .findFirst()
                .orElse(null);
    }

    private Room getRoomOfPerson(int personId) {
        return game.getRooms().stream()
                .filter(r -> {
                    return !r.getPeople().stream()
                            .filter(p -> p.getId() == personId)
                            .collect(Collectors.toList())
                            .isEmpty();
                })
                .findFirst()
                .orElse(null);
    }

    private Person getPerson(int personId) {
        return game.getRooms().stream()
                .map(r -> r.getPeople())
                .flatMap(p -> p.stream())
                .filter(p -> p.getId() == personId)
                .findFirst()
                .orElse(null);
    }

    private boolean isStoryRequiredForCommand(Command.Name command) {
        switch (command) {
            case END:
            case RESTART:
            case INVENTORY:
            case SCORE:
            case HELP:
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
            case WALK_TO:
            case LOOK_AT:
            case READ:
            case SPEAK:
            case COMBINE:
            case SING:
                return false;
            case OPEN:
            case CLOSE:
            case PUSH:
            case PULL:
            case PICK_UP:
            case GIVE:
                return true;
            default:
                throw new AssertionError(command.name());
        }
    }

    private boolean isValidStory(Story story) {
        Boolean isRoomValid = story.getInput().getRoom() == null
                || story.getInput().getRoom() == game.getCurrentRoom().getId();

        if (!isRoomValid) {
            return false;
        }

        return story.getInput().getInventoryRequirements() == null
                || story.getInput().getInventoryRequirements().isEmpty()
                || game.getInventory().stream()
                        .map(o -> o.getId())
                        .collect(Collectors.toSet())
                        .containsAll(story.getInput().getInventoryRequirements());
    }
}
