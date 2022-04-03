/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.saltarelli.journey.Game;
import com.saltarelli.journey.type.AdvObject;
import com.saltarelli.journey.type.Command;
import com.saltarelli.journey.type.Direction;
import com.saltarelli.journey.type.Person;
import com.saltarelli.journey.type.Room;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class ResourcesReader {

    private static final String PATH = "./resources/";
    private static final String GAME_FILENAME = "Game";
    private static final String GAMEPLAY_FILENAME = "Gameplay";
    private static final String ROOMS_FILENAME = "Rooms";
    private static final String OBJECTS_FILENAME = "Objects";
    private static final String COMMANDS_FILENAME = "Commands";
    private static final String PEOPLE_FILENAME = "People";
    private static final String EXCEPTIONS_FILENAME = "Exceptions";
    private static final String STOPWORDS_FILENAME = "Stopwords";
    

    public static GameJSON fetchGame() {
        return fetchJSON(PATH + GAME_FILENAME);
    }
    
    public static Set<Gameplay> fetchGameplaySet() {
        return fetchJSON(PATH + GAMEPLAY_FILENAME);
    }

    public static Set<RoomJSON> fetchRooms() {
        return fetchJSON(PATH + ROOMS_FILENAME);
    }

    public static Set<AdvObjectJSON> fetchObjects() {
        return fetchJSON(PATH + OBJECTS_FILENAME);
    }

    public static Set<CommandJSON> fetchCommands() {
        return fetchJSON(PATH + COMMANDS_FILENAME);
    }

    public static Set<PersonJSON> fetchPeople() {
        return fetchJSON(PATH + PEOPLE_FILENAME);
    }
    
    public static Set<ExceptionDescription> fetchExceptions() {
        return fetchJSON(PATH + EXCEPTIONS_FILENAME);
    }

    public static Set<String> fetchStopwords() {
        Set<String> set = new HashSet<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(PATH + STOPWORDS_FILENAME)));
            while (reader.ready()) {
                set.add(reader.readLine().trim().toLowerCase());
            }
            reader.close();
            return set;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }

    private static <T> T fetchJSON(String filePath) {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(filePath)));
            T resource = new Gson().fromJson(reader, new TypeToken<T>() {
            }.getType());
            reader.close();
            return resource;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
