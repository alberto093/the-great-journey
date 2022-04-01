/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alberto
 */
public class Direction implements Matchable {

    public enum Kind {
       NORTH, EAST, SOUTH, OVEST
    }
    
    private final List<String> alias;
    
    Direction() {
        this.alias = Collections.emptyList();
    }
    
    Direction(String... alias) {
        this.alias = Arrays.asList(alias); 
    }

    @Override
    public Boolean match(String token) {
        return alias.contains(token.toLowerCase());
    }
}
