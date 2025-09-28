package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.RecomendacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recomendacao")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecomendacaoController {

    @Autowired
    RecomendacaoService recomendacaoService;

    @GetMapping("/getRecomendacao/{idUsuario}")
    public ResponseEntity<List<LivroEntity>> getRecomendacao(@PathVariable Integer idUsuario) {
        try {
            List<LivroEntity> recomendacoes = recomendacaoService.getRecomendacoesUsuario(idUsuario);
            if (recomendacoes == null || recomendacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(recomendacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
