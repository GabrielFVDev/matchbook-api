package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.entities.LivroRecusadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRecusadoRepository extends JpaRepository<LivroRecusadoEntity, Integer> {
    List<LivroRecusadoEntity> findByIdUsuario(Integer idUsuario);

    boolean existsByIdUsuarioAndIdLivro(Integer idUsuario, Integer idLivro);
}

