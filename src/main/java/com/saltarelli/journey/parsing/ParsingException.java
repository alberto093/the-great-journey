/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.parsing;

/**
 *
 * @author Alberto
 */
public class ParsingException extends Exception {

    public enum Kind {
        EMPTY_INPUT, 
        UNKNOWN_COMMAND, 
        MISSING_PARAMETER, 
        LONG_INPUT, 
        DIRECTION_UNAVAILABLE,
        MISSING_INVENTORY,
        INVALID_INPUT
    }
    
    private final Kind kind;
    private final String additionalDescription;

    public ParsingException(Kind kind, String message) {
        super(message);
        this.kind = kind;
        this.additionalDescription = "";
    }
    
    public ParsingException(Kind kind, String additionalDescription, String message) {
        super(message);
        this.kind = kind;
        this.additionalDescription = additionalDescription;
    }

    public Kind getKind() {
        return kind;
    }
    
    public String getAdditionalDescription() {
        return additionalDescription;
    }
}
