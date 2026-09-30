package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Helper;
import com.ufund.api.ufundapi.model.Need;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for HelperFileDAOTest
 */
@Tag("Persistence-tier")
public class HelpersFileDAOTest {

    HelpersFileDAO helpersFileDAO;
    Helper[] testHelpers;
    Need[] testNeeds;
    ObjectMapper mockObjectMapper;

    /**
     * Before each test, we will create test needs & helpers array and mock objectMapper injection
     *
     * @throws IOException if error occurs during write
     */
    @BeforeEach
    public void setupHelperFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);

        testNeeds = new Need[3];
        testNeeds[0] = new Need(99, "testOne", "testTypeOne", 1, 1);
        testNeeds[1] = new Need(100, "testTwo", "testTypeTwo", 2, 2);
        testNeeds[2] = new Need(101, "testThree", "testTypeThree", 69, 69);

        testHelpers = new Helper[3];
        testHelpers[0] = new Helper("testOne", "testOne");
        testHelpers[1] = new Helper("testTwo", "testTwo");
        testHelpers[2] = new Helper("testThree", "testThree");
        testHelpers[0].addNeed(testNeeds[0]);

        // When the object mapper is supposed to read from the file
        // the mock object mapper will return the need array above
        when(mockObjectMapper.readValue(new File("data/helpers.json"), Helper[].class)).thenReturn(testHelpers);
        when(mockObjectMapper.readValue(new File("data/cupboard.json"), Need[].class)).thenReturn(testNeeds);
        helpersFileDAO = new HelpersFileDAO("data/helpers.json", mockObjectMapper);
    }

    @Test
    public void testGetHelper() {
        // Invoke
        Helper helper;

        try {
            helper = helpersFileDAO.getHelper("testOne");
        } catch (IOException e) {
            helper = null;
        }

        // Analyze
        assertEquals(helper.getUsername(), testHelpers[0].getUsername());
        assertEquals(helper.getPassword(), testHelpers[0].getPassword());
    }

    @Test
    public void testGetHelperDNE() {
        // Invoke
        Helper helper;

        try {
            helper = helpersFileDAO.getHelper("DNE");
        } catch (IOException e) {
            helper = null;
        }

        // Analyze
        assertNull(helper);
    }

    @Test
    public void testGetFundingBasket() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.getFundingBasket("testOne");
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertEquals(1, fundingBasket.length);
        assertEquals("testOne", fundingBasket[0].getName());
    }

    @Test
    public void testGetFundingBasketDNE() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.getFundingBasket("fdsa");
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertNull(fundingBasket, "fundingBasket should be null");
    }

    @Test
    public void testAddNeed() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.addNeed("testTwo", new Need(99, "testTwo", "testTypeTwo", 1, 1));
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertEquals(1, fundingBasket.length);
        assertEquals("testTwo", fundingBasket[0].getName());
    }

    @Test
    public void testAddNeedDNE() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.addNeed("DNE", new Need(99, "testTwo", "testTypeTwo", 1, 1));
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertNull(fundingBasket, "fundingBasket should be null");
    }

    @Test
    public void testRemoveNeed() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.removeNeed("testOne", helpersFileDAO.getFundingBasket("testOne")[0].getId());
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertEquals(0, fundingBasket.length);
    }

    @Test
    public void testRemoveNeedDNE() {
        // Invoke
        Need[] fundingBasket;

        try {
            fundingBasket = helpersFileDAO.removeNeed("testOne", 1);
        } catch (IOException e) {
            fundingBasket = null;
        }

        // Analyze
        assertEquals(1, fundingBasket.length);
    }

    @Test
    public void testCheckoutNeeds() {
        // Invoke
        Helper helper;
        Need[] checkedOutNeeds;

        try {
            helper = helpersFileDAO.getHelper("testOne");
            checkedOutNeeds = helpersFileDAO.checkoutNeeds("testOne");
        } catch (IOException e) {
            helper = null;
            checkedOutNeeds = null;
        }

        // Analyze
        assertEquals(1, checkedOutNeeds.length);
        assertEquals(0, helper.getFundingBasket().length);
    }

    @Test
    public void testCheckoutNeedsDNE() {
        // Invoke
        Helper helper;
        Need[] checkedOutNeeds;

        try {
            helper = helpersFileDAO.getHelper("DNE");
            checkedOutNeeds = helpersFileDAO.checkoutNeeds("DNE");
        } catch (IOException e) {
            helper = null;
            checkedOutNeeds = null;
        }

        // Analyze
        assertNull(helper);
        assertNull(checkedOutNeeds, "checkedOutNeeds should be null");
    }

    @Test
    public void testCreateAccount() {
        // Invoke
        Helper createdHelper;
        Helper retrievedHelper;

        try {
            createdHelper = helpersFileDAO.createAccount("newUser", "newPassword");

            // Confirms the helper was added to the map
            retrievedHelper = helpersFileDAO.getHelper("newUser");
        } catch (IOException e) {
            createdHelper = null;
            retrievedHelper = null;
        }

        // Analyze
        assertEquals(createdHelper.getUsername(), retrievedHelper.getUsername());
    }

    @Test
    public void testCreateAccountExists() {
        // Invoke
        Helper createdHelper;

        try {
            createdHelper = helpersFileDAO.createAccount("testOne", "newPassword");

        } catch (IOException e) {
            createdHelper = null;
        }

        // Analyze
        assertNull(createdHelper);
    }

    @Test
    public void testNeedUpdatedOrDeleted() {
        // Invoke
        Need[] fundingBasket1;
        Need[] fundingBasket2;

        // Add needs to both test helpers
        try {
            fundingBasket1 = helpersFileDAO.addNeed("testOne", testNeeds[0]);
            fundingBasket2 = helpersFileDAO.addNeed("testTwo", testNeeds[1]);
        } catch (IOException e) {
            fundingBasket1 = null;
            fundingBasket2 = null;
        }

        // Analyze - make sure both were added correctly
        assertEquals(1, fundingBasket1.length);
        assertEquals("testOne", fundingBasket1[0].getName());

        assertEquals(1, fundingBasket2.length);
        assertEquals("testTwo", fundingBasket2[0].getName());

        // Next, we have update one need with CupboardDAO and delete the other with CupboardDAO
        try {
            helpersFileDAO.needUpdatedOrDeleted(testNeeds[0].getId());
            helpersFileDAO.needUpdatedOrDeleted(testNeeds[1].getId());
            fundingBasket1 = helpersFileDAO.getFundingBasket("testOne");
            fundingBasket2 = helpersFileDAO.getFundingBasket("testTwo");
        } catch (IOException e) { }

        // Analyze - make sure they were deleted from both funding baskets
        assertEquals(0, fundingBasket1.length);
        assertEquals(0, fundingBasket2.length);
    }
}