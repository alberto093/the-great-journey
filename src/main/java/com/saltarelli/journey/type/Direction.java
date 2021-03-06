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
public class Direction implements Matchable {

    public enum Kind {
       NORTH, EAST, SOUTH, WEST
    }
    
    private final Kind kind;
    
    private final Set<String> alias;
    
    public Direction(Kind kind, Set<String> alias) {
        this.kind = kind;
        this.alias = alias;
    }

    public Kind getKind() {
        return kind;
    }

    public Set<String> getAlias() {
        return alias;
    }

    @Override
    public Boolean match(String token) {
        return alias.contains(token.toLowerCase());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.kind);
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
        final Direction other = (Direction) obj;
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }
    
    
}
