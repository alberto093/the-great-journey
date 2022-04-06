/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import java.util.Set;

/**
 *
 * @author Alberto
 */
public class PersonJSON {

    private int id;
    private int room;
    private String name;
    private String description;
    private Set<String> alias;
    private Set<CustomCommandMessage> customCommandMessages;

    public int getId() {
        return id;
    }

    public int getRoom() {
        return room;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public Set<CustomCommandMessage> getCustomCommandMessages() {
        return customCommandMessages;
    }
}
