package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Implements the functionality for JSON file-based persistence for Needs that have been Funded
 */
@Component
public class FundedFileDAO extends NeedsDAO{

    /**
     * Creates a Needs Data Access Object
     *
     * @param filename     Filename to read from and write to
     * @param objectMapper Provides serialization and deserialization
     * @throws IOException if error occurs accessing the specified file
     */
    public FundedFileDAO(@Value("${funded.file}") String filename, ObjectMapper objectMapper) throws IOException {
        super(filename, objectMapper);
    }
}
