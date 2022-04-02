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
            List<Command> commands,
            List<Direction> directions,
            List<AdvObject> inventory,
            Room currentRoom) throws ParsingException {

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        tokens = removeStopwordsFromTokens(tokens);

        if (tokens.isEmpty()) {
            throw new ParsingException("Invalid input", ParsingException.Kind.EMPTY_INPUT);
        } else {
            String commandAlias = tokens.remove(0);
            Command command = matchableFromToken(commandAlias, commands);
            if (command == null) {
                throw new ParsingException("Missing command for " + tokens.get(0), ParsingException.Kind.UNKNOWN_COMMAND);
            } else {
                switch (command.getName()) {
                    case END:
                        return handleSingleCommand(command, commandAlias, tokens);
                    case INVENTORY:
                        return handleSingleCommand(command, commandAlias, tokens);
                    case NORTH:
                        return handleDirectionCommand(command, commandAlias, input, Arrays.asList(commandAlias), directions, currentRoom);
                    case SOUTH:
                        return handleDirectionCommand(command, commandAlias, input, Arrays.asList(commandAlias), directions, currentRoom);
                    case EAST:
                        return handleDirectionCommand(command, commandAlias, input, Arrays.asList(commandAlias), directions, currentRoom);
                    case WEST:
                        return handleDirectionCommand(command, commandAlias, input, Arrays.asList(commandAlias), directions, currentRoom);
                    case OPEN:
                        return handleBooleanCommand(
                                command,
                                commandAlias,
                                input,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanOpen(),
                                e -> e.getIsOpen(),
                                false,
                                ParsingException.Kind.CANT_OPEN,
                                ParsingException.Kind.MISSING_OPEN_ELEMENT);
                    case CLOSE:
                        return handleBooleanCommand(
                                command,
                                commandAlias,
                                input,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanClose(),
                                e -> e.getIsOpen(),
                                true,
                                ParsingException.Kind.CANT_CLOSE,
                                ParsingException.Kind.MISSING_CLOSE_ELEMENT);
                    case PUSH:
                        return handleBooleanCommand(
                                command,
                                commandAlias,
                                input,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanPush(),
                                e -> e.getIsPush(),
                                false,
                                ParsingException.Kind.CANT_PUSH,
                                ParsingException.Kind.MISSING_PUSH_ELEMENT);
                    case PULL:
                        return handleBooleanCommand(
                                command,
                                commandAlias,
                                input,
                                tokens,
                                currentRoom.getPeople(),
                                currentRoom.getObjects(),
                                inventory,
                                e -> e.getCanPull(),
                                e -> e.getIsPush(),
                                false,
                                ParsingException.Kind.CANT_PULL,
                                ParsingException.Kind.MISSING_PULL_ELEMENT);
                    case WALK_TO:
                        return handleDirectionCommand(command, commandAlias, input, tokens, directions, currentRoom);
                    case PICK_UP:
                        return handlePickupCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case GIVE:
                        return handleGiveCommand(command, commandAlias, input, tokens, currentRoom.getPeople(), currentRoom.getObjects(), inventory);
                    case LOOK_AT:
                        return handleLookCommand(command, input, tokens, currentRoom, inventory);
                }
            }
        }
        return null;
    }

    private ParserOutput handleSingleCommand(Command command, String commandAlias, List<String> tokens) throws ParsingException {
        switch (tokens.size()) {
            case 0:
                return new ParserOutput(command);
            default:
                throw getLongInputException(commandAlias);
        }
    }

    private ParsingException getLongInputException(String lastValidToken) {
        return new ParsingException(
                "Long input, last valid token: " + lastValidToken,
                ParsingException.Kind.LONG_INPUT,
                lastValidToken,
                "");
    }

    private ParserOutput handleBooleanCommand(
            Command command,
            String commandAlias,
            String input,
            List<String> tokens,
            List<Person> people,
            List<AdvObject> objects,
            List<AdvObject> inventory,
            Predicate<? super InteractiveElement> canDoPredicate,
            Predicate<? super InteractiveElement> isDonePredicate,
            Boolean checkDo,
            ParsingException.Kind exceptionCant,
            ParsingException.Kind exceptionMissing) throws ParsingException {
        
        switch (tokens.size()) {
            case 0:
                if (objects.size() == 1) {
                    return handleBooleanElement(command, objects.get(0), canDoPredicate, isDonePredicate, checkDo, exceptionCant);
                } else if (people.size() == 1) {
                    throw getCantDoException(people.get(0).getName(), exceptionCant, people.get(0).customMessageForCommand(command.getName()));
                } else {
                    throw new ParsingException("Missing element", exceptionMissing, commandAlias, "");
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
                    throw new ParsingException("Unknown element", ParsingException.Kind.UNKNOWN_ELEMENT);
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
            ParsingException.Kind exceptionKind) throws ParsingException {

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

    private ParsingException getCantDoException(String additionalDescription, ParsingException.Kind exceptionKind, String elementCustomMessage) {
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
        return new ParsingException(message, exceptionKind, additionalDescription, elementCustomMessage);
    }

    private ParserOutput handleDirectionCommand(
            Command command,
            String commandAlias,
            String input,
            List<String> tokens,
            List<Direction> directions,
            Room currentRoom) throws ParsingException {
        
        switch (tokens.size()) {
            case 0:
                throw new ParsingException("Missing direction", ParsingException.Kind.MISSING_DIRECTION, commandAlias, "");
            case 1:
                Direction direction = matchableFromToken(tokens.get(0), directions);

                if (direction == null) {
                    throw new ParsingException("Invalid direction", ParsingException.Kind.INVALID_DIRECTION);
                } else {
                    Room nextRoom = currentRoom.getRoomWithDirection(direction);

                    if (nextRoom == null) {
                        throw new ParsingException("Wrong direction", ParsingException.Kind.WRONG_DIRECTION, direction.getAlias().get(0), "");
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
            List<Person> people,
            List<AdvObject> objects,
            List<AdvObject> inventory) throws ParsingException {
        
        switch (tokens.size()) {
            case 0:
                Integer numberOfElements = objects.size() + people.size();

                switch (numberOfElements) {
                    case 1:
                        InteractiveElement element = !objects.isEmpty() ? objects.get(0) : people.get(0);

                        if (element.getCanTake()) {
                            if (element instanceof AdvObject) {
                                return new ParserOutput(command, (AdvObject) element);
                            } else {
                                return new ParserOutput(command, (Person) element);
                            }
                        } else {
                            throw new ParsingException("Element can't be taken", ParsingException.Kind.CANT_TAKE, "", element.customMessageForCommand(command.getName()));
                        }
                    default:
                        throw new ParsingException("Missing element", ParsingException.Kind.MISSING_TAKE_ELEMENT, commandAlias, "");
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
                    throw new ParsingException("Unknown element", ParsingException.Kind.UNKNOWN_ELEMENT);
                } else if (element == inventoryObject) {
                    throw new ParsingException("Can't take object from inventory", ParsingException.Kind.TAKE_FROM_INVENTORY);
                } else if (element.getCanTake()) {
                    if (element instanceof AdvObject) {
                        return new ParserOutput(command, (AdvObject) element);
                    } else {
                        return new ParserOutput(command, (Person) element);
                    }
                } else {
                    throw new ParsingException("Element can't be taken", ParsingException.Kind.CANT_TAKE, "", element.customMessageForCommand(command.getName()));
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
            List<Person> people,
            List<AdvObject> objects,
            List<AdvObject> inventory) throws ParsingException {
        
        switch (tokens.size()) {
            case 0:
                throw new ParsingException("Missing element", ParsingException.Kind.MISSING_GIVE_ELEMENT, commandAlias, "");
            case 1:
                AdvObject object = matchableFromToken(tokens.get(0), objects);
                Person person = matchableFromToken(tokens.get(0), people);
                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                if (object != null) {
                    throw new ParsingException("Can't give object from a room", ParsingException.Kind.CANT_GIVE, tokens.get(0), "");
                } else if (person != null) {
                    throw new ParsingException(
                            "Missing element", 
                            ParsingException.Kind.MISSING_GIVE_ELEMENT, 
                            input.substring(0, input.indexOf(tokens.get(0) + tokens.size())),
                            "");
                } else if (inventoryObject != null) {
                    return new ParserOutput(command, Player.getInstance(), inventoryObject);
                } else {
                    throw new ParsingException("Unknown element", ParsingException.Kind.UNKNOWN_ELEMENT);
                }
            case 2:
                int objectIndex;
                person = matchableFromToken(tokens.get(0), people);

                if (person == null) {
                    person = matchableFromToken(tokens.get(1), people);

                    if (person == null) {
                        throw new ParsingException("Unknown element", ParsingException.Kind.UNKNOWN_ELEMENT);
                    } else {
                        objectIndex = 0;
                    }
                } else {
                    objectIndex = 1;
                }

                inventoryObject = matchableFromToken(tokens.get(objectIndex), inventory);

                if (inventoryObject == null) {
                    throw new ParsingException("Can't give object from a room", ParsingException.Kind.CANT_GIVE, tokens.get(objectIndex), "");
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
            List<AdvObject> inventory) throws ParsingException {
        
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
                    throw new ParsingException("Unknown element", ParsingException.Kind.UNKNOWN_ELEMENT);
                }
            default:
                throw getLongInputException(input.substring(0, input.indexOf(tokens.get(0) + tokens.size())));
        }
    }

    private <T extends Matchable> T matchableFromToken(String token, List<T> items) {
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
