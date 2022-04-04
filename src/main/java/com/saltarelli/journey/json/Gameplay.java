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
            private Integer moveToRoomID; // -1, sparisce dal gioco
            private Boolean moveToInventory; // true, si rimuove dalla stanza e si aggiunge all'inventario
            private String description; // se non vuota aggiorna la descrizione dell'oggetto
            private Boolean canOpen;
            private Boolean canClose;
            private Boolean isOpen;
            private Boolean canTake;
            private Boolean canPush;
            private Boolean canPull;
            private Boolean isPush;
            private Set<CustomCommandMessage> customCommandMessages;

            public int getId() {
                return id;
            }

            public Integer getMoveToRoomID() {
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

            public Set<CustomCommandMessage> getCustomCommandMessages() {
                return customCommandMessages;
            }
        }

        public class EditingPerson {

            private int id;
            private Integer moveToRoomID; // -1, sparisce dal gioco
            private String description; // se non vuota aggiorna la descrizione della persona
            private Set<CustomCommandMessage> customCommandMessages;

            public int getId() {
                return id;
            }

            public Integer getMoveToRoomID() {
                return moveToRoomID;
            }

            public String getDescription() {
                return description;
            }

            public Set<CustomCommandMessage> getCustomCommandMessages() {
                return customCommandMessages;
            }
        }
        
        public class EditingRoom {
            private int id;
            private String description;

            public int getId() {
                return id;
            }

            public String getDescription() {
                return description;
            }
        }

        private EditingObject object;
        private EditingPerson person;
        private EditingRoom room;

        public EditingObject getObject() {
            return object;
        }

        public EditingPerson getPerson() {
            return person;
        }
        
        public EditingRoom getRoom() {
            return room;
        }
    }

    public static class Input {

        private Command.Name command;
        private Integer room;
        private Set<Integer> inventaryRequirements;
        private Integer person;
        private Set<Integer> objects;

        public Command.Name getCommand() {
            return command;
        }

        public Integer getRoom() {
            return room;
        }

        public Set<Integer> getInventaryRequirements() {
            return inventaryRequirements;
        }

        public Integer getPerson() {
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
    private Integer score;
    private Boolean isLast;

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public Integer getScore() {
        return score;
    }

    public Boolean getIsLast() {
        return isLast;
    }
}
