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
public class GameplayHandlerMessage extends GameplayHandlerResponse {
    private String message;
    
    protected GameplayHandlerMessage(String message, int score, Boolean isLast) {
        this.message = message;
        this.score = score;
        this.isLast = isLast;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
 
    @Override
    public GameplayHandlerResponseType getType() {
        return GameplayHandlerResponseType.MESSAGE;
    }
}
