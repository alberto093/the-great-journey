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
   
    public abstract GameplayHandlerResponseType getType();
    
    public static final GameplayHandlerResponse newMessage(String message) {
        return new GameplayHandlerMessage(message);
    }

    public static final GameplayHandlerResponse newQuestion(String question, String yesAnswer, String noAnswer) {
        return new GameplayHandlerQuestion(question, yesAnswer, noAnswer);
    }
}
