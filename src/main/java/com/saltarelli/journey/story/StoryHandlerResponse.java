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
public abstract class StoryHandlerResponse {
    
    public Integer score;
    
    public Boolean isLast;

    public Integer getScore() {
        return score != null ? score : 0;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public abstract StoryHandlerResponseType getType();
    
    public static final StoryHandlerResponse newMessage(String message, Integer score, Boolean isLast) {
        return new StoryHandlerMessage(message, score, isLast);
    }

    public static final StoryHandlerResponse newQuestion(String question, String yesAnswer, String noAnswer) {
        return new StoryHandlerQuestion(question, yesAnswer, noAnswer);
    }
}
