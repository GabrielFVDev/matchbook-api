package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.models.UserPreferencesModel;
import com.matchbook.matchbook.repositories.BookReactionRepository;
import com.matchbook.matchbook.repositories.UserPreferencesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private static final String UID = "uid-ana";

    @Mock
    private BookService bookService;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private BookReactionRepository bookReactionRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    // A book whose page count was left blank is not a short book: it gets no reading-level weight.
    @Test
    void doesNotTreatBlankPageCountAsShortBook() {
        UserPreferencesModel preferences = new UserPreferencesModel();
        preferences.setFirebaseUid(UID);
        preferences.setReadingLevel("beginner");
        BookModel withoutPageCount = book(1, "Without page count");
        BookModel shortBook = book(2, "Short book");
        shortBook.setPageCount(150);

        when(userPreferencesRepository.findById(UID)).thenReturn(Optional.of(preferences));
        when(bookService.getVisibleBooks(UID)).thenReturn(List.of(withoutPageCount, shortBook));
        when(bookReactionRepository.findByFirebaseUid(UID)).thenReturn(List.of());

        List<BookModel> recommendations = recommendationService.getUserRecommendations(UID);

        assertEquals(List.of("Short book", "Without page count"),
                recommendations.stream().map(BookModel::getTitle).toList());
    }

    private static BookModel book(int id, String title) {
        BookModel book = new BookModel();
        book.setId(id);
        book.setTitle(title);
        return book;
    }
}
