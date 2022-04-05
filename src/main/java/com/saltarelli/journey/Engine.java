/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.gameplay.GameplayHandler;
import com.saltarelli.journey.json.AdvObjectJSON;
import com.saltarelli.journey.json.CommandJSON;
import com.saltarelli.journey.json.ExceptionDescription;
import com.saltarelli.journey.json.GameJSON;
import com.saltarelli.journey.json.PersonJSON;
import com.saltarelli.journey.json.ResourcesReader;
import com.saltarelli.journey.json.RoomJSON;
import com.saltarelli.journey.gameplay.GameplayHandlerResponse;
import com.saltarelli.journey.gameplay.GameplayHandlerMessage;
import com.saltarelli.journey.gameplay.GameplayHandlerQuestion;
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class Engine implements Runnable {

    private final Game game;

    private final GameplayHandler gameplayHandler;

    private final Parser parser;

    private final Scanner scanner;

    private final Collection<ExceptionDescription> exceptions;
    
    private final Set<PredefinedCommand> predefinedCommands;

    private int moves = 0;

    public Engine(InputStream inputStream, PrintStream printStream) {
        GameJSON gameJSON = ResourcesReader.fetchGame();
        this.game = new Game(gameJSON);
        this.scanner = new Scanner(inputStream);
        prepareRooms();
        preparePeople();
        preparePlayer();
        prepareObjects();
        prepareCommands();
        prepareDirections();

        this.gameplayHandler = new GameplayHandler(this.game, ResourcesReader.fetchGameplaySet());
        this.exceptions = ResourcesReader.fetchExceptions();
        this.predefinedCommands = ResourcesReader.fetchPredefinedCommands();
        this.parser = new Parser(ResourcesReader.fetchStopwords());
        
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void prepareRooms() {

        Collection<RoomJSON> roomsJSON = ResourcesReader.fetchRooms();

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
    }

    private void preparePeople() {
        Collection<PersonJSON> peopleJSON = ResourcesReader.fetchPeople();

        Set<Person> people = peopleJSON.stream()
                .map(Person::new)
                .collect(Collectors.toSet());

        peopleJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getRoom(), this.game.getRooms());
                    Person person = people.stream()
                            .filter(p -> p.getId() == json.getId())
                            .findFirst()
                            .get();

                    room.getPeople().add(person);
                });
    }

    private void preparePlayer() {
        PersonJSON playerJSON = ResourcesReader.fetchPlayer();
        Player.setInstance(new Player(playerJSON));
    }

    private void prepareObjects() {
        Collection<AdvObjectJSON> objectsJSON = ResourcesReader.fetchObjects();

        Set<AdvObject> objects = objectsJSON.stream()
                .map(AdvObject::new)
                .collect(Collectors.toSet());

        objectsJSON.stream()
                .forEach(json -> {
                    AdvObject object = objects.stream()
                            .filter(o -> o.getId() == json.getId())
                            .findFirst()
                            .get();

                    Room room = findRoomWithID(json.getRoom(), this.game.getRooms());

                    if (room != null) {
                        room.getObjects().add(object);
                    } else {
                        game.getInvisibleObjects().add(object);
                    }
                });
    }

    private void prepareCommands() {
        Collection<CommandJSON> commandsJSON = ResourcesReader.fetchCommands();

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
        System.out.println(game.getIntroduction());

        Optional<Boolean> showHelp = Optional.empty();

        do {
            System.out.println();
            System.out.println(game.getHelpQuestion());
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(false);
            }
        } while (!showHelp.isPresent());

        System.out.println();
        if (showHelp.get()) {
            printHelp();
        }

        System.out.println(game.getTitle());
        System.out.println();
        System.out.println(game.getDescription());
        System.out.println();

        System.out.println(game.getCurrentRoom().getName());
        System.out.println(game.getCurrentRoom().getDescription());
        System.out.println();

        scanNextLine("");
    }

    private void restartGame() {
        Optional<Boolean> shouldRestart = Optional.empty();

        do {
            System.out.println(game.getRestartQuestion());
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(false);
            }
        } while (!shouldRestart.isPresent());

        if (shouldRestart.get()) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            start();
        }
    }

    private void endGame() {
        Optional<Boolean> shouldEnd = Optional.empty();

        do {
            System.out.println(game.getEndQuestion());
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
        System.out.println(game.getEndGameMessage());
        printScore();
        scanner.nextLine();
        System.exit(0);
    }

    private void printScore() {
        System.out.println(String.format(game.getScoreMessage(), game.getCurrentScore(), game.getMaxScore(), moves));
        System.out.println();
    }
    
    private void printHelp() {
        System.out.println(game.getHelp());
        System.out.println();
    }

    private void printInventory() {
        if (game.getInventory().isEmpty()) {
            System.out.println(game.getInventoryEmpty());
        } else {
            System.out.println(game.getInventoryFull());

            game.getInventory().stream().forEach(o -> {
                String inventoryDescription = Optional
                        .ofNullable(o.customMessageForCommand(Command.Name.INVENTORY))
                        .orElse(o.getName());
                System.out.println("\t - " + inventoryDescription);
            });
        }
        
        System.out.println();
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

            System.out.println();
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
                        GameplayHandlerResponse gameplayResponse = gameplayHandler.processOutput(output);
                        handleGameplayResponse(output, gameplayResponse);
                        break;
                }
            } catch (ParserException ex) {
                handleParserException(ex);
            }
        }
    }

    private void handleGameplayResponse(ParserOutput output, GameplayHandlerResponse response) {

        switch (response.getType()) {
            case MESSAGE:
                GameplayHandlerMessage responseMessage = (GameplayHandlerMessage) response;

                System.out.println(responseMessage.getMessage());
                System.out.println();

                if (responseMessage.getScore() != null && responseMessage.getScore() > 0) {
                    game.setCurrentScore(game.getCurrentScore() + responseMessage.getScore());
                    System.out.println(String.format(game.getIncreaseScoreMessage(), responseMessage.getScore()));
                    System.out.println();
                }

                if (response.getIsLast() != null && response.getIsLast()) {
                    finishGame();
                }
                break;
            case QUESTION:
                GameplayHandlerQuestion responseQuestion = (GameplayHandlerQuestion) response;

                Optional<Boolean> isYesAnswer = Optional.empty();

                do {
                    System.out.println(responseQuestion.getQuestion());
                    String answer = scanner.nextLine();

                    if (game.getYesAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(true);
                    } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(false);
                    }
                } while (!isYesAnswer.isPresent());

                GameplayHandlerResponse questionResponse;

                if (isYesAnswer.get()) {
                    questionResponse = gameplayHandler.processQuestionAnswer(true, output);
                } else {
                    questionResponse = gameplayHandler.processQuestionAnswer(false, output);
                }

                System.out.println();

                handleGameplayResponse(output, questionResponse);
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

        System.out.println(message);
        System.out.println();
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
