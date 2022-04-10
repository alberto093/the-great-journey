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
    private Room room;
    private final Person person;
    private final List<AdvObject> objects;
    private final String inputMessage;

    public ParserOutput(Command.Name command) {
        this.command = command;
        this.room = null;
        this.person = null;
        this.objects = Collections.emptyList();
        this.inputMessage = null;
    }

    public ParserOutput(Command.Name command, String inputMessage, AdvObject... objects) {
        this.command = command;
        this.room = null;
        this.person = null;
        this.objects = Arrays.asList(objects);
        this.inputMessage = inputMessage;
    }

    public ParserOutput(Command.Name command, Room room) {
        this.command = command;
        this.room = room;
        this.person = null;
        this.objects = Collections.emptyList();
        this.inputMessage = null;
    }

    public ParserOutput(Command.Name command, Person person, String inputMessage) {
        this.command = command;
        this.room = null;
        this.person = person;
        this.objects = Collections.emptyList();
        this.inputMessage = inputMessage;
    }

    public ParserOutput(Command.Name command, String inputMessage, Person person, AdvObject... objects) {
        this.command = command;
        this.room = null;
        this.person = person;
        this.objects = Arrays.asList(objects);
        this.inputMessage = inputMessage;
    }

    public Command.Name getCommand() {
        return command;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Person getPerson() {
        return person;
    }

    public List<AdvObject> getObjects() {
        return objects;
    }

    public String getInputMessage() {
        return inputMessage;
    }
}
