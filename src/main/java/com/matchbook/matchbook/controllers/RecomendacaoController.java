package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.RecomendacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/getRecomendacaoAdaptativa/{idUsuario}")
    public ResponseEntity<List<LivroEntity>> getRecomendacaoAdaptativa(@PathVariable Integer idUsuario) {
        try {
            List<LivroEntity> recomendacoes = recomendacaoService.getRecomendacoesAdaptativas(idUsuario);
            if (recomendacoes == null || recomendacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(recomendacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/recusar")
    public ResponseEntity<Map<String, Object>> recusarLivro(@RequestParam Integer idUsuario, @RequestParam Integer idLivro) {
        try {
            Map<String, Object> resultado = recomendacaoService.recusarLivroComAnalise(idUsuario, idLivro);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/aceitar")
    public ResponseEntity<Void> aceitarLivro(@RequestParam Integer idUsuario, @RequestParam Integer idLivro) {
        try {
            recomendacaoService.aceitarLivro(idUsuario, idLivro);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/estatisticas/{idUsuario}")
    public ResponseEntity<Map<String, Object>> getEstatisticasUsuario(@PathVariable Integer idUsuario) {
        try {
            Map<String, Object> estatisticas = recomendacaoService.getEstatisticasRecomendacao(idUsuario);
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/resetarAlgoritmo/{idUsuario}")
    public ResponseEntity<Void> resetarAlgoritmo(@PathVariable Integer idUsuario) {
        try {
            recomendacaoService.resetarHistoricoUsuario(idUsuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
