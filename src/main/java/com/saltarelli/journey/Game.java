/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.files.GameJSON;
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
public class Game {
    
    private String introduction = "";
    
    private String title = "";
    
    private String description = "";
    
    private String helpQuestion = "";
    
    private String restartQuestion = "";
    
    private String endQuestion = "";
    
    private String inventoryEmpty = "";
    
    private String inventoryFull = "";
    
    private String help = "";
    
    private Set<String> yesAlias = new HashSet<>();
    
    private Set<String> noAlias = new HashSet<>();
    
    private Set<Direction> directions = new HashSet<>();

    private Set<Room> rooms = new HashSet<>();

    private Set<Command> commands = new HashSet<>();

    private Set<AdvObject> inventory = new HashSet<>();

    private Room currentRoom;
    
    private int maxScore;
    
    public Game(GameJSON json) {
        this.introduction = json.getIntroduction();
        this.title = json.getTitle();
        this.description = json.getDescription();
        this.helpQuestion = json.getHelpQuestion();
        this.restartQuestion = json.getRestartQuestion();
        this.endQuestion = json.getEndQuestion();
        this.inventoryEmpty = json.getInventoryEmpty();
        this.inventoryFull = json.getInventoryFull();
        this.help = json.getHelp();
        this.yesAlias = json.getYesAlias();
        this.noAlias = json.getNoAlias();
        this.maxScore = json.getMaxScore();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHelpQuestion() {
        return helpQuestion;
    }

    public String getRestartQuestion() {
        return restartQuestion;
    }

    public void setRestartQuestion(String restartQuestion) {
        this.restartQuestion = restartQuestion;
    }

    public String getInventoryEmpty() {
        return inventoryEmpty;
    }

    public void setInventoryEmpty(String inventoryEmpty) {
        this.inventoryEmpty = inventoryEmpty;
    }

    public String getInventoryFull() {
        return inventoryFull;
    }

    public void setInventoryFull(String inventoryFull) {
        this.inventoryFull = inventoryFull;
    }
    
    

    public void setHelpQuestion(String helpQuestion) {
        this.helpQuestion = helpQuestion;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public Set<String> getYesAlias() {
        return yesAlias;
    }

    public void setYesAlias(Set<String> yesAlias) {
        this.yesAlias = yesAlias;
    }

    public Set<String> getNoAlias() {
        return noAlias;
    }

    public void setNoAlias(Set<String> noAlias) {
        this.noAlias = noAlias;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public void setDirections(Set<Direction> directions) {
        this.directions = directions;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Set<Command> getCommands() {
        return commands;
    }

    public void setCommands(Set<Command> commands) {
        this.commands = commands;
    }

    public Set<AdvObject> getInventory() {
        return inventory;
    }

    public void setInventory(Set<AdvObject> inventory) {
        this.inventory = inventory;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

}
