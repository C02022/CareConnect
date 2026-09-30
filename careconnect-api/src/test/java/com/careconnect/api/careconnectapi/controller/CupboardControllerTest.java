package com.careconnect.api.careconnectapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.careconnect.api.careconnectapi.model.Need;

import com.careconnect.api.careconnectapi.persistence.CupboardFileDAO;
import com.careconnect.api.careconnectapi.persistence.HelpersDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Test the Cupboard Controller Class
 */
@Tag("Controller-tier")
public class CupboardControllerTest {
    private CupboardController cupboardController;
    private CupboardFileDAO mockCupboardDAO;
    private HelpersDAO mockHelperDAO;

    /**
     * Before each test, create a new UFundController object and inject
     * a mock CupboardDAO
     */
    @BeforeEach
    public void setupCupboardController() {
        mockCupboardDAO = mock(CupboardFileDAO.class);
        mockHelperDAO = mock(HelpersDAO.class);
        cupboardController = new CupboardController(mockCupboardDAO, mockHelperDAO);
    }

    @Test
    public void testGetNeed() throws IOException {
        // Setup
        Need need = new Need(99, "testNeedOne", "testTypeOne", 1, 1);
        // When same ID is passed in, our mock CupboardDAO will return Need object
        when(mockCupboardDAO.getNeed(need.getId())).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(need.getId());

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(need,response.getBody());
    }

    @Test
    public void testGetNeedNotFound() throws Exception {
        // Setup
        int needId = 99;
        // When same id is passed in, our mock CupboardDAO will return null,
        // simulating no need found
        when(mockCupboardDAO.getNeed(needId)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testGetNeedHandleException() throws Exception {
        // Setup
        int needId = 99;
        // When getNeed is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeed(needId);

        // Invoke
        ResponseEntity<Need> response = cupboardController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testSearchNeedExists() throws Exception {
        // Setup
        Need need = new Need(99, "testNeedOne", "testTypeOne", 1, 1);
        Need[] needArray = new Need[1];
        needArray[0] = need;

        // When name is passed in, our mock CupboardDAO will return Need array
        when(mockCupboardDAO.findNeeds(need.getName())).thenReturn(needArray);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds(need.getName());

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(need, response.getBody()[0]);
    }

    @Test
    public void testSearchNeedDNE() throws Exception {
        // Setup
        Need need = new Need(99, "testNeedOne", "testTypeOne", 1, 1);

        // When name is passed in, our mock CupboardDAO will return Need array
        when(mockCupboardDAO.findNeeds("DNE")).thenReturn(null);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds(need.getName());

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testSearchHandleException() throws Exception {
        // Setup
        String name = "Exception";
        // When getNeed is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).findNeeds("Exception");

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.searchNeeds("Exception");

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

     @Test
     public void testCreateNeed() throws IOException {  // createNeed may throw IOException
         // Setup
         Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);
         // when createNeed is called, return true simulating successful
         // creation and save
         when(mockCupboardDAO.createNeed(need)).thenReturn(need);

         // Invoke
         ResponseEntity<Need> response = cupboardController.createNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.CREATED,response.getStatusCode());
         assertEquals(need, response.getBody());
     }
 
     @Test
     public void testCreateNeedFailed() throws IOException {  // createNeed may throw IOException
         // Setup
         Need need = new Need(1, "Toe Surgery", "SURGERY", 1, 2000);
         Need[] returnedNeeds = new Need[1];
         returnedNeeds[0] = need;

         // When findNeeds() is called, return an array of length 1 simulating that
         // A need with the name "Toe Surgery" has already been created, so the expected reponse is CONFLICT
         when(mockCupboardDAO.findNeeds(need.getName())).thenReturn(returnedNeeds);
 
         // Invoke
         ResponseEntity<Need> response = cupboardController.createNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
     }
 
     @Test
     public void testCreateNeedHandleException() throws IOException {  // createNeed may throw IOException
         // Setup
         Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);
 
         // When createNeed is called on the Mock Cupboard DAO, throw an IOException
         doThrow(new IOException()).when(mockCupboardDAO).createNeed(need);
 
         // Invoke
         ResponseEntity<Need> response = cupboardController.createNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
     }

     @Test
     public void testUpdateNeed() throws IOException { // updateNeed may throw IOException
         // Setup
         Need need = new Need(1, "Open Chest Surgery", "SURGERY", 200, 1000);
         // when updateNeed is called, return true simulating successful
         // update and save
         when(mockCupboardDAO.updateNeed(need)).thenReturn(need);
         ResponseEntity<Need> response = cupboardController.updateNeed(need);
         need.setName("Bolt");
 
         // Invoke
         response = cupboardController.updateNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.OK,response.getStatusCode());
         assertEquals(need,response.getBody());
     }
 
     @Test
     public void testUpdateNeedFailed() throws IOException { // updateNeed may throw IOException
         // Setup
         Need need = new Need(99, "NOT A Surgery", "SURGERY", 200, 1000);
         // when updateNeed is called, return true simulating successful
         // update and save
         when(mockCupboardDAO.updateNeed(need)).thenReturn(null);
 
         // Invoke
         ResponseEntity<Need> response = cupboardController.updateNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
     }
 
     @Test
     public void testUpdateNeedHandleException() throws IOException { // updateNeed may throw IOException
         // Setup
         Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);
         // When updateNeed is called on the Mock Cupboard DAO, throw an IOException
         doThrow(new IOException()).when(mockCupboardDAO).updateNeed(need);
 
         // Invoke
         ResponseEntity<Need> response = cupboardController.updateNeed(need);
 
         // Analyze
         assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
     }

    @Test
     public void testGetNeeds() throws IOException {
        // Setup
        Need[] needs = new Need[2];
        needs[0] = new Need(99, "testNeedOne", "testTypeOne", 1, 1);
        needs[1] = new Need(100, "testNeedTwo", "testTypeTwo", 2, 2);
        // When getNeeds is called return the needs created above
        when(mockCupboardDAO.getNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(needs,response.getBody());
     }

     @Test
     public void testGetNeedsHandleException() throws IOException {
        // Setup
        // When getNeeds is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).getNeeds();

        // Invoke
        ResponseEntity<Need[]> response = cupboardController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
     }

    @Test
    public void testDeleteNeed() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // when deleteNeed is called return true, simulating successful deletion
        when(mockCupboardDAO.deleteNeed(needId)).thenReturn(true);

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void testDeleteNeedNotFound() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // when deleteNeed is called return false, simulating failed deletion
        when(mockCupboardDAO.deleteNeed(needId)).thenReturn(false);

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testDeleteNeedHandleException() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // When deleteNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockCupboardDAO).deleteNeed(needId);

        // Invoke
        ResponseEntity<Need> response = cupboardController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }
}
