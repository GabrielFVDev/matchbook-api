package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.models.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookModel, Integer> {
    List<BookModel> findByOwnerUidIsNullOrOwnerUid(String ownerUid);

    List<BookModel> findByOwnerUid(String ownerUid);
}
