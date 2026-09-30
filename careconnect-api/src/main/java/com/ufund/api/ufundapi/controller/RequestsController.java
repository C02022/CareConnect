package com.ufund.api.ufundapi.controller;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.RequestsFileDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the REST API requests for the requests resource
 */
@RestController
@RequestMapping("requests")
public class RequestsController {
    private static final Logger LOG = Logger.getLogger(RequestsController.class.getName());
    private final RequestsFileDAO requestsDao;

    /**
     * Creates a REST API Controller for the UFund to respond to requested needs
     *
     * @param requestsDao DAO to access requested need data
     */
    public RequestsController(RequestsFileDAO requestsDao) {
        this.requestsDao = requestsDao;
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
        LOG.info("GET /requests/" + id);
        try {
            Need need = requestsDao.getNeed(id);
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
        LOG.info("GET /requests");
        try {
            Need[] requests = requestsDao.getNeeds();
            return new ResponseEntity<>(requests,HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
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
        LOG.info("POST /requests/ " + need);

        try {
            Need[] foundNeedsArray = requestsDao.findNeeds(need.getName());

            if (foundNeedsArray == null || foundNeedsArray.length == 0) {
                Need createdNeed = requestsDao.createNeed(need);
                return new ResponseEntity<>(createdNeed, HttpStatus.CREATED);
            } else {

                // If the need isn't identical to an existing one, add it. (Checks, quantity, cost, etc.)
                for (Need foundNeed : foundNeedsArray) {
                    if (foundNeed.equals(need)) return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                Need createdNeed = requestsDao.createNeed(need);
                return new ResponseEntity<>(createdNeed, HttpStatus.CREATED);
            }
        } catch (IOException e) {
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
        LOG.info("PUT /requests/ " + need);

        try {
            Need updatedNeed = requestsDao.updateNeed(need);
            if (updatedNeed != null) {
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
        LOG.info("DELETE /requests/" + id);

        try {
            boolean deleted = requestsDao.deleteNeed(id);

            // If successfully deleted, return OK. Otherwise, return NOT_FOUND
            if(deleted) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
