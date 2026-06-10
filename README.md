# Raízes do Nordeste API

API REST em Java/Spring Boot para gestão de pedidos multicanal, cardápio, estoque, pagamentos mock, fidelidade e conformidade LGPD da rede **Raízes do Nordeste**.

## Tecnologias

- Java 17
- Spring Boot 3.2
- Spring Security + JWT (stateless)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway (migrations)
- Springdoc OpenAPI (Swagger)
- Maven
- Lombok
- spring-dotenv (carregamento de `.env`)

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker (para PostgreSQL local)

## Como executar

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd raizesdonordeste.api
```

### 2. Configurar variáveis de ambiente

Copie o arquivo de exemplo e ajuste os valores:

```bash
cp .env.example .env
```

Edite o `.env` com suas credenciais. **Nunca commite o `.env`** — ele já está no `.gitignore`.

| Variável | Descrição |
|----------|-----------|
| `DB_URL` | URL JDBC do PostgreSQL |
| `DB_USER` | Usuário do banco (aplicação) |
| `DB_PASSWORD` | Senha do banco (aplicação) |
| `POSTGRES_DB` | Nome do banco (Docker Compose) |
| `POSTGRES_USER` | Usuário do banco (Docker Compose) |
| `POSTGRES_PASSWORD` | Senha do banco (Docker Compose) |
| `JWT_SECRET` | Chave secreta JWT (mín. 32 caracteres) |
| `JWT_EXPIRATION_MS` | Expiração do token em ms |
| `SERVER_PORT` | Porta da API (padrão: 8080) |

A aplicação carrega o `.env` automaticamente via [spring-dotenv](https://github.com/paulschwarz/spring-dotenv) na inicialização.

### 3. Subir o PostgreSQL

```bash
docker-compose up -d
```

O Docker Compose também lê o `.env` para configurar o container PostgreSQL.

### 4. Executar a aplicação

```bash
mvn spring-boot:run
```

As migrations Flyway rodam automaticamente na inicialização (`V1__schema.sql` e `V2__seed.sql`).

> **Importante:** Se você já rodou o projeto antes com IDs numéricos, recrie o banco:
> ```bash
> docker-compose down -v && docker-compose up -d
> ```

Todos os IDs da API são **UUID**. Os dados de seed usam UUIDs fixos para facilitar testes.

## Swagger

Acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui.html
```

## IDs de seed (UUID)

| Recurso | UUID |
|---------|------|
| Unidade Centro | `11111111-1111-1111-1111-111111111101` |
| Produto Baião de Dois | `33333333-3333-3333-3333-333333333301` |

## Usuários de seed

| Email | Senha | Role |
|-------|-------|------|
| gerente@raizes.com | 123456 | GERENTE |
| atendente@raizes.com | 123456 | ATENDENTE |
| cozinha@raizes.com | 123456 | COZINHA |

## Endpoints principais

| Método | Rota | Permissão |
|--------|------|-----------|
| POST | `/auth/register` | Público |
| POST | `/auth/login` | Público |
| GET | `/usuarios/me` | Autenticado |
| DELETE | `/usuarios/me` | CLIENTE |
| GET/POST/PUT/DELETE | `/unidades` | GERENTE |
| GET | `/produtos?unidadeId=` | Público |
| POST/PUT/DELETE | `/produtos` | GERENTE |
| GET | `/estoque` | GERENTE |
| POST | `/estoque/movimentar` | GERENTE |
| POST/GET | `/pedidos` | Conforme role |
| PATCH | `/pedidos/{id}/status` | COZINHA, GERENTE |
| POST | `/pagamentos/simular` | CLIENTE, ATENDENTE |
| GET | `/fidelidade/saldo` | CLIENTE |
| POST | `/fidelidade/resgatar` | CLIENTE |

## Coleção Postman

1. Abra o Postman
2. Importe o arquivo `RaizesBackend.postman_collection.json`
3. Configure as variáveis de ambiente:
   - `baseUrl`: `http://localhost:8080`
   - `token`: preenchido automaticamente após o login
4. Execute os cenários na ordem sugerida

## Testes

```bash
mvn test
```

Os testes usam H2 em memória com profile `test`.

## Estrutura de pacotes

```
br.com.raizes
├── domain          # Entidades, enums, exceções
├── application     # Serviços, DTOs, mappers
├── infrastructure  # Repositories, JWT, auditoria
└── api             # Controllers, exception handler, Swagger
```

## Regras de negócio

- Pedidos validam estoque na criação (sem baixa imediata)
- Pagamento aprovado baixa estoque e move status para `EM_PREPARACAO`
- Cartões mock terminados em `1111` são recusados
- Promoção de segunda-feira aplica 10% de desconto automaticamente
- Fidelidade: 1 ponto por R$1 em pedidos `ENTREGUE`; 100 pontos = R$10 de desconto
- Cadastro exige `consentimentoLGPD: true`
