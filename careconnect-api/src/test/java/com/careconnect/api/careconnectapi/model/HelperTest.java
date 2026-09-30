package com.careconnect.api.careconnectapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The unit test suite for the Need class
 *
 */
@Tag("Model-tier")
public class HelperTest {

    @Test
    public void testGetters() {
        // Setup
        String expectedUsername = "testOne";
        String expectedPassword = "testOne";
        Need[] expectedBasket = new Need[0];

        // Invoke
        Helper helper = new Helper(expectedUsername, expectedPassword);

        // Analyze
        assertEquals(expectedUsername, helper.getUsername());
        assertEquals(expectedPassword, helper.getPassword());
        assertEquals(expectedBasket.length, helper.getFundingBasket().length);
    }

    @Test
    public void testAddNeed() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;
        Need need = new Need(id, name, type, quantity, cost);

        String expectedUsername = "testOne";
        String expectedPassword = "testOne";
        Helper helper = new Helper(expectedUsername, expectedPassword);

        // Invoke
        helper.addNeed(need);

        // Analyze
        assertEquals(1, helper.getFundingBasket().length);
        assertEquals("testNameOne", helper.getFundingBasket()[0].getName());
    }

    @Test
    public void testRemoveNeed() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;
        Need need1 = new Need(id, name, type, quantity, cost);

        int id2 = 100;
        String name2 = "testNameTwo";
        String type2 = "testTypeTwo";
        int quantity2 = 1;
        int cost2 = 1;
        Need need2 = new Need(id2, name2, type2, quantity2, cost2);

        String expectedUsername = "testOne";
        String expectedPassword = "testOne";
        Helper helper = new Helper(expectedUsername, expectedPassword);

        // Invoke
        helper.addNeed(need1);
        helper.addNeed(need2);
        helper.removeNeed(id);

        // Analyze
        assertEquals(1, helper.getFundingBasket().length);
        assertEquals("testNameTwo", helper.getFundingBasket()[0].getName());
    }

    @Test
    public void testCheckout() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;
        Need need1 = new Need(id, name, type, quantity, cost);

        int id2 = 100;
        String name2 = "testNameTwo";
        String type2 = "testTypeTwo";
        int quantity2 = 1;
        int cost2 = 1;
        Need need2 = new Need(id2, name2, type2, quantity2, cost2);

        String expectedUsername = "testOne";
        String expectedPassword = "testOne";
        Helper helper = new Helper(expectedUsername, expectedPassword);

        // Invoke
        helper.addNeed(need1);
        helper.addNeed(need2);
        helper.checkout();

        // Analyze
        assertEquals(0, helper.getFundingBasket().length);
    }

    @Test
    public void testToString() {
        String expectedUsername = "testOne";
        String expectedPassword = "testOne";
        String expectedString = "Helper [username=testOne, fundingBasket=[]]";
        Helper helper = new Helper(expectedUsername, expectedPassword);

        // Analyze
        assertEquals(expectedString, helper.toString());
    }
}
