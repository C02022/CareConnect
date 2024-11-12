package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Class to represent a Helper object
 */
public class Helper {

    @JsonProperty("username") private String username;
    @JsonProperty("password") private String password;
    @JsonProperty("fundingBasket") private ArrayList<Need> fundingBasket;

    /**
     * Creates a new Helper object
     *
     * @param username Helper's username
     * @param password Helper's password
     */
    public Helper(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
        fundingBasket = new ArrayList<>();
    }

    /**
     * Retrieves the username of the Helper
     * @return Helper's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the password of the Helper
     * @return Helper's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the funding basket in array form. Simplifies API calls
     * @return Helper's funding basket
     */
    public Need[] getFundingBasket() {
        Need[] needsArray = new Need[fundingBasket.size()];
        fundingBasket.toArray(needsArray);
        return needsArray;
    }

    /**
     * Adds a Need to the funding basket
     * @param need Need to be added
     */
    public void addNeed(Need need) {
        fundingBasket.add(need);
    }

    /**
     * Removes a Need from the funding basket
     * @param id ID of need to be removed
     * @return true if need was found and removed
     * false if need was not found
     */
    public boolean removeNeed(int id) {
        return fundingBasket.removeIf(need -> need.getId() == id);
    }

    /**
     * Clears the funding basket of all needs
     */
    public void checkout() {
        fundingBasket.clear();
    }

    /**
     * Returns Helper in "Helper [username=%s, fundingBasket=%s]" format
     * @return formatted Need string
     */
    public String toString(){
        String format = "Helper [username=%s, fundingBasket=%s]";
        return String.format(format, username, fundingBasket.toString());
    }
}
