# 📚 Matchbook - Sistema de Recomendação Adaptativa de Livros

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![Java](https://img.shields.io/badge/Java-17-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Maven](https://img.shields.io/badge/Maven-3.8+-red)
![Docker](https://img.shields.io/badge/Docker-🐳-blue)

## 🎯 Sobre o Projeto

O **Matchbook** é um sistema inteligente de recomendação de livros que utiliza algoritmos adaptativos para sugerir
livros baseados no comportamento e preferências do usuário. O diferencial do sistema está na sua capacidade de *
*aprender e se adaptar** conforme o usuário aceita ou recusa recomendações.

## 🧠 Algoritmo de Recomendação Adaptativa

### Como Funciona

O sistema implementa **4 estratégias diferentes** que são aplicadas automaticamente baseadas no histórico de interações
do usuário:

#### 📊 **Estratégias de Recomendação**

| Estratégia        | Condição      | Comportamento                                        |
|-------------------|---------------|------------------------------------------------------|
| **PADRÃO**        | 0-4 recusas   | Algoritmo original baseado em preferências do perfil |
| **DIVERSIFICADO** | 5-9 recusas   | Mistura preferências com diversidade de gêneros      |
| **EXPLORATÓRIO**  | 10+ recusas   | Recomendações aleatórias para quebrar padrões ruins  |
| **REFINADO**      | 3+ aceitações | Foca nas preferências aprendidas pelo comportamento  |

### 🔄 Processo de Adaptação

1. **Usuário recebe recomendação** → Aceita ou Recusa
2. **Sistema registra interação** → Analisa padrões de comportamento
3. **Algoritmo se adapta** → Muda estratégia baseada no histórico
4. **Próximas recomendações** → Usa nova estratégia adaptada

### 🎲 Cálculo de Score

O sistema calcula um score para cada livro baseado em:

- **40%** - Compatibilidade com gênero favorito
- **30%** - Compatibilidade com autor preferido
- **30%** - Adequação ao nível de leitura (páginas)

## 🚀 Como Executar o Projeto

### 📋 Pré-requisitos

- **Java 17** ou superior
- **Maven 3.8+**
- **Docker** e **Docker Compose**
- **Git**

### 🐧 Executando no Linux

```bash
# 1. Clonar o repositório
git clone <url-do-repositorio>
cd matchbook

# 2. Compilar o projeto
./mvnw clean package -DskipTests

# 3. Subir com Docker Compose
docker compose up --build

# 4. Acessar a aplicação
# API: http://localhost:8080
# MySQL: localhost:3307
```

### 🪟 Executando no Windows

```cmd
# 1. Clonar o repositório
git clone <url-do-repositorio>
cd matchbook

# 2. Compilar o projeto
mvnw.cmd clean package -DskipTests

# 3. Subir com Docker Compose
docker compose up --build

# 4. Acessar a aplicação
# API: http://localhost:8080
# MySQL: localhost:3307
```

### 🐳 Executando com Docker

O projeto inclui configuração completa do Docker Compose que sobe:

- **MySQL 8.0** na porta 3307
- **Spring Boot App** na porta 8080
- **Dados iniciais** carregados automaticamente

```bash
# Subir os serviços
docker compose up --build

# Parar os serviços
docker compose down

# Ver logs
docker compose logs -f

# Rebuild completo
docker compose down && docker compose up --build
```

## 📡 API Endpoints

### 👤 Usuários

| Método | Endpoint            | Descrição                |
|--------|---------------------|--------------------------|
| GET    | `/api/usuario`      | Listar todos os usuários |
| GET    | `/api/usuario/{id}` | Buscar usuário por ID    |
| POST   | `/api/usuario`      | Criar novo usuário       |
| PUT    | `/api/usuario/{id}` | Atualizar usuário        |
| DELETE | `/api/usuario/{id}` | Deletar usuário          |

### 📚 Livros

| Método | Endpoint                               | Descrição              |
|--------|----------------------------------------|------------------------|
| GET    | `/api/livro`                           | Listar todos os livros |
| GET    | `/api/livro/{id}`                      | Buscar livro por ID    |
| GET    | `/api/livro/recomendacoes/{usuarioId}` | Recomendações simples  |
| POST   | `/api/livro`                           | Criar novo livro       |
| PUT    | `/api/livro/{id}`                      | Atualizar livro        |
| DELETE | `/api/livro/{id}`                      | Deletar livro          |

### 🤖 Sistema de Recomendação Adaptativa

| Método | Endpoint                                           | Descrição                          |
|--------|----------------------------------------------------|------------------------------------|
| GET    | `/api/recomendacao/getRecomendacao/{id}`           | Recomendações algoritmo original   |
| GET    | `/api/recomendacao/getRecomendacaoAdaptativa/{id}` | Recomendações algoritmo adaptativo |
| POST   | `/api/recomendacao/aceitar`                        | Aceitar recomendação               |
| POST   | `/api/recomendacao/recusar`                        | Recusar recomendação               |
| GET    | `/api/recomendacao/estatisticas/{id}`              | Estatísticas do usuário            |
| POST   | `/api/recomendacao/resetarAlgoritmo/{id}`          | Resetar histórico do usuário       |

### 💚 Livros Curtidos

| Método | Endpoint                        | Descrição              |
|--------|---------------------------------|------------------------|
| GET    | `/api/livro-aceito/{usuarioId}` | Listar livros curtidos |
| POST   | `/api/livro-aceito`             | Curtir livro           |
| DELETE | `/api/livro-aceito`             | Descurtir livro        |

## 📊 Exemplos de Uso

### Aceitar uma Recomendação

```bash
curl -X POST "http://localhost:8080/api/recomendacao/aceitar" \
  -d "idUsuario=1&idLivro=5"
```

### Recusar uma Recomendação

```bash
curl -X POST "http://localhost:8080/api/recomendacao/recusar" \
  -d "idUsuario=1&idLivro=3"
```

### Ver Estatísticas do Algoritmo

```bash
curl "http://localhost:8080/api/recomendacao/estatisticas/1"
```

**Resposta:**

```json
{
  "totalRecusas": 7,
  "totalAceites": 2,
  "taxaAceitacao": 22.22,
  "estrategiaAtual": "DIVERSIFICADO"
}
```

### Obter Recomendações Adaptativas

```bash
curl "http://localhost:8080/api/recomendacao/getRecomendacaoAdaptativa/1"
```

## 🗄️ Estrutura do Banco de Dados

### Tabelas Principais

- **usuarios** - Dados dos usuários e preferências
- **livros** - Catálogo de livros disponíveis
- **livros_aceitos** - Histórico de livros curtidos
- **livros_recusados** - Histórico de livros recusados

### Dados Iniciais

O sistema vem com 30 livros pré-cadastrados e usuários de exemplo para teste.

## 🔧 Configuração

### Variáveis de Ambiente (Docker)

- `SPRING_DATASOURCE_URL` - URL do banco MySQL
- `SPRING_DATASOURCE_USERNAME` - Usuário do banco
- `SPRING_DATASOURCE_PASSWORD` - Senha do banco
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Configuração do Hibernate

### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/matchbook
spring.datasource.username=matchbook_user
spring.datasource.password=matchbook_pass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🧪 Testando o Sistema

1. **Criar um usuário** via POST `/api/usuario`
2. **Obter recomendações** via GET `/api/recomendacao/getRecomendacaoAdaptativa/{id}`
3. **Aceitar/Recusar livros** para treinar o algoritmo
4. **Ver como o algoritmo se adapta** via GET `/api/recomendacao/estatisticas/{id}`

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido com ❤️ usando Spring Boot + Docker + Algoritmos Adaptativos**
