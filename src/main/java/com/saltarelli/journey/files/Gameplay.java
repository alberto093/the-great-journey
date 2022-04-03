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
public class Gameplay {

    private class Input {

        private Command.Name command;
        private int roomID;
        private Set<Integer> inventaryRequirements;
        private int person;
        private Set<Integer> objects;

        public Command.Name getCommand() {
            return command;
        }

        public int getRoomID() {
            return roomID;
        }

        public Set<Integer> getInventaryRequirements() {
            return inventaryRequirements;
        }

        public int getPerson() {
            return person;
        }

        public Set<Integer> getObjects() {
            return objects;
        }
        
        
    }

    private class Output {
        private class EditingObject {
            private int id;
            private int moveToRoomID; // -1, sparisce dal gioco
            private Boolean moveToInventory; // true, si rimuove dalla stanza e si aggiunge all'inventario
            private String newDescription; // se non vuota aggiorna la descrizione dell'oggetto

            public int getId() {
                return id;
            }

            public int getMoveToRoomID() {
                return moveToRoomID;
            }

            public Boolean getMoveToInventory() {
                return moveToInventory;
            }

            public String getNewDescription() {
                return newDescription;
            }
            
            
        }
        
        private class EditingPerson {
            private int id;
            private int roomID; // -1, sparisce dal gioco
            private String newDescription; // se non vuota aggiorna la descrizione della persona

            public int getId() {
                return id;
            }

            public int getRoomID() {
                return roomID;
            }

            public String getNewDescription() {
                return newDescription;
            }
        }
        
        private String message;
        private EditingObject editObject;
        private EditingPerson editPerson;

        public String getMessage() {
            return message;
        }

        public EditingObject getEditObject() {
            return editObject;
        }

        public EditingPerson getEditPerson() {
            return editPerson;
        }
    }
    
    private Input input;
    private Output output;
    private int score;
    private Boolean isLast;

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public int getScore() {
        return score;
    }

    public Boolean getIsLast() {
        return isLast;
    }
    
    
}
