package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import com.ua.reststream1.models.Storage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

@Service
public class UploaderService {

    private final ValidationService validationService;

    public UploaderService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public ResponseEntity<String> uploadFileOnServer(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Could not upload empty file on server");
        } else {
            try {
                String content = new String(file.getBytes(), StandardCharsets.UTF_8);
                List<List<String>> transformedContent = Storage.transformFile(content);

                FileContent fileContent = new FileContent(content, transformedContent);
                validationService.validateFileContent(fileContent);
                String localName = generateNameForFileOnServer();

                if (Storage.files.containsValue(fileContent)) {
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body("File is already uploaded on server");
                }
                Storage.files.put(localName, fileContent);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Successfully uploaded file on server under name: " + localName);
            } catch (IOException exception) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Could not read file that was sent on server");
            }
        }
    }

    public ResponseEntity<String> getFileFromServer(String fileName) {
        if (Storage.files.containsKey(fileName)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Storage.files.get(fileName).getOriginalContent());
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("There is no file with such name stored on a server");
        }
    }


    private String generateNameForFileOnServer () {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
