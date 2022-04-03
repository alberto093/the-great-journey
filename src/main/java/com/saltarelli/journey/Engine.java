/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.files.ResourcesReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import com.saltarelli.journey.parsing.Parser;
import com.saltarelli.journey.parsing.ParserException;
import com.saltarelli.journey.parsing.ParserOutput;
import java.io.PrintStream;
import java.util.Optional;

/**
 *
 * @author Alberto
 */
public class Engine {

    private final Game game;
    
    private final Gameplay gameplay;

    private final Parser parser;

    private final Scanner scanner = new Scanner(System.in);

    private final PrintStream console;

    public Engine(Game game) {
        this.game = game;
        this.gameplay = new Gameplay(game);
        this.parser = new Parser(ResourcesReader.fetchStopwords());
        this.console = System.out;
    }

    public void start() {
        console.println(game.getIntroduction());
        console.println();
        console.println(game.getHelpQuestion());

        Optional<Boolean> showHelp = Optional.empty();
        while (showHelp.isEmpty()) {
            String answer = scanner.nextLine();

            if (game.getYesAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(true);
            } else if (game.getNoAlias().contains(answer.toLowerCase())) {
                showHelp = Optional.of(false);
            }
        }

        if (showHelp.get()) {
            System.out.println();
            console.println(game.getHelp());
            System.out.println();
        }
        
        console.println(game.getTitle());
        System.out.println();
        console.println(game.getDescription());
        System.out.println();

        startGame();
    }

    private void restartGame() {

    }

    private void endGame() {

    }
    
    private void printInventory() {
    
    }

    private void startGame() {
        System.out.println(game.getCurrentRoom().getName());
        System.out.println();
        System.out.println(game.getCurrentRoom().getDescription());
        System.out.println();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            try {
                ParserOutput output = parser.parse(
                        command,
                        game.getCommands(),
                        game.getDirections(),
                        game.getInventory(),
                        game.getCurrentRoom());
                
                switch (output.getCommand().getName()) {
                    case END:
                        endGame();
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
        switch (ex.getKind()) {
            case EMPTY_INPUT:
                break;
            case UNKNOWN_COMMAND:
                break;
            case LONG_INPUT:
                break;
            case CANT_OPEN:
                break;
            case CANT_CLOSE:
                break;
            case CANT_PUSH:
                break;
            case CANT_PULL:
                break;
            case CANT_GIVE:
                break;
            case CANT_SPEAK:
                break;
            case MISSING_OPEN_ELEMENT:
                break;
            case MISSING_CLOSE_ELEMENT:
                break;
            case MISSING_PUSH_ELEMENT:
                break;
            case MISSING_PULL_ELEMENT:
                break;
            case MISSING_TAKE_ELEMENT:
                break;
            case MISSING_GIVE_ELEMENT:
                break;
            case MISSING_USE_ELEMENT:
                break;
            case MISSING_READ_ELEMENT:
                break;
            case MISSING_SPEAK_ELEMENT:
                break;
            case MISSING_COMBINE_ELEMENT:
                break;
            case MINIMUM_COMBINE:
                break;
            case CANT_COMBINE:
                break;
            case CANT_TAKE:
                break;
            case TAKE_FROM_INVENTORY:
                break;
            case MISSING_DIRECTION:
                break;
            case INVALID_DIRECTION:
                break;
            case WRONG_DIRECTION:
                break;
            case UNKNOWN_ELEMENT:
                break;
            default:
                throw new AssertionError(ex.getKind().name());
        
        }
        
    }
}
