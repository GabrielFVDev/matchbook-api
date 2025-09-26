package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.entities.LivroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<LivroEntity, Integer> {
}
