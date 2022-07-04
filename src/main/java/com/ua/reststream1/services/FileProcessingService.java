package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import com.ua.reststream1.models.Storage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

@Service
public class FileProcessingService {

    public ResponseEntity<String> countMaxValueInColumn(String fileName, String columnName) {
        try {
            FileContent file = Storage.files.get(fileName);
            int index = file.getHeaders().indexOf(columnName);
            double max =
                    getStreamOfNumbersInColumn(file, index)
                            .max()
                            .orElseThrow(NoSuchElementException::new);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.valueOf(max));
        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Could not count max value in column: " + columnName);
        }
    }

    public ResponseEntity<String> countSumOfColumn(String fileName, String columnName) {
        try {
            FileContent file = Storage.files.get(fileName);
            int index = file.getHeaders().indexOf(columnName);
            double sum =
                    getStreamOfNumbersInColumn(file, index)
                            .sum();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.valueOf(sum));

        } catch (NoSuchElementException exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Could not count sum of column: " + columnName);
        }
    }

    private DoubleStream getStreamOfNumbersInColumn(FileContent fileContent, int index) {
        return fileContent.getContent().stream()
                .filter(row -> !(row.get(index) == null || row.get(index).isEmpty()))
                .mapToDouble(row -> Double.parseDouble(row.get(index)));
    }

}
