/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.parsing;

import java.util.Objects;

/**
 *
 * @author Alberto
 */
public class ParserException extends Exception {

    public enum Kind {
        EMPTY_INPUT,
        UNKNOWN_COMMAND,
        LONG_INPUT,
        CANT_OPEN,
        CANT_CLOSE,
        CANT_PUSH,
        CANT_PULL,
        CANT_GIVE,
        CANT_SPEAK,
        CANT_USE_SELF,
        CANT_USE_PERSON_ON_PERSON,
        MISSING_OPEN_ELEMENT,
        MISSING_CLOSE_ELEMENT,
        MISSING_PUSH_ELEMENT,
        MISSING_PULL_ELEMENT,
        MISSING_TAKE_ELEMENT,
        MISSING_GIVE_ELEMENT,
        MISSING_USE_ELEMENT,
        MISSING_USE_WITH_ELEMENT,
        MISSING_READ_ELEMENT,
        MISSING_SPEAK_ELEMENT,
        MISSING_COMBINE_ELEMENT,
        MINIMUM_COMBINE,
        CANT_COMBINE,
        CANT_TAKE,
        TAKE_FROM_INVENTORY,
        MISSING_DIRECTION,
        INVALID_DIRECTION,
        WRONG_DIRECTION,
        UNKNOWN_ELEMENT,
        PREDEFINED_COMMAND
    }
    
    private final Kind kind;
    private final String additionalDescription;
    private final String customOutputMessage;

    public ParserException(String message, Kind kind) {
        super(message);
        this.kind = kind;
        this.additionalDescription = null;
        this.customOutputMessage = null;
    }
    
    public ParserException(String message, Kind kind, String additionalDescription, String customOutputMessage) {
        super(message);
        this.kind = kind;
        this.additionalDescription = additionalDescription;
        this.customOutputMessage = customOutputMessage;
    }

    public Kind getKind() {
        return kind;
    }
    
    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public String getCustomOutputMessage() {
        return customOutputMessage;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.kind);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParserException other = (ParserException) obj;
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }
}
