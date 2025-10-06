package com.matchbook.matchbook.services;

import com.matchbook.matchbook.entities.LivroAceitoEntity;
import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.repositories.LivroAceitoRepository;
import com.matchbook.matchbook.repositories.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroAceitoService {

    @Autowired
    private LivroAceitoRepository livroAceitoRepository;

    @Autowired
    private LivroRepository livroRepository;

    public List<LivroEntity> getLivrosCurtidosPorUsuario(Integer usuarioId) {
        List<LivroAceitoEntity> aceitos = livroAceitoRepository.findByIdUsuario(usuarioId);

        return aceitos.stream()
                .map(aceito -> livroRepository.findById(aceito.getIdLivro()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public boolean isLivroCurtido(Integer usuarioId, Integer livroId) {
        return livroAceitoRepository.existsByIdUsuarioAndIdLivro(usuarioId, livroId);
    }

    public LivroAceitoEntity curtirLivro(Integer usuarioId, Integer livroId) {
        if (!livroAceitoRepository.existsByIdUsuarioAndIdLivro(usuarioId, livroId)) {
            LivroAceitoEntity aceite = new LivroAceitoEntity();
            aceite.setIdUsuario(usuarioId);
            aceite.setIdLivro(livroId);
            return livroAceitoRepository.save(aceite);
        }
        return null; // Já existe
    }

    public boolean descurtirLivro(Integer usuarioId, Integer livroId) {
        List<LivroAceitoEntity> aceitos = livroAceitoRepository.findByIdUsuario(usuarioId);

        Optional<LivroAceitoEntity> aceito = aceitos.stream()
                .filter(a -> a.getIdLivro().equals(livroId))
                .findFirst();

        if (aceito.isPresent()) {
            livroAceitoRepository.delete(aceito.get());
            return true;
        }
        return false;
    }

    public Integer contarLivrosCurtidos(Integer usuarioId) {
        return livroAceitoRepository.findByIdUsuario(usuarioId).size();
    }
}
