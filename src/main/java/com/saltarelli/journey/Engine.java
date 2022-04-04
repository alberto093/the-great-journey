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
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class Engine {

    private final Game game;

    private final GameplayHandler gameplayHandler;

    private final Parser parser;

    private final Scanner scanner = new Scanner(System.in);

    private final PrintStream console;

    private final Collection<ExceptionDescription> exceptions;
    
    private int moves = 0;

    public Engine() {
        GameJSON gameJSON = ResourcesReader.fetchGame();
        this.game = new Game(gameJSON);

        prepareRooms();
        preparePeople();
        prepareObjects();
        prepareCommands();

        this.gameplayHandler = new GameplayHandler(this.game, ResourcesReader.fetchGameplaySet());
        this.exceptions = ResourcesReader.fetchExceptions();
        this.parser = new Parser(ResourcesReader.fetchStopwords());
        this.console = System.out;
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

    private void prepareObjects() {
        Collection<AdvObjectJSON> objectsJSON = ResourcesReader.fetchObjects();

        Set<AdvObject> objects = objectsJSON.stream()
                .map(AdvObject::new)
                .collect(Collectors.toSet());

        objectsJSON.stream()
                .forEach(json -> {
                    Room room = findRoomWithID(json.getRoom(), this.game.getRooms());
                    AdvObject object = objects.stream()
                            .filter(o -> o.getId() == json.getId())
                            .findFirst()
                            .get();

                    room.getObjects().add(object);
                });
    }

    private void prepareCommands() {
        Collection<CommandJSON> commandsJSON = ResourcesReader.fetchCommands();

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
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(false);
            }
        } while (showHelp.isEmpty());

        console.println();
        if (showHelp.get()) {
            console.println(game.getHelp());
            console.println();
        }

        console.println(game.getTitle());
        console.println();
        console.println(game.getDescription());
        console.println();

        console.println(game.getCurrentRoom().getName());
        console.println();
        console.println(game.getCurrentRoom().getDescription());
        console.println();

        scanNextLine("");
    }

    private void restartGame() {
        Optional<Boolean> shouldRestart = Optional.empty();

        do {
            console.println(game.getRestartQuestion());
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
    
    private void finishGame() {
        console.println(game.getEndGameMessage());
        scanner.nextLine();
        System.exit(0);
    }
    
    private void printScore() {
        console.println(String.format(game.getScoreMessage(), game.getCurrentScore(), game.getMaxScore(), moves));
        console.println();
    }

    private void printInventory() {
        if (game.getInventory().isEmpty()) {
            console.println(game.getInventoryEmpty());
        } else {
            console.println(game.getInventoryFull());
            
            game.getInventory().stream().forEach(o -> {
                String inventoryDescription = Optional
                        .ofNullable(o.customMessageForCommand(Command.Name.INVENTORY))
                        .orElse(o.getName());
                console.println("\t - " + inventoryDescription);
            });

            console.println();
        }
    }

    private void scanNextLine(String previousInput) {
        while (scanner.hasNextLine()) {
            
            String input; 
            if (previousInput.isEmpty()) {
                input = scanner.nextLine();
            } else {
                input = previousInput + " " + scanner.nextLine();
            }
            moves += 1;     

            try {
                ParserOutput output = parser.parse(
                        input,
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
                    case SCORE:
                        printScore();
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
                
                console.println(responseMessage.getMessage());
                console.println();
                
                if (responseMessage.score) {
                    game.setCurrentScore(game.getCurrentScore() + 1);
                    console.println(game.getIncreaseScoreMessage());
                    console.println();
                }
                
                if (response.isLast) {
                    finishGame();
                }
                break;
            case QUESTION:
                GameplayHandlerQuestion responseQuestion = (GameplayHandlerQuestion) response;

                Optional<Boolean> isYesAnswer = Optional.empty();

                do {
                    console.println(responseQuestion.getQuestion());
                    String answer = scanner.nextLine();

                    if (game.getYesAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(true);
                    } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                        isYesAnswer = Optional.of(false);
                    }
                } while (isYesAnswer.isEmpty());
                
                GameplayHandlerResponse questionResponse;

                if (isYesAnswer.get()) {
                    console.println(responseQuestion.getYesAnswer());
                    questionResponse = gameplayHandler.processQuestionAnswer(true, output);
                } else {
                    console.println(responseQuestion.getNoAnswer());
                    questionResponse = gameplayHandler.processQuestionAnswer(false, output);
                }

                console.println();
                
                handleGameplayResponse(output, questionResponse);
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

                    if (!game.getCurrentRoom().getWrongDirectionMessage().isEmpty()) {
                        message = game.getCurrentRoom().getWrongDirectionMessage();
                    } else {
                        message = this.exceptions.stream()
                                .filter(e -> e.getName().name().equals(ex.getKind().name()))
                                .findFirst()
                                .map(e -> String.format(e.getMessage(), ex.getAdditionalDescription()))
                                .orElse(ex.getMessage());
                    }

                    break;
            }
        }

        console.println(message);
        console.println();
        scanNextLine(previousInput);
    }

    private Room findRoomWithID(int id, Set<Room> rooms) {
        return rooms.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .get();
    }
}
