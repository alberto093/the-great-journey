/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.files.AdvObjectJSON;
import com.saltarelli.journey.files.CommandJSON;
import com.saltarelli.journey.files.ExceptionDescription;
import com.saltarelli.journey.files.GameJSON;
import com.saltarelli.journey.files.PersonJSON;
import com.saltarelli.journey.files.ResourcesReader;
import com.saltarelli.journey.files.RoomJSON;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import com.saltarelli.journey.parsing.Parser;
import com.saltarelli.journey.parsing.ParserException;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Room;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Alberto
 */
public class Engine {

    private final Game game;

    private final GameplayHandler gameplay;

    private final Parser parser;

    private final Scanner scanner = new Scanner(System.in);

    private final PrintStream console;

    private final Set<ExceptionDescription> exceptions;

    public Engine() {
        GameJSON gameJSON = ResourcesReader.fetchGame();
        this.game = new Game(gameJSON);

        prepareRooms();
        preparePeople();
        prepareObjects();
        prepareCommands();

        this.gameplay = new GameplayHandler(this.game, ResourcesReader.fetchGameplay());
        this.exceptions = ResourcesReader.fetchExceptions();
        this.parser = new Parser(ResourcesReader.fetchStopwords());
        this.console = System.out;
    }

    private void prepareRooms() {
        Set<RoomJSON> roomsJSON = ResourcesReader.fetchRooms();

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
    }

    private void preparePeople() {
        Set<PersonJSON> peopleJSON = ResourcesReader.fetchPeople();

        Stream<Person> people = peopleJSON.stream()
                .map(Person::new);

        peopleJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getRoom(), this.game.getRooms());
                    Person person = people
                            .filter(p -> p.getId() == json.getId())
                            .findFirst()
                            .get();

                    room.getPeople().add(person);
                });
    }

    private void prepareObjects() {
        Set<AdvObjectJSON> objectsJSON = ResourcesReader.fetchObjects();

        Stream<AdvObject> objects = objectsJSON.stream()
                .map(AdvObject::new);

        objectsJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getRoom(), this.game.getRooms());
                    AdvObject object = objects
                            .filter(o -> o.getId() == json.getId())
                            .findFirst()
                            .get();

                    room.getObjects().add(object);
                });
    }

    private void prepareCommands() {
        Set<CommandJSON> commandsJSON = ResourcesReader.fetchCommands();

        Set<Command> commands = commandsJSON.stream()
                .map(Command::new)
                .collect(Collectors.toSet());

        this.game.setCommands(commands);
    }

    public void start() {
        console.println(game.getIntroduction());

        Optional<Boolean> showHelp = Optional.empty();

        do {
            console.println(game.getHelpQuestion());
            console.println();
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(false);
            }
        } while (showHelp.isEmpty());

        if (showHelp.get()) {
            System.out.println();
            console.println(game.getHelp());
            System.out.println();
        }

        console.println(game.getTitle());
        System.out.println();
        console.println(game.getDescription());
        System.out.println();

        System.out.println(game.getCurrentRoom().getName());
        System.out.println();
        System.out.println(game.getCurrentRoom().getDescription());
        System.out.println();

        scanNextLine("");
    }

    private void restartGame() {
        Optional<Boolean> shouldRestart = Optional.empty();

        do {
            console.println(game.getRestartQuestion());
            console.println();

            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                shouldRestart = Optional.of(false);
            }
        } while (shouldRestart.isEmpty());

        if (shouldRestart.get()) {
            console.print("\033[H\033[2J");
            console.flush();
            start();
        }
    }

    private void endGame() {
        Optional<Boolean> shouldEnd = Optional.empty();

        do {
            console.println(game.getEndQuestion());
            console.println();

            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                shouldEnd = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                shouldEnd = Optional.of(false);
            }
        } while (shouldEnd.isEmpty());

        if (shouldEnd.get()) {
            System.exit(0);
        }
    }

    private void printInventory() {

    }

    private void scanNextLine(String previousInput) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            try {
                ParserOutput output = parser.parse(
                        previousInput + " " + input,
                        game.getCommands(),
                        game.getDirections(),
                        game.getInventory(),
                        game.getCurrentRoom());

                switch (output.getCommand().getName()) {
                    case END:
                        endGame();
                        break;
                    case RESTART:
                        restartGame();
                        break;
                    case INVENTORY:
                        printInventory();
                        break;
                    default:
                        String outputMessage = gameplay.processOutput(output);
                        console.print(outputMessage);
                        console.println();
                }
            } catch (ParserException ex) {
                handleParserException(ex);
            }
        }
    }

    private void handleParserException(ParserException ex) {
        String message = "";
        String previousInput = "";

        if (!ex.getCustomOutputMessage().isEmpty()) {
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
                            .filter(e -> e.getName().name() == ex.getKind().name())
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
                            .filter(e -> e.getName().name() == ex.getKind().name())
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
                            .filter(e -> e.getName().name() == ex.getKind().name())
                            .findFirst()
                            .map(e -> e.getMessage())
                            .orElse(ex.getMessage());

                    previousInput = ex.getAdditionalDescription();
                    break;
                case WRONG_DIRECTION:

                    if (!game.getCurrentRoom().getWrongDirectionMessage().isEmpty()) {
                        message = game.getCurrentRoom().getWrongDirectionMessage();
                    } else {
                        message = this.exceptions.stream()
                                .filter(e -> e.getName().name() == ex.getKind().name())
                                .findFirst()
                                .map(e -> String.format(e.getMessage(), ex.getAdditionalDescription()))
                                .orElse(ex.getMessage());
                    }

                    break;
            }
        }

        console.println(message);
        scanNextLine(previousInput);
    }

    private Room findRoomWithID(int id, Set<Room> rooms) {
        return rooms.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .get();
    }
}
