package com.ua.reststream1.services;

import com.ua.reststream1.Storage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class FileService {

    public ResponseEntity<String> uploadFileOnServer(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not upload empty file on server");
        } else {
            try {
                Storage.fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
                return ResponseEntity.status(HttpStatus.OK).body("Successfully uploaded file on server");
            } catch (IOException exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not read uploaded file");
            }
        }
    }

    public ResponseEntity<String> countMaxValueInColumn(String columnName) {
        try {
            List<List<String>> content = transformFile();
            int index = content.get(0).indexOf(columnName);
            double max =
                    getStreamOfNumbersInColumn(content, index)
                            .max().orElseThrow(NoSuchElementException::new);
            return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(max));
        } catch (IOException | NoSuchElementException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not count max value in column: " + columnName);
        }
    }

    public ResponseEntity<String> countSumOfColumn(String columnName) {
        try {
            List<List<String>> content = transformFile();
            int index = content.get(0).indexOf(columnName);
            double sum =
                    getStreamOfNumbersInColumn(content, index)
                            .sum();
            return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(sum));
        } catch (IOException | NoSuchElementException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not count sum of column: " + columnName);
        }
    }

    private DoubleStream getStreamOfNumbersInColumn(List<List<String>> content, int index) {
        return content.subList(1, content.size()).stream()
                .filter(row -> !(row.get(index) == null || row.get(index).isEmpty()))
                .mapToDouble(row -> Double.parseDouble(row.get(index)));
//                .map(num -> num == (int)num ? (int) num : num);
    }

    private List<List<String>> transformFile() throws FileNotFoundException {
        if (Storage.fileContent == null) {
            throw new FileNotFoundException("File was not uploaded on server");
        }
        String content = Storage.fileContent;
        String[] lines = content.split("\n");
        Pattern pattern = Pattern.compile("[^;]*?(?=;|$)");
        return Arrays.stream(lines).map(line -> {
            Matcher matcher = pattern.matcher(line);
            List<String> words = new ArrayList<>();
            while (matcher.find()) {
                words.add(matcher.group());
            }
            return words;
        }).collect(Collectors.toList());

    }
}
