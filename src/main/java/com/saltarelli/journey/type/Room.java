/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.RoomJSON;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class Room {

    private int id;

    private String name;

    private String description;

    private Room south = null;

    private Room north = null;

    private Room east = null;

    private Room west = null;

    private String wrongDirectionMessage = "";

    private final Set<AdvObject> objects = new HashSet<>();

    private final Set<Person> people = new HashSet<>();

    public Room(int id) {
        this.id = id;
    }

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Room(RoomJSON json) {
        this.id = json.getId();
        this.name = json.getName();
        this.description = json.getDescription();
        this.wrongDirectionMessage = json.getWrongDirectionMessage();
    }

    public Room getRoomWithDirection(Direction direction) {
        switch (direction.getKind()) {
            case NORTH:
                return north;
            case EAST:
                return east;
            case SOUTH:
                return south;
            case WEST:
                return west;
            default:
                throw new AssertionError(direction.getKind().name());
        }
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getSouth() {
        return south;
    }

    public void setSouth(Room south) {
        this.south = south;
    }

    public Room getNorth() {
        return north;
    }

    public void setNorth(Room north) {
        this.north = north;
    }

    public Room getEast() {
        return east;
    }

    public void setEast(Room east) {
        this.east = east;
    }

    public Room getWest() {
        return west;
    }

    public void setWest(Room west) {
        this.west = west;
    }

    public Set<AdvObject> getObjects() {
        return objects;
    }

    public Set<Person> getPeople() {
        return people;
    }

    public String getWrongDirectionMessage() {
        return description;
    }

    public void setWrongDirectionMessage(String wrongDirectionMessage) {
        this.wrongDirectionMessage = wrongDirectionMessage;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.id;
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
        final Room other = (Room) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
