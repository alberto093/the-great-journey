/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.files.AdvObjectJSON;

/**
 *
 * @author Alberto
 */
public class AdvObject extends InteractiveElement {

    public AdvObject(AdvObjectJSON json) {
        this.id = json.getId();
        this.name = json.getName();
        this.description = json.getDescription();
        this.inventoryDescription = json.getInventoryDescription();
        this.alias = json.getAlias();
        this.customCommandMessages = json.getCustomCommandMessages();
    }
}
