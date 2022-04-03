package com.saltarelli.journey.files;

import com.saltarelli.journey.type.Command;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alberto
 */
public class CustomCommandMessage {

    private Command.Name command;
    private String description;

    public Command.Name getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

}
