package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.models.BookReactionModel;
import com.matchbook.matchbook.models.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookReactionRepository extends JpaRepository<BookReactionModel, Integer> {
    List<BookReactionModel> findByFirebaseUid(String firebaseUid);

    List<BookReactionModel> findByFirebaseUidAndTypeOrderByReactedAtDesc(String firebaseUid, ReactionType type);

    Optional<BookReactionModel> findByFirebaseUidAndBookId(String firebaseUid, Integer bookId);

    long countByFirebaseUidAndType(String firebaseUid, ReactionType type);

    void deleteByFirebaseUid(String firebaseUid);

    void deleteByBookId(Integer bookId);

    void deleteByBookIdIn(Collection<Integer> bookIds);
}
