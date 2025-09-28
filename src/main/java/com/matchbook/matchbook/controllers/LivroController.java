package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.LivroService;
import com.matchbook.matchbook.services.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livro")
@Tag(name = "Livros", description = "API para gerenciamento de livros e recomendações")
public class LivroController {

    @Autowired
    LivroService livroService;

    @Autowired
    RecomendacaoService recomendacaoService;

    @GetMapping("/getAll")
    @Operation(summary = "Buscar todos os livros", description = "Retorna uma lista de todos os livros cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de livros encontrada")
    public ResponseEntity<List<LivroEntity>> getAllLivros() {
        List<LivroEntity> livros = livroService.getAllLivros();
        if (livros.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(livros);
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Buscar livro por ID", description = "Retorna os dados de um livro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroEntity> getLivro(@RequestParam Integer id) {
        Optional<LivroEntity> livro = livroService.getLivro(id);
        return ResponseEntity.of(livro);
    }

    @PostMapping("/create")
    @Operation(summary = "Criar novo livro", description = "Cria um novo livro no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<LivroEntity> createLivro(@RequestBody LivroEntity livro) {
        LivroEntity novoLivro = livroService.createLivro(livro);
        if (novoLivro.isDisponivel()) {
            return ResponseEntity.ok(novoLivro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Atualizar livro", description = "Atualiza os dados de um livro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroEntity> updateLivro(@RequestBody LivroEntity livro) {
        LivroEntity updatedLivro = livroService.updateLivro(livro);
        if (updatedLivro != null) {
            return ResponseEntity.ok(updatedLivro);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Deletar livro", description = "Remove um livro do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<Void> deleteLivro(@RequestParam Integer id) {
        livroService.deleteLivro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recomendacoes/{usuarioId}")
    @Operation(summary = "Obter recomendações de livros",
            description = "Retorna uma lista de livros recomendados para um usuário específico baseado em suas preferências")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendações encontradas"),
            @ApiResponse(responseCode = "204", description = "Nenhuma recomendação encontrada"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
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
