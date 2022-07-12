package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileProcessingServiceTest {


    @Spy
    private FileProcessingService fileProcessingService;

    @Mock
    private FileContent fileContent;

    @Mock
    private List<String> headers;

    @Mock
    private List<List<String>> content;

    @Before
    public void init() {
        doReturn(fileContent).when(fileProcessingService).getFileContent(anyString());
        when(fileContent.getHeaders()).thenReturn(headers);
        when(fileContent.getContent()).thenReturn(content);
        when(headers.indexOf(anyString())).thenReturn(1);
    }

    @Test
    public void checkCountMaxValueProperWork() {

        doReturn(DoubleStream.generate(() -> new Random().nextInt(100)).limit(100))
                .when(fileProcessingService).getStreamOfNumbersInColumn(fileContent, 1);

        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = fileProcessingService
                .countMaxValueInColumn("test", "test")
                .getStatusCode();

        assertEquals("Expected operation to be performed successfully", expected, actual);

    }

    @Test
    public void checkErrorHandlingWhileCountingMaxValueInColumnWithNaN() {
        doThrow(new NumberFormatException())
                .when(fileProcessingService).getStreamOfNumbersInColumn(fileContent, 1);

        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Column: test contains NaN values");
        ResponseEntity<String> actual = fileProcessingService
                .countMaxValueInColumn("test", "test");

        assertEquals("Expected operation to fail", expected, actual);
    }

    @Test
    public void checkErrorHandlingWhenNoMaxElementCanBeFound() {
        doReturn(DoubleStream.empty())
                .when(fileProcessingService).getStreamOfNumbersInColumn(fileContent, 1);

        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Could not count max value in column: test");
        ResponseEntity<String> actual = fileProcessingService
                .countMaxValueInColumn("test", "test");

        assertEquals("Expected operation to fail", expected, actual);
    }

    @Test
    public void checkCountSumOfColumnProperWork() {

        doReturn(DoubleStream.of(1.0, 2.0, 3.0, 4, 5, 6, 7, 8, 9, 10))
                .when(fileProcessingService).getStreamOfNumbersInColumn(fileContent, 1);

        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.OK)
                .body(String.valueOf(55.0));
        ResponseEntity<String> actual = fileProcessingService
                .countSumOfColumn("test", "test");

        assertEquals("Expected operation to be performed successfully", expected, actual);

    }

    @Test
    public void checkCountSumWhenNanValueIsPresentInColumn() {
        doThrow(new NumberFormatException())
                .when(fileProcessingService).getStreamOfNumbersInColumn(fileContent, 1);

        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Column: test contains NaN values");
        ResponseEntity<String> actual = fileProcessingService
                .countSumOfColumn("test", "test");

        assertEquals("Expected operation to fail", expected, actual);
    }

}