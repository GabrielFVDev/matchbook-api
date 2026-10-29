package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BookModel>> getAllBooks() {
        List<BookModel> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<BookModel> getBook(@RequestParam Integer id) {
        Optional<BookModel> book = bookService.getBook(id);
        return ResponseEntity.of(book);
    }

    @PostMapping("/create")
    public ResponseEntity<BookModel> createBook(@RequestBody BookModel book) {
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @PutMapping("/update")
    public ResponseEntity<BookModel> updateBook(@RequestBody BookModel book) {
        BookModel updatedBook = bookService.updateBook(book);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBook(@RequestParam Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
