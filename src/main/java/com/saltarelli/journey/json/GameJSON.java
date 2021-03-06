/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import java.util.Set;

/**
 *
 * @author Alberto
 */
public class GameJSON {

    private String introduction;
    private String title;
    private String description;
    private String helpQuestion;
    private String restartQuestion;
    private String endQuestion;
    private String uselessCombineCommand;
    private String endGameMessage;
    private String increaseScoreMessage;
    private String scoreMessage;
    private String inventoryEmpty;
    private String inventoryFull;
    private String help;
    private String unknownOutput;
    private Set<String> yesAlias;
    private Set<String> noAlias;
    private int maxScore;
    private int initialRoom;

    public String getIntroduction() {
        return introduction;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHelpQuestion() {
        return helpQuestion;
    }

    public String getRestartQuestion() {
        return restartQuestion;
    }
    
    public String getEndQuestion() {
        return endQuestion;
    }
    
    public String getUselessCombineCommand() {
        return uselessCombineCommand;
    }

    public String getEndGameMessage() {
        return endGameMessage;
    }
    
    public String getIncreaseScoreMessage() {
        return increaseScoreMessage;
    }

    public String getScoreMessage() {
        return scoreMessage;
    }

    public String getInventoryEmpty() {
        return inventoryEmpty;
    }

    public String getInventoryFull() {
        return inventoryFull;
    }

    public String getHelp() {
        return help;
    }
    
    public String getUnknownOutput() {
        return unknownOutput;
    }

    public Set<String> getYesAlias() {
        return yesAlias;
    }

    public Set<String> getNoAlias() {
        return noAlias;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getInitialRoom() {
        return initialRoom;
    }
}
