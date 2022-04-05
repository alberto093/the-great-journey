package com.saltarelli.journey.parsing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.saltarelli.journey.json.PredefinedCommand;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Matchable;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.InteractiveElement;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Player;
import com.saltarelli.journey.type.Room;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 * @author Alberto
 */
public class Parser {

    private final Collection<String> stopwords;

    public Parser(Collection<String> stopwords) {
        this.stopwords = stopwords;
    }

    public ParserOutput parse(
            String input,
            Set<Command> commands,
            Set<Direction> directions,
            Set<AdvObject> inventory,
            Room currentRoom,
            Collection<PredefinedCommand> predefinedCommands) throws ParserException {

        String trim = input.trim();
        List<String> tokens;

        switch (trim.length()) {
            case 0:
                tokens = Collections.emptyList();
                break;
            case 1:
                tokens = new ArrayList<>(Arrays.asList(trim));
                break;
            default:
                tokens = new ArrayList<>(Arrays.asList(trim.split("\\s+")));

                if (tokens.size() > 1) {
                    String firstToken = trim.split("\\s+")[0];
                    tokens = new ArrayList<>(Arrays.asList(firstToken));
                    tokens.addAll(Arrays.asList(trim.substring(firstToken.length() + 1).split(getStopwordsRegex())));
                    tokens = clearTokens(tokens, currentRoom, inventory, directions);
                    tokens.removeAll(Arrays.asList("", null));
                }
        }

        if (tokens.isEmpty()) {
            throw new ParserException("Invalid input", ParserException.Kind.EMPTY_INPUT);
        } else {
            String commandAlias = tokens.remove(0);
            
            PredefinedCommand predefinedCommand= predefinedAnswerForCommand(commandAlias, predefinedCommands);
                    
            if (predefinedCommand != null) {
                throw new ParserException("Predefined answer", ParserException.Kind.PREDEFINED_COMMAND, "", predefinedCommand.getAnswer());
            }

            Command command = matchableFromToken(commandAlias, commands);

            if (command == null) {
                throw new ParserException("Missing command for " + commandAlias, ParserException.Kind.UNKNOWN_COMMAND);
            } else {
                Command.Name commandName = command.getName();
                switch (commandName) {
                    case END:
                    case RESTART:
                    case INVENTORY:
                    case SCORE:
                    case SING:
                    case HELP:
                        return handleSingleCommand(commandName, commandAlias, tokens);
                    case NORTH:
                    case SOUTH:
                    case EAST:
                    case WEST:
                        return handleDirectionCommand(Command.Name.WALK_TO, commandAlias, trim, Arrays.asList(commandAlias), directions, currentRoom);
                    case WALK_TO:
                        return handleDirectionCommand(commandName, commandAlias, trim, tokens, directions, currentRoom);
                    case OPEN:
                        return handleBooleanCommand(commandName,
                                commandAlias,
                                trim,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanOpen(),
                                e -> e.getIsOpen(),
                                false,
                                ParserException.Kind.CANT_OPEN,
                                ParserException.Kind.MISSING_OPEN_ELEMENT);
                    case CLOSE:
                        return handleBooleanCommand(commandName,
                                commandAlias,
                                trim,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanClose(),
                                e -> e.getIsOpen(),
                                true,
                                ParserException.Kind.CANT_CLOSE,
                                ParserException.Kind.MISSING_CLOSE_ELEMENT);
                    case PUSH:
                        return handleBooleanCommand(commandName,
                                commandAlias,
                                trim,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanPush(),
                                e -> e.getIsPush(),
                                false,
                                ParserException.Kind.CANT_PUSH,
                                ParserException.Kind.MISSING_PUSH_ELEMENT);
                    case PULL:
                        return handleBooleanCommand(commandName,
                                commandAlias,
                                trim,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanPull(),
                                e -> e.getIsPush(),
                                false,
                                ParserException.Kind.CANT_PULL,
                                ParserException.Kind.MISSING_PULL_ELEMENT);
                    case PICK_UP:
                        return handlePickupCommand(commandName, commandAlias, trim, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case GIVE:
                        return handleGiveCommand(commandName, commandAlias, trim, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case LOOK_AT:
                        return handleLookCommand(commandName, trim, tokens, currentRoom, inventory);
                    case READ:
                        return handleReadCommand(commandName, commandAlias, trim, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case SPEAK:
                        return handleSpeakCommand(commandName, commandAlias, trim, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case COMBINE:
                        return handleCombineCommand(commandName, commandAlias, trim, tokens, inventory);
                    default:
                        throw new AssertionError(commandName.name());
                }
            }
        }
    }

    private ParserOutput handleSingleCommand(Command.Name command, String commandAlias, List<String> tokens) throws ParserException {
        switch (tokens.size()) {
            case 0:
                return new ParserOutput(command);
            default:
                throw getLongInputException(commandAlias);
        }
    }

    private ParserException getLongInputException(String lastValidToken) {
        return new ParserException(
                "Long input, last valid token: " + lastValidToken,
                ParserException.Kind.LONG_INPUT,
                lastValidToken,
                "");
    }

    private ParserOutput handleBooleanCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Person> people,
            Set<AdvObject> objects,
            Set<AdvObject> inventory,
            Predicate<? super InteractiveElement> canDoPredicate,
            Predicate<? super InteractiveElement> isDonePredicate,
            Boolean checkDo,
            ParserException.Kind exceptionCant,
            ParserException.Kind exceptionMissing) throws ParserException {

        switch (tokens.size()) {
            case 0:
                if (objects.size() == 1) {
                    return handleBooleanElement(command, objects.iterator().next(), objects.iterator().next().getName(), canDoPredicate, isDonePredicate, checkDo, exceptionCant);
                } else if (people.size() == 1) {
                    throw getCantDoException(people.iterator().next().getName(), exceptionCant, people.iterator().next().customMessageForCommand(command));
                } else {
                    throw new ParserException("Missing element", exceptionMissing, commandAlias, "");
                }
            case 1:
                InteractiveElement object = matchableFromToken(tokens.get(0), objects);
                InteractiveElement person = matchableFromToken(tokens.get(0), people);
                InteractiveElement player = playerFromToken(tokens.get(0));
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(player), Optional.ofNullable(inventoryObject))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (element != null) {
                    return handleBooleanElement(command, element, tokens.get(0), canDoPredicate, isDonePredicate, checkDo, exceptionCant);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handleBooleanElement(
            Command.Name command,
            InteractiveElement element,
            String elementAlias,
            Predicate<? super InteractiveElement> canDoPredicate,
            Predicate<? super InteractiveElement> isDonePredicate,
            Boolean checkDo,
            ParserException.Kind exceptionKind) throws ParserException {

        if (canDoPredicate.test(element) && checkDo != isDonePredicate.test(element)) {
            if (element instanceof AdvObject) {
                return new ParserOutput(command, (AdvObject) element);
            } else {
                return new ParserOutput(command, (Person) element);
            }
        } else {
            throw getCantDoException(elementAlias, exceptionKind, element.customMessageForCommand(command));
        }
    }

    private ParserException getCantDoException(String additionalDescription, ParserException.Kind exceptionKind, String elementCustomMessage) {
        String message = "Object " + additionalDescription;
        switch (exceptionKind) {
            case CANT_OPEN:
                message += " can't be opened";
                break;
            case CANT_CLOSE:
                message += " can't be closed";
                break;
            case CANT_PUSH:
                message += " can't be pushed";
                break;
            case CANT_PULL:
                message += " can't be pulled";
                break;
            default:
                throw new AssertionError(exceptionKind.name());

        }
        return new ParserException(message, exceptionKind, additionalDescription, elementCustomMessage);
    }

    private ParserOutput handleDirectionCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Direction> directions,
            Room currentRoom) throws ParserException {

        switch (tokens.size()) {
            case 0:
                throw new ParserException("Missing direction", ParserException.Kind.MISSING_DIRECTION, commandAlias, "");
            case 1:
                Direction direction = matchableFromToken(tokens.get(0), directions);

                if (direction == null) {
                    throw new ParserException("Invalid direction", ParserException.Kind.INVALID_DIRECTION);
                } else {
                    Room nextRoom = currentRoom.getRoomWithDirection(direction);

                    if (nextRoom == null) {
                        throw new ParserException("Wrong direction", ParserException.Kind.WRONG_DIRECTION, direction.getAlias().iterator().next(), "");
                    } else {
                        return new ParserOutput(command, nextRoom);
                    }
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handlePickupCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Person> people,
            Set<AdvObject> objects,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                Integer numberOfElements = objects.size() + people.size();

                switch (numberOfElements) {
                    case 1:
                        InteractiveElement element = !objects.isEmpty() ? objects.iterator().next() : people.iterator().next();

                        if (element.getCanTake()) {
                            if (element instanceof AdvObject) {
                                return new ParserOutput(command, (AdvObject) element);
                            } else {
                                return new ParserOutput(command, (Person) element);
                            }
                        } else {
                            throw new ParserException("Element can't be taken", ParserException.Kind.CANT_TAKE, tokens.get(0), element.customMessageForCommand(command));
                        }
                    default:
                        throw new ParserException("Missing element", ParserException.Kind.MISSING_TAKE_ELEMENT, commandAlias, "");
                }
            case 1:
                InteractiveElement object = matchableFromToken(tokens.get(0), objects);
                InteractiveElement person = matchableFromToken(tokens.get(0), people);
                InteractiveElement player = playerFromToken(tokens.get(0));
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(player), Optional.ofNullable(inventoryObject))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (element == null) {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                } else if (element == inventoryObject) {
                    throw new ParserException("Can't take object from inventory", ParserException.Kind.TAKE_FROM_INVENTORY);
                } else if (element.getCanTake() != null && element.getCanTake()) {
                    if (element instanceof AdvObject) {
                        return new ParserOutput(command, (AdvObject) element);
                    } else {
                        return new ParserOutput(command, (Person) element);
                    }
                } else {
                    throw new ParserException("Element can't be taken", ParserException.Kind.CANT_TAKE, tokens.get(0), element.customMessageForCommand(command));
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handleGiveCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Person> people,
            Set<AdvObject> objects,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                throw new ParserException("Missing element", ParserException.Kind.MISSING_GIVE_ELEMENT, commandAlias, "");
            case 1:
                AdvObject object = matchableFromToken(tokens.get(0), objects);
                Person person = matchableFromToken(tokens.get(0), people);
                Person player = playerFromToken(tokens.get(0));
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null) {
                    throw new ParserException("Can't give object from a room", ParserException.Kind.CANT_GIVE, tokens.get(0), "");
                } else if (person != null || player != null) {
                    throw new ParserException(
                            "Missing element",
                            ParserException.Kind.MISSING_GIVE_ELEMENT,
                            input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()),
                            "");
                } else if (inventoryObject != null) {
                    if (people.size() == 1) {
                        return new ParserOutput(command, people.iterator().next(), inventoryObject);
                    } else {
                       return new ParserOutput(command, Player.getInstance(), inventoryObject); 
                    }
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            case 2:
                int objectIndex;
                person = Stream.of(Optional.ofNullable(matchableFromToken(tokens.get(0), people)), Optional.ofNullable(playerFromToken(tokens.get(0))))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (person == null) {
                    person = Stream.of(Optional.ofNullable(matchableFromToken(tokens.get(1), people)), Optional.ofNullable(playerFromToken(tokens.get(1))))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .findFirst()
                            .orElse(null);

                    if (person == null) {
                        throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                    } else {
                        objectIndex = 0;
                    }
                } else {
                    objectIndex = 1;
                }

                object = matchableFromToken(tokens.get(objectIndex), objects);
                inventoryObject = matchableFromToken(tokens.get(objectIndex), inventory);

                if (inventoryObject != null) {
                    return new ParserOutput(command, person, inventoryObject);
                } else if (object != null) {
                    throw new ParserException("Can't give object from a room", ParserException.Kind.CANT_GIVE, tokens.get(objectIndex), "");
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(1)) + tokens.get(1).length()));
        }
    }

    private ParserOutput handleLookCommand(
            Command.Name command,
            String input,
            List<String> tokens,
            Room currentRoom,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                return new ParserOutput(command, currentRoom);
            case 1:
                AdvObject object = matchableFromToken(tokens.get(0), currentRoom.getObjects());
                Person person = matchableFromToken(tokens.get(0), currentRoom.getPeople());
                Player player = playerFromToken(tokens.get(0));
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null) {
                    return new ParserOutput(command, object);
                } else if (person != null) {
                    return new ParserOutput(command, person);
                } else if (player != null) {
                    return new ParserOutput(command, player);
                } else if (inventoryObject != null) {
                    return new ParserOutput(command, inventoryObject);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handleReadCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Person> people,
            Set<AdvObject> objects,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                Integer numberOfElements = objects.size() + people.size();

                switch (numberOfElements) {
                    case 1:
                        InteractiveElement element = !objects.isEmpty() ? objects.iterator().next() : people.iterator().next();
                        if (element instanceof AdvObject) {
                            return new ParserOutput(command, (AdvObject) element);
                        } else {
                            return new ParserOutput(command, (Person) element);
                        }
                    default:
                        throw new ParserException("Missing element", ParserException.Kind.MISSING_READ_ELEMENT, commandAlias, "");
                }
            case 1:
                InteractiveElement object = matchableFromToken(tokens.get(0), objects);
                InteractiveElement person = matchableFromToken(tokens.get(0), people);
                InteractiveElement player = playerFromToken(tokens.get(0));
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(player), Optional.ofNullable(inventoryObject))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (element == null) {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                } else {
                    if (element instanceof AdvObject) {
                        return new ParserOutput(command, (AdvObject) element);
                    } else {
                        return new ParserOutput(command, (Person) element);
                    }
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handleSpeakCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<Person> people,
            Set<AdvObject> objects,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                switch (people.size()) {
                    case 0:
                        return new ParserOutput(command, Player.getInstance());
                    case 1:
                        return new ParserOutput(command, people.iterator().next());
                    default:
                        throw new ParserException("Missing person", ParserException.Kind.MISSING_SPEAK_ELEMENT, input, "");
                }
            case 1:
                AdvObject object = matchableFromToken(tokens.get(0), objects);
                Person person = matchableFromToken(tokens.get(0), people);
                Player player = playerFromToken(tokens.get(0));
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null || inventoryObject != null) {
                    throw new ParserException("Can't speak within an object", ParserException.Kind.CANT_SPEAK, "", "");
                } else if (person != null) {
                    return new ParserOutput(command, person);
                } else if (player != null) {
                    return new ParserOutput(command, player);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0)) + tokens.get(0).length()));
        }
    }

    private ParserOutput handleCombineCommand(
            Command.Name command,
            String commandAlias,
            String input,
            List<String> tokens,
            Set<AdvObject> inventory) throws ParserException {

        switch (tokens.size()) {
            case 0:
                throw new ParserException("Missing elements", ParserException.Kind.MISSING_COMBINE_ELEMENT, input, "");
            case 1:
                throw new ParserException("Minimum two elements", ParserException.Kind.MINIMUM_COMBINE, "", "");
            default:
                List<AdvObject> objects = tokens.stream()
                        .map(t -> matchableFromToken(t, inventory))
                        .filter(o -> o != null)
                        .collect(Collectors.toList());
                
                if (objects.size() == tokens.size()) {
                    return new ParserOutput(command, objects.toArray(new AdvObject[0]));
                } else {
                    throw new ParserException("Can't combine objects/people not in inventory", ParserException.Kind.CANT_COMBINE, "", "");
                }
        }
    }

    private <T extends Matchable> T matchableFromToken(String token, Set<T> items) {
        return items.stream()
                .filter(c -> c.match(token.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
    
    private PredefinedCommand predefinedAnswerForCommand(String command, Collection<PredefinedCommand> predefinedCommands) {
        return predefinedCommands.stream()
                .filter(pc -> pc.getCommands().contains(command))
                .findFirst()
                .orElse(null);
    }

    private Player playerFromToken(String token) {
        if (Player.getInstance().match(token)) {
            return Player.getInstance();
        } else {
            return null;
        }
    }

    private String getStopwordsRegex() {
        String regex = stopwords.stream().reduce("\\b(", (result, stopword) -> result + stopword + "|");
        regex = (regex.substring(0, regex.length() - 1));
        return regex + ")\\b";
    }

    private List<String> clearTokens(List<String> tokens, Room currentRoom, Set<AdvObject> inventory, Set<Direction> directions) {
        Set<AdvObject> visibleObjects = new HashSet<>(currentRoom.getObjects());
        visibleObjects.addAll(inventory);

        Set<Person> allPeople = new HashSet<>(currentRoom.getPeople());
        allPeople.add(Player.getInstance());

        return tokens.stream()
                .flatMap(s -> reduceTokens(Collections.emptyList(), s, visibleObjects, allPeople, directions).stream())
                .collect(Collectors.toList());
    }

    private List<String> reduceTokens(List<String> tokens, String input, Set<AdvObject> visibleObjects, Set<Person> allPeople, Set<Direction> directions) {
        String trim = input.trim();
        trim = trim.toLowerCase();
        trim = trim.replaceAll("\\s+", " ");
        List<String> innerTokens = Arrays.asList(trim.split("\\s+"));

        if (innerTokens.size() == 1) {

            return Stream.of(tokens, innerTokens)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

        } else if (matchableFromToken(trim, visibleObjects) != null
                || matchableFromToken(trim, allPeople) != null
                || matchableFromToken(trim, directions) != null) {

            return Stream.of(tokens, Arrays.asList(trim))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else {

            List<String> newTokens = Stream.of(tokens, innerTokens.subList(0, 1))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            int newInputIndex = input.indexOf(innerTokens.get(0)) + innerTokens.get(0).length();

            return reduceTokens(newTokens, input.substring(newInputIndex), visibleObjects, allPeople, directions);
        }
    }
}
