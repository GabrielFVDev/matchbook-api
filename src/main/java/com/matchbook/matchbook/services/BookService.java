package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<BookModel> getBook(Integer id) {
        return bookRepository.findById(id);
    }

    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    // Always inserts: an id sent in the body would make save() overwrite an existing book
    public BookModel createBook(BookModel book) {
        book.setId(0);
        return bookRepository.save(book);
    }

    public BookModel updateBook(BookModel book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }
}
