package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class ValidationService {

    public void validateFileContent(FileContent file)
            throws IllegalStateException, FileNotFoundException {

        int numOfHeaders = file.getHeaders().size();
        if (file.getTransformedContent().size() <= 1) {
            throw new IllegalArgumentException("Expected file to have a header row and at least one content row");
        }
        if (file.getContent().stream().filter(row -> row.size() == numOfHeaders).count() != file.getContent().size()) {
            throw new IllegalStateException("Number of rows in a file is inconsistent");
        }

    }
}
