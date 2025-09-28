package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.LivroService;
import com.matchbook.matchbook.services.RecomendacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livro")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LivroController {

    @Autowired
    LivroService livroService;

    @Autowired
    RecomendacaoService recomendacaoService;

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

    @PostMapping("/create")
    public ResponseEntity<LivroEntity> createLivro(@RequestBody LivroEntity livro) {
        LivroEntity novoLivro = livroService.createLivro(livro);
        if (novoLivro.isDisponivel()) {
            return ResponseEntity.ok(novoLivro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<LivroEntity> updateLivro(@RequestBody LivroEntity livro) {
        LivroEntity updatedLivro = livroService.updateLivro(livro);
        if (updatedLivro != null) {
            return ResponseEntity.ok(updatedLivro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteLivro(@RequestParam Integer id) {
        livroService.deleteLivro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recomendacoes/{usuarioId}")
    public ResponseEntity<List<LivroEntity>> getRecomendacoes(@PathVariable Integer usuarioId) {
        try {
            List<LivroEntity> recomendacoes = recomendacaoService.getRecomendacoesUsuario(usuarioId);
            if (recomendacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(recomendacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
