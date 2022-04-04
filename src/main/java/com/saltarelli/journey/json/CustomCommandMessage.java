package com.saltarelli.journey.json;

import com.saltarelli.journey.type.Command;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.command);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomCommandMessage other = (CustomCommandMessage) obj;
        if (this.command != other.command) {
            return false;
        }
        return true;
    }

}
