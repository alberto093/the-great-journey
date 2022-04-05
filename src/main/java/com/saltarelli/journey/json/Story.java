/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import com.saltarelli.journey.parsing.ParserOutput;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Matchable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class Story {

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
                private Integer score;
                private Boolean delete;
                private Editing editing;
                private Boolean isLast;

                public String getMessage() {
                    return message;
                }

                public Integer getScore() {
                    return score != null ? score : 0;
                }

                public Boolean getDelete() {
                    return delete != null ? delete : false;
                }

                public Editing getEditing() {
                    return editing;
                }

                public Boolean getIsLast() {
                    return isLast;
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
    private Integer score;
    private Boolean isLast;
    private Boolean delete;

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public Integer getScore() {
        return score != null ? score : 0;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public Boolean getDelete() {
        return delete != null ? delete : false;
    }

    public boolean match(ParserOutput output, Set<AdvObject> inventory) {
        if (input.command != output.getCommand()) {
            return false;
        }
        
        boolean matchPerson;
        if (input.person == null && output.getPerson() == null) {
            matchPerson = true;
        } else if (input.person != null && output.getPerson() != null) { 
            matchPerson = input.person == output.getPerson().getId();
        } else { 
            matchPerson = false;
        }
        
        boolean matchObjects;
        if ((input.objects == null || input.objects.isEmpty()) && (output.getObjects() == null || output.getObjects().isEmpty())) {
            matchObjects = true;
        } else if ((input.objects != null && !input.objects.isEmpty()) && (output.getObjects() != null && !output.getObjects().isEmpty())) {
            matchObjects = input.objects.equals(output.getObjects().stream().map(o -> o.getId()).collect(Collectors.toSet()));
        } else {
            matchObjects = false;
        }
        
        boolean matchRoom = input.room == null || input.room == output.getRoom().getId();
                
        boolean matchInventory;
        if (input.inventoryRequirements == null || input.inventoryRequirements.isEmpty()) {
            matchInventory = true;
        } else if ((input.inventoryRequirements != null && !input.inventoryRequirements.isEmpty()) && (inventory != null && !inventory.isEmpty())) {
            matchInventory = inventory.stream()
                    .map(o -> o.getId())
                    .collect(Collectors.toSet())
                    .containsAll(input.inventoryRequirements);
        } else {
            matchInventory = false;
        }
        
        return matchPerson && matchObjects && matchRoom && matchInventory;
    }

}
