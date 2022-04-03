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

    private final GameDescription game;

    private final Parser parser;

    private final Scanner scanner = new Scanner(System.in);

    private final PrintStream console;

    public Engine(GameDescription game) {
        this.game = game;
        game.init();
        this.parser = new Parser(ResourcesReader.fetchStopwords());
        this.console = System.out;
    }

    public void start() {
        console.println(game.getTitle());
        System.out.println();
        console.println(game.getDescription());
        System.out.println();
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
        }

        startGame();
    }

    public void restartGame() {

    }

    public void endGame() {

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
/*
                if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                    System.out.println("Addio!");
                    break;
                } else {
                    game.nextMove(p, System.out);
                    System.out.println();
                }
*/
            } catch (ParserException ex) {
                handleParserException(ex);
            }
        }
    }

    private void handleParserException(ParserException ex) {

    }
}
