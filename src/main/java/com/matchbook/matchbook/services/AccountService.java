package com.matchbook.matchbook.services;

import com.matchbook.matchbook.models.BookModel;
import com.matchbook.matchbook.repositories.BookReactionRepository;
import com.matchbook.matchbook.repositories.BookRepository;
import com.matchbook.matchbook.repositories.UserPreferencesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final BookRepository bookRepository;
    private final BookReactionRepository bookReactionRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    public AccountService(BookRepository bookRepository,
                          BookReactionRepository bookReactionRepository,
                          UserPreferencesRepository userPreferencesRepository) {
        this.bookRepository = bookRepository;
        this.bookReactionRepository = bookReactionRepository;
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @Transactional
    public void deleteAccountData(String firebaseUid) {
        List<Integer> ownBookIds = bookRepository.findByOwnerUid(firebaseUid).stream()
                .map(BookModel::getId)
                .toList();
        if (!ownBookIds.isEmpty()) {
            bookReactionRepository.deleteByBookIdIn(ownBookIds);
            bookRepository.deleteAllById(ownBookIds);
        }

        bookReactionRepository.deleteByFirebaseUid(firebaseUid);
        userPreferencesRepository.deleteById(firebaseUid);
    }
}
