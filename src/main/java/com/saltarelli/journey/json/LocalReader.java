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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Alberto
 */
public class LocalReader implements ResourcesReader {
    
    private static final String PATH = "./resources/";
    private static final String GAME_FILENAME = "Game";
    private static final String STORIES_FILENAME = "Stories";
    private static final String ROOMS_FILENAME = "Rooms";
    private static final String OBJECTS_FILENAME = "Objects";
    private static final String COMMANDS_FILENAME = "Commands";
    private static final String PEOPLE_FILENAME = "People";
    private static final String PLAYER_FILENAME = "Player";
    private static final String EXCEPTIONS_FILENAME = "Exceptions";
    private static final String STOPWORDS_FILENAME = "Stopwords";
    private static final String PREDEFINED_FILENAME = "PredefinedCommands";

    @Override
    public GameJSON fetchGame() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + GAME_FILENAME)));
            GameJSON game = new Gson().fromJson(reader, GameJSON.class);
            reader.close();
            return game;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<Story> fetchStories() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + STORIES_FILENAME)));
            Type setType = new TypeToken<HashSet<Story>>(){}.getType();
            Set<Story> rooms = new Gson().fromJson(reader, setType);
            reader.close();
            return rooms;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<RoomJSON> fetchRooms() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + ROOMS_FILENAME)));
            Type setType = new TypeToken<HashSet<RoomJSON>>(){}.getType();
            Set<RoomJSON> rooms = new Gson().fromJson(reader, setType);
            reader.close();
            return rooms;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<AdvObjectJSON> fetchObjects() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + OBJECTS_FILENAME)));
            Type setType = new TypeToken<HashSet<AdvObjectJSON>>(){}.getType();
            Set<AdvObjectJSON> rooms = new Gson().fromJson(reader, setType);
            reader.close();
            return rooms;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<CommandJSON> fetchCommands() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + COMMANDS_FILENAME)));
            Type setType = new TypeToken<HashSet<CommandJSON>>(){}.getType();
            Set<CommandJSON> commands = new Gson().fromJson(reader, setType);
            reader.close();
            return commands;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<PersonJSON> fetchPeople() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + PEOPLE_FILENAME)));
            Type setType = new TypeToken<HashSet<PersonJSON>>(){}.getType();
            Set<PersonJSON> people = new Gson().fromJson(reader, setType);
            reader.close();
            return people;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public PersonJSON fetchPlayer() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + PLAYER_FILENAME)));
            PersonJSON player = new Gson().fromJson(reader, PersonJSON.class);
            reader.close();
            return player;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<ExceptionDescription> fetchExceptions() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + EXCEPTIONS_FILENAME)));
            Type setType = new TypeToken<HashSet<ExceptionDescription>>(){}.getType();
            Set<ExceptionDescription> exceptions = new Gson().fromJson(reader, setType);
            reader.close();
            return exceptions;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<PredefinedCommand> fetchPredefinedCommands() {
        try {
            Reader reader = new BufferedReader(new FileReader(new File(PATH + PREDEFINED_FILENAME)));
            Type setType = new TypeToken<HashSet<PredefinedCommand>>(){}.getType();
            Set<PredefinedCommand> commands = new Gson().fromJson(reader, setType);
            reader.close();
            return commands;
        } catch (JsonIOException | JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<String> fetchStopwords() {
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
}
