package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Every route acts on the logged-in user: the "sub" of the Firebase token.
@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BookModel>> getAllBooks(@AuthenticationPrincipal Jwt token) {
        List<BookModel> books = bookService.getVisibleBooks(token.getSubject());
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<BookModel> getBook(@AuthenticationPrincipal Jwt token, @RequestParam Integer id) {
        return ResponseEntity.of(bookService.getBook(token.getSubject(), id));
    }

    @PostMapping("/create")
    public ResponseEntity<BookModel> createBook(@AuthenticationPrincipal Jwt token, @RequestBody BookModel book) {
        return ResponseEntity.ok(bookService.createBook(token.getSubject(), book));
    }

    // 403 when the book is not the user's (shared books included), 404 when the user cannot see it
    @PutMapping("/update")
    public ResponseEntity<BookModel> updateBook(@AuthenticationPrincipal Jwt token, @RequestBody BookModel book) {
        return ResponseEntity.ok(bookService.updateBook(token.getSubject(), book));
    }

    // Same rule as update
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBook(@AuthenticationPrincipal Jwt token, @RequestParam Integer id) {
        bookService.deleteBook(token.getSubject(), id);
        return ResponseEntity.noContent().build();
    }

    // "My books". Liking a book is POST /api/recommendation/accept.
    @GetMapping("/liked")
    public ResponseEntity<List<BookModel>> getLikedBooks(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(bookService.getLikedBooks(token.getSubject()));
    }

    @GetMapping("/liked/count")
    public ResponseEntity<Map<String, Long>> countLikedBooks(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(Map.of("total", bookService.countLikedBooks(token.getSubject())));
    }

    @DeleteMapping("/liked")
    public ResponseEntity<Void> unlikeBook(@AuthenticationPrincipal Jwt token, @RequestParam Integer bookId) {
        if (bookService.unlikeBook(token.getSubject(), bookId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
