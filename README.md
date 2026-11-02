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

Não precisa de JDK na máquina: a imagem compila o projeto dentro do próprio Docker. Os serviços (app + banco) sobem via Docker.

```bash
# 1. Informa o projeto Firebase (o .env fica fora do git)
cp .env.example .env
#    - preencha FIREBASE_PROJECT_ID com o projectId do firebaseConfig do front

# 2. Cria a configuração local a partir do exemplo, se ainda não existir (o compose já passa o banco e a senha)
cp -n src/main/resources/application.properties.example src/main/resources/application.properties

# 3. Subir (compila e monta a imagem)
docker compose up --build
```

A imagem local leva junto o seu `application.properties`: ela é só para a sua máquina, não a envie para nenhum registro.

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

## Deploy no Render + Aiven

A API roda no **Render** (plano Free, montada a partir deste `Dockerfile`) e o banco é um MySQL grátis no **Aiven**, os dois no plano grátis. Na nuvem, a configuração vem do perfil `cloud` (`src/main/resources/application-cloud.properties`), que lê tudo de variáveis de ambiente: nenhum segredo vai para o git. Os valores são copiados do painel de um serviço e colados direto no painel do outro.

### 1. Banco no Aiven

1. Crie a conta em https://console.aiven.io e um serviço **MySQL** no plano **Free**.
2. Na página do serviço, em *Connection information*, ficam **Host**, **Port**, **User** (`avnadmin`), **Password** e **Database name** (`defaultdb`). Não precisa baixar o certificado: o `sslMode=REQUIRED` do `DB_URL` já criptografa a conexão (sem conferir o certificado do Aiven, o que basta para um projeto pessoal).

### 2. API no Render

1. Crie a conta em https://render.com e ligue o GitHub.
2. **New → Web Service**, repositório `matchbook-api`, branch `main`, **Language: Docker**, **Instance Type: Free**. Deixe o *Health Check Path* vazio: toda rota `/api` exige login e responderia 401.
3. Em **Environment Variables**:

| Variável | Valor | Onde achar |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `cloud` | fixo: liga o perfil da nuvem |
| `DB_URL` | `jdbc:mysql://<host>:<porta>/defaultdb?sslMode=REQUIRED` | Host e Port do Aiven |
| `DB_USERNAME` | `avnadmin` | User do Aiven |
| `DB_PASSWORD` | a senha | Password do Aiven |
| `FIREBASE_PROJECT_ID` | o `projectId` | console do Firebase → Configurações do projeto (o mesmo do `.env` local) |

Não precisa criar `PORT` (o Render define sozinho e o perfil `cloud` usa) nem `JAVA_TOOL_OPTIONS`: o `Dockerfile` já ajusta a JVM para os 512 MB e o 0,1 de CPU do plano Free (`-XX:MaxRAMPercentage=60 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1`). Uma variável `JAVA_TOOL_OPTIONS` no Render substitui esse valor, se um dia precisar mudar.

4. **Deploy.** No log do Render, `Started MatchbookApplication` é o sinal de que a API ligou. Conferência de fora (`401` quer dizer: a API está no ar e exige login):

```bash
curl -s -o /dev/null -w '%{http_code}\n' https://<serviço>.onrender.com/api/me
```

### 3. Os 30 livros iniciais (uma vez só)

Depois da primeira partida da API (é ela que cria as tabelas no `defaultdb`), carregue só o `INSERT` do `init-data.sql`. O arquivo inteiro não serve: ele faz `USE matchbook` e cria a tabela `books` com outros tamanhos. O comando usa o cliente `mysql` de um contêiner (não precisa instalar nada), e a senha é digitada sem aparecer na tela nem ficar no histórico:

```bash
read -s "MYSQL_PWD?Senha do Aiven: " && export MYSQL_PWD   # zsh; no bash: read -s -p "Senha do Aiven: " MYSQL_PWD && export MYSQL_PWD
{ echo 'SET NAMES utf8mb4;'; sed -n '/^INSERT IGNORE/,$p' init-data.sql; } \
  | docker run --rm -i -e MYSQL_PWD mysql:8.0 \
      mysql --default-character-set=utf8mb4 --ssl-mode=REQUIRED -h <host> -P <porta> -u avnadmin defaultdb
docker run --rm -e MYSQL_PWD mysql:8.0 \
  mysql --ssl-mode=REQUIRED -h <host> -P <porta> -u avnadmin defaultdb -e 'SELECT COUNT(*) FROM books'
unset MYSQL_PWD
```

O `SELECT` deve mostrar `30`. **Rode o `INSERT` uma vez só:** a tabela não impede título repetido, então rodar de novo duplica os livros.

### Limites do plano grátis

- O Render **dorme o serviço depois de 15 minutos sem acesso**. O primeiro acesso depois disso espera a API ligar de novo: **perto de 3 minutos** com o 0,1 de CPU do plano Free (medido num contêiner limitado a 0,1 CPU e 512 MB, usando 220 MB), e o front fica carregando nesse meio-tempo.
- 750 horas de serviço por mês por conta (dá para um serviço ligado o mês inteiro).
- O Aiven Free tem 1 GB de disco e aceita poucas conexões; o perfil `cloud` abre no máximo 3.

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
