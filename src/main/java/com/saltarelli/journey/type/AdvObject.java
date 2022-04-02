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
public class AdvObject extends InteractiveElement {

    @Override
    public Boolean match(String token) {
        return true;
    }

    @Override
    public String customMessageForCommand(Command.Name command) {
        return "";
    }

}
