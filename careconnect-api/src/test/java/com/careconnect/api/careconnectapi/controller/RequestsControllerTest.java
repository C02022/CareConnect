package com.careconnect.api.careconnectapi.controller;

import com.careconnect.api.careconnectapi.model.Need;
import com.careconnect.api.careconnectapi.persistence.CupboardFileDAO;
import com.careconnect.api.careconnectapi.persistence.HelpersDAO;
import com.careconnect.api.careconnectapi.persistence.RequestsFileDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("Controller-tier")
public class RequestsControllerTest {
    private RequestsController requestsController;
    private RequestsFileDAO mockRequestsDAO;

    /**
     * Before each test, create a new UFundController object and inject
     * a mock CupboardDAO
     */
    @BeforeEach
    public void setupCupboardController() {
        mockRequestsDAO = mock(RequestsFileDAO.class);
        requestsController = new RequestsController(mockRequestsDAO);
    }

    @Test
    public void testGetNeed() throws IOException {
        // Setup
        Need need = new Need(99, "testNeedOne", "testTypeOne", 1, 1);
        // When same ID is passed in, our mock CupboardDAO will return Need object
        when(mockRequestsDAO.getNeed(need.getId())).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = requestsController.getNeed(need.getId());

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
        when(mockRequestsDAO.getNeed(needId)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = requestsController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testGetNeedHandleException() throws Exception {
        // Setup
        int needId = 99;
        // When getNeed is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockRequestsDAO).getNeed(needId);

        // Invoke
        ResponseEntity<Need> response = requestsController.getNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testCreateNeed() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);
        // when createNeed is called, return true simulating successful
        // creation and save
        when(mockRequestsDAO.createNeed(need)).thenReturn(need);

        // Invoke
        ResponseEntity<Need> response = requestsController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(need, response.getBody());
    }

    @Test
    public void testCreateNeedFailed() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need(1, "Toe Surgery", "SURGERY", 1, 2000);
        Need[] needArray = new Need[1];
        needArray[0] = need;

        // When findNeeds() is called, return an array of length 1 simulating that
        // A need with the name "Toe Surgery" has already been created, so the expected response is CONFLICT
        when(mockRequestsDAO.findNeeds(need.getName())).thenReturn(needArray);

        // Invoke
        ResponseEntity<Need> response = requestsController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    public void testCreateNeedHandleException() throws IOException {  // createNeed may throw IOException
        // Setup
        Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);

        // When createNeed is called on the Mock Cupboard DAO, throw an IOException
        doThrow(new IOException()).when(mockRequestsDAO).createNeed(need);

        // Invoke
        ResponseEntity<Need> response = requestsController.createNeed(need);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testUpdateNeed() throws IOException { // updateNeed may throw IOException
        // Setup
        Need need = new Need(1, "Open Chest Surgery", "SURGERY", 200, 1000);
        // when updateNeed is called, return true simulating successful
        // update and save
        when(mockRequestsDAO.updateNeed(need)).thenReturn(need);
        ResponseEntity<Need> response = requestsController.updateNeed(need);
        need.setName("Bolt");

        // Invoke
        response = requestsController.updateNeed(need);

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
        when(mockRequestsDAO.updateNeed(need)).thenReturn(null);

        // Invoke
        ResponseEntity<Need> response = requestsController.updateNeed(need);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testUpdateNeedHandleException() throws IOException { // updateNeed may throw IOException
        // Setup
        Need need = new Need(99, "Open Heart Surgery", "SURGERY", 200, 1000);
        // When updateNeed is called on the Mock Cupboard DAO, throw an IOException
        doThrow(new IOException()).when(mockRequestsDAO).updateNeed(need);

        // Invoke
        ResponseEntity<Need> response = requestsController.updateNeed(need);

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
        when(mockRequestsDAO.getNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need[]> response = requestsController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(needs,response.getBody());
    }

    @Test
    public void testGetNeedsHandleException() throws IOException {
        // Setup
        // When getNeeds is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockRequestsDAO).getNeeds();

        // Invoke
        ResponseEntity<Need[]> response = requestsController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void testDeleteNeed() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // when deleteNeed is called return true, simulating successful deletion
        when(mockRequestsDAO.deleteNeed(needId)).thenReturn(true);

        // Invoke
        ResponseEntity<Need> response = requestsController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void testDeleteNeedNotFound() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // when deleteNeed is called return false, simulating failed deletion
        when(mockRequestsDAO.deleteNeed(needId)).thenReturn(false);

        // Invoke
        ResponseEntity<Need> response = requestsController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void testDeleteNeedHandleException() throws IOException { // deleteNeed may throw IOException
        // Setup
        int needId = 99;
        // When deleteNeed is called on the Mock Need DAO, throw an IOException
        doThrow(new IOException()).when(mockRequestsDAO).deleteNeed(needId);

        // Invoke
        ResponseEntity<Need> response = requestsController.deleteNeed(needId);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }
}
