package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.LivroAceitoEntity;
import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.services.LivroAceitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livros-curtidos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LivroAceitoController {

    @Autowired
    private LivroAceitoService livroAceitoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LivroEntity>> getLivrosCurtidos(@PathVariable Integer usuarioId) {
        try {
            List<LivroEntity> livrosCurtidos = livroAceitoService.getLivrosCurtidosPorUsuario(usuarioId);

            if (livrosCurtidos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(livrosCurtidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Verifica se um livro específico foi curtido por um usuário
     */
    @GetMapping("/usuario/{usuarioId}/livro/{livroId}")
    public ResponseEntity<Map<String, Boolean>> verificarSeCurtido(
            @PathVariable Integer usuarioId,
            @PathVariable Integer livroId) {
        try {
            boolean curtido = livroAceitoService.isLivroCurtido(usuarioId, livroId);
            return ResponseEntity.ok(Map.of("curtido", curtido));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Adiciona um livro à lista de curtidos
     */
    @PostMapping("/curtir")
    public ResponseEntity<Map<String, Object>> curtirLivro(
            @RequestParam Integer usuarioId,
            @RequestParam Integer livroId) {
        try {
            LivroAceitoEntity resultado = livroAceitoService.curtirLivro(usuarioId, livroId);

            if (resultado != null) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Livro adicionado aos curtidos com sucesso",
                        "id", resultado.getId()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "sucesso", false,
                        "mensagem", "Livro já estava na lista de curtidos"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Erro interno do servidor"
            ));
        }
    }

    /**
     * Remove um livro da lista de curtidos
     */
    @DeleteMapping("/descurtir")
    public ResponseEntity<Map<String, Object>> descurtirLivro(
            @RequestParam Integer usuarioId,
            @RequestParam Integer livroId) {
        try {
            boolean removido = livroAceitoService.descurtirLivro(usuarioId, livroId);

            if (removido) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Livro removido dos curtidos com sucesso"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "sucesso", false,
                        "mensagem", "Livro não estava na lista de curtidos"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Erro interno do servidor"
            ));
        }
    }

    /**
     * Conta quantos livros o usuário curtiu
     */
    @GetMapping("/usuario/{usuarioId}/contador")
    public ResponseEntity<Map<String, Long>> contarLivrosCurtidos(@PathVariable Integer usuarioId) {
        try {
            long total = livroAceitoService.contarLivrosCurtidos(usuarioId);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
