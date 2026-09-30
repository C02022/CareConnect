package com.careconnect.api.careconnectapi.persistence;

import java.io.IOException;

import com.careconnect.api.careconnectapi.model.Need;
import com.careconnect.api.careconnectapi.model.Helper;

/**
 * Interface for persistence of Helper objects in helpers.json
 */
public interface HelpersDAO {

    /**
     * Gets all helpers in the helpers.json file
     *
     * @return Array of all helpers
     * @throws IOException if an issue occurs with storage
     */
    Helper[] getHelpers() throws IOException;

    /**
     * Gets a helper specified by its username
     *
     * @param username Username of the helper
     * @return Matching helper object
     * @throws IOException if an issue occurs with storage
     */
    Helper getHelper(String username) throws IOException;

    /**
     * Adds a need into a helper's funding basket
     * 
     * @param need the need that is being added to the helper's funding basket
     * @return The updated funding basket of the helper, with the new need added
     * @throws IOException if an issue occurs with storage
     */
    Need[] addNeed(String username, Need need) throws IOException;

    /**
     * Removes a need from a helper's funding basket
     *
     * @param id The ID of the need that is being removed from the helper's funding basket
     * @return The updated funding basket of the helper, minus the removed need
     * @throws IOException if an issue occurs with storage
     */
    Need[] removeNeed(String username, int id) throws IOException;

    /**
     * Retrieves a helper's funding basket
     *
     * @param username The helper's username
     * @return The funding basket (can be empty)
     * @throws IOException if an issue occurs with storage
     */
    Need[] getFundingBasket(String username) throws IOException;

    /**
     * Empties helper's funding basket and returns it, simulating a checkout
     *
     * @param username The helper's username
     * @return An ArrayList of all needs currently in the basket
     * @throws IOException if an issue occurs with storage
     */
    Need[] checkoutNeeds(String username) throws IOException;

    /**
     * Creates a new helper account given a username and password
     * 
     * @param username The username for the new helper account
     * @param password The password for the new helper account
     * @return The new instance of Helper if the account was successfully 
     * created or null if it was not created
     * @throws IOException if an issue occurs with storage
     */
    Helper createAccount(String username, String password) throws IOException;

    /**
     * In the case that the admin updates or deletes a need on the website,
     * that should be reflected in the funding basket of the helpers with that need.
     *
     * As for why we remove from their basket when it is only an update, we don't want
     * a customer to be confused and checkout a need after something such as the cost is changed.
     *
     * @param id ID of the need that was deleted
     * @throws IOException if an issue occurs with storage
     */
    void needUpdatedOrDeleted(int id) throws IOException;

}
