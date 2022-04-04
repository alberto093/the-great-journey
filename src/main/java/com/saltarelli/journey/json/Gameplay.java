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
                return moveToInventory != null ? moveToInventory : false;
            }

            public String getDescription() {
                return description;
            }

            public Boolean getCanOpen() {
                return canOpen != null ? canOpen : false;
            }

            public Boolean getCanClose() {
                return canClose != null ? canClose : false;
            }

            public Boolean getIsOpen() {
                return isOpen != null ? isOpen : false;
            }

            public Boolean getCanTake() {
                return canTake != null ? canTake : false;
            }

            public Boolean getCanPush() {
                return canPush != null ? canPush : false;
            }

            public Boolean getCanPull() {
                return canPull != null ? canPull : false;
            }

            public Boolean getIsPush() {
                return isPush != null ? isPush : false;
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

        private Set<EditingObject> objects;
        private Set<EditingPerson> people;
        private Set<EditingRoom> rooms;

        public Set<EditingObject> getObjects() {
            return objects;
        }

        public Set<EditingPerson> getPeople() {
            return people;
        }
        
        public Set<EditingRoom> getRooms() {
            return rooms;
        }
    }

    public static class Input {

        private Command.Name command;
        private Integer room;
        private Set<Integer> inventoryRequirements;
        private Integer person;
        private Set<Integer> objects;

        public Command.Name getCommand() {
            return command;
        }

        public Integer getRoom() {
            return room;
        }

        public Set<Integer> getInventoryRequirements() {
            return inventoryRequirements;
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
                private Boolean score;
                private Boolean delete;
                private Editing editing;
                private Boolean isLast;

                public String getMessage() {
                    return message;
                }
                
                public Boolean getScore() {
                    return score != null ? score : false;
                }

                public Boolean getDelete() {
                    return delete != null ? delete : false;
                }

                public Editing getEditing() {
                    return editing;
                }
                
                public Boolean getIsLast() {
                    return isLast != null ? isLast : false;
                }
            }

            private Answer yesAnswer;
            private Answer noAnswer;

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
    private Boolean score;
    private Boolean isLast;
    private Boolean delete;

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public Boolean getScore() {
        return score;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public Boolean getDelete() {
        return delete;
    }
}
