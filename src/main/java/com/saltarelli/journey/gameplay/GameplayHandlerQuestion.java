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
public class GameplayHandlerQuestion extends GameplayHandlerResponse {
    private final String question;
    private final String yesAnswer;
    private final String noAnswer;
    
    protected GameplayHandlerQuestion(String question, String yesAnswer, String noAnswer) {
        this.question = question;
        this.yesAnswer = yesAnswer;
        this.noAnswer = noAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getYesAnswer() {
        return yesAnswer;
    }

    public String getNoAnswer() {
        return noAnswer;
    }
    
    @Override
    public GameplayHandlerResponseType getType() {
        return GameplayHandlerResponseType.QUESTION;
    }
}
