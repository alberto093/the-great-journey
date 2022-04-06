/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import com.saltarelli.journey.parsing.ParserException;

/**
 *
 * @author Alberto
 */
public class ExceptionDescription {
    private ParserException.Kind name;
    private String message;

    public ParserException.Kind getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
    
    
}
