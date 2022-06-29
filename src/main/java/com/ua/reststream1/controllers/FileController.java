package com.ua.reststream1.controllers;

import com.ua.reststream1.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/getMaxValue")
    public ResponseEntity<String> getMaxValue(@RequestParam String columnName) {
        return fileService.countMaxValueInColumn(columnName);
    }

    @GetMapping("/getSum")
    public ResponseEntity<String> getSum(@RequestParam String columnName) {
        return fileService.countSumOfColumn(columnName);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFileOnServer(file);
    }
}
