/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.files;

import com.saltarelli.journey.type.Command;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class AdvObjectJSON {
    private int id;
    private int room;
    private String name;
    private Set<String> alias;
    private String description;
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

    public Set<String> getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    public Set<CustomCommandMessage> getCustomCommandMessages() {
        return customCommandMessages;
    }
}
