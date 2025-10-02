package com.matchbook.matchbook.services;

import com.matchbook.matchbook.entities.LivroAceitoEntity;
import com.matchbook.matchbook.entities.LivroEntity;
import com.matchbook.matchbook.entities.LivroRecusadoEntity;
import com.matchbook.matchbook.entities.UsuarioEntity;
import com.matchbook.matchbook.repositories.LivroAceitoRepository;
import com.matchbook.matchbook.repositories.LivroRecusadoRepository;
import com.matchbook.matchbook.repositories.LivroRepository;
import com.matchbook.matchbook.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecomendacaoService {

    @Autowired
    LivroRepository livroRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    LivroRecusadoRepository livroRecusadoRepository;

    @Autowired
    LivroAceitoRepository livroAceitoRepository;

    // Algoritmo original simples
    public List<LivroEntity> getRecomendacoesUsuario(Integer usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<LivroEntity> todosLivros = livroRepository.findAll();
        List<LivroRecusadoEntity> recusados = livroRecusadoRepository.findByIdUsuario(usuarioId);
        List<Integer> idsRecusados = recusados.stream().map(LivroRecusadoEntity::getIdLivro).toList();

        List<LivroEntity> livrosFiltrados = todosLivros.stream()
                .filter(livro -> !idsRecusados.contains(livro.getId()))
                .collect(Collectors.toList());

        return calcularRecomendacoes(usuario, livrosFiltrados);
    }

    // Algoritmo adaptativo inteligente
    public List<LivroEntity> getRecomendacoesAdaptativas(Integer usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<LivroEntity> todosLivros = livroRepository.findAll();
        List<LivroRecusadoEntity> recusados = livroRecusadoRepository.findByIdUsuario(usuarioId);
        List<LivroAceitoEntity> aceitos = livroAceitoRepository.findByIdUsuario(usuarioId);

        // Análise de padrões
        Map<String, Double> preferenciasAprendidas = analisarPadroes(aceitos, recusados, todosLivros);

        // Determinar estratégia baseada no histórico
        String estrategia = determinarEstrategia(recusados, aceitos);

        return aplicarEstrategiaRecomendacao(usuario, todosLivros, recusados, aceitos, preferenciasAprendidas, estrategia);
    }

    // Registrar recusa com análise
    public Map<String, Object> recusarLivroComAnalise(Integer usuarioId, Integer livroId) {
        if (!livroRecusadoRepository.existsByIdUsuarioAndIdLivro(usuarioId, livroId)) {
            LivroRecusadoEntity recusa = new LivroRecusadoEntity();
            recusa.setIdUsuario(usuarioId);
            recusa.setIdLivro(livroId);
            livroRecusadoRepository.save(recusa);
        }

        // Analisar padrão da recusa
        Map<String, Object> resultado = new HashMap<>();
        List<LivroRecusadoEntity> todasRecusas = livroRecusadoRepository.findByIdUsuario(usuarioId);

        resultado.put("totalRecusas", todasRecusas.size());
        resultado.put("algoritmoAlterado", todasRecusas.size() >= 5);
        resultado.put("sugestao", todasRecusas.size() >= 5 ?
                "Algoritmo será mais exploratório" : "Algoritmo mantém foco nas preferências");

        return resultado;
    }

    // Registrar aceitação
    public void aceitarLivro(Integer usuarioId, Integer livroId) {
        if (!livroAceitoRepository.existsByIdUsuarioAndIdLivro(usuarioId, livroId)) {
            LivroAceitoEntity aceite = new LivroAceitoEntity();
            aceite.setIdUsuario(usuarioId);
            aceite.setIdLivro(livroId);
            livroAceitoRepository.save(aceite);
        }
    }

    // Estatísticas do usuário
    public Map<String, Object> getEstatisticasRecomendacao(Integer usuarioId) {
        List<LivroRecusadoEntity> recusados = livroRecusadoRepository.findByIdUsuario(usuarioId);
        List<LivroAceitoEntity> aceitos = livroAceitoRepository.findByIdUsuario(usuarioId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecusas", recusados.size());
        stats.put("totalAceites", aceitos.size());
        stats.put("taxaAceitacao", aceitos.size() > 0 ?
                (double) aceitos.size() / (aceitos.size() + recusados.size()) * 100 : 0);
        stats.put("estrategiaAtual", determinarEstrategia(recusados, aceitos));

        return stats;
    }

    // Resetar histórico
    public void resetarHistoricoUsuario(Integer usuarioId) {
        livroRecusadoRepository.deleteAll(livroRecusadoRepository.findByIdUsuario(usuarioId));
        livroAceitoRepository.deleteAll(livroAceitoRepository.findByIdUsuario(usuarioId));
    }

    // Métodos auxiliares privados
    private Map<String, Double> analisarPadroes(List<LivroAceitoEntity> aceitos,
                                                List<LivroRecusadoEntity> recusados,
                                                List<LivroEntity> todosLivros) {
        Map<String, Double> preferencias = new HashMap<>();

        // Analisar gêneros aceitos
        Map<String, Integer> generosAceitos = new HashMap<>();
        Map<String, Integer> generosRecusados = new HashMap<>();

        for (LivroAceitoEntity aceito : aceitos) {
            LivroEntity livro = todosLivros.stream()
                    .filter(l -> Objects.equals(l.getId(), aceito.getIdLivro()))
                    .findFirst().orElse(null);
            if (livro != null && livro.getGenero() != null) {
                generosAceitos.put(livro.getGenero(), generosAceitos.getOrDefault(livro.getGenero(), 0) + 1);
            }
        }

        for (LivroRecusadoEntity recusado : recusados) {
            LivroEntity livro = todosLivros.stream()
                    .filter(l -> Objects.equals(l.getId(), recusado.getIdLivro()))
                    .findFirst().orElse(null);
            if (livro != null && livro.getGenero() != null) {
                generosRecusados.put(livro.getGenero(), generosRecusados.getOrDefault(livro.getGenero(), 0) + 1);
            }
        }

        // Calcular preferências aprendidas
        for (String genero : generosAceitos.keySet()) {
            int aceitas = generosAceitos.get(genero);
            int recusas = generosRecusados.getOrDefault(genero, 0);
            double preferencia = (double) aceitas / (aceitas + recusas);
            preferencias.put(genero, preferencia);
        }

        return preferencias;
    }

    private String determinarEstrategia(List<LivroRecusadoEntity> recusados, List<LivroAceitoEntity> aceitos) {
        int totalRecusas = recusados.size();
        int totalAceites = aceitos.size();

        if (totalRecusas >= 10) return "EXPLORATORIO";
        if (totalAceites >= 3) return "REFINADO";
        if (totalRecusas >= 5) return "DIVERSIFICADO";
        return "PADRAO";
    }

    private List<LivroEntity> aplicarEstrategiaRecomendacao(UsuarioEntity usuario,
                                                            List<LivroEntity> todosLivros,
                                                            List<LivroRecusadoEntity> recusados,
                                                            List<LivroAceitoEntity> aceitos,
                                                            Map<String, Double> preferenciasAprendidas,
                                                            String estrategia) {

        List<Integer> idsRecusados = recusados.stream().map(LivroRecusadoEntity::getIdLivro).toList();
        List<Integer> idsAceitos = aceitos.stream().map(LivroAceitoEntity::getIdLivro).toList();

        List<LivroEntity> livrosFiltrados = todosLivros.stream()
                .filter(LivroEntity::isDisponivel)
                .filter(livro -> !idsRecusados.contains(livro.getId()))
                .filter(livro -> !idsAceitos.contains(livro.getId()))
                .collect(Collectors.toList());

        return switch (estrategia) {
            case "EXPLORATORIO" -> aplicarAlgoritmoExploratorio(livrosFiltrados);
            case "REFINADO" -> aplicarAlgoritmoRefinado(usuario, livrosFiltrados, preferenciasAprendidas);
            case "DIVERSIFICADO" -> aplicarAlgoritmoDiversificado(usuario, livrosFiltrados, preferenciasAprendidas);
            default -> calcularRecomendacoes(usuario, livrosFiltrados);
        };
    }

    private List<LivroEntity> aplicarAlgoritmoExploratorio(List<LivroEntity> livros) {
        // Algoritmo mais aleatório e diverso
        Collections.shuffle(livros);
        return livros.stream().limit(10).collect(Collectors.toList());
    }

    private List<LivroEntity> aplicarAlgoritmoRefinado(UsuarioEntity usuario,
                                                       List<LivroEntity> livros,
                                                       Map<String, Double> preferenciasAprendidas) {
        // Foca nas preferências aprendidas
        return livros.stream()
                .sorted(Comparator.comparingDouble((LivroEntity livro) ->
                        calcularScoreRefinado(usuario, livro, preferenciasAprendidas)).reversed())
                .collect(Collectors.toList());
    }

    private List<LivroEntity> aplicarAlgoritmoDiversificado(UsuarioEntity usuario,
                                                            List<LivroEntity> livros,
                                                            Map<String, Double> preferenciasAprendidas) {
        // Mistura preferências com diversidade
        Map<String, List<LivroEntity>> livrosPorGenero = livros.stream()
                .collect(Collectors.groupingBy(l -> l.getGenero() != null ? l.getGenero() : "Outros"));

        List<LivroEntity> resultado = new ArrayList<>();
        int porGenero = Math.max(1, 10 / livrosPorGenero.size());

        for (List<LivroEntity> livrosGenero : livrosPorGenero.values()) {
            livrosGenero.sort(Comparator.comparingDouble((LivroEntity livro) ->
                    calcularScoreRefinado(usuario, livro, preferenciasAprendidas)).reversed());
            resultado.addAll(livrosGenero.stream().limit(porGenero).collect(Collectors.toList()));
        }

        return resultado;
    }

    private double calcularScoreRefinado(UsuarioEntity usuario, LivroEntity livro, Map<String, Double> preferenciasAprendidas) {
        double score = calcularScore(usuario, livro);

        // Adicionar peso das preferências aprendidas
        if (livro.getGenero() != null && preferenciasAprendidas.containsKey(livro.getGenero())) {
            score += preferenciasAprendidas.get(livro.getGenero()) * 0.5; // 50% de influência das preferências aprendidas
        }

        return score;
    }

    // Métodos originais mantidos
    private List<LivroEntity> calcularRecomendacoes(UsuarioEntity usuario, List<LivroEntity> livros) {
        return livros.stream()
                .filter(LivroEntity::isDisponivel)
                .sorted(Comparator.comparingDouble((LivroEntity livro) -> calcularScore(usuario, livro)).reversed())
                .collect(Collectors.toList());
    }

    private double calcularScore(UsuarioEntity usuario, LivroEntity livro) {
        double score = 0.0;

        if (livro.getGenero() != null && livro.getGenero().equalsIgnoreCase(usuario.getGeneroFavorito())) {
            score += 0.4;
        }

        if (usuario.getAutorPreferido() != null && !usuario.getAutorPreferido().isEmpty() &&
                livro.getAutor() != null && livro.getAutor().equalsIgnoreCase(usuario.getAutorPreferido())) {
            score += 0.3;
        }

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
