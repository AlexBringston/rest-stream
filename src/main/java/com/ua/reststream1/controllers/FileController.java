package com.ua.reststream1.controllers;

import com.ua.reststream1.services.FileProcessingService;
import com.ua.reststream1.services.UploaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private final UploaderService uploaderService;
    private final FileProcessingService fileProcessingService;

    public FileController(UploaderService uploaderService, FileProcessingService fileProcessingService) {
        this.uploaderService = uploaderService;
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return uploaderService.uploadFileOnServer(file);
    }

    @GetMapping("/fileByName")
    public ResponseEntity<String> getFileFromServer(@RequestParam String fileName) {
        return uploaderService.getFileFromServer(fileName);
    }

    @GetMapping("/maxValueInColumn")
    public ResponseEntity<String> getMaxValue(@RequestParam String fileName, @RequestParam String columnName) {
        return fileProcessingService.countMaxValueInColumn(fileName, columnName);
    }

    @GetMapping("/sumOfColumnValues")
    public ResponseEntity<String> getSum(@RequestParam String fileName, @RequestParam String columnName) {
        return fileProcessingService.countSumOfColumn(fileName, columnName);
    }
}
