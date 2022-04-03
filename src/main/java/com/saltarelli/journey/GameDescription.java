/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.Room;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public abstract class GameDescription {
    
    private final String title = "";
    
    private final String description = "";
    
    private final String helpQuestion = "";
    
    private final String help = "";
    
    private final Set<String> yesAlias = new HashSet<>();
    
    private final Set<String> noAlias = new HashSet<>();
    
    private final List<Direction> directions = new ArrayList<>();

    private final List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

    private final List<AdvObject> inventory = new ArrayList<>();

    private Room currentRoom;
    
    private GamePlay gameplay;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHelpQuestion() {
        return helpQuestion;
    }

    public String getHelp() {
        return help;
    }

    public Set<String> getYesAlias() {
        return yesAlias;
    }

    public Set<String> getNoAlias() {
        return noAlias;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<AdvObject> getInventory() {
        return inventory;
    }

    public GamePlay getGameplay() {
        return gameplay;
    }

    public abstract void init();

}
