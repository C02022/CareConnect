package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.FundedFileDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/funded")
public class FundedController {
    private static final Logger LOG = Logger.getLogger(FundedController.class.getName());
    private final FundedFileDAO fundedDao;

    /**
     * Creates a REST API Controller for the UFund to respond to helper requests
     *
     * @param fundedDao DAO to access funded needs data
     */
    public FundedController(FundedFileDAO fundedDao) {
        this.fundedDao = fundedDao;
    }

    /**
     * Responds to the GET request for the entire {@linkplain Need funded needs}
     *
     * @return ResponseEntity with array of {@link Need need} objects (may be empty) and
     * HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("")
    public ResponseEntity<Need[]> getNeeds() {
        LOG.info("GET /funded");
        try {
            Need[] funded = fundedDao.getNeeds();
            return new ResponseEntity<>(funded, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
