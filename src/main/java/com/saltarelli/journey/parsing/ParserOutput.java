/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.parsing;

import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Room;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alberto
 */
public class ParserOutput {
    private final Command.Name command;
    private final Room room;
    private final Person person;
    private final List<AdvObject> objects;

    public ParserOutput(Command.Name command) {
        this.command = command;
        this.room = null;
        this.person = null;
        this.objects = Collections.emptyList();
    }
    
    public ParserOutput(Command.Name command, AdvObject... objects) {
        this.command = command;
        this.room = null;
        this.person = null;
        this.objects = Arrays.asList(objects);
    }
    
    public ParserOutput(Command.Name command, Room room) {
        this.command = command;
        this.room = room;
        this.person = null;
        this.objects = Collections.emptyList();
    }
    
    public ParserOutput(Command.Name command, Person person) {
        this.command = command;
        this.room = null;
        this.person = person;
        this.objects = Collections.emptyList();
    }
    
    public ParserOutput(Command.Name command, Person person, AdvObject... objects) {
        this.command = command;
        this.room = null;
        this.person = person;
        this.objects = Arrays.asList(objects);
    }

    public Command.Name getCommand() {
        return command;
    }

    public Room getRoom() {
        return room;
    }

    public Person getPerson() {
        return person;
    }

    public List<AdvObject> getObjects() {
        return objects;
    }
}
