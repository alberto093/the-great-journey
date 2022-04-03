/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.files;

import java.util.Set;

/**
 *
 * @author Alberto
 */
public class RoomJSON {
    private int id;
    private String name;
    private String description;
    private Set<Integer> objects;
    private Integer north;
    private Integer south;
    private Integer east;
    private Integer west;
    private String wrongDirectionMessage;
}
