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
public class CommandJSON {
    private Command.Name name;
    private Set<String> alias;

    public Command.Name getName() {
        return name;
    }

    public Set<String> getAlias() {
        return alias;
    }
    
    
}
