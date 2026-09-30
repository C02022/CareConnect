package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.persistence.CupboardFileDAO;
import com.ufund.api.ufundapi.persistence.HelpersDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ufund.api.ufundapi.model.Need;

/**
 * Handles the REST API requests for the Cupboard resource
 */

@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private final CupboardFileDAO cupboardDao;
    private final HelpersDAO helperDao;

    /**
     * Creates a REST API Controller for the UFund to respond to cupboard requests
     * 
     * @param cupboardDao DAO to access data and perform CRUD operations
     * @param helperDao DAO to access helper data and update funding baskets in certain cases
     */
    public CupboardController(CupboardFileDAO cupboardDao, HelpersDAO helperDao) {
        this.cupboardDao = cupboardDao;
        this.helperDao = helperDao;
    }

    /**
     * Responds to the GET request for a {@linkplain Need need} for the given id
     * 
     * @param id The id used to locate the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if found<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Need> getNeed(@PathVariable int id){
        LOG.info("GET /cupboard/" + id);
        try {
            Need need = cupboardDao.getNeed(id);
            if (need != null){
                return new ResponseEntity<>(need, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for the entire {@linkplain Need cupboard}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty) and
     * HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("")
    public ResponseEntity<Need[]> getNeeds() {
        LOG.info("GET /cupboard");
        try {
            Need[] cupboard = cupboardDao.getNeeds();
            return new ResponseEntity<>(cupboard,HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@linkplain Need needs in cupboard} whose name contains
     * the text in name
     * 
     * @param name The name parameter which contains the text used to find the {@link Need need}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty) and
     * HTTP status of OK<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     * <p>
     * Example: Find all needs that contain the text "ma"
     * GET http://localhost:8080/cupboard?name=ma
     */
    @GetMapping("/")
    public ResponseEntity<Need[]> searchNeeds(@RequestParam String name) {
        LOG.info("GET /cupboard/?name=" + name);

        try{
            Need[] needs = cupboardDao.findNeeds(name);
            if (needs != null) {
                return new ResponseEntity<>(needs, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a {@linkplain Need need} with the provided need object
     * 
     * @param need - The {@link Need need} to create
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of CONFLICT if {@link Need need} object already exists<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {
        LOG.info("POST /cupboard/ " + need);

        try {
            Need[] foundNeedsArray = cupboardDao.findNeeds(need.getName());

            if (foundNeedsArray == null || foundNeedsArray.length == 0) {
                Need createdNeed = cupboardDao.createNeed(need);
                return new ResponseEntity<>(createdNeed, HttpStatus.CREATED);
            }
            else {

                // If the need isn't identical to an existing one, add it. (Checks, quantity, cost, etc.)
                for(Need foundNeed : foundNeedsArray) {
                    if(foundNeed.equals(need)) return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                Need createdNeed = cupboardDao.createNeed(need);
                return new ResponseEntity<>(createdNeed, HttpStatus.CREATED);
            }
        } 
        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@linkplain Need need} with the provided {@linkplain Need need} object, if it exists
     * 
     * @param need The {@link Need need} to update
     * 
     * @return ResponseEntity with updated {@link Need need} object and HTTP status of OK if updated<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<Need> updateNeed(@RequestBody Need need) {
        LOG.info("PUT /cupboard/ " + need);

        try {
            Need updatedNeed = cupboardDao.updateNeed(need);
            if (updatedNeed != null) {
                helperDao.needUpdatedOrDeleted(need.getId());
                return new ResponseEntity<>(updatedNeed, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } 
        catch (IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} with the given id
     * 
     * @param id The id of the {@link Need need} to deleted
     * 
     * @return ResponseEntity HTTP status of OK if deleted<br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Need> deleteNeed(@PathVariable int id) {
        LOG.info("DELETE /cupboard/" + id);

        try {
            boolean deleted = cupboardDao.deleteNeed(id);
            
            // If successfully deleted, return OK. Otherwise, return NOT_FOUND
            if(deleted) {
                helperDao.needUpdatedOrDeleted(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
