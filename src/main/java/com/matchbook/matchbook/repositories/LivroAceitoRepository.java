package com.matchbook.matchbook.repositories;

import com.matchbook.matchbook.entities.LivroAceitoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroAceitoRepository extends JpaRepository<LivroAceitoEntity, Integer> {
    List<LivroAceitoEntity> findByIdUsuario(Integer idUsuario);

    boolean existsByIdUsuarioAndIdLivro(Integer idUsuario, Integer idLivro);
}
