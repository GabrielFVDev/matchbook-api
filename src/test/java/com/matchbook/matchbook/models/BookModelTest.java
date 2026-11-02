package com.matchbook.matchbook.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class BookModelTest {

    // The add-book form sends null when the year or the page count is left blank.
    @Test
    void keepsBlankYearAndPageCountEmpty() throws Exception {
        BookModel book = new ObjectMapper().readValue(
                "{\"title\": \"Duna\", \"publicationYear\": null, \"pageCount\": null}", BookModel.class);

        assertNull(book.getPublicationYear());
        assertNull(book.getPageCount());
    }
}
