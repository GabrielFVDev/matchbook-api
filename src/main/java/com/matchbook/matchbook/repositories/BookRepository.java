package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.models.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookModel, Integer> {
}
