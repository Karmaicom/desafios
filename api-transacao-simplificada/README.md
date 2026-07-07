# API de Transacao Simplificada

API REST desenvolvida em Java com Spring Boot para simular transferencias financeiras entre usuarios comuns e lojistas. O projeto implementa o fluxo de validacao de uma transacao, consulta um servico autorizador externo, atualiza saldos em banco de dados e dispara notificacao apos a transferencia.

## Sumario

- [Sobre o projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Regras de negocio](#regras-de-negocio)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Documentacao da API](#documentacao-da-api)
- [Endpoint principal](#endpoint-principal)
- [Dados iniciais](#dados-iniciais)
- [Banco de dados](#banco-de-dados)
- [Testes](#testes)
- [Troubleshooting](#troubleshooting)

## Sobre o projeto

Este projeto representa uma versao simplificada de uma API de pagamentos. A aplicacao permite que um usuario comum envie dinheiro para outro usuario comum ou para um lojista, respeitando as regras basicas de saldo, permissao de usuario e autorizacao externa.

O codigo esta no diretorio `api-transacao-simplificada` deste repositorio.

## Funcionalidades

- Realizar transferencia entre usuarios.
- Validar se o pagador existe.
- Validar se o recebedor existe.
- Impedir que usuarios do tipo lojista realizem transferencias.
- Validar se o pagador possui saldo suficiente.
- Consultar servico externo de autorizacao antes de confirmar a transacao.
- Atualizar saldo do pagador e do recebedor.
- Persistir o historico da transacao.
- Enviar notificacao apos a transferencia.
- Popular usuarios e carteiras iniciais ao subir a aplicacao.
- Expor documentacao interativa via Swagger/OpenAPI.

## Tecnologias

- Java 21
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring Cloud OpenFeign
- PostgreSQL 16
- Docker e Docker Compose
- Maven
- Lombok
- Springdoc OpenAPI / Swagger UI

## Regras de negocio

1. Usuarios comuns podem enviar dinheiro para usuarios comuns e lojistas.
2. Lojistas podem receber dinheiro, mas nao podem realizar transferencias.
3. O pagador precisa ter saldo suficiente.
4. A transferencia precisa ser autorizada pelo servico externo.
5. A atualizacao dos saldos e o registro da transacao ocorrem dentro de uma transacao de banco de dados.
6. A notificacao e enviada apos a transacao ser salva.

## Arquitetura

Organizacao principal dos pacotes:

```text
src/main/java/com/ppay/apitransacaosimplificada
├── configurations   # Configuracoes da aplicacao, Swagger e carga inicial
├── controllers      # Controllers REST
├── dtos             # Objetos de entrada e saida
├── entities         # Entidades JPA
├── enums            # Enums do dominio
├── exceptions       # Excecoes customizadas
├── infraestructure  # Clientes Feign para servicos externos
├── reporitories     # Repositorios Spring Data JPA
└── services         # Regras de negocio
```

Fluxo resumido da transferencia:

```text
POST /transfer
  -> TransacaoController
  -> TransacaoService
  -> UsuarioService busca pagador e recebedor
  -> valida tipo do pagador
  -> valida saldo
  -> AutorizacaoService consulta servico externo
  -> CarteiraService atualiza saldos
  -> TransacaoRepository salva transacao
  -> NotificacaoService envia notificacao
```

## Como executar

### Pre-requisitos

- Java 21 instalado.
- Docker e Docker Compose instalados.
- Git instalado.

### 1. Clone o repositorio

```bash
git clone https://github.com/Karmaicom/desafios.git
cd desafios/api-transacao-simplificada
```

### 2. Suba o PostgreSQL e o pgAdmin

```bash
docker compose up -d
```

Servicos iniciados pelo Docker Compose:

| Servico | URL/porta | Credenciais |
| --- | --- | --- |
| PostgreSQL | `localhost:5433` | usuario `coti`, senha `Coti@2026` |
| pgAdmin | `http://localhost:5050` | email `admin@admin.com`, senha `Coti@2026` |

### 3. Execute a aplicacao

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

## Documentacao da API

Com a aplicacao em execucao, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

Tambem e possivel consultar a especificacao OpenAPI em:

```text
http://localhost:8080/v3/api-docs
```

## Endpoint principal

### Realizar transferencia

```http
POST /transfer
Content-Type: application/json
```

Body:

```json
{
  "value": 100.00,
  "payer": 1,
  "payee": 2
}
```

Resposta esperada em caso de aceite:

```http
202 Accepted
```

Exemplo com cURL:

```bash
curl -X POST http://localhost:8080/transfer \
  -H "Content-Type: application/json" \
  -d '{"value":100.00,"payer":1,"payee":2}'
```

No Windows PowerShell:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/transfer" `
  -ContentType "application/json" `
  -Body '{"value":100.00,"payer":1,"payee":2}'
```

## Dados iniciais

Na primeira execucao, caso a tabela de usuarios esteja vazia, a aplicacao cadastra automaticamente os seguintes usuarios e carteiras:

| ID esperado | Nome | Email | Documento | Tipo | Saldo inicial |
| --- | --- | --- | --- | --- | --- |
| 1 | Carlos Silva | `carlos@email.com` | `101111111111` | `COMUM` | `1000.00` |
| 2 | Ana Souza | `ana@email.com` | `22222222222` | `COMUM` | `2000.00` |
| 3 | Loja Exemplo | `loja@email.com` | `33333333333` | `LOJISTA` | `5000.00` |

Exemplos validos:

```json
{
  "value": 50.00,
  "payer": 1,
  "payee": 2
}
```

```json
{
  "value": 75.00,
  "payer": 2,
  "payee": 3
}
```

Exemplo invalido, pois lojista nao pode transferir:

```json
{
  "value": 10.00,
  "payer": 3,
  "payee": 1
}
```

## Banco de dados

Configuracao padrao da aplicacao:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/bd-api-transacaosimplificada
    username: coti
    password: Coti@2026
```

As tabelas sao criadas/atualizadas automaticamente pelo Hibernate por causa da configuracao:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

Principais tabelas:

- `usuario`
- `carteira`
- `transacao`

## Servicos externos

A API utiliza clientes Feign para integrar com os endpoints externos do desafio:

| Finalidade | Metodo | URL |
| --- | --- | --- |
| Autorizacao de transferencia | `GET` | `https://util.devi.tools/api/v2/authorize` |
| Envio de notificacao | `POST` | `https://util.devi.tools/api/v1/notify` |

Se algum desses servicos estiver indisponivel, a transferencia pode falhar durante a autorizacao ou notificacao.

## Testes

Para executar os testes:

Linux/macOS:

```bash
./mvnw test
```

Windows:

```bash
mvnw.cmd test
```

## Build

Para gerar o artefato da aplicacao:

Linux/macOS:

```bash
./mvnw clean package
```

Windows:

```bash
mvnw.cmd clean package
```

O arquivo `.jar` sera gerado em:

```text
target/api-transacao-simplificada-0.0.1-SNAPSHOT.jar
```

## Executando via Dockerfile

Depois de gerar o `.jar`, construa a imagem:

```bash
docker build -t api-transacao-simplificada .
```

Execute o container:

```bash
docker run --rm -p 8080:8080 api-transacao-simplificada
```

Observacao: ao executar a aplicacao em container, ajuste a URL do PostgreSQL para apontar para o host correto do banco de dados. O `application.yaml` atual usa `localhost:5433`, configuracao adequada para execucao local fora do container.

## Troubleshooting

### Porta 5433 ja esta em uso

Altere o mapeamento de portas no `docker-compose.yaml`:

```yaml
ports:
  - "5434:5432"
```

Depois ajuste tambem a URL em `application.yaml`:

```yaml
url: jdbc:postgresql://localhost:5434/bd-api-transacaosimplificada
```

### Banco nao conecta

Verifique se os containers estao em execucao:

```bash
docker compose ps
```

Consulte os logs do PostgreSQL:

```bash
docker compose logs postgres
```

### Swagger nao abre

Confirme se a aplicacao subiu sem erros e se a porta `8080` esta livre:

```text
http://localhost:8080/swagger-ui/index.html
```

### Transferencia nao autorizada

A autorizacao depende do endpoint externo `https://util.devi.tools/api/v2/authorize`. Se o servico retornar negativa ou estiver indisponivel, a transferencia nao sera concluida.

## Autor

Karmaicom Martins

- GitHub: [Karmaicom](https://github.com/Karmaicom)
- Email: `karmaicom@gmail.com`
