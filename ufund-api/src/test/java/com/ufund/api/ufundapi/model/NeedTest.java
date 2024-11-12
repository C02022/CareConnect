package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The unit test suite for the Need class
 * 
 */
@Tag("Model-tier")
public class NeedTest {
    @Test
    public void testGetters() {
        // Setup
        int expected_id = 99;
        String expected_name = "testNeedOne";
        String expected_type = "testTypeOne";
        int expected_quantity = 1;
        int expected_cost = 1;

        // Invoke
        Need need = new Need(expected_id, expected_name, expected_type, expected_quantity, expected_cost);

        // Analyze
        assertEquals(expected_id,need.getId());
        assertEquals(expected_name,need.getName());
        assertEquals(expected_type,need.getType());
        assertEquals(expected_quantity, need.getQuantity());
        assertEquals(expected_cost, need.getCost());
    }

    @Test
    public void testSetName() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;

        Need need = new Need(id, name, type, quantity, cost);

        String expected_name = "Expected Name";

        // Invoke
        need.setName(expected_name);

        // Analyze
        assertEquals(expected_name,need.getName());
    }

    @Test
    public void testToString() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;
        
        String format = "Need [id=%d, name=%s, type=%s, quantity=%d, cost=%d]";
        String expected_string = String.format(format,id,name,type,quantity,cost);
        Need need = new Need(id, name, type, quantity, cost);

        // Invoke
        String actual_string = need.toString();

        // Analyze
        assertEquals(expected_string,actual_string);
    }

    @Test
    public void testSetId() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;

        // Invoke
        int expectedId = 5;
        Need need = new Need(id, name, type, quantity, cost);
        need.setId(5);

        // Analyze
        assertEquals(expectedId, need.getId());
    }
    
    @Test
    public void testEqualsTrue() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;

        Boolean expectedResult = true;
        Need need1 = new Need(id, name, type, quantity, cost);
        Need need2 = new Need(id, name, type, quantity, cost);


        // Invoke
        Boolean actualResult = need1.equals(need2);

        // Analyze 
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testEqualsFalse() {
        // Setup
        int id = 99;
        String name = "testNameOne";
        String type = "testTypeOne";
        int quantity = 1;
        int cost = 1;

        String differentName = "testNameNotOne";

        Need need1 = new Need(id, name, type, quantity, cost);
        Need need2 = new Need(id, differentName, type, quantity, cost);

        // Invoke
        boolean actualResult = need1.equals(need2);

        // Analyze 
        assertFalse(actualResult);
    }
}
