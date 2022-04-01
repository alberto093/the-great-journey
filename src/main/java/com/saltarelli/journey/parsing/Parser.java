/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.saltarelli.journey.type.Matchable;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.parsing.ParsingException;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Player;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

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
            List<Person> persons,
            List<AdvObject> objects,
            List<AdvObject> inventory) throws ParsingException {

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        tokens = removeStopwordsFromTokens(tokens);

        if (tokens.isEmpty()) {
            throw new ParsingException(ParsingException.Kind.EMPTY_INPUT, "Invalid input");
        } else {
            Command command = matchableFromToken(tokens.remove(0), commands);
            if (command == null) {
                throw new ParsingException(ParsingException.Kind.UNKNOWN_COMMAND, "Missing command for " + tokens.get(0));
            } else {
                switch (command.getMode()) {
                    case DIRECTION:
                        switch (tokens.size()) {
                            case 0:
                                throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input, missing direction");
                            case 1:
                                Direction direction = matchableFromToken(tokens.get(0), directions);

                                if (direction == null) {
                                    throw new ParsingException(ParsingException.Kind.DIRECTION_UNAVAILABLE, "Unavailable direction: " + tokens.get(0));
                                } else {
                                    return new ParserOutput(command, direction);
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                    case PERSON:
                        switch (tokens.size()) {
                            case 0:
                                return new ParserOutput(command, Player.getInstance());
                            case 1:
                                Person person = matchableFromToken(tokens.get(0), persons);

                                if (person == null) {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input, missing person");
                                } else {
                                    return new ParserOutput(command, person);
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                    case PERSON_INVENTORY:
                        switch (tokens.size()) {
                            case 0:
                                throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input");
                            case 1:
                            case 2:
                                Person person = matchableFromToken(tokens.get(0), persons);
                                AdvObject object = matchableFromToken(tokens.get(1), inventory);

                                if (person != null && object != null) {
                                    return new ParserOutput(command, person, object);
                                } else if (person == null && object == null) {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input");
                                } else if (person == null) {
                                    if (persons.size() == 1) {
                                        person = persons.get(0);
                                    } else {
                                        person = Player.getInstance();
                                    }
                                    return new ParserOutput(command, person, object);
                                } else {
                                    if (inventory.size() == 1) {
                                        return new ParserOutput(command, person, inventory.get(0));
                                    } else {
                                        throw new ParsingException(ParsingException.Kind.MISSING_INVENTORY, "Invalid inventory object");
                                    }
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                    case SINGLE_OBJECT:
                        switch (tokens.size()) {
                            case 0:
                                if (objects.size() == 1) {
                                    return new ParserOutput(command, objects.get(0));
                                } else {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input");
                                }
                            case 1:
                                AdvObject object = matchableFromToken(tokens.get(0), objects);

                                if (object == null) {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid object");
                                } else {
                                    return new ParserOutput(command, object);
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                    case MULTI_OBJECT:
                        switch (tokens.size()) {
                            case 0:
                                if (objects.size() == 1) {
                                    return new ParserOutput(command, objects.get(0));
                                } else {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input");
                                }
                            case 1:
                            case 2:
                                AdvObject firstObjectMatch = matchableFromToken(tokens.get(0), objects);
                                AdvObject firstInventoryMatch = matchableFromToken(tokens.get(0), inventory);
                                AdvObject secondObjectMatch = matchableFromToken(tokens.get(1), objects);
                                AdvObject secondInventoryMatch = matchableFromToken(tokens.get(1), inventory);

                                AdvObject firstObject = Optional.ofNullable(firstObjectMatch).orElse(firstInventoryMatch);
                                AdvObject secondObject = Optional.ofNullable(secondObjectMatch).orElse(secondInventoryMatch);

                                if (firstObject != null && secondObject != null) {
                                    return new ParserOutput(command, firstObject, secondObject);
                                } else if (firstObject != null && tokens.size() == 1) {
                                    return new ParserOutput(command, firstObject);
                                } else {
                                    throw new ParsingException(ParsingException.Kind.MISSING_PARAMETER, "Invalid input");
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                    case ENVIRONMENT:
                        switch (tokens.size()) {
                            case 0:
                                return new ParserOutput(command);
                            case 1:
                                Person person = matchableFromToken(tokens.get(0), persons);
                                AdvObject object = matchableFromToken(tokens.get(0), objects);
                                AdvObject inventoryObject = matchableFromToken(tokens.get(0), inventory);

                                if (person != null) {
                                    return new ParserOutput(command, person);
                                } else if (object != null || inventoryObject != null) {
                                    return new ParserOutput(command, Optional.ofNullable(object).orElse(inventoryObject));
                                }
                            default:
                                String additionalDescription = tokens.subList(1, tokens.size() - 1).toString();
                                throw new ParsingException(ParsingException.Kind.LONG_INPUT, additionalDescription, "Long input, invalid tokens: " + additionalDescription);
                        }
                }
            }
        }
        throw new ParsingException(ParsingException.Kind.INVALID_INPUT, "Invalid input");
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
