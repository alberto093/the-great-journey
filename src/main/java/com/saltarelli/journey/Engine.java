/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.story.StoryHandler;
import com.saltarelli.journey.json.AdvObjectJSON;
import com.saltarelli.journey.json.CommandJSON;
import com.saltarelli.journey.json.ExceptionDescription;
import com.saltarelli.journey.json.GameJSON;
import com.saltarelli.journey.json.LocalReader;
import com.saltarelli.journey.json.PersonJSON;
import com.saltarelli.journey.json.ResourcesReader;
import com.saltarelli.journey.json.RoomJSON;
import com.saltarelli.journey.story.StoryHandlerResponse;
import com.saltarelli.journey.story.StoryHandlerMessage;
import com.saltarelli.journey.story.StoryHandlerQuestion;
import com.saltarelli.journey.json.PredefinedCommand;
import java.util.Scanner;
import java.util.Set;
import com.saltarelli.journey.parsing.Parser;
import com.saltarelli.journey.parsing.ParserException;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Player;
import com.saltarelli.journey.type.Room;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Alberto
 */
public class Engine implements Runnable {

    private final Game restorationGame;
    
    private final Game game;

    private final StoryHandler storyHandler;

    private final Parser parser;

    private final Scanner scanner;
    
    private final PrintStream printStream;

    private final ResourcesReader resourcesReader;
    
    private final Collection<ExceptionDescription> exceptions;
    
    private final Collection<PredefinedCommand> predefinedCommands;

    private int moves = 0;

    public Engine(InputStream inputStream, PrintStream printStream) {
        this.resourcesReader = new LocalReader();
        GameJSON gameJSON = resourcesReader.fetchGame();
        this.game = new Game(gameJSON);
        this.restorationGame = new Game(gameJSON);
        this.scanner = new Scanner(inputStream);
        prepareRooms();
        preparePeople(this.game);
        preparePlayer();
        prepareObjects(this.game);
        prepareCommands();
        prepareDirections();
        

        this.storyHandler = new StoryHandler(this.game, resourcesReader.fetchStories());
        this.exceptions = resourcesReader.fetchExceptions();
        this.predefinedCommands = resourcesReader.fetchPredefinedCommands();
        this.parser = new Parser(resourcesReader.fetchStopwords());
        this.printStream = printStream;
    }

    private void prepareRooms() {

        Collection<RoomJSON> roomsJSON = resourcesReader.fetchRooms();

        Set<Room> rooms = roomsJSON.stream()
                .map(Room::new)
                .collect(Collectors.toSet());

        roomsJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getId(), rooms);

                    if (json.getNorth() != null) {
                        room.setNorth(findRoomWithID(json.getNorth(), rooms));
                    }

                    if (json.getSouth() != null) {
                        room.setSouth(findRoomWithID(json.getSouth(), rooms));
                    }

                    if (json.getEast() != null) {
                        room.setEast(findRoomWithID(json.getEast(), rooms));
                    }

                    if (json.getWest() != null) {
                        room.setWest(findRoomWithID(json.getWest(), rooms));
                    }
                });

        this.game.setRooms(rooms);
        this.game.setCurrentRoom(findRoomWithID(this.game.getInitialRoom(), rooms));
        
        Set<Room> restorationRooms = new HashSet<>(rooms);
        this.restorationGame.setRooms(new HashSet<>(rooms));
        this.restorationGame.setCurrentRoom(findRoomWithID(this.restorationGame.getInitialRoom(), restorationRooms));
    }

    private void preparePeople(Game game) {
        Collection<PersonJSON> peopleJSON = resourcesReader.fetchPeople();

        Set<Person> people = peopleJSON.stream()
                .map(Person::new)
                .collect(Collectors.toSet());

        peopleJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getRoom(), game.getRooms());
                    Person person = people.stream()
                            .filter(p -> p.getId() == json.getId())
                            .findFirst()
                            .get();

                    room.getPeople().add(person);
                });
    }

    private void preparePlayer() {
        PersonJSON playerJSON = resourcesReader.fetchPlayer();
        Player.setInstance(new Player(playerJSON));
    }

    private void prepareObjects(Game game) {
        Collection<AdvObjectJSON> objectsJSON = resourcesReader.fetchObjects();

        Set<AdvObject> objects = objectsJSON.stream()
                .map(AdvObject::new)
                .collect(Collectors.toSet());

        objectsJSON.stream()
                .forEach(json -> {
                    AdvObject object = objects.stream()
                            .filter(o -> o.getId() == json.getId())
                            .findFirst()
                            .get();

                    Room room = findRoomWithID(json.getRoom(), game.getRooms());

                    if (room != null) {
                        room.getObjects().add(object);
                    } else {
                        game.getInvisibleObjects().add(object);
                    }
                });
    }

    private void prepareCommands() {
        Collection<CommandJSON> commandsJSON = resourcesReader.fetchCommands();

        Set<Command> commands = commandsJSON.stream()
                .map(Command::new)
                .collect(Collectors.toSet());

        this.game.setCommands(commands);
    }

    private void prepareDirections() {
        Set<Direction> directions = Arrays.stream(Direction.Kind.values())
                .map(d -> {
                    Command command = findCommandFromDirection(d);
                    return new Direction(d, command.getAlias());
                })
                .collect(Collectors.toSet());

        this.game.setDirections(directions);
    }

    public void run() {
        printStream.println(game.getIntroduction());

        Optional<Boolean> showHelp = Optional.empty();

        do {
            printStream.println();
            printStream.println(game.getHelpQuestion());
            
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(false);
            }
        } while (!showHelp.isPresent());

        printStream.println();
        if (showHelp.get()) {
            printHelp();
        }

        printStream.println(game.getTitle());
        printStream.println();
        printStream.println(game.getDescription());
        printStream.println();

        printStream.println(game.getCurrentRoom().getName());
        printStream.println(game.getCurrentRoom().getDescription());
        printStream.println();

        scanNextLine("");
    }

    private void restartGame() {
        Optional<Boolean> shouldRestart = Optional.empty();

        do {
            printStream.println(game.getRestartQuestion());
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(false);
            }
        } while (!shouldRestart.isPresent());

        if (shouldRestart.get()) {
            this.moves = 0;
            this.game.getInventory().clear();
            this.game.setCurrentScore(0);
            
            Set<Room> rooms = new HashSet<>(this.restorationGame.getRooms());
            this.game.setRooms(rooms);
            this.game.setCurrentRoom(findRoomWithID(this.restorationGame.getInitialRoom(), rooms));
            preparePeople(this.game);
            prepareObjects(this.game);
            
            IntStream.range(0, 10)
                    .forEach(i -> printStream.println());
         
            run();
        }
    }

    private void endGame() {
        Optional<Boolean> shouldEnd = Optional.empty();

        do {
            printStream.println(game.getEndQuestion());
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                shouldEnd = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                shouldEnd = Optional.of(false);
            }
        } while (!shouldEnd.isPresent());

        if (shouldEnd.get()) {
            System.exit(0);
        }
    }

    private void finishGame() {
        printStream.println(game.getEndGameMessage());
        printScore();
        scanner.nextLine();
        System.exit(0);
    }

    private void printScore() {
        printStream.println(String.format(game.getScoreMessage(), game.getCurrentScore(), game.getMaxScore(), moves));
        printStream.println();
    }
    
    private void printHelp() {
        printStream.println(game.getHelp());
        printStream.println();
    }

    private void printInventory() {
        if (game.getInventory().isEmpty()) {
            printStream.println(game.getInventoryEmpty());
        } else {
            printStream.println(game.getInventoryFull());

            game.getInventory().stream().forEach(o -> {
                String inventoryDescription = Optional
                        .ofNullable(o.customMessageForCommand(Command.Name.INVENTORY))
                        .orElse(o.getName());
                printStream.println("\t - " + inventoryDescription);
            });
        }
        
        printStream.println();
    }

    private void scanNextLine(String input) {
        String previousInput = input;
        while (scanner.hasNextLine()) {

            String newInput;
            if (previousInput.isEmpty()) {
                newInput = scanner.nextLine();
            } else {
                newInput = previousInput + " " + scanner.nextLine();
            }

            printStream.println();
            previousInput = "";
            moves += 1;

            try {
                ParserOutput output = parser.parse(
                        newInput,
                        game.getCommands(),
                        game.getDirections(),
                        game.getInventory(),
                        game.getCurrentRoom(),
                        predefinedCommands);
                
                if (output.getRoom() == null) {
                    output.setRoom(game.getCurrentRoom());
                }

                switch (output.getCommand()) {
                    case END:
                        endGame();
                        break;
                    case RESTART:
                        restartGame();
                        break;
                    case INVENTORY:
                        moves -= 1;
                        printInventory();
                        break;
                    case SCORE:
                        moves -= 1;
                        printScore();
                        break;
                    case HELP:
                        moves -= 1;
                        printHelp();
                        break;
                    default:
                        StoryHandlerResponse storyResponse = storyHandler.processOutput(output);
                        handleStoryResponse(output, storyResponse);
                        break;
                }
            } catch (ParserException ex) {
                handleParserException(ex);
            }
        }
    }

    private void handleStoryResponse(ParserOutput output, StoryHandlerResponse response) {

        switch (response.getType()) {
            case MESSAGE:
                StoryHandlerMessage responseMessage = (StoryHandlerMessage) response;

                printStream.println(responseMessage.getMessage());
                printStream.println();

                if (responseMessage.getScore() != null && responseMessage.getScore() > 0) {
                    game.setCurrentScore(game.getCurrentScore() + responseMessage.getScore());
                    printStream.println(String.format(game.getIncreaseScoreMessage(), responseMessage.getScore()));
                    printStream.println();
                }

                if (response.getIsLast() != null && response.getIsLast()) {
                    finishGame();
                }
                break;
            case QUESTION:
                StoryHandlerQuestion responseQuestion = (StoryHandlerQuestion) response;

                Optional<Boolean> isYesAnswer = Optional.empty();

                do {
                    printStream.println(responseQuestion.getQuestion());
                    String answer = scanner.nextLine();

                    if (game.getYesAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(true);
                    } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(false);
                    }
                } while (!isYesAnswer.isPresent());

                StoryHandlerResponse questionResponse;

                if (isYesAnswer.get()) {
                    questionResponse = storyHandler.processQuestionAnswer(true, output);
                } else {
                    questionResponse = storyHandler.processQuestionAnswer(false, output);
                }

                printStream.println();

                handleStoryResponse(output, questionResponse);
                break;
            default:
                throw new AssertionError(response.getType().name());
        }
    }

    private void handleParserException(ParserException ex) {
        String message = "";
        String previousInput = "";

        if (ex.getCustomOutputMessage() != null && !ex.getCustomOutputMessage().isEmpty()) {
            message = ex.getCustomOutputMessage();
        } else {
            switch (ex.getKind()) {
                case EMPTY_INPUT:
                case UNKNOWN_COMMAND:
                case CANT_SPEAK:
                case MINIMUM_COMBINE:
                case CANT_COMBINE:
                case TAKE_FROM_INVENTORY:
                case INVALID_DIRECTION:
                case UNKNOWN_ELEMENT:
                    message = this.exceptions.stream()
                            .filter(e -> e.getName().name().equals(ex.getKind().name()))
                            .findFirst()
                            .map(e -> e.getMessage())
                            .orElse(ex.getMessage());
                    break;
                case LONG_INPUT:
                case CANT_OPEN:
                case CANT_CLOSE:
                case CANT_PUSH:
                case CANT_PULL:
                case CANT_GIVE:
                case CANT_TAKE:
                    message = this.exceptions.stream()
                            .filter(e -> e.getName().name().equals(ex.getKind().name()))
                            .findFirst()
                            .map(e -> String.format(e.getMessage(), ex.getAdditionalDescription()))
                            .orElse(ex.getMessage());
                    break;
                case MISSING_OPEN_ELEMENT:
                case MISSING_CLOSE_ELEMENT:
                case MISSING_PUSH_ELEMENT:
                case MISSING_PULL_ELEMENT:
                case MISSING_TAKE_ELEMENT:
                case MISSING_GIVE_ELEMENT:
                case MISSING_USE_ELEMENT:
                case MISSING_READ_ELEMENT:
                case MISSING_SPEAK_ELEMENT:
                case MISSING_COMBINE_ELEMENT:
                case MISSING_DIRECTION:
                    message = this.exceptions.stream()
                            .filter(e -> e.getName().name().equals(ex.getKind().name()))
                            .findFirst()
                            .map(e -> e.getMessage())
                            .orElse(ex.getMessage());

                    previousInput = ex.getAdditionalDescription();
                    break;
                case WRONG_DIRECTION:
                    if (game.getCurrentRoom().getWrongDirectionMessage() != null && !game.getCurrentRoom().getWrongDirectionMessage().isEmpty()) {
                        message = game.getCurrentRoom().getWrongDirectionMessage();
                    } else {
                        message = this.exceptions.stream()
                                .filter(e -> e.getName().name().equals(ex.getKind().name()))
                                .findFirst()
                                .map(e -> String.format(e.getMessage(), ex.getAdditionalDescription()))
                                .orElse(ex.getMessage());
                    }

                    break;
                default:
                    throw new AssertionError(ex.getKind().name());
            }
        }

        printStream.println(message);
        printStream.println();
        scanNextLine(previousInput);
    }

    private Room findRoomWithID(int id, Set<Room> rooms) {
        return rooms.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private Command findCommandFromDirection(Direction.Kind direction) {
        return game.getCommands().stream()
                .filter(c -> {
                    switch (direction) {
                        case NORTH:
                            return c.getName() == Command.Name.NORTH;
                        case EAST:
                            return c.getName() == Command.Name.EAST;
                        case SOUTH:
                            return c.getName() == Command.Name.SOUTH;
                        case WEST:
                            return c.getName() == Command.Name.WEST;
                        default:
                            throw new AssertionError(direction.name());
                    }
                })
                .findFirst()
                .get();
    }
}
