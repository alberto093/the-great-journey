/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.files;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.saltarelli.journey.GameDescription;
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
    private static final String ROOMS_FILENAME = "Rooms";
    private static final String OBJECTS_FILENAME = "Objects";
    private static final String COMMANDS_FILENAME = "Commands";
    private static final String DIRECTIONS_FILENAME = "Directions";
    private static final String PEOPLE_FILENAME = "People";
    private static final String STOPWORDS_FILENAME = "Stopwords";

    public static GameDescription loadGame() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + GAME_FILENAME)));
            GameDescription resource = new Gson().fromJson(reader, GameDescription.class);
            reader.close();
            return resource;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Set<Room> fetchRooms() {
        return fetchResource(PATH + ROOMS_FILENAME);
    }

    public static Set<AdvObject> fetchObjects() {
        return fetchResource(PATH + OBJECTS_FILENAME);
    }

    public static Set<Command> fetchCommands() {
        return fetchResource(PATH + COMMANDS_FILENAME);
    }

    public static Set<Direction> fetchDirections() {
        return fetchResource(PATH + DIRECTIONS_FILENAME);
    }

    public static Set<Person> fetchPeople() {
        return fetchResource(PATH + PEOPLE_FILENAME);
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

    private static <T> Set<T> fetchResource(String filePath) {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(filePath)));
            Set<T> resource = new Gson().fromJson(reader, new TypeToken<Set<T>>() {
            }.getType());
            reader.close();
            return resource;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Collections.emptySet();
    }
}
