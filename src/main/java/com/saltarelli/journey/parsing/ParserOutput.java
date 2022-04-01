/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.parsing;

import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.Person;

/**
 *
 * @author Alberto
 */
public class ParserOutput {
    private final Command command;
    private final Direction direction;
    private final Person person;
    private final AdvObject object;
    private final AdvObject secondaryObject;

    // Look
    public ParserOutput(Command command) {
        this.command = command;
        this.direction = null;
        this.person = null;
        this.object = null;
        this.secondaryObject = null;
    }
    
    // Take pillow
    public ParserOutput(Command command, AdvObject object) {
        this.command = command;
        this.direction = null;
        this.person = null;
        this.object = object;
        this.secondaryObject = null;
    }
    
    // Go to North
    public ParserOutput(Command command, Direction direction) {
        this.command = command;
        this.direction = direction;
        this.person = null;
        this.object = null;
        this.secondaryObject = null;
    }

    // Use lighter with cigarette
    public ParserOutput(Command command, AdvObject object, AdvObject secondaryObject) {
        this.command = command;
        this.direction = null;
        this.person = null;
        this.object = object;
        this.secondaryObject = secondaryObject;
    }
    
    // Speak to Mario
    public ParserOutput(Command command, Person person) {
        this.command = command;
        this.direction = null;
        this.person = person;
        this.object = null;
        this.secondaryObject = null;
    }
    
    // Give Mario the lighter
    public ParserOutput(Command command, Person person, AdvObject object) {
        this.command = command;
        this.direction = null;
        this.person = person;
        this.object = object;
        this.secondaryObject = null;
    }

    public Command getCommand() {
        return command;
    }
    
    public Direction getDirection() {
        return direction;
    }

    public AdvObject getObject() {
        return object;
    }

    public AdvObject getSecondaryObject() {
        return secondaryObject;
    }
}
