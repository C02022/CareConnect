package com.careconnect.api.careconnectapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.careconnect.api.careconnectapi.model.Need;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for CupboardFileDAO
 */
@Tag("Persistence-tier")
public class CupboardFileDAOTest {

    CupboardFileDAO cupboardFileDAO;
    Need[] testNeeds;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create test needs and mock object mapper injection
     *
     * @throws IOException if error occurs during write
     */
    @BeforeEach
    public void setupCupboardFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testNeeds = new Need[3];
        testNeeds[0] = new Need(99, "testOne", "testTypeOne", 1, 1);
        testNeeds[1] = new Need(100, "testTwo", "testTypeTwo", 2, 2);
        testNeeds[2] = new Need(101, "testThree", "testTypeThree", 69, 69);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the need array above
        when(mockObjectMapper.readValue(new File("data/cupboard.json"), Need[].class)).thenReturn(testNeeds);
        cupboardFileDAO = new CupboardFileDAO("data/cupboard.json", mockObjectMapper);
    }

    @Test
    public void testGetNeeds() {
        // Invoke
        Need[] cupboard;

        try{
            cupboard = cupboardFileDAO.getNeeds();
        } catch (IOException e){
            cupboard = null;
        }

        // Analyze
        assertEquals(cupboard.length, testNeeds.length);
        for (int i = 0; i < testNeeds.length; ++i){
            assertEquals(cupboard[i].getId(), testNeeds[i].getId());
            assertEquals(cupboard[i].getName(), testNeeds[i].getName());
            assertEquals(cupboard[i].getType(), testNeeds[i].getType());
            assertEquals(cupboard[i].getQuantity(), testNeeds[i].getQuantity());
            assertEquals(cupboard[i].getCost(), testNeeds[i].getCost());
        }
    }

    @Test
    public void testGetNeed() {
        // Invoke
        Need need;

        try{
            need = cupboardFileDAO.getNeed(99);
        } catch (IOException e) {
            need = null;
        }

        // Analyze
        assertEquals(need.getId(), testNeeds[0].getId());
        assertEquals(need.getName(), testNeeds[0].getName());
        assertEquals(need.getType(), testNeeds[0].getType());
        assertEquals(need.getQuantity(), testNeeds[0].getQuantity());
        assertEquals(need.getCost(), testNeeds[0].getCost());
    }

    @Test
    public void testGetNeedDNE() {
        // Invoke
        Need need;

        try{
            need = cupboardFileDAO.getNeed(1);
        } catch (IOException e) {
            need = null;
        }

        // Analyze
        assertNull(need);
    }

    @Test
    public void testFindExistingNeed() {
        // Invoke
        Need[] needs;

        try {
            needs = cupboardFileDAO.findNeeds("testo");
        } catch (IOException e) {
            needs = null;
        }

        assertEquals(needs.length, 1);
        assertEquals(needs[0].getId(), testNeeds[0].getId());
        assertEquals(needs[0].getName(), testNeeds[0].getName());
        assertEquals(needs[0].getType(), testNeeds[0].getType());
        assertEquals(needs[0].getQuantity(), testNeeds[0].getQuantity());
        assertEquals(needs[0].getCost(), testNeeds[0].getCost());
    }

    @Test
    public void testFindExistingNeeds() {
        // Invoke
        Need[] needs;

        try {
            needs = cupboardFileDAO.findNeeds("test");
        } catch (IOException e) {
            needs = null;
        }

        assertEquals(needs.length, 3);
        for(int i = 0; i < needs.length; i++) {
            assertEquals(needs[i].getId(), testNeeds[i].getId());
            assertEquals(needs[i].getName(), testNeeds[i].getName());
            assertEquals(needs[i].getType(), testNeeds[i].getType());
            assertEquals(needs[i].getQuantity(), testNeeds[i].getQuantity());
            assertEquals(needs[i].getCost(), testNeeds[i].getCost());
        }
    }

    @Test
    public void testFindMissingNeed() {
        // Invoke
        Need[] needs;

        try {
            needs = cupboardFileDAO.findNeeds("empty");
        } catch (IOException e) {
            needs = null;
        }

        assertEquals(needs.length, 0);

    }

    @Test
    public void testDeleteNeed() {
        boolean deleted;

        try{
            deleted = cupboardFileDAO.deleteNeed(99);
        } catch (IOException e) {
            deleted = false;
        }

        assertTrue(deleted);
    }

    @Test
    public void testDeleteMissingNeed() {
        boolean deleted;

        try {
            deleted = cupboardFileDAO.deleteNeed(1);
        } catch (IOException e) {
            deleted = false;
        }

        assertFalse(deleted);
    }

    @Test
    public void testFileUpdateOnDelete() {
        boolean deleted;
        Need[] needs;

        try{
            deleted = cupboardFileDAO.deleteNeed(99);
            needs = cupboardFileDAO.getNeeds();
        } catch (IOException e) {
            deleted = false;
            needs = null;
        }

        assertTrue(deleted);
        assertEquals(needs.length, 2);
    }

    @Test
    public void testCreateNeed() {
        Need need = new Need(5, "New Need", "Test", 1, 1);
        Need returnedNeed;
        Need[] array;

        try {
            returnedNeed = cupboardFileDAO.createNeed(need);
            array = cupboardFileDAO.getNeeds();
        } catch (IOException e) {
            returnedNeed = null;
            array = null;
        }

        assertNotNull(returnedNeed);
        assertEquals(returnedNeed.getName(), "New Need");
        assertEquals(array.length, 4);
    }

    @Test
    public void testUpdateNeed() {
        Need need = new Need(99, "testOneUpdated", "testTypeOne", 1, 1);
        Need updatedNeed;

        try{
            cupboardFileDAO.updateNeed(need);
            updatedNeed = cupboardFileDAO.getNeed(99);
        } catch (IOException e) {
            updatedNeed = null;
        }

        assertNotNull(updatedNeed);
        assertEquals(updatedNeed.getId(), need.getId());
        assertEquals(updatedNeed.getName(), need.getName());
        assertEquals(updatedNeed.getType(), need.getType());
        assertEquals(updatedNeed.getQuantity(), need.getQuantity());
        assertEquals(updatedNeed.getCost(), need.getCost());

    }

    @Test
    public void updateNeedDNE() {
        Need need = new Need(1, "DNE", "testTypeOne", 1, 1);
        Need updatedNeed;

        try{
            updatedNeed = cupboardFileDAO.updateNeed(need);
        } catch (IOException e) {
            updatedNeed = null;
        }

        assertNull(updatedNeed);

    }
}
