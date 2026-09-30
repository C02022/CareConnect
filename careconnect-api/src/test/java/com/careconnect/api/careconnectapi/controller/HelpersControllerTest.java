package com.careconnect.api.careconnectapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.careconnect.api.careconnectapi.persistence.CupboardFileDAO;
import com.careconnect.api.careconnectapi.model.Helper;
import com.careconnect.api.careconnectapi.model.Need;

import com.careconnect.api.careconnectapi.persistence.FundedFileDAO;
import com.careconnect.api.careconnectapi.persistence.HelpersDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Tag("Controller-tier")
public class HelpersControllerTest {
    private HelpersController helpersController;
    private HelpersDAO mockHelperDAO;
    private CupboardFileDAO mockCupboardDAO;
    private FundedFileDAO mockFundedDAO;

    @BeforeEach
    public void setupHelperController() {
        mockHelperDAO = mock(HelpersDAO.class);
        mockCupboardDAO = mock(CupboardFileDAO.class);
        mockFundedDAO = mock(FundedFileDAO.class);
        helpersController = new HelpersController(mockHelperDAO, mockCupboardDAO, mockFundedDAO);
    }

    @Test
    public void testGetHelpers() throws IOException {
        // Setup
        String testUserOne = "testUserOne";
        Helper helper = new Helper(testUserOne, "testPasswordOne");
        String testUserTwo = "testUserTwo";
        Helper helper2 = new Helper(testUserTwo, "testPasswordOne");
        Helper[] helpersArray = new Helper[2];
        helpersArray[0] = helper;
        helpersArray[1] = helper2;

        when(mockHelperDAO.getHelpers()).thenReturn(helpersArray);

        // Invoke
        ResponseEntity<Helper[]> response = helpersController.getHelpers();

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(helpersArray, response.getBody());
    }

    @Test
    public void testGetHelpersDNE() throws IOException {
        when(mockHelperDAO.getHelpers()).thenReturn(null);

        // Invoke
        ResponseEntity<Helper[]> response = helpersController.getHelpers();

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetHelpersHandleException() throws IOException {

        doThrow(new IOException()).when(mockHelperDAO).getHelpers();

        // Invoke
        ResponseEntity<Helper[]> response = helpersController.getHelpers();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetHelper() throws IOException {
        // Setup
        String testUserOne = "testUserOne";
        Helper helper = new Helper(testUserOne, "testPasswordOne");

        when(mockHelperDAO.getHelper(testUserOne)).thenReturn(helper);

        // Invoke
        ResponseEntity<Helper> response = helpersController.getHelper(testUserOne);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(helper, response.getBody());
    }

    @Test
    public void testGetHelperDNE() throws IOException {
        // Setup
        String testUserOne = "testUserOne";
        Helper helper = new Helper(testUserOne, "testPasswordOne");

        when(mockHelperDAO.getHelper(testUserOne)).thenReturn(null);

        // Invoke
        ResponseEntity<Helper> response = helpersController.getHelper(testUserOne);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetHelperHandleException() throws IOException {
        // Setup
        String testUserOne = "testUserOne";
        Helper helper = new Helper(testUserOne, "testPasswordOne");

        doThrow(new IOException()).when(mockHelperDAO).getHelper(testUserOne);

        // Invoke
        ResponseEntity<Helper> response = helpersController.getHelper(testUserOne);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCreateAccount() throws IOException {
        // Setup
        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        when(mockHelperDAO.createAccount(testUserName, testPassword)).thenReturn(testHelper);

        // Invoke
        ResponseEntity<Helper> response = helpersController.createAccount(testHelper);

        // Analyze
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testHelper, response.getBody());
    }

    @Test
    public void testCreateAccountAlreadyExists() throws IOException {
        // Setup
        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        when(mockHelperDAO.createAccount(testUserName, testPassword)).thenReturn(null);

        // Invoke
        ResponseEntity<Helper> response = helpersController.createAccount(testHelper);

        // Analyze
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateAccountHandleError() throws IOException {
        // Setup
        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        doThrow(new IOException()).when(mockHelperDAO).createAccount(testUserName, testPassword);

        // Invoke
        ResponseEntity<Helper> response = helpersController.createAccount(testHelper);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testValidCredentials() throws IOException {
        // Setup
        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);
        
        when(mockHelperDAO.getHelper(testUserName)).thenReturn(testHelper);
        
        // Invoke
        ResponseEntity<Boolean> response = helpersController.authenticateCredentials(testUserName, testPassword);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    public void testInvalidUsername() throws IOException {
        // Setup
        String testUserName = "test1";
        String testPassword = "pass";

        // Invoke
        ResponseEntity<Boolean> response = helpersController.authenticateCredentials(testUserName, testPassword);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    public void testInvalidPassword() throws IOException {
        // Setup
        String testUserName = "test";
        String testPassword = "pass";
        String incorrectPass = "notPass";
        Helper testHelper = new Helper(testUserName, testPassword);
        
        when(mockHelperDAO.getHelper(testUserName)).thenReturn(testHelper);

        // Invoke
        ResponseEntity<Boolean> response = helpersController.authenticateCredentials(testUserName, incorrectPass);

        // Analyze
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    public void testAddNeed() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[1];
        needArr[0] = need;
        
        String testUserName = "test1";
        String testPassword = "pass";

        when(mockCupboardDAO.getNeed(id)).thenReturn(need);
        when(mockHelperDAO.addNeed(testUserName, need)).thenReturn(needArr);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.addNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(needArr, response.getBody());
    }

    @Test
    public void testAddNeedNotInCupboard() throws IOException {
        // Setup
        int id = 121;

        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.addNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddNeedNotAdded() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[1];
        needArr[0] = need;

        String testUserName = "test1";
        String testPassword = "pass";

        when(mockCupboardDAO.getNeed(id)).thenReturn(need);
        when(mockHelperDAO.addNeed(testUserName, need)).thenReturn(null);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.addNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testAddNeedHandleException() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[1];
        needArr[0] = need;

        String testUserName = "test1";
        String testPassword = "pass";

        doThrow(new IOException()).when(mockCupboardDAO).getNeed(id);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.addNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRemoveNeed() throws IOException {
        // Setup 
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[0];
        
        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        when(mockHelperDAO.getHelper(testUserName)).thenReturn(testHelper);
        when(mockCupboardDAO.getNeed(id)).thenReturn(need);
        when(mockHelperDAO.removeNeed(testUserName, id)).thenReturn(needArr);
    
        // Invoke 
        ResponseEntity<Need[]> response = helpersController.removeNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(needArr, response.getBody());
    }

    @Test
    public void testRemoveNeedHandleException() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[0];

        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        doThrow(new IOException()).when(mockHelperDAO).removeNeed(testUserName, id);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.removeNeed(testUserName, id);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckout() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[1];
        needArr[0] = need;

        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        when(mockHelperDAO.checkoutNeeds(testUserName)).thenReturn(needArr);
        when(mockCupboardDAO.deleteNeed(id)).thenReturn(true);

        // Invoke 
        ResponseEntity<Need[]> response = helpersController.checkout(testUserName);

        // Analyze
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(needArr, response.getBody());
    }

    @Test
    public void testCheckoutHandleException() throws IOException {
        // Setup
        int id = 121;
        String name = "Walter White Chemotherapy";
        String type = "Chemotherapy";
        int quantity = 10;
        int cost = 10000;
        Need need = new Need(id, name, type, quantity, cost);
        Need[] needArr = new Need[1];
        needArr[0] = need;

        String testUserName = "test1";
        String testPassword = "pass";
        Helper testHelper = new Helper(testUserName, testPassword);

        doThrow(new IOException()).when(mockHelperDAO).checkoutNeeds(testUserName);

        // Invoke
        ResponseEntity<Need[]> response = helpersController.checkout(testUserName);

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
