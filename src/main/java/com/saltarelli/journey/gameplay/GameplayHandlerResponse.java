/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.gameplay;

/**
 *
 * @author Alberto
 */
public abstract class GameplayHandlerResponse {
    
    public int score;
    
    public Boolean isLast;

    public int getScore() {
        return score;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public abstract GameplayHandlerResponseType getType();
    
    public static final GameplayHandlerResponse newMessage(String message, int score, Boolean isLast) {
        return new GameplayHandlerMessage(message, score, isLast);
    }

    public static final GameplayHandlerResponse newQuestion(String question, String yesAnswer, String noAnswer) {
        return new GameplayHandlerQuestion(question, yesAnswer, noAnswer);
    }
}
