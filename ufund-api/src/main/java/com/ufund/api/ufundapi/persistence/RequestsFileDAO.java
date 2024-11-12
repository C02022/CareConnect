package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Implements the functionality for JSON file-based persistence for Requested Needs
 */
@Component
public class RequestsFileDAO extends NeedsDAO{

    /**
     * Creates a NeedsDAO object for interacting with the requests file
     *
     * @param filename Filename of the requests, stored in application.properties
     * @param objectMapper Injected ObjectMapper used to write/read to/from JSON files
     * @throws IOException if error occurs accessing the specified file
     */
    public RequestsFileDAO(@Value("${requests.file}") String filename, ObjectMapper objectMapper) throws IOException {
        super(filename, objectMapper);
    }

}
