package com.careconnect.api.careconnectapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.careconnect.api.careconnectapi.model.Need;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract class used to handle all functionality for interacting with needs
 * in our system, whether that be in the cupboard.json, requests.json, or funded.json file
 */
public abstract class NeedsDAO {
    private Map<Integer, Need> needs;
    private final ObjectMapper objectMapper;
    private static int nextId;
    private final String filename;

    /**
     * Creates a Needs Data Access Object
     *
     * @param filename Filename to read from and write to
     * @param objectMapper Provides serialization and deserialization
     *
     * @throws IOException if error occurs accessing the specified file
     */
    protected NeedsDAO(String filename, ObjectMapper objectMapper) throws IOException{
        this.filename = filename;

        // If file does not exist, creates a new empty file
        if(!new File(filename).exists()) new File(filename).createNewFile();

        // Enables pretty printing
        this.objectMapper = objectMapper;
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    /**
     * Loads Need objects from the JSON file into the map
     */
    private void load() {
        needs = new TreeMap<>();
        nextId = 0;

        // Deserializes JSON objects from file into an array of Need objects
        Need[] needsArray;

        // Try to read the file
        try{
            needsArray = objectMapper.readValue(new File(filename),Need[].class);

            // If needs file is empty, ObjectMapper throws an exception
        } catch (Exception e) {
            needsArray = new Need[0];
        }

        // For each need object, add it to the map created and keep track of the greatest id
        for(Need need : needsArray) {
            needs.put(need.getId(), need);
            if(need.getId() > nextId) nextId = need.getId();
        }

        // Make the next id one greater than the maximum for any newly created Needs
        nextId++;
    }

    /**
     * Saves the Need objects from the map into the file as an array of JSON objects
     *
     * @return true if the Needs were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Need[] needsArray = getNeedsArray();

        // Serializes Need objects to JSON objects in the file
        objectMapper.writeValue(new File(filename), needsArray);
        return true;
    }

    /**
     * Generates the next id for a new Need
     *
     * @return The next id
     */
    private static synchronized int nextId() {
        int id = nextId;
        nextId++;
        return id;
    }

    /**
     * Generates an array of Needs from the tree map
     *
     * @return  The array of Needs, may be empty
     */
    private Need[] getNeedsArray() {
        return getNeedsArray(null);
    }

    /**
     * Generates an array of Needs from the tree map for any
     * Need object that contains the text specified by containsText
     *
     * If containsText is null, the array contains all the Needs
     * in the tree map
     *
     * @return  The array of Needs, may be empty
     */
    private Need[] getNeedsArray(String containsText) {
        ArrayList<Need> needsArrayList = new ArrayList<>();

        for(Need need : needs.values()) {
            if(containsText == null || need.getName().toLowerCase().contains(containsText.toLowerCase())) needsArrayList.add(need);
        }

        Need[] needsArray = new Need[needsArrayList.size()];
        needsArrayList.toArray(needsArray);
        return needsArray;
    }

    /**
     * Gets all needs within the JSON File
     *
     * @return All needs that are stored in an array
     *
     * @throws IOException if an issue occurs with storage
     */
    public Need[] getNeeds() throws IOException {
        synchronized(needs) {
            return getNeedsArray();
        }
    }

    /**
     * Gets a need specified by its id
     *
     * @param id id of the need
     * @return Matching need object
     * @throws IOException if an issue occurs with storage
     */
    public Need getNeed(int id) throws IOException {
        synchronized(needs){
            return needs.getOrDefault(id, null);
        }
    }

    /**
     * Finds a need that contains specific text
     *
     * @param containsText Text to match against
     * @return Matching need
     * @throws IOException if an issue occurs with storage
     */
    public Need[] findNeeds(String containsText) throws IOException {
        synchronized(needs) {
            return getNeedsArray(containsText);
        }
    }

    /**
     * Creates and saves a need object
     *
     * @param need Need object to be created
     * @return Created need object if it is successfully created, otherwise null
     * @throws IOException if an issue occurs with storage
     */
    public Need createNeed(Need need) throws IOException {
        synchronized(needs) {
            // We create a new need object because the id field is immutable
            // and we need to assign the next unique id
            Need newNeed = new Need(nextId(), need.getName(), need.getType(), need.getQuantity(), need.getCost());
            needs.put(newNeed.getId(), newNeed);
            save(); // may throw an IOException
            return newNeed;
        }
    }

    /**
     * Updates and saves a need object
     *
     * @param need Need to be updated
     * @return Updated need if it is successfully updated, otherwise null
     * @throws IOException if an issue occurs with storage
     */
    public Need updateNeed(Need need) throws IOException {
        synchronized(needs) {
            if (!needs.containsKey(need.getId())) {
                return null; // need does not exist
            }

            needs.put(need.getId(), need);
            save(); // may throw an IOException
            return need;
        }
    }

    /**
     * Deletes a need object with the given name
     *
     * @param id ID of need to be deleted
     * @return true if it was deleted, false otherwise
     * @throws IOException if an issue occurs with storage
     */
    public boolean deleteNeed(int id) throws IOException {
        synchronized(needs) {
            if(needs.containsKey(id)) {
                needs.remove(id);
                return save();

            } else return false;
        }
    }
}
