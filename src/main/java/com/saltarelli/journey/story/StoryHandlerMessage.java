/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.story;

/**
 *
 * @author Alberto
 */
public class StoryHandlerMessage extends StoryHandlerResponse {
    private String message;
    
    protected StoryHandlerMessage(String message, Integer score, Boolean isLast) {
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
    public StoryHandlerResponseType getType() {
        return StoryHandlerResponseType.MESSAGE;
    }
}
