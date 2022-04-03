/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.files.CustomCommandMessage;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public abstract class InteractiveElement implements Matchable, CustomCommandHandler {
    
    protected int id = 0;
    
    protected String name = "";
    
    protected String description = "";
    
    protected Set<String> alias = new HashSet<>();
    
    protected Set<CustomCommandMessage> customCommandMessages;
    
    protected Boolean canOpen = false;
    
    protected Boolean canClose = false;
    
    protected Boolean isOpen = false;
    
    protected Boolean canTake = false;
    
    protected Boolean canPush = false;
    
    protected Boolean canPull = false;
    
    protected Boolean isPush = false;
    
    protected Boolean canRead = false;
    
    @Override
    public Boolean match(String token) {
        return name == token.toLowerCase() || alias.contains(token.toLowerCase());
    }

    @Override
    public String customMessageForCommand(Command.Name command) {
        return customCommandMessages.stream()
                .filter(c -> c.getCommand() == command)
                .findFirst()
                .map(c -> c.getDescription())
                .orElse("");
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public Set<String> getAlias() {
        return alias;
    }

    public Boolean getCanOpen() {
        return canOpen;
    }
    
    public Boolean getCanClose() {
        return canClose;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public Boolean getCanTake() {
        return canTake;
    }    

    public Boolean getCanPush() {
        return canPush;
    }

    public Boolean getCanPull() {
        return canPull;
    }

    public Boolean getIsPush() {
        return isPush;
    }
    
    public Boolean getCanRead() {
        return canRead;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
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
        final InteractiveElement other = (InteractiveElement) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
