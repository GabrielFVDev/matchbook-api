package com.matchbook.matchbook.services;

import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.entities.UsuarioEntity;
import com.matchbook.matchbook.repositories.LivroRepository;
import com.matchbook.matchbook.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecomendacaoService {

    @Autowired
    LivroRepository livroRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public List<LivroEntity> getRecomendacoesUsuario(Integer usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<LivroEntity> todosLivros = livroRepository.findAll();

        return calcularRecomendacoes(usuario, todosLivros);
    }

    private List<LivroEntity> calcularRecomendacoes(UsuarioEntity usuario, List<LivroEntity> livros) {
        return livros.stream()
                .filter(LivroEntity::isDisponivel) // Apenas livros disponíveis
                .sorted(Comparator.comparingDouble((LivroEntity livro) -> calcularScore(usuario, livro)).reversed())
                .collect(Collectors.toList());
    }

    private double calcularScore(UsuarioEntity usuario, LivroEntity livro) {
        double score = 0.0;

        // Gênero (40% do score)
        if (livro.getGenero() != null && livro.getGenero().equalsIgnoreCase(usuario.getGeneroFavorito())) {
            score += 0.4;
        }

        // Autor preferido (30% do score) - se o usuário tiver autor preferido definido
        if (usuario.getAutorPreferido() != null && !usuario.getAutorPreferido().isEmpty() &&
                livro.getAutor() != null && livro.getAutor().equalsIgnoreCase(usuario.getAutorPreferido())) {
            score += 0.3;
        }

        // Nível de leitura baseado no número de páginas (30% do score)
        score += calcularScoreNivelLeitura(usuario.getNivelLeitura(), livro.getNumeroPaginas());

        return score;
    }

    private double calcularScoreNivelLeitura(String nivelLeitura, int numeroPaginas) {
        if (nivelLeitura == null) {
            return 0.0;
        }

        return switch (nivelLeitura.toLowerCase()) {
            case "iniciante" -> numeroPaginas <= 200 ? 0.3 : 0.0;
            case "intermediário" -> (numeroPaginas > 200 && numeroPaginas <= 500) ? 0.3 : 0.0;
            case "avançado" -> numeroPaginas > 500 ? 0.3 : 0.0;
            default -> 0.0;
        };
    }
}
