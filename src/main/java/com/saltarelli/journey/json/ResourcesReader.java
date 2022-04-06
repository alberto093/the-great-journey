/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import java.util.Collection;

/**
 *
 * @author Alberto
 */
public interface ResourcesReader {
    GameJSON fetchGame();
    Collection<Story> fetchStories();
    Collection<RoomJSON> fetchRooms();
    Collection<AdvObjectJSON> fetchObjects();
    Collection<CommandJSON> fetchCommands();
    Collection<PersonJSON> fetchPeople();
    PersonJSON fetchPlayer();
    Collection<ExceptionDescription> fetchExceptions();
    Collection<PredefinedCommand> fetchPredefinedCommands();
    Collection<String> fetchStopwords();
}
