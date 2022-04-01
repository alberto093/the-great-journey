/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

/**
 *
 * @author Alberto
 */
public class Player extends Person {
    private static final Player instance = new Player();
    
    private Player() {
    
    }
    
    public static Player getInstance() {
        return instance;
    }
    
    @Override
    public Boolean match(String token) {
        return true;
    }
}
