/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.json;

import java.util.Collection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Alberto
 */
public class RemoteReader implements ResourcesReader {

    private static final String HOST_NAME = "https://run.mocky.io/v3/";

    private enum Endpoint {
        GAME("87691d67-9820-431a-bc36-0064ba727707"),
        STORIES("7b2f0a2f-be96-4cb5-a7b4-50a0006264a1"),
        ROOMS("9b375252-f648-4c17-8c73-5d64ecb8bdff"),
        OBJECTS("8217f3bb-f758-4b43-a232-c19dcfea5314"),
        COMMANDS("2b7d0014-e18e-4a8c-bfa8-19e1db8cb63d"),
        PEOPLE("9fb3e2ff-d4dc-4cc2-9890-b35d0cd99ce4"),
        PLAYER("e36b1903-16ab-4849-9a64-24f8adb75318"),
        EXCEPTIONS("64e51ba4-d0ac-4895-8028-72dd00cf1ca4"),
        STOPWORDS("78ec41ab-3880-4839-b4b2-ac55a8e38997"),
        PREDEFINED_COMMANDS("8d3b5a59-958c-4714-b974-17d2f29157ff");

        public final String path;

        private Endpoint(String path) {
            this.path = path;
        }
    }
    
    private final WebTarget target;

    public RemoteReader() {
        Client client = ClientBuilder.newClient();
        target = client.target(HOST_NAME);
    }

    @Override
    public GameJSON fetchGame() {
        Response response = target.path(Endpoint.GAME.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        return new Gson().fromJson(json, GameJSON.class);
    }

    @Override
    public Collection<Story> fetchStories() {
        Response response = target.path(Endpoint.STORIES.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<Story>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public Collection<RoomJSON> fetchRooms() {
        Response response = target.path(Endpoint.ROOMS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<RoomJSON>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public Collection<AdvObjectJSON> fetchObjects() {
        Response response = target.path(Endpoint.OBJECTS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<AdvObjectJSON>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

       
    @Override
    public Collection<CommandJSON> fetchCommands() {
        Response response = target.path(Endpoint.COMMANDS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<CommandJSON>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public Collection<PersonJSON> fetchPeople() {
        Response response = target.path(Endpoint.PEOPLE.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<PersonJSON>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public PersonJSON fetchPlayer() {
        Response response = target.path(Endpoint.GAME.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        return new Gson().fromJson(json, PersonJSON.class);
    }

    @Override
    public Collection<ExceptionDescription> fetchExceptions() {
        Response response = target.path(Endpoint.EXCEPTIONS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<ExceptionDescription>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public Collection<PredefinedCommand> fetchPredefinedCommands() {
        Response response = target.path(Endpoint.PREDEFINED_COMMANDS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<PredefinedCommand>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

    @Override
    public Collection<String> fetchStopwords() {
        Response response = target.path(Endpoint.STOPWORDS.path).request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        Type setType = new TypeToken<HashSet<String>>(){}.getType();
        return new Gson().fromJson(json, setType);
    }

}
