/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import com.saltarelli.journey.type.Command;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class AdvObjectJSON {
    private int id;
    private int room;
    private String name;
    private Set<String> alias;
    private String description;
    private Boolean canOpen;
    private Boolean canClose;
    private Boolean isOpen;
    private Boolean canTake;
    private Boolean canPush;
    private Boolean canPull;
    private Boolean isPush;
    
    private Set<CustomCommandMessage> customCommandMessages;

    public int getId() {
        return id;
    }

    public int getRoom() {
        return room;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
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
    
    public Set<CustomCommandMessage> getCustomCommandMessages() {
        return customCommandMessages;
    }
}
