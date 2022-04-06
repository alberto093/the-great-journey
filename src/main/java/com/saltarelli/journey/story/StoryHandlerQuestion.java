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
public class StoryHandlerQuestion extends StoryHandlerResponse {
    private String question;
    private final String yesAnswer;
    private final String noAnswer;
    
    protected StoryHandlerQuestion(String question, String yesAnswer, String noAnswer) {
        this.question = question;
        this.yesAnswer = yesAnswer;
        this.noAnswer = noAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getYesAnswer() {
        return yesAnswer;
    }

    public String getNoAnswer() {
        return noAnswer;
    }
    
    @Override
    public StoryHandlerResponseType getType() {
        return StoryHandlerResponseType.QUESTION;
    }
}
