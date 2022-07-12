package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import com.ua.reststream1.models.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UploaderServiceTest {

    @Mock
    private ValidationService validationService;

    @Spy
    @InjectMocks
    private UploaderService uploaderService;

    @Before
    public void init() {
        Storage.files.clear();
    }
    @Test
    public void checkIfEmptyFileIsNotUploaded() {
        MultipartFile file = null;
        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Could not upload empty file on server");
        ResponseEntity<String> actual = uploaderService.uploadFileOnServer(file);
        assertEquals("Received unexpected response on null file", expected, actual);

        file = new MockMultipartFile("data", "info.csv", "text/plain", "".getBytes());
        actual = uploaderService.uploadFileOnServer(file);

        assertEquals("Received unexpected response on empty file", expected, actual);
    }

    @Test
    public void checkThatNameForAFileIsGeneratedProperly() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("data", "info.csv", "text/plain", "text".getBytes());
        uploaderService.uploadFileOnServer(mockMultipartFile);
        verify(uploaderService, times(1)).generateNameForFileOnServer();
    }

    @Test
    public void checkAddingSameFileSeveralTimesIsForbidden() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("data", "info.csv", "text/plain", "text".getBytes());
        HttpStatus actualStatus = uploaderService.uploadFileOnServer(mockMultipartFile).getStatusCode();
        assertEquals("Expected successful upload of file on server",
                HttpStatus.OK, actualStatus);
        mockMultipartFile = new MockMultipartFile("data", "info.csv", "text/plain", "text".getBytes());
        actualStatus = uploaderService.uploadFileOnServer(mockMultipartFile).getStatusCode();
        assertEquals("Expected second upload of the file on server to be forbidden",
                HttpStatus.NOT_ACCEPTABLE, actualStatus);
    }

    @Test
    public void checkValidationFailInUploader() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("data", "info.csv", "text/plain", "text".getBytes());
        doThrow(new IllegalStateException("Expected file to have a header row and at least one content row"))
                .when(validationService).validateFileContent(any(FileContent.class));
        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Expected file to have a header row and at least one content row");
        ResponseEntity<String> actual = uploaderService.uploadFileOnServer(mockMultipartFile);
        assertEquals("Expected upload to fail because of validator", expected, actual);
    }

    @Test
    public void checkIOFail() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doThrow(new IOException())
                .when(mockMultipartFile).getBytes();
        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Could not read file that was sent on server");
        ResponseEntity<String> actual = uploaderService.uploadFileOnServer(mockMultipartFile);
        assertEquals("Expected upload to fail because of IO error", expected, actual);
    }

    @Test
    public void checkSuccessfulUploadOfFileFromServer() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("data", "info.csv", "text/plain", "text".getBytes());
        when(uploaderService.generateNameForFileOnServer()).thenReturn("basicName");
        uploaderService.uploadFileOnServer(mockMultipartFile);
        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.OK)
                .body(Storage.files.get("basicName").getOriginalContent());
        ResponseEntity<String> actual = uploaderService.getFileFromServer("basicName");
        assertEquals("File received from server is not as expected", expected, actual);
    }

    @Test
    public void checkThatProgramWillNotReturnFileWithUnknownName() {
        ResponseEntity<String> expected = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("There is no file with such name stored on a server");
        ResponseEntity<String> actual = uploaderService.getFileFromServer("basicName");
        assertEquals("Expected file not to be found by given name", expected, actual);
    }
}