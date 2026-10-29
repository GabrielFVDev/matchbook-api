package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.models.BookReactionModel;
import com.matchbook.matchbook.models.ReactionType;
import com.matchbook.matchbook.models.UserPreferencesModel;
import com.matchbook.matchbook.repositories.BookReactionRepository;
import com.matchbook.matchbook.repositories.UserPreferencesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final BookService bookService;
    private final UserPreferencesRepository userPreferencesRepository;
    private final BookReactionRepository bookReactionRepository;

    public RecommendationService(BookService bookService,
                                 UserPreferencesRepository userPreferencesRepository,
                                 BookReactionRepository bookReactionRepository) {
        this.bookService = bookService;
        this.userPreferencesRepository = userPreferencesRepository;
        this.bookReactionRepository = bookReactionRepository;
    }

    // Recommendations only draw from the books the user can see (see BookService)
    public List<BookModel> getUserRecommendations(String firebaseUid) {
        UserPreferencesModel preferences = findPreferences(firebaseUid);
        List<BookModel> unseenBooks = filterUnseen(bookService.getVisibleBooks(firebaseUid),
                bookReactionRepository.findByFirebaseUid(firebaseUid));

        return calculateRecommendations(preferences, unseenBooks);
    }

    public List<BookModel> getAdaptiveRecommendations(String firebaseUid) {
        UserPreferencesModel preferences = findPreferences(firebaseUid);
        List<BookModel> allBooks = bookService.getVisibleBooks(firebaseUid);
        List<BookReactionModel> reactions = bookReactionRepository.findByFirebaseUid(firebaseUid);

        Map<String, Double> learnedPreferences = analyzePatterns(reactions, allBooks);
        String strategy = determineStrategy(reactions);
        List<BookModel> unseenBooks = filterUnseen(allBooks, reactions);

        return switch (strategy) {
            case "EXPLORATORY" -> applyExploratoryAlgorithm(unseenBooks);
            case "REFINED" -> applyRefinedAlgorithm(preferences, unseenBooks, learnedPreferences);
            case "DIVERSIFIED" -> applyDiversifiedAlgorithm(preferences, unseenBooks, learnedPreferences);
            default -> calculateRecommendations(preferences, unseenBooks);
        };
    }

    public Map<String, Object> rejectBookWithAnalysis(String firebaseUid, Integer bookId) {
        saveReaction(firebaseUid, bookId, ReactionType.DISLIKED);

        // Analyzes the rejection pattern
        long totalRejections = bookReactionRepository.countByFirebaseUidAndType(firebaseUid, ReactionType.DISLIKED);

        Map<String, Object> result = new HashMap<>();
        result.put("totalRejections", totalRejections);
        result.put("algorithmChanged", totalRejections >= 5);
        // The suggestion is shown on screen as is, so its text stays in Portuguese
        result.put("suggestion", totalRejections >= 5 ?
                "Algoritmo será mais exploratório" : "Algoritmo mantém foco nas preferências");

        return result;
    }

    public void acceptBook(String firebaseUid, Integer bookId) {
        saveReaction(firebaseUid, bookId, ReactionType.LIKED);
    }

    public Map<String, Object> getRecommendationStatistics(String firebaseUid) {
        List<BookReactionModel> reactions = bookReactionRepository.findByFirebaseUid(firebaseUid);
        long totalAcceptances = count(reactions, ReactionType.LIKED);
        long totalRejections = count(reactions, ReactionType.DISLIKED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRejections", totalRejections);
        stats.put("totalAcceptances", totalAcceptances);
        stats.put("acceptanceRate", totalAcceptances > 0 ?
                (double) totalAcceptances / (totalAcceptances + totalRejections) * 100 : 0);
        stats.put("currentStrategy", determineStrategy(reactions));

        return stats;
    }

    // Resets the history
    @Transactional
    public void resetUserHistory(String firebaseUid) {
        bookReactionRepository.deleteByFirebaseUid(firebaseUid);
    }

    // Private helper methods

    // Without saved preferences the user still gets recommendations, just without the preference weight
    private UserPreferencesModel findPreferences(String firebaseUid) {
        return userPreferencesRepository.findById(firebaseUid).orElseGet(UserPreferencesModel::new);
    }

    // Reacting again to the same book replaces the previous reaction.
    // Only books the user can see: otherwise someone else's book would leak into "My books".
    private void saveReaction(String firebaseUid, Integer bookId, ReactionType type) {
        bookService.getBook(firebaseUid, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        BookReactionModel reaction = bookReactionRepository.findByFirebaseUidAndBookId(firebaseUid, bookId)
                .orElseGet(BookReactionModel::new);
        reaction.setFirebaseUid(firebaseUid);
        reaction.setBookId(bookId);
        reaction.setType(type);
        reaction.setReactedAt(LocalDateTime.now());
        bookReactionRepository.save(reaction);
    }

    private List<BookModel> filterUnseen(List<BookModel> books, List<BookReactionModel> reactions) {
        Set<Integer> seenIds = reactions.stream().map(BookReactionModel::getBookId).collect(Collectors.toSet());
        return books.stream()
                .filter(book -> !seenIds.contains(book.getId()))
                .collect(Collectors.toList());
    }

    private long count(List<BookReactionModel> reactions, ReactionType type) {
        return reactions.stream().filter(reaction -> reaction.getType() == type).count();
    }

    private Map<String, Double> analyzePatterns(List<BookReactionModel> reactions, List<BookModel> allBooks) {
        Map<Integer, BookModel> booksById = allBooks.stream()
                .collect(Collectors.toMap(BookModel::getId, Function.identity()));

        Map<String, Integer> acceptedGenres = new HashMap<>();
        Map<String, Integer> rejectedGenres = new HashMap<>();

        for (BookReactionModel reaction : reactions) {
            BookModel book = booksById.get(reaction.getBookId());
            if (book == null || book.getGenre() == null) {
                continue;
            }
            Map<String, Integer> genres = reaction.getType() == ReactionType.LIKED ? acceptedGenres : rejectedGenres;
            genres.merge(book.getGenre(), 1, Integer::sum);
        }

        Map<String, Double> preferences = new HashMap<>();
        for (String genre : acceptedGenres.keySet()) {
            int acceptedCount = acceptedGenres.get(genre);
            int rejectedCount = rejectedGenres.getOrDefault(genre, 0);
            preferences.put(genre, (double) acceptedCount / (acceptedCount + rejectedCount));
        }

        return preferences;
    }

    private String determineStrategy(List<BookReactionModel> reactions) {
        long totalRejections = count(reactions, ReactionType.DISLIKED);
        long totalAcceptances = count(reactions, ReactionType.LIKED);

        if (totalRejections >= 10) return "EXPLORATORY";
        if (totalAcceptances >= 3) return "REFINED";
        if (totalRejections >= 5) return "DIVERSIFIED";
        return "DEFAULT";
    }

    private List<BookModel> applyExploratoryAlgorithm(List<BookModel> books) {
        Collections.shuffle(books);
        return books.stream().limit(10).collect(Collectors.toList());
    }

    private List<BookModel> applyRefinedAlgorithm(UserPreferencesModel preferences,
                                                  List<BookModel> books,
                                                  Map<String, Double> learnedPreferences) {
        // Focuses on the learned preferences
        return books.stream()
                .sorted(Comparator.comparingDouble((BookModel book) ->
                        calculateRefinedScore(preferences, book, learnedPreferences)).reversed())
                .collect(Collectors.toList());
    }

    private List<BookModel> applyDiversifiedAlgorithm(UserPreferencesModel preferences,
                                                      List<BookModel> books,
                                                      Map<String, Double> learnedPreferences) {
        // With nothing left to show, there are no genres to divide by
        if (books.isEmpty()) {
            return books;
        }

        // Mixes preferences with diversity
        Map<String, List<BookModel>> booksByGenre = books.stream()
                .collect(Collectors.groupingBy(b -> b.getGenre() != null ? b.getGenre() : "Other"));

        List<BookModel> result = new ArrayList<>();
        int perGenre = Math.max(1, 10 / booksByGenre.size());

        for (List<BookModel> genreBooks : booksByGenre.values()) {
            genreBooks.sort(Comparator.comparingDouble((BookModel book) ->
                    calculateRefinedScore(preferences, book, learnedPreferences)).reversed());
            result.addAll(genreBooks.stream().limit(perGenre).toList());
        }

        return result;
    }

    private double calculateRefinedScore(UserPreferencesModel preferences, BookModel book, Map<String, Double> learnedPreferences) {
        double score = calculateScore(preferences, book);

        // Adds the weight of the learned preferences
        if (book.getGenre() != null && learnedPreferences.containsKey(book.getGenre())) {
            score += learnedPreferences.get(book.getGenre()) * 0.5; // 50% influence of the learned preferences
        }

        return score;
    }

    private List<BookModel> calculateRecommendations(UserPreferencesModel preferences, List<BookModel> books) {
        return books.stream()
                .sorted(Comparator.comparingDouble((BookModel book) -> calculateScore(preferences, book)).reversed())
                .collect(Collectors.toList());
    }

    private double calculateScore(UserPreferencesModel preferences, BookModel book) {
        double score = 0.0;

        if (book.getGenre() != null && book.getGenre().equalsIgnoreCase(preferences.getFavoriteGenre())) {
            score += 0.4;
        }

        if (preferences.getFavoriteAuthor() != null && !preferences.getFavoriteAuthor().isEmpty() &&
                book.getAuthor() != null && book.getAuthor().equalsIgnoreCase(preferences.getFavoriteAuthor())) {
            score += 0.3;
        }

        score += calculateReadingLevelScore(preferences.getReadingLevel(), book.getPageCount());

        return score;
    }

    private double calculateReadingLevelScore(String readingLevel, int pageCount) {
        if (readingLevel == null) {
            return 0.0;
        }

        return switch (readingLevel.toLowerCase()) {
            case "beginner" -> pageCount <= 200 ? 0.3 : 0.0;
            case "intermediate" -> (pageCount > 200 && pageCount <= 500) ? 0.3 : 0.0;
            case "advanced" -> pageCount > 500 ? 0.3 : 0.0;
            default -> 0.0;
        };
    }
}
