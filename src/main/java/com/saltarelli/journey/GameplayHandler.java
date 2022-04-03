/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey;

import com.saltarelli.journey.files.Gameplay;
import com.saltarelli.journey.parsing.ParserOutput;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class GameplayHandler {
    
    private final Game game;
    private final Set<Gameplay> gameplay;
    
    public GameplayHandler(Game game, Set<Gameplay> gameplay) {
        this.game = game;
        this.gameplay = gameplay;
    }
    
    public String processOutput(ParserOutput output) {
        return "";
    }
}
