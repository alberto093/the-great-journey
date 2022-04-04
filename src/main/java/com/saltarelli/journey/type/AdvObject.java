/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.AdvObjectJSON;
import java.util.HashSet;

/**
 *
 * @author Alberto
 */
public class AdvObject extends InteractiveElement {

    public AdvObject(AdvObjectJSON json) {
        this.id = json.getId();
        this.name = json.getName();
        this.description = json.getDescription();
        this.canOpen = json.getCanOpen() == null ? false : json.getCanOpen();
        this.canClose = json.getCanClose() == null ? false : json.getCanClose();
        this.isOpen = json.getIsOpen() == null ? false : json.getIsOpen();
        this.canTake = json.getCanTake() == null ? false : json.getCanTake();
        this.canPush = json.getCanPush() == null ? false : json.getCanPush();
        this.canPull = json.getCanPull() == null ? false : json.getCanPull();
        this.isPush = json.getIsPush() == null ? false : json.getIsPush();
        this.alias = json.getAlias();
        
        if (json.getCustomCommandMessages() != null) {
            this.customCommandMessages = json.getCustomCommandMessages();
        } else {
            this.customCommandMessages = new HashSet<>();
        }
    }
}
