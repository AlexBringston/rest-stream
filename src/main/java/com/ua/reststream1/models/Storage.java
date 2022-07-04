package com.ua.reststream1.models;

import lombok.Data;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class Storage {
    public static final Map<String, FileContent> files = new HashMap<>();

    public static List<List<String>> transformFile(String content) throws FileNotFoundException {

        if (content == null) {
            throw new FileNotFoundException("Could not transform empty file content");
        }
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
