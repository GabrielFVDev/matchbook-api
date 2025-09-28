package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController("/api/livro")
public class LivroController {

    @Autowired
    LivroService livroService;

    @GetMapping("/getAll")
    public ResponseEntity<List<LivroEntity>> getAllLivros() {
        List<LivroEntity> livros = livroService.getAllLivros();
        if (livros.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(livros);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<LivroEntity> getLivro(@RequestParam Integer id) {
        Optional<LivroEntity> livro = livroService.getLivro(id);
        return ResponseEntity.of(livro);
    }

    @GetMapping("/create")
    public ResponseEntity<LivroEntity> createLivro() {
        LivroEntity livro = livroService.createLivro();
        if (livro.isDisponivel()) {
            return ResponseEntity.ok(livro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/update")
    public ResponseEntity<LivroEntity> updateLivro(LivroEntity livro) {
        LivroEntity updatedLivro = livroService.updateLivro(livro);
        if (updatedLivro != null) {
            return ResponseEntity.ok(updatedLivro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteLivro(Integer id) {
        livroService.deleteLivro(id);
        return ResponseEntity.noContent().build();
    }
}
