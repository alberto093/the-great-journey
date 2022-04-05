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
        EMPTY_INPUT, // Sei in silenzio stampa?
        UNKNOWN_COMMAND, // Non conosco questo verbo.
        LONG_INPUT, // Ho capito la frase solo fino a: \(additionalDescription).
        CANT_OPEN, // Non puoi aprire \(additionalDescription).
        CANT_CLOSE, // Non puoi chiudere \(additionalDescription).
        CANT_PUSH, // Non puoi spingere \(additionalDescription).
        CANT_PULL, // Non puoi tirare \(additionalDescription).
        CANT_GIVE, // Non puoi dare \(additionalDescription) perchè non è nell'inventario.
        CANT_SPEAK, // Ehi! Puoi farlo solo con esseri viventi.
        MISSING_OPEN_ELEMENT, // Cosa vuoi aprire? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_CLOSE_ELEMENT, // Cosa vuoi chiudere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_PUSH_ELEMENT, // Cosa vuoi spingere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_PULL_ELEMENT, // Cosa vuoi tirare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_TAKE_ELEMENT, // Cosa puoi prendere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_GIVE_ELEMENT, // Cosa vuoi dare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_USE_ELEMENT, // Cosa vuoi usare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_READ_ELEMENT, // Cosa vuoi leggere? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_SPEAK_ELEMENT, // Con chi vuoi parlare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MISSING_COMBINE_ELEMENT, // Cosa vuoi unire? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        MINIMUM_COMBINE, // Non puoi combinare un oggetto con se stesso.
        CANT_COMBINE, // Puoi farlo solo con oggetti nel tuo inventario.
        CANT_TAKE, // Non puoi prendere \(additionalDescription).
        TAKE_FROM_INVENTORY, // Già in possesso.
        MISSING_DIRECTION, // Dove vuoi andare? --> al parser verrà inviata la stringa precedente (additionalDescription) + il nuovo input
        INVALID_DIRECTION, // Non è qualcosa in cui puoi entrare.
        WRONG_DIRECTION, // Se la stanza è ha il wrongDirectionMessage mostro quello altrimenti dico: "A (additionalDescription) non c'è niente di interessante."
        UNKNOWN_ELEMENT, // Non vedi nulla del genere.
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
