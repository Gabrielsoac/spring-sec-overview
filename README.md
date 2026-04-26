# Overview Security - API com Spring Security & JWT

## Objetivo

Esta é uma aplicação para demonstrar a implementação de um setup completo de **autenticação e autorização com Spring Security** utilizando **tokens JWT (JSON Web Tokens)** em arquitetura **Stateless**.

O projeto leva a base educacional para entender como configurar uma API REST segura com Spring Boot, sem dependências de sessão no servidor.

---

## O que foi implementado

### Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 4.0.5** - Framework web
- **Spring Security** - Segurança e autenticação
- **JWT (Auth0 Java JWT 4.4.0)** - Tokens stateless
- **PostgreSQL** - Banco de dados
- **Spring Data JPA** - ORM para persistência
- **Flyway** - Migrations de banco de dados
- **Lombok** - Redução de boilerplate
- **Jakarta Validation** - Validação de dados

### Features Principais

**Autenticação JWT**

- Geração de tokens JWT com expiração de 24 horas
- Validação de tokens em cada requisição
- Armazenamento seguro de usuários com senha criptografada

**Registro de Usuários**

- Endpoint para criar novos usuários
- Criptografia de senha com BCrypt
- Validação de dados de entrada

**Login Seguro**

- Autenticação via email e senha
- Retorno de token JWT válido
- Integração com Spring Security Manager

**Segurança Configurada**

- CSRF desabilitado (apropriado para APIs stateless)
- CORS configurado
- Sessão Stateless (SessionCreationPolicy.STATELESS)
- Filtro customizado para validação de JWT
- Endpoints protegidos que exigem autenticação

**Persistência de Dados**

- Entidade User com JPA
- Migrations automáticas com Flyway
- Repositório com Spring Data JPA

---

## Como Utilizar

### 1. Pré-requisitos

- Java 17 ou superior instalado
- Maven instalado
- Docker e Docker Compose (para banco de dados)

### 2. Configurar o Banco de Dados

Inicie o PostgreSQL usando Docker Compose:

```bash
docker-compose up -d
```

Isso criará um container PostgreSQL com:

- **Host**: localhost
- **Porta**: 5432
- **Usuário**: postgres
- **Senha**: 1234
- **Database**: security

### 3. Executar a Aplicação

No diretório raiz do projeto:

```bash
./mvnw spring-boot:run
```

Ou, se preferir usar Maven instalado:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### 4. Fazer Requisições

#### Registrar um novo usuário

**Endpoint**: `POST /auth/register`

**Headers**:

```
Content-Type: application/json
```

**Body**:

```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Resposta (201)**:

```json
{
  "name": "João Silva",
  "email": "joao@example.com"
}
```

#### Fazer login

**Endpoint**: `POST /auth/login`

**Headers**:

```
Content-Type: application/json
```

**Body**:

```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Resposta (200)**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> **Importante**: Guarde bem o token JWT retornado. Ele será necessário para acessar endpoints protegidos.

#### Acessar endpoints protegidos

Para acessar qualquer endpoint protegido, inclua o token no header `Authorization`:

**Headers**:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Estrutura do Projeto

```
src/main/java/com/gabrielsoac/overview_security/
├── config/
│   ├── AuthConfig.java           # Implementação de UserDetailsService
│   ├── JWTUserData.java          # Record com dados do usuário no JWT
│   ├── SecurityConfig.java       # Configuração principal de segurança
│   ├── SecurityFilter.java       # Filtro customizado para validar JWT
│   └── TokenConfig.java          # Geração e validação de tokens JWT
├── controller/
│   └── AuthController.java       # Endpoints de autenticação
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   └── RegisterUserRequest.java
│   └── response/
│       ├── LoginResponse.java
│       └── RegisterUserResponse.java
├── entity/
│   └── User.java                 # Entidade User que implementa UserDetails
├── repository/
│   └── UserRepository.java       # Repositório JPA para User
└── OverviewSecurityApplication.java  # Classe principal
```

---

## Detalhes Técnicos

### Flow de Autenticação

1. **Registro**: Usuário envia email, nome e senha → Senha é criptografada com BCrypt → Usuário é salvo no banco
2. **Login**: Usuário envia email e senha → Spring Security valida credenciais → Token JWT é gerado e retornado
3. **Requisição Protegida**: Cliente envia token no header `Authorization: Bearer <token>` → SecurityFilter valida token → Request é processado

### Configuração de Segurança

**SecurityConfig.java** define:

- CSRF desabilitado (apropriado para APIs REST stateless)
- CORS habilitado
- Sessão Stateless (sem armazenamento de sessão no servidor)
- `/auth/login` e `/auth/register` públicos (sem autenticação)
- Todos os outros endpoints exigem autenticação
- SecurityFilter adicionado antes de UsernamePasswordAuthenticationFilter

### Geração de Token JWT

O token contém:

- **Subject**: Email do usuário
- **Claim `userId`**: ID do usuário no banco
- **Expiração**: 24 horas a partir da emissão
- **Algoritmo**: HMAC256 com secret key

**Nota**: Na produção, altere a secret key em `TokenConfig.java` para uma chave forte e segura!

---

## Testando a Aplicação

### Com cURL

```bash
# Registrar usuário
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria",
    "email": "maria@example.com",
    "password": "senha123"
  }'

# Fazer login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@example.com",
    "password": "senha123"
  }'
```

### Com Postman

1. Crie uma collection
2. Importe os endpoints de `/auth/register` e `/auth/login`
3. Execute o registro e o login
4. Copie o token retornado
5. Para endpoints protegidos, use: `Authorization: Bearer <seu-token>`

---

## Configurações Importantes

### `application.properties`

```properties
# Banco de dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/security
spring.datasource.username=postgres
spring.datasource.password=1234

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Flyway (migrations)
spring.flyway.enabled=true
```

Deve ser alterado em produção:

- Credenciais do banco
- Host/porta do PostgreSQL
- Logging do SQL

### `TokenConfig.java`

```java
private String secret = "secret";
```

Para produção é necessário utilizar uma environment, removendo o dado hardcoded:

---

## Extensões Possíveis

Este projeto é um setup básico, mas algumas ideias do que pode ser realizado à partir dele:

- **Refresh Tokens** - Implementar tokens de refresh para renovar acesso
- **Roles e Permissões** - Adicionar authorities (ADMIN, USER, etc.)
- **Rate Limiting** - Limitar tentativas de login
- **Email Verification** - Verificar email antes de ativar usuário
- **OAuth2** - Integrar com Google, GitHub, etc.
- **Auditoria** - Registrar tentativas de login e operações

---

## Referências

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io - Introduction to JSON Web Tokens](https://jwt.io/introduction)
- [Spring Boot Security Guide](https://spring.io/guides/topicals/spring-security-architecture/)
- [Auth0 Java JWT Library](https://github.com/auth0/java-jwt)

---
