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
        EMPTY_INPUT, // Sei in silenzio stampa?
        UNKNOWN_COMMAND, // Non conosco questo verbo.
        LONG_INPUT, // Ho capito la frase solo fino a: \(additionalDescription).
        CANT_OPEN, // Non puoi aprire \(additionalDescription).
        CANT_CLOSE, // Non puoi chiudere \(additionalDescription).
        CANT_PUSH, // Non puoi spingere \(additionalDescription).
        CANT_PULL, // Non puoi tirare \(additionalDescription).
        MISSING_OPEN_ELEMENT, // Cosa vuoi aprire? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_CLOSE_ELEMENT, // Cosa vuoi chiudere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_PUSH_ELEMENT, // Cosa vuoi spingere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_PULL_ELEMENT, // Cosa vuoi tirare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_TAKE_ELEMENT, // Cosa puoi prendere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        CANT_TAKE, // Non puoi prendere \(additionalDescription).
        TAKE_FROM_INVENTORY, // Già in possesso.
        MISSING_DIRECTION, // Dove vuoi andare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        INVALID_DIRECTION, // Non è qualcosa in cui puoi entrare.
        WRONG_DIRECTION, // Se la stanza è ha il wrongDirectionMessage mostro quello altrimenti dico: "A (additionalDescription) non c'è niente di interessante."
        UNKNOWN_ELEMENT, // Non vedi nulla del genere.
        
    }
    
    private final Kind kind;
    private final String additionalDescription;
    private final String customOutputMessage;

    public ParsingException(String message, Kind kind) {
        super(message);
        this.kind = kind;
        this.additionalDescription = null;
        this.customOutputMessage = null;
    }
    
    public ParsingException(String message, Kind kind, String additionalDescription, String customOutputMessage) {
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
}
