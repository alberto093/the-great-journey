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
    private final String message;
    
    protected GameplayHandlerMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public GameplayHandlerResponseType getType() {
        return GameplayHandlerResponseType.MESSAGE;
    }
}
