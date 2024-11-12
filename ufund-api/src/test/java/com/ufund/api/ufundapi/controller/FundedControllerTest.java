package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.FundedFileDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Tag("Controller-tier")
public class FundedControllerTest {

    private FundedController fundedController;
    private FundedFileDAO mockFundedDAO;

    /**
     * Before each test, create a new UFundController object and inject
     * a mock CupboardDAO
     */
    @BeforeEach
    public void setupCupboardController() {
        mockFundedDAO = mock(FundedFileDAO.class);
        fundedController = new FundedController(mockFundedDAO);
    }

    @Test
    public void testGetNeeds() throws IOException {
        // Setup
        Need[] needs = new Need[2];
        needs[0] = new Need(99, "testNeedOne", "testTypeOne", 1, 1);
        needs[1] = new Need(100, "testNeedTwo", "testTypeTwo", 2, 2);
        // When getNeeds is called return the needs created above
        when(mockFundedDAO.getNeeds()).thenReturn(needs);

        // Invoke
        ResponseEntity<Need[]> response = fundedController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(needs,response.getBody());
    }

    @Test
    public void testGetNeedsHandleException() throws IOException {
        // Setup
        // When getNeeds is called on the Mock CupboardDAO, throw an IOException
        doThrow(new IOException()).when(mockFundedDAO).getNeeds();

        // Invoke
        ResponseEntity<Need[]> response = fundedController.getNeeds();

        // Analyze
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }
}
