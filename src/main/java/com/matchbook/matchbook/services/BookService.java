package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.models.BookReactionModel;
import com.matchbook.matchbook.models.ReactionType;
import com.matchbook.matchbook.repositories.BookReactionRepository;
import com.matchbook.matchbook.repositories.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookReactionRepository bookReactionRepository;

    public BookService(BookRepository bookRepository, BookReactionRepository bookReactionRepository) {
        this.bookRepository = bookRepository;
        this.bookReactionRepository = bookReactionRepository;
    }

    public List<BookModel> getVisibleBooks(String firebaseUid) {
        return bookRepository.findByOwnerUidIsNullOrOwnerUid(firebaseUid);
    }

    // Empty when the book does not exist or belongs to someone else
    public Optional<BookModel> getBook(String firebaseUid, Integer id) {
        return bookRepository.findById(id).filter(book -> isVisibleTo(book, firebaseUid));
    }

    // Always inserts: an id sent in the body would make save() overwrite an existing book
    public BookModel createBook(String firebaseUid, BookModel book) {
        book.setId(0);
        book.setOwnerUid(firebaseUid);
        return bookRepository.save(book);
    }

    public BookModel updateBook(String firebaseUid, BookModel book) {
        BookModel existing = findOwnedBook(firebaseUid, book.getId());
        book.setOwnerUid(firebaseUid);
        book.setCreatedAt(existing.getCreatedAt());
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(String firebaseUid, Integer id) {
        findOwnedBook(firebaseUid, id);
        bookReactionRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    // "My books": the liked books, most recently liked first
    public List<BookModel> getLikedBooks(String firebaseUid) {
        List<BookReactionModel> likes =
                bookReactionRepository.findByFirebaseUidAndTypeOrderByReactedAtDesc(firebaseUid, ReactionType.LIKED);
        Map<Integer, BookModel> booksById = bookRepository
                .findAllById(likes.stream().map(BookReactionModel::getBookId).toList())
                .stream()
                .collect(Collectors.toMap(BookModel::getId, Function.identity()));

        return likes.stream()
                .map(like -> booksById.get(like.getBookId()))
                .filter(Objects::nonNull)
                .filter(book -> isVisibleTo(book, firebaseUid))
                .toList();
    }

    public long countLikedBooks(String firebaseUid) {
        return bookReactionRepository.countByFirebaseUidAndType(firebaseUid, ReactionType.LIKED);
    }

    // False when the book was not liked. Once unliked, the book can show up in the recommendations again.
    public boolean unlikeBook(String firebaseUid, Integer bookId) {
        Optional<BookReactionModel> like = bookReactionRepository.findByFirebaseUidAndBookId(firebaseUid, bookId)
                .filter(reaction -> reaction.getType() == ReactionType.LIKED);
        like.ifPresent(bookReactionRepository::delete);
        return like.isPresent();
    }

    // 404 when the user cannot see the book; 403 when they can see it but did not create it (shared books)
    private BookModel findOwnedBook(String firebaseUid, Integer id) {
        BookModel book = getBook(firebaseUid, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!firebaseUid.equals(book.getOwnerUid())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return book;
    }

    private boolean isVisibleTo(BookModel book, String firebaseUid) {
        return book.getOwnerUid() == null || book.getOwnerUid().equals(firebaseUid);
    }
}
