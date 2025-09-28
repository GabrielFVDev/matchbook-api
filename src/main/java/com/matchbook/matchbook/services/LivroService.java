package com.matchbook.matchbook.services;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.repositories.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    LivroRepository livroRepository;

    public Optional<LivroEntity> getLivro(Integer id) {
        return livroRepository.findById(id);
    }

    public List<LivroEntity> getAllLivros() {
        return livroRepository.findAll();
    }

    public LivroEntity createLivro() {
        LivroEntity livro = livroRepository.save(new LivroEntity());
        return livro;
    }

    public LivroEntity updateLivro(LivroEntity livro) {
        // Lógica para atualizar um livro
        return livroRepository.save(livro);
    }

    public void deleteLivro(Integer id) {
        livroRepository.deleteById(id);
    }
}
