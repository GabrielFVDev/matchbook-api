Melhorias para o Algoritmo de Recomendação de Livros
Objetivo:

Adaptar o algoritmo de recomendação de livros para considerar o feedback do usuário, especialmente quando o usuário recusa um livro. O algoritmo deve se tornar mais flexível e dinâmico, ajustando-se às preferências e recusas do usuário.

Estratégias de Melhoria:
1. Registrar Recusas de Livros

Tabela/Relacionamento de Recusa: Crie uma tabela livros_recusados ou adicione um campo no UsuarioEntity para armazenar livros que o usuário recusou.

Motivos de Recusa: Permita que o usuário forneça um motivo para a recusa (ex.: gênero, autor, número de páginas, ano de publicação). Esse feedback será utilizado para ajustar as futuras recomendações.

Exemplo:

// Exemplo de relacionamento de livros recusados
List<LivroEntity> livrosRecusados;

-- 

2. Adaptar a Lógica de Recomendação

Penalizar Gêneros ou Autores Recusados: Se o usuário rejeitar livros de um determinado gênero ou autor, penalize a recomendação futura desses livros, diminuindo a pontuação deles.

Adaptação por Ano de Publicação: Se o usuário rejeitar livros antigos, penalize livros mais velhos nas futuras recomendações.

Elemento Surpresa: Adicione uma chance de recomendar livros fora do perfil usual do usuário, para dar mais variedade e surpreendê-lo.

Exemplo de Código:

private double calcularScore(UsuarioEntity usuario, LivroEntity livro) {
double score = 0.0;

    // Penaliza gêneros rejeitados
    if (livro.getGenero() != null && usuario.getGenerosRecusados().contains(livro.getGenero())) {
        score -= 0.2;
    }

    // Penaliza autores rejeitados
    if (livro.getAutor() != null && usuario.getAutoresRecusados().contains(livro.getAutor())) {
        score -= 0.3;
    }

    // Penaliza livros antigos, se o usuário recusou livros desse tipo
    if (livro.getAnoPublicacao() != null && livro.getAnoPublicacao() < 2000) {
        if (usuario.getRecusouLivrosAntigos()) {
            score -= 0.1;
        }
    }

    // Elemento surpresa (recomendação fora do perfil)
    if (Math.random() < 0.1) {
        score *= 0.5;
    }

    return score;
}

-- 


3. Feedback Dinâmico

Registrar Recusa: Quando o usuário recusa um livro, adicione o livro à lista de recusas no banco de dados. Utilize esse histórico para ajustar a pontuação de recomendações futuras.

Exemplo de Código para Adicionar Recusa:

public void registrarRecusa(Integer usuarioId, Integer livroId) {
UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    LivroEntity livro = livroRepository.findById(livroId)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

    usuario.getLivrosRecusados().add(livro); // Adiciona livro à lista de recusados
    usuarioRepository.save(usuario); // Salva as alterações no banco
}


--


4. Exploração e Diversificação (Elemento Surpresa)

Exploração Controlada: Para evitar que o sistema de recomendação fique muito repetitivo, adicione uma chance de sugerir livros que não se encaixam exatamente no perfil do usuário. Isso pode ser feito ajustando a pontuação de recomendação de forma que uma pequena parte das recomendações seja "diferente".

Exemplo de Código para Exploração:
private List<LivroEntity> calcularRecomendacoes(UsuarioEntity usuario, List<LivroEntity> livros) {
return livros.stream()
.filter(LivroEntity::isDisponivel)
.map(livro -> {
double score = calcularScore(usuario, livro);

                // Exploração: 10% das vezes, recomenda livros fora do perfil
                if (Math.random() < 0.1) {
                    score *= 0.5;  // Diminui a pontuação para livros "diferentes"
                }

                livro.setScore(score); // Supondo que LivroEntity tem um campo de score
                return livro;
            })
            .sorted(Comparator.comparingDouble(LivroEntity::getScore).reversed())
            .collect(Collectors.toList());
}


--