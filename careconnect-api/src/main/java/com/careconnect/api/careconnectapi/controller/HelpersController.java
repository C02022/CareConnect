package com.careconnect.api.careconnectapi.controller;

import com.careconnect.api.careconnectapi.model.Helper;
import com.careconnect.api.careconnectapi.model.Need;
import com.careconnect.api.careconnectapi.persistence.CupboardFileDAO;
import com.careconnect.api.careconnectapi.persistence.FundedFileDAO;
import com.careconnect.api.careconnectapi.persistence.HelpersDAO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("helpers")
public class HelpersController {
    private static final Logger LOG = Logger.getLogger(HelpersController.class.getName());
    private final HelpersDAO helperDao;
    private final CupboardFileDAO cupboardDao;
    private final FundedFileDAO fundedDao;

    /**
     * Creates a REST API Controller for the UFund to respond to helper requests
     *
     * @param helperDao DAO to access helper data and perform CRUD operations
     * @param cupboardDao DAO to access cupboard data and perform CRUD operations
     */
    public HelpersController(HelpersDAO helperDao, CupboardFileDAO cupboardDao, FundedFileDAO fundedDao) {
        this.helperDao = helperDao;
        this.cupboardDao = cupboardDao;
        this.fundedDao = fundedDao;
    }

    /**
     * Responds to the GET request for all {@linkplain Helper helper}s
     *
     * @return ResponseEntity with {@link Helper helper} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/all")
    public ResponseEntity<Helper[]> getHelpers() {
        LOG.info("GET /helpers/all");

        try {
            Helper[] helpers = helperDao.getHelpers();
            if (helpers != null) {
                return new ResponseEntity<>(helpers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for a {@linkplain Helper helper} for the given username
     *
     * @param username The username used to locate the {@link Helper helper}
     *
     * @return ResponseEntity with {@link Helper helper} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("")
    public ResponseEntity<Helper> getHelper(@RequestParam String username) {
        LOG.info("GET /helpers?username=" + username);

        try {
            Helper helper = helperDao.getHelper(username);
            if (helper != null) {
                return new ResponseEntity<>(helper, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a {@linkplain Helper helper} with the provided helper object
     *
     * @param helper - The {@link Helper helper} to create
     *
     * @return ResponseEntity with created {@link Helper helper} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of CONFLICT if {@link Helper helper} object already exists<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Helper> createAccount(@RequestBody Helper helper) {
        LOG.info("POST /helpers/ " + helper);

        try {
            Helper createdHelper = helperDao.createAccount(helper.getUsername(), helper.getPassword());

            // If Helper already exists in helpers file, return CONFLICT status with custom header indicating so
            if(createdHelper == null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("User-Already-Exists", "1");
                return new ResponseEntity<>(headers, HttpStatus.CONFLICT);

            // Otherwise, account was created, return helper object
            } else {
                return new ResponseEntity<>(createdHelper, HttpStatus.CREATED);
            }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method used to authenticate a login attempt from a helper/admin on the frontend webpage.
     *
     * @param username Entered username
     * @param password Entered password
     * @return {@link Helper helper} account with OK if it exists and the credentials are correct
     * NOT_FOUND if the helper account does not exist,
     * and FORBIDDEN if the credentials are incorrect.
     */
    @GetMapping("/login")
    public ResponseEntity<Boolean> authenticateCredentials(@RequestParam String username, @RequestParam String password) {
        LOG.info("AUTHENTICATE /helpers/login?username=" + username + "&password=" + password);

        try{
            // Get helper account
            Helper selectedHelper = helperDao.getHelper(username);

            // If account doesn't exist, return NOT_FOUND
            if(selectedHelper == null) return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);

            // If password is correct, return Helper object with OK
            if(selectedHelper.getPassword().equals(password)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }

            // Otherwise, if password is incorrect return FORBIDDEN message
            return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a {@linkplain Need need} to a {@linkplain Helper helper}'s funding basket
     *
     * @param username Username of the specified helper who wants to add to their basket
     * @param id ID of Need item to be added
     *
     * @return ResponseEntity with updated array of {@link Need need} objects and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of NOT_FOUND if {@link Helper helper} does not exist<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping ("/add")
    public ResponseEntity<Need[]> addNeed(@RequestParam String username, @RequestParam int id) {
        LOG.info("GET NEED /helpers/add?username=" + username + "&id=" + id);
        try {

            // If need doesn't exist in cupboard, return 404
            Need currentNeed = cupboardDao.getNeed(id);
            if (currentNeed == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // If need wasn't able to be added, helper DNE return 404
            Need[] currentFundingBasket = helperDao.addNeed(username, currentNeed);
            if (currentFundingBasket == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Otherwise, return current funding basket
            return new ResponseEntity<>(currentFundingBasket, HttpStatus.CREATED);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a {@linkplain Need need} from a {@linkplain Helper helper}'s funding basket
     *
     * @param username Username of the specified helper who wants to remove from their basket
     * @param id Need ID to be removed
     *
     * @return ResponseEntity with updated array of {@link Need need} objects and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if {@link Helper helper} does not exist<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Need[]> removeNeed(@RequestParam String username, @RequestParam int id) {
        LOG.info("DELETE NEED /helpers/remove?username=" + username + "&id=" + id);
        try {

            // Remove need. If funding basket is null, user DNE return NOT_FOUND
            Need[] newFundingBasket = helperDao.removeNeed(username, id);
            if(newFundingBasket == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            // Return new funding basket after the remove
            return new ResponseEntity<>(newFundingBasket, HttpStatus.OK);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes all {@linkplain Need need} objects from a {@linkplain Helper helper}'s funding basket,
     * simulating a checkout. Removes those needs from the cupboard as well, so access to CupboardDAO is required.
     *
     * @param username Username of the specified helper who wants to check out their basket
     *
     * @return ResponseEntity with updated array of {@link Need need} objects and HTTP status of OK<br>
     * ResponseEntity with HTTP status of NOT_FOUND if {@link Helper helper} does not exist<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/checkout")
    public ResponseEntity<Need[]> checkout(@RequestParam String username) {
        LOG.info("CHECKOUT /helpers/checkout?username=" + username);
        try {

            // If helper doesn't exist or has no needs in basket, return 404
            Need[] checkedOutNeeds = helperDao.checkoutNeeds(username);
            if (checkedOutNeeds == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // For each need that the helper funds, remove that need from the cupboard
            for(Need need : checkedOutNeeds) {
                cupboardDao.deleteNeed(need.getId());
                fundedDao.createNeed(need);
            }

            return new ResponseEntity<>(checkedOutNeeds, HttpStatus.OK);
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
