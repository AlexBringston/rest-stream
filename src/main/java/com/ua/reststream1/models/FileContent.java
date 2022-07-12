package com.ua.reststream1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileContent {
    private String originalContent;
    private List<List<String>> transformedContent;

    public List<String> getHeaders() {
        if (checkIfFileContentIsInvalid()) {
            throw new IllegalStateException("Expected to have file contents initialized, received nothing");
        }
        return transformedContent.get(0);
    }

    public List<List<String>> getContent() {
        if (checkIfFileContentIsInvalid()) {
            throw new IllegalStateException("Expected to have file contents initialized, received nothing");
        }
        return transformedContent.subList(1,transformedContent.size());
    }

    private boolean checkIfFileContentIsInvalid() {
        return originalContent == null || originalContent.isEmpty() || transformedContent.size() == 0;
    }
}
