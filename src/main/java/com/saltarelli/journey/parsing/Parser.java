package com.saltarelli.journey.parsing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Matchable;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.InteractiveElement;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Player;
import com.saltarelli.journey.type.Room;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Alberto
 */
public class Parser {

    private final Set<String> stopwords;

    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    public ParserOutput parse(
            String input,
            Set<Command> commands,
            Set<Direction> directions,
            Set<AdvObject> inventory,
            Room currentRoom) throws ParserException {

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        tokens = removeStopwordsFromTokens(tokens);

        if (tokens.isEmpty()) {
            throw new ParserException("Invalid input", ParserException.Kind.EMPTY_INPUT);
        } else {
            String commandAlias = tokens.remove(0);
            Command command = matchableFromToken(commandAlias, commands);
            if (command == null) {
                throw new ParserException("Missing command for " + tokens.get(0), ParserException.Kind.UNKNOWN_COMMAND);
            } else {
                switch (command.getName()) {
                    case END:
                    case RESTART:
                    case INVENTORY:
                        return handleSingleCommand(command, commandAlias, tokens);
                    case NORTH:
                    case SOUTH:
                    case EAST:
                    case WEST:
                    case WALK_TO:
                        return handleDirectionCommand(command, commandAlias, input, Arrays.asList(commandAlias), directions, currentRoom);
                    case OPEN:
                        return handleBooleanCommand(command,
                                commandAlias,
                                input,
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
                        return handleBooleanCommand(command,
                                commandAlias,
                                input,
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
                        return handleBooleanCommand(command,
                                commandAlias,
                                input,
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
                        return handleBooleanCommand(command,
                                commandAlias,
                                input,
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
                        return handlePickupCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case GIVE:
                        return handleGiveCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case LOOK_AT:
                        return handleLookCommand(command, input, tokens, currentRoom, inventory);
                    case READ:
                        return handleReadCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case SPEAK:
                        return handleSpeakCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case COMBINE:
                        return handleCombineCommand(command, commandAlias, input, tokens, inventory);
                }
            }
        }
        return null;
    }

    private ParserOutput handleSingleCommand(Command command, String commandAlias, List<String> tokens) throws ParserException {
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
            Command command,
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
                    return handleBooleanElement(command, objects.iterator().next(), canDoPredicate, isDonePredicate, checkDo, exceptionCant);
                } else if (people.size() == 1) {
                    throw getCantDoException(people.iterator().next().getName(), exceptionCant, people.iterator().next().customMessageForCommand(command.getName()));
                } else {
                    throw new ParserException("Missing element", exceptionMissing, commandAlias, "");
                }
            case 1:
                InteractiveElement object = matchableFromToken(tokens.get(0), objects);
                InteractiveElement person = matchableFromToken(tokens.get(0), people);
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(inventoryObject))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (element != null) {
                    return handleBooleanElement(command, element, canDoPredicate, isDonePredicate, checkDo, exceptionCant);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private ParserOutput handleBooleanElement(
            Command command,
            InteractiveElement element,
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
            throw getCantDoException(element.getName(), exceptionKind, element.customMessageForCommand(command.getName()));
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
            Command command,
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
                        throw new ParserException("Wrong direction", ParserException.Kind.WRONG_DIRECTION, direction.getAlias().get(0), "");
                    } else {
                        return new ParserOutput(command, nextRoom);
                    }
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private ParserOutput handlePickupCommand(
            Command command,
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
                            throw new ParserException("Element can't be taken", ParserException.Kind.CANT_TAKE, "", element.customMessageForCommand(command.getName()));
                        }
                    default:
                        throw new ParserException("Missing element", ParserException.Kind.MISSING_TAKE_ELEMENT, commandAlias, "");
                }
            case 1:
                InteractiveElement object = matchableFromToken(tokens.get(0), objects);
                InteractiveElement person = matchableFromToken(tokens.get(0), people);
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(inventoryObject))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (element == null) {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                } else if (element == inventoryObject) {
                    throw new ParserException("Can't take object from inventory", ParserException.Kind.TAKE_FROM_INVENTORY);
                } else if (element.getCanTake()) {
                    if (element instanceof AdvObject) {
                        return new ParserOutput(command, (AdvObject) element);
                    } else {
                        return new ParserOutput(command, (Person) element);
                    }
                } else {
                    throw new ParserException("Element can't be taken", ParserException.Kind.CANT_TAKE, "", element.customMessageForCommand(command.getName()));
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private ParserOutput handleGiveCommand(
            Command command,
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
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null) {
                    throw new ParserException("Can't give object from a room", ParserException.Kind.CANT_GIVE, tokens.get(0), "");
                } else if (person != null) {
                    throw new ParserException(
                            "Missing element",
                            ParserException.Kind.MISSING_GIVE_ELEMENT,
                            input.substring(0, input.indexOf(tokens.get(0) + tokens.size())),
                            "");
                } else if (inventoryObject != null) {
                    return new ParserOutput(command, Player.getInstance(), inventoryObject);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            case 2:
                int objectIndex;
                person = matchableFromToken(tokens.get(0), people);

                if (person == null) {
                    person = matchableFromToken(tokens.get(1), people);

                    if (person == null) {
                        throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                    } else {
                        objectIndex = 0;
                    }
                } else {
                    objectIndex = 1;
                }

                inventoryObject = matchableFromToken(tokens.get(objectIndex), inventory);

                if (inventoryObject == null) {
                    throw new ParserException("Can't give object from a room", ParserException.Kind.CANT_GIVE, tokens.get(objectIndex), "");
                } else {
                    return new ParserOutput(command, person, inventoryObject);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(1) + tokens.size())));
        }
    }

    private ParserOutput handleLookCommand(
            Command command,
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
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null) {
                    return new ParserOutput(command, object);
                } else if (person != null) {
                    return new ParserOutput(command, person);
                } else if (inventoryObject != null) {
                    return new ParserOutput(command, inventoryObject);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private ParserOutput handleReadCommand(
            Command command,
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
                InteractiveElement inventoryObject = matchableFromToken(tokens.get(0), inventory);

                InteractiveElement element = Stream.of(Optional.ofNullable(object), Optional.ofNullable(person), Optional.ofNullable(inventoryObject))
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
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private ParserOutput handleSpeakCommand(
            Command command,
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
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null || inventoryObject != null) {
                    throw new ParserException("Can't speak within an object", ParserException.Kind.CANT_SPEAK, "", "");
                } else if (person != null) {
                    return new ParserOutput(command, person);
                } else {
                    throw new ParserException("Unknown element", ParserException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(1) + tokens.size())));
        }
    }

    private ParserOutput handleCombineCommand(
            Command command,
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
                Stream<AdvObject> stream = tokens.stream()
                        .map(t -> matchableFromToken(t, inventory));

                if (stream.anyMatch(o -> o != null)) {
                    return new ParserOutput(command, (AdvObject[]) stream.toArray());
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

    private List<String> removeStopwordsFromTokens(List<String> tokens) {
        return tokens.stream()
                .filter(t -> !stopwords.contains(t.toLowerCase()))
                .collect(Collectors.toList());
    }
}
