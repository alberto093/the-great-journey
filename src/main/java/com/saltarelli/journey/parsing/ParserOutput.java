/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.parsing;

import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.InteractiveElement;
import com.saltarelli.journey.type.Room;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alberto
 */
public class ParserOutput {
    private final Command command;
    private final Room nextRoom;
    private final InteractiveElement person;
    private final List<InteractiveElement> objects;

    public ParserOutput(Command command) {
        this.command = command;
        this.nextRoom = null;
        this.person = null;
        this.objects = Collections.emptyList();
    }
    
    public ParserOutput(Command command, InteractiveElement... objects) {
        this.command = command;
        this.nextRoom = null;
        this.person = null;
        this.objects = Arrays.asList(objects);
    }
    
    public ParserOutput(Command command, Room nextRoom) {
        this.command = command;
        this.nextRoom = nextRoom;
        this.person = null;
        this.objects = Collections.emptyList();
    }
    
    // Speak to Mario
    public ParserOutput(Command command, InteractiveElement person) {
        this.command = command;
        this.nextRoom = null;
        this.person = person;
        this.objects = Collections.emptyList();
    }
    
    // Give Mario the lighter
    public ParserOutput(Command command, InteractiveElement person, InteractiveElement... objects) {
        this.command = command;
        this.nextRoom = null;
        this.person = person;
        this.objects = Arrays.asList(objects);
    }

    public Command getCommand() {
        return command;
    }
}
