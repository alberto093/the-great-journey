/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.PersonJSON;

/**
 *
 * @author Alberto
 */

// This should be a singleton but requires a constructor method in order create from JSON.
public class Player extends Person {

    private static Player instance = null;

    public Player(PersonJSON json) {
        super(json);
    }

    public static Player getInstance() {
        return instance;
    }
    
    public static void setInstance(Player player) {
        instance = player;
    }
}

