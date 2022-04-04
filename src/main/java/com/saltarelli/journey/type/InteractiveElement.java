/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.CustomCommandMessage;
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

    @Override
    public Boolean match(String token) {
        return name.equals(token.toLowerCase()) || alias.contains(token.toLowerCase());
    }

    @Override
    public String customMessageForCommand(Command.Name command) {
        if (customCommandMessages != null && !customCommandMessages.isEmpty()) {
            return customCommandMessages.stream()
                    .filter(c -> c.getCommand() == command)
                    .findFirst()
                    .map(c -> c.getDescription())
                    .orElse(null);
        } else {
            return null;
        }
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

    public Set<CustomCommandMessage> getCustomCommandMessages() {
        return customCommandMessages;
    }

    public Boolean getCanOpen() {
        return canOpen != null ? canOpen : false;
    }

    public Boolean getCanClose() {
        return canClose != null ? canClose : false;
    }

    public Boolean getIsOpen() {
        return isOpen != null ? isOpen : false;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getCanTake() {
        return canTake != null ? canTake : false;
    }

    public Boolean getCanPush() {
        return canPush != null ? canPush : false;
    }

    public Boolean getCanPull() {
        return canPull != null ? canPull : false;
    }

    public Boolean getIsPush() {
        return isPush != null ? isPush : false;
    }

    public void setIsPush(Boolean isPush) {
        this.isPush = isPush;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCanOpen(Boolean canOpen) {
        this.canOpen = canOpen;
    }

    public void setCanClose(Boolean canClose) {
        this.canClose = canClose;
    }

    public void setCanTake(Boolean canTake) {
        this.canTake = canTake;
    }

    public void setCanPush(Boolean canPush) {
        this.canPush = canPush;
    }

    public void setCanPull(Boolean canPull) {
        this.canPull = canPull;
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
