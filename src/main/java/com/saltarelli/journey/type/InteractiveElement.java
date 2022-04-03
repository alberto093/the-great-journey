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
public abstract class InteractiveElement implements Matchable, CustomCommandHandler {
    
    private final String name = "";
    
    private final String description = "";
    
    private final Boolean canOpen = false;
    
    private final Boolean canClose = false;
    
    private final Boolean isOpen = false;
    
    private final Boolean canTake = false;
    
    private final Boolean canPush = false;
    
    private final Boolean canPull = false;
    
    private final Boolean isPush = false;
    
    private final Boolean canRead = false;
    
    @Override
    public abstract Boolean match(String token);

    @Override
    public abstract String customMessageForCommand(Command.Name command);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
}
