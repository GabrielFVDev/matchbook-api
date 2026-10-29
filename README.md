# Matchbook API

API de recomendação de livros que aprende com o comportamento do usuário: a cada livro aceito ou recusado, o algoritmo ajusta as próximas sugestões.

## Stack

- Java 17
- Spring Boot 3.5 (Web, Data JPA, Validation, Security)
- MySQL 8
- Maven (wrapper incluso)
- Firebase Auth (todas as rotas exigem token JWT do Firebase)
- Docker + Docker Compose

## Como rodar

### Com Docker (recomendado)

É preciso ter JDK 17 instalado só para compilar. Os serviços (app + banco) sobem via Docker.

```bash
# 1. Compilar
./mvnw clean package -DskipTests

# 2. Subir
docker compose up --build
```

- API: http://localhost:8080
- MySQL: `localhost:3307` (usuário `matchbook_user`, senha `matchbook_pass`)
- O banco já sobe com 30 livros pré-cadastrados (`init-data.sql`)

```bash
docker compose down      # parar
docker compose logs -f   # acompanhar logs
```

### Rodando local (sem o container da aplicação)

```bash
# 1. Sobe apenas o banco
docker compose up mysql -d

# 2. Cria a configuração a partir do exemplo
cp src/main/resources/application.properties.example src/main/resources/application.properties
#    - troque jdbc:mysql://mysql:3306/... por jdbc:mysql://localhost:3307/...
#    - ajuste usuário e senha (por padrão do compose: matchbook_user / matchbook_pass)

# 3. Informa o projeto Firebase
export FIREBASE_PROJECT_ID=seu-projeto

# 4. Roda a aplicação
./mvnw spring-boot:run
```

## Autenticação

Todas as rotas `/api/**` exigem um ID token do Firebase validado no header:

```
Authorization: Bearer <idToken>
```

Não há `id` de usuário na URL: cada rota age sobre o usuário logado (o campo `sub` do token).

## Endpoints

### Livros — `/api/book`

| Método | Rota             | Descrição                                       |
|--------|------------------|-------------------------------------------------|
| GET    | `/getAll`        | Lista os livros visíveis para o usuário         |
| GET    | `/get?id=`       | Busca um livro                                  |
| POST   | `/create`        | Cria um livro                                   |
| PUT    | `/update`        | Atualiza um livro                               |
| DELETE | `/delete?id=`    | Deleta um livro                                 |
| GET    | `/liked`         | Lista os livros curtidos                        |
| GET    | `/liked/count`   | Quantidade de livros curtidos                   |
| DELETE | `/liked?bookId=` | Descurtir um livro                              |

Só é possível atualizar ou deletar livros do próprio usuário.

### Recomendações — `/api/recommendation`

| Método | Rota                        | Descrição                                                |
|--------|-----------------------------|----------------------------------------------------------|
| GET    | `/getRecommendation`        | Recomendações do algoritmo padrão                        |
| GET    | `/getAdaptiveRecommendation`| Recomendações do algoritmo adaptativo                    |
| POST   | `/accept?bookId=`           | Aceita (curte) um livro                                  |
| POST   | `/reject?bookId=`           | Recusa um livro e retorna a análise do padrão de recusas |
| GET    | `/statistics`               | Recusas, aceites e estratégia atual do algoritmo         |
| POST   | `/resetAlgorithm`           | Apaga todo o histórico de interações do usuário          |

### Perfil — `/api/me`

| Método | Rota                 | Descrição                                                                   |
|--------|----------------------|-----------------------------------------------------------------------------|
| GET    | `/preferences`       | Preferências do usuário                                                     |
| PUT    | `/preferences`       | Salva as preferências                                                       |
| DELETE | `/`                  | Apaga os dados do usuário (chamar antes de excluir a conta no Firebase)     |

## Algoritmo adaptativo

O score de cada livro considera: 40% compatibilidade com o gênero favorito, 30% com o autor preferido e 30% adequação ao nível de leitura (páginas).

Com base no histórico de aceites e recusas, o sistema escolhe a estratégia da próxima rodada:

| Estratégia    | Quando                          | O que faz                                              |
|---------------|---------------------------------|--------------------------------------------------------|
| `DEFAULT`     | menos de 5 recusas (< 3 aceites)| Score normal pelas preferências do perfil              |
| `DIVERSIFIED` | 5 a 9 recusas                   | Mistura as preferências com gêneros variados           |
| `REFINED`     | 3 ou mais aceites               | Prioriza os gostos aprendidos com o comportamento (50% do score) |
| `EXPLORATORY` | 10 ou mais recusas              | Livros aleatórios, para quebrar o padrão               |

A verificação segue essa ordem: `EXPLORATORY` > `REFINED` > `DIVERSIFIED` > `DEFAULT`. Ou seja, com 10+ recusas e 3+ aceites, vence a estratégia exploratória.

## Exemplo de uso

```bash
# Aceitar uma recomendação
curl -X POST "http://localhost:8080/api/recommendation/accept?bookId=5" \
  -H "Authorization: Bearer $TOKEN"

# Ver as estatísticas do algoritmo
curl "http://localhost:8080/api/recommendation/statistics" \
  -H "Authorization: Bearer $TOKEN"
```
