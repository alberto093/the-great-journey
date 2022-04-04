/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.gameplay;

import com.saltarelli.journey.Game;
import com.saltarelli.journey.json.Gameplay;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Room;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class GameplayHandler {

    private static final String DEFAULT_MESSAGE = "Ok!";

    private final Game game;
    private final Set<Gameplay> gameplaySet;

    public GameplayHandler(Game game, Set<Gameplay> gameplaySet) {
        this.game = game;
        this.gameplaySet = gameplaySet;
    }

    public GameplayHandlerResponse processOutput(ParserOutput output) {
        String message = null;

        switch (output.getCommand().getName()) {
            case END:
            case RESTART:
            case INVENTORY:
            case SCORE:
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
                        .of(customMessageResponse(output))
                        .orElse(handleLookAtCommand(output));
                break;
            case COMBINE:
                message = Optional
                        .of(customMessageResponse(output))
                        .orElse(game.getUselessCombineCommand());
                break;
            case SING:
                break;
        }

        GameplayHandlerResponse response = handleGameplayResponse(output);

        if (response != null) {
            switch (response.getType()) {
                case MESSAGE:
                    message = message + "\n\n" + ((GameplayHandlerMessage) response).getMessage();
                    ((GameplayHandlerMessage) response).setMessage(message);
                    return response;
                case QUESTION:
                    message = message + "\n\n" + ((GameplayHandlerQuestion) response).getQuestion();
                    ((GameplayHandlerQuestion) response).setQuestion(message);
                    return response;
            }
        } else if (message != null && !message.isEmpty()) {
            return GameplayHandlerResponse.newMessage(message, 0, false);
        }

        return GameplayHandlerResponse.newMessage(DEFAULT_MESSAGE, 0, false);
    }

    public GameplayHandlerResponse processQuestionAnswer(Boolean yesAnswer, ParserOutput output) {
        Gameplay gameplay = fetchGameplayFrom(output);
        GameplayHandlerResponse response = handleGameplay(gameplay, true, yesAnswer);
        if (response != null) {
            return response;
        } else {
            return GameplayHandlerResponse.newMessage(DEFAULT_MESSAGE, 0, false);
        }
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
            message = DEFAULT_MESSAGE;
        }

        return message;
    }

    private GameplayHandlerResponse handleGameplayResponse(ParserOutput output) {
        Gameplay gameplay = fetchGameplayFrom(output);

        if (gameplay != null) {
            return handleGameplay(gameplay, false, false);
        } else {
            return null;
        }
    }

    private String customMessageResponse(ParserOutput output) {
        Command.Name command = output.getCommand().getName();

        if (output.getPerson() != null && !output.getPerson().customMessageForCommand(command).isEmpty()) {
            return output.getPerson().customMessageForCommand(command);
        } else if (!output.getObjects().isEmpty()) {
            List<String> customMessages = output.getObjects().stream()
                    .map(o -> o.customMessageForCommand(command))
                    .distinct()
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (!customMessages.isEmpty()) {
                return String.join("\n", customMessages);
            }
        }

        return null;
    }

    private Gameplay fetchGameplayFrom(ParserOutput output) {
        Command.Name command = output.getCommand().getName();

        return gameplaySet.stream()
                .filter(gp -> {
                    return gp.getInput().getCommand() == command
                            && (gp.getInput().getRoom() == null || gp.getInput().getRoom() == game.getCurrentRoom().getId())
                            && (gp.getInput().getPerson() == null || gp.getInput().getPerson() == output.getPerson().getId())
                            && (gp.getInput().getInventaryRequirements() == null || 
                                gp.getInput().getInventaryRequirements().isEmpty() || 
                                game.getInventory().stream()
                                    .map(o -> o.getId())
                                    .collect(Collectors.toSet())
                                    .containsAll(gp.getInput().getInventaryRequirements()));

                })
                .findFirst()
                .orElse(null);
    }

    private GameplayHandlerResponse handleGameplay(Gameplay gameplay, Boolean checkAnswer, Boolean yesAnswer) {
        // Editing game
        if (gameplay.getOutput().getEditing() != null) {
            handleEditing(gameplay.getOutput().getEditing());
        }

        // Editing game with answer
        if (gameplay.getOutput().getQuestion() != null && checkAnswer) {
            if (yesAnswer) {
                if (gameplay.getOutput().getQuestion().getYesAnswer().getEditing() != null) {
                    handleEditing(gameplay.getOutput().getQuestion().getYesAnswer().getEditing());
                }
            } else {
                if (gameplay.getOutput().getQuestion().getNoAnswer().getEditing() != null) {
                    handleEditing(gameplay.getOutput().getQuestion().getNoAnswer().getEditing());
                }
            }
        }
        
        // create gameplay response
        if (gameplay.getOutput().getQuestion() != null && !checkAnswer) {
            return GameplayHandlerResponse.newQuestion(
                    gameplay.getOutput().getQuestion().getMessage(), 
                    gameplay.getOutput().getQuestion().getYesAnswer().getMessage(), 
                    gameplay.getOutput().getQuestion().getNoAnswer().getMessage());
        } else if (gameplay.getOutput().getMessage() != null && !gameplay.getOutput().getMessage().isEmpty()) {
            return GameplayHandlerResponse.newMessage(gameplay.getOutput().getMessage(), gameplay.getScore(), gameplay.getIsLast());
        }
        
        return null;
    }

    private void handleEditing(Gameplay.Editing editing) {
        if (editing.getObject() != null) {
            editObject(editing.getObject());
        }

        if (editing.getPerson() != null) {
            editPerson(editing.getPerson());
        }
    }

    private void editObject(Gameplay.Editing.EditingObject editingObject) {
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

    private void editPerson(Gameplay.Editing.EditingPerson editingPerson) {
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
        AdvObject inventoryObject = game.getInventory().stream()
                .filter(o -> o.getId() == objectId)
                .findFirst()
                .orElse(null);

        if (inventoryObject != null) {
            return inventoryObject;
        } else {
            return game.getRooms().stream()
                    .map(r -> r.getObjects())
                    .flatMap(o -> o.stream())
                    .filter(o -> o.getId() == objectId)
                    .findFirst()
                    .orElse(null);
        }
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
}
