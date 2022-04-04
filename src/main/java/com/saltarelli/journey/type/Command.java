/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.CommandJSON;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class Command implements Matchable {

    public enum Name {
        END,
        RESTART,
        INVENTORY, 
        SCORE,
        NORTH, 
        SOUTH, 
        EAST, 
        WEST, 
        OPEN, 
        CLOSE, 
        PUSH, 
        PULL, 
        WALK_TO, 
        PICK_UP, 
        GIVE, 
        LOOK_AT,
        READ,
        SPEAK,
        COMBINE,
        SING
    }

    private final Name name;

    private Set<String> alias;
    
    public Command(CommandJSON json) {
        this.name = json.getName();
        this.alias = json.getAlias();
    }

    public Name getName() {
        return name;
    }

    @Override
    public Boolean match(String token) {
        return this.alias.contains(token);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
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
        final Command other = (Command) obj;
        if (this.name != other.name) {
            return false;
        }
        return true;
    }

}
