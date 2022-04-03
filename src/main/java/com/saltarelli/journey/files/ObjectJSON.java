/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.files;

import com.saltarelli.journey.type.Command;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class ObjectJSON {
    
    private class CommandOutput {
        private Command.Name command;
        private String description;
    }
    
    private int id;
    private int room;
    private String name;
    private Set<String> alias;
    private String description;
    private Set<CommandOutput> commandOutput;
}
