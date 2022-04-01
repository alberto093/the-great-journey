/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class Command implements Matchable {
    
    public enum Kind {
    END, INVENTORY, NORD, SOUTH, EAST, WEST, OPEN, CLOSE, PUSH, PULL, 
    WALK_TO, PICK_UP, TALK_TO, GIVE, USE, LOOK_AT, TURN_ON, TURN_OFF
    }
    
    public enum Mode {
        DIRECTION, PERSON, PERSON_INVENTORY, SINGLE_OBJECT, MULTI_OBJECT, ENVIRONMENT
    }

    private final Kind kind;
    
    private final Mode mode;

    private Set<String> alias;

    public Command(Kind kind, Mode mode, String name, Set<String> alias) {
        this.kind = kind;
        this.mode = mode;
        this.alias = alias;
    }

    public Kind getType() {
        return kind;
    }
    
    public Mode getMode() {
        return mode;
    }
    
    @Override
    public Boolean match(String token) {
        return this.alias.contains(token);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.kind);
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
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

}