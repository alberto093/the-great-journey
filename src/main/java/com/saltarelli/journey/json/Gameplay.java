/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import com.saltarelli.journey.type.Command;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class Gameplay {

    public class Editing {

        public class EditingObject {

            private int id;
            private int moveToRoomID; // -1, sparisce dal gioco
            private Boolean moveToInventory; // true, si rimuove dalla stanza e si aggiunge all'inventario
            private String description; // se non vuota aggiorna la descrizione dell'oggetto
            private Boolean canOpen;
            private Boolean canClose;
            private Boolean isOpen;
            private Boolean canTake;
            private Boolean canPush;
            private Boolean canPull;
            private Boolean isPush;
            private Boolean canRead;
            private Set<CustomCommandMessage> customCommandMessages;

            public int getId() {
                return id;
            }

            public int getMoveToRoomID() {
                return moveToRoomID;
            }

            public Boolean getMoveToInventory() {
                return moveToInventory;
            }

            public String getDescription() {
                return description;
            }

            public Boolean getCanOpen() {
                return canOpen;
            }

            public Boolean getCanClose() {
                return canClose;
            }

            public Boolean getIsOpen() {
                return isOpen;
            }

            public Boolean getCanTake() {
                return canTake;
            }

            public Boolean getCanPush() {
                return canPush;
            }

            public Boolean getCanPull() {
                return canPull;
            }

            public Boolean getIsPush() {
                return isPush;
            }

            public Boolean getCanRead() {
                return canRead;
            }

            public Set<CustomCommandMessage> getCustomCommandMessages() {
                return customCommandMessages;
            }
        }

        public class EditingPerson {

            private int id;
            private int roomID; // -1, sparisce dal gioco
            private String newDescription; // se non vuota aggiorna la descrizione della persona
            private Set<CustomCommandMessage> customCommandMessages;

            public int getId() {
                return id;
            }

            public int getRoomID() {
                return roomID;
            }

            public String getNewDescription() {
                return newDescription;
            }
            
            public Set<CustomCommandMessage> getCustomCommandMessages() {
                return customCommandMessages;
            }
        }

        private EditingObject object;
        private EditingPerson person;

        public EditingObject getObject() {
            return object;
        }

        public EditingPerson getPerson() {
            return person;
        }
    }

    public static class Input {

        private Command.Name command;
        private int roomID;
        private Set<Integer> inventaryRequirements;
        private int person;
        private Set<Integer> objects;

        public Input(Command.Name command, int roomID, int person, Set<Integer> objects) {
            this.command = command;
            this.roomID = roomID;
            this.inventaryRequirements = Collections.emptySet();
            this.person = person;
            this.objects = objects;
        }

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

    public class Output {

        public class Question {

            public class Answer {

                private String message;
                private Editing editing;

                public String getMessage() {
                    return message;
                }

                public Editing getEditing() {
                    return editing;
                }

            }

            private String message;
            private Answer yesAnswer;
            private Answer noAnswer;

            public String getMessage() {
                return message;
            }

            public Answer getYesAnswer() {
                return yesAnswer;
            }

            public Answer getNoAnswer() {
                return noAnswer;
            }
        }

        private Question question;
        private String message;
        private Editing editing;

        public Question getQuestion() {
            return question;
        }

        public String getMessage() {
            return message;
        }

        public Editing getEditing() {
            return editing;
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
