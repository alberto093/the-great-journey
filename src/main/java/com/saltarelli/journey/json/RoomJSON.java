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
public class RoomJSON {
    private int id;
    private String name;
    private String description;
    private Integer north;
    private Integer south;
    private Integer east;
    private Integer west;
    private String wrongDirectionMessage;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNorth() {
        return north;
    }

    public Integer getSouth() {
        return south;
    }

    public Integer getEast() {
        return east;
    }

    public Integer getWest() {
        return west;
    }

    public String getWrongDirectionMessage() {
        return wrongDirectionMessage;
    }
}
