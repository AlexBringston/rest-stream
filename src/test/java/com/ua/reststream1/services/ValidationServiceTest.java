package com.ua.reststream1.services;

import com.ua.reststream1.models.FileContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidationServiceTest {

    @Mock
    private FileContent fileContentMock;

    @Spy
    private ValidationService validationService;

    @Test
    public void checkThatValidationWorksProperly() {
        ArrayList<String> headers = new ArrayList<>(
                Arrays.asList(
                        "Title", "Release Date", "Color/B&W", "Genre", "Language", "Country", "Rating", "Lead Actor",
                        "Director Name", "Lead Actor FB Likes", "Cast FB Likes", "Director FB Likes", "Movie FB Likes",
                        "IMDb Score (1-10)", "Total Reviews", "Duration (min)", "Gross Revenue", "Budget"
                ));
        ArrayList<String> contentRow = new ArrayList<>(
                Arrays.asList(
                        "Annie Get Your Gun", "4/2/1950", "Color", "Biography", "English", "USA",
                        "Passed", "Keenan Wynn", "George Sidney", "277", "731", "24", "456", "7", "21",
                        "107", "8000000", "3768785")
        );
        ArrayList<List<String>> content =
                new ArrayList<>(Collections.singletonList(contentRow));
        when(fileContentMock.getTransformedContent()).thenReturn(new ArrayList<>(Arrays.asList(headers,
                contentRow)));
        when(fileContentMock.getHeaders()).thenReturn(headers);
        when(fileContentMock.getContent()).thenReturn(content);
        assertDoesNotThrow(() -> validationService.validateFileContent(fileContentMock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkThatEmptyFileIsRejectedByValidator() {
        when(fileContentMock.getTransformedContent()).thenReturn(new ArrayList<>());
        validationService.validateFileContent(fileContentMock);
    }

    @Test(expected = IllegalStateException.class)
    public void checkThatFileWithOnlyHeaderRowIsRejected() {
        ArrayList<String> headers = new ArrayList<>(Arrays.asList("Title",
                "Release Date", "Color/B&W", "Genre", "Language", "Country", "Rating", "Lead Actor", "Director Name", "Lead " +
                        "Actor", "FB Likes", "Cast FB Likes", "Director FB Likes", "Movie FB Likes", "IMDb Score (1-10)",
                "Total Reviews", "Duration (min)", "Gross Revenue", "Budget"
        ));
        ArrayList<List<String>> content = new ArrayList<>(Collections.singletonList(new ArrayList<>()));
        when(fileContentMock.getTransformedContent()).thenReturn(new ArrayList<>(Arrays.asList(headers,
                new ArrayList<>())));
        when(fileContentMock.getHeaders()).thenReturn(headers);
        when(fileContentMock.getContent()).thenReturn(content);
        validationService.validateFileContent(fileContentMock);

    }

}