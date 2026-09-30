package com.careconnect.api.careconnectapi.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Implements the functionality for JSON file-based persistence for Needs in the Cupboard
 */
 @Component
public class CupboardFileDAO extends NeedsDAO {

    /**
     * Creates a NeedsDAO object for interacting with the cupboard file
     *
     * @param filename Filename of the cupboard, stored in application.properties
     * @param objectMapper Injected ObjectMapper used to write/read to/from JSON files
     */
     public CupboardFileDAO(@Value("${cupboard.file}") String filename, ObjectMapper objectMapper) throws IOException {
        super(filename, objectMapper);
     }

}