/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.type;

import com.saltarelli.journey.json.PersonJSON;
import java.util.HashSet;

/**
 *
 * @author Alberto
 */
public class Person extends InteractiveElement {

    public Person(PersonJSON json) {
        this.id = json.getId();
        this.name = json.getName();
        this.description = json.getDescription();
        this.alias = json.getAlias();
   
        if (json.getCustomCommandMessages() != null) {
            this.customCommandMessages = json.getCustomCommandMessages();
        } else {
            this.customCommandMessages = new HashSet<>();
        }
    }
}
