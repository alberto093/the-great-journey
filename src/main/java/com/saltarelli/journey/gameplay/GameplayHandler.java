/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.gameplay;

import com.saltarelli.journey.Game;
import com.saltarelli.journey.json.Gameplay;
import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.Command;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        }
        
        GameplayHandlerResponse response = handleGameplayResponse(output);

        if (response != null) {
            switch (response.getType()) {
                case MESSAGE:
                    message = message + "\n\n" + ((GameplayHandlerMessage) response).getMessage();
                    return GameplayHandlerResponse.newMessage(message);
                case QUESTION:
                    message = message + "\n\n" + ((GameplayHandlerQuestion) response).getQuestion();
                    return GameplayHandlerResponse.newQuestion(
                            message, 
                            ((GameplayHandlerQuestion) response).getYesAnswer(), 
                            ((GameplayHandlerQuestion) response).getNoAnswer());
            }
        } else if (message != null && !message.isEmpty()) {
            return GameplayHandlerResponse.newMessage(message);
        }
        
        return GameplayHandlerResponse.newMessage(DEFAULT_MESSAGE);
    }
    
    public GameplayHandlerResponse processQuestionAnswer(Boolean yesAnswer, ParserOutput output) {
        Gameplay gameplay = fetchGameplayFrom(output);
        
        if (gameplay != null) {
            return handleGameplay(gameplay, true, yesAnswer);
        } else {
            return null;
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
                            && gp.getInput().getRoomID() == game.getCurrentRoom().getId()
                            && gp.getInput().getPerson() == output.getPerson().getId()
                            && game.getInventory().stream()
                                    .map(o -> o.getId())
                                    .collect(Collectors.toSet())
                                    .containsAll(gp.getInput().getInventaryRequirements());

                })
                .findFirst()
                .orElse(null);
    }
    
    private GameplayHandlerResponse handleGameplay(Gameplay gameplay, Boolean checkAnswer, Boolean yesAnswer) {
        return null;
    }
}
