package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recomendacao")
@Tag(name = "Recomendações", description = "API para sistema de recomendação de livros")
public class RecomendacaoController {

    @Autowired
    RecomendacaoService recomendacaoService;

    @GetMapping("/getRecomendacao/{idUsuario}")
    @Operation(summary = "Obter recomendações personalizadas",
            description = "Retorna uma lista de livros recomendados para um usuário baseado em suas preferências de gênero, autor preferido e nível de leitura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendações encontradas com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma recomendação encontrada para este usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
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
