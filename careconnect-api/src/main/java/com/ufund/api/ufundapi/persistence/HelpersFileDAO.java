package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implements the functionality for JSON file-based persistence for Helpers in the system
 */
@Component
public class HelpersFileDAO implements HelpersDAO{
    private Map<String, Helper> helperAccounts;
    private final ObjectMapper objectMapper;
    private final String filename;

    /**
     * Creates a Helper File Data Access Object
     *
     * @param filename Filename to read from and write to
     * @param objectMapper Provides serialization and deserialization
     *
     * @throws IOException if error occurs accessing the specified file
     */
    public HelpersFileDAO(@Value("${helpers.file}") String filename, ObjectMapper objectMapper) throws IOException{
        this.filename = filename;

        // If file does not exist, creates a new empty file
        if(!new File(filename).exists()) new File(filename).createNewFile();

        // Enables pretty printing
        this.objectMapper = objectMapper;
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    /**
     * Loads Helper objects from the JSON file into the map
     */
    private void load() {
        helperAccounts = new TreeMap<>();

        // Deserializes JSON objects from file into an array of Helper objects
        Helper[] helperArray;

        // Try to read the file
        try{
            helperArray = objectMapper.readValue(new File(filename),Helper[].class);

        // If cupboard.json file is empty, ObjectMapper throws an exception
        } catch (Exception e) {
            helperArray = new Helper[0];
        }

        // For each helper, add it to the map
        for(Helper helper : helperArray) {
            helperAccounts.put(helper.getUsername().toLowerCase(), helper);
        }
    }

    /**
     * Saves the Helper objects from the map into the file as an array of JSON objects
     *
     * @return true if the Helpers were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Helper[] helpersArray;

        // Get all helpers in the map
        ArrayList<Helper> helpersArrayList = new ArrayList<>(helperAccounts.values());

        // Translate from ArrayList to Array
        helpersArray = new Helper[helpersArrayList.size()];
        helpersArrayList.toArray(helpersArray);

        // Serializes Helper objects to JSON objects in the file
        objectMapper.writeValue(new File(filename), helpersArray);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Helper[] getHelpers() throws IOException {
        synchronized (helperAccounts) {
            ArrayList<Helper> helpersArrayList = new ArrayList<>();
            helpersArrayList.addAll(helperAccounts.values());

            Helper[] helpersArray = new Helper[helpersArrayList.size()];
            helpersArrayList.toArray(helpersArray);
            return helpersArray;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Helper getHelper(String username) throws IOException {
        synchronized(helperAccounts) {
            return helperAccounts.getOrDefault(username.toLowerCase(), null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Need[] getFundingBasket(String username) throws IOException {
        synchronized(helperAccounts) {
            Helper currentHelper = helperAccounts.get(username.toLowerCase());
            if(currentHelper == null) {
                return null;
            }

            return currentHelper.getFundingBasket();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] addNeed(String username, Need need) throws IOException {
        synchronized(helperAccounts) {
            Helper currentHelper = helperAccounts.get(username.toLowerCase());
            if (currentHelper == null) {
                return null;
            }
            Need[] currentFundingBasket = currentHelper.getFundingBasket();

            boolean exists = false;
            for(Need existingNeed : currentFundingBasket) {
                if (existingNeed.getId() == need.getId()) exists = true;
            }

            if(!exists) currentHelper.addNeed(need);
            Need[] updatedFundingBasket = currentHelper.getFundingBasket();

            save();
            return updatedFundingBasket;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] removeNeed(String username, int id) throws IOException {
        synchronized(helperAccounts) {
            Helper currentHelper = helperAccounts.get(username.toLowerCase());
            if (currentHelper == null) {
                return null;
            }
            boolean removed = currentHelper.removeNeed(id);
            if (!removed) {
                return currentHelper.getFundingBasket();
            }
            Need[] updatedFundingBasket = currentHelper.getFundingBasket();

            save();
            return updatedFundingBasket;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] checkoutNeeds(String username) throws IOException {
        synchronized(helperAccounts) {
            Helper currentHelper = helperAccounts.get(username.toLowerCase());
            if (currentHelper == null) {
                return null;
            }
            Need[] checkedOutNeeds = currentHelper.getFundingBasket();
            currentHelper.checkout();

            // Remove the checked out needs from all other accounts in the system
            for (Helper helper : helperAccounts.values()) {
                for (Need need : checkedOutNeeds) helper.removeNeed(need.getId());
            }

            save();
            return checkedOutNeeds;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Helper createAccount(String username, String password) throws IOException {
        synchronized (helperAccounts) {
            if(!helperAccounts.containsKey(username.toLowerCase())) {
                Helper helper = new Helper(username.toLowerCase(), password);
                helperAccounts.put(username.toLowerCase(), helper);

                save();
                return helper;

            } else return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void needUpdatedOrDeleted(int id) throws IOException{
        synchronized (helperAccounts) {
            for(Helper helper : helperAccounts.values()) {
                helper.removeNeed(id);
            }
        }
    }
}
