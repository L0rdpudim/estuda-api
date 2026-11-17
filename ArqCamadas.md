# Arquitetura do backend — Estuda

## 1. Visão geral

O Estuda utiliza uma aplicação única (monólito modular), organizada em camadas. A proposta é separar as funcionalidades por módulo e manter, dentro de cada módulo, as responsabilidades de controller, service, repository, entity e DTO.

Essa organização evita complexidade desnecessária neste estágio e permite ampliar o backend conforme as funcionalidades forem implementadas.

## 2. Estrutura atual

A raiz dos pacotes Java é `br.com.estuda.apoioestudos`. A classe `ApoioEstudosApiApplication` fica diretamente nessa raiz para permitir que o Spring Boot encontre os componentes dos subpacotes.

No código analisado, a estrutura implementada ainda é inicial:

```text
src/main/java/br/com/estuda/apoioestudos/
├── ApoioEstudosApiApplication.java
├── controllers/
│   └── StatusController.java
├── services/
│   └── StatusService.java
└── dtos/
    └── StatusResponse.java
```

O endpoint de status é a funcionalidade atualmente implementada. Os módulos de negócio e suas camadas devem ser criados conforme cada funcionalidade entrar em desenvolvimento.

## 3. Estrutura prevista

A organização futura será por módulo funcional:

```text
br/com/estuda/apoioestudos/
├── ApoioEstudosApiApplication.java
├── usuario/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── professor/
├── conteudo/
├── atividade/
├── enem/
├── progresso/
├── common/
│   └── exception/
└── config/
```

Os módulos `professor`, `conteudo`, `atividade`, `enem` e `progresso` deverão receber subpacotes conforme suas primeiras classes forem criadas. Não é necessário criar pacotes vazios antecipadamente.

## 4. Responsabilidades das camadas

| Camada | Responsabilidade |
|---|---|
| `controller` | Receber requisições HTTP e retornar respostas. |
| `service` | Executar casos de uso e aplicar regras de negócio. |
| `repository` | Consultar e persistir dados. |
| `entity` | Representar os dados e objetos persistidos. |
| `dto` | Definir os dados recebidos e enviados pela API. |
| `config` | Reunir configurações técnicas e beans do Spring. |
| `common` | Manter recursos realmente compartilhados, como exceções globais. |

Controllers não devem acessar repositories diretamente. Services coordenam as operações e regras de negócio. Repositories não devem conhecer HTTP nem DTOs da API. Entities não devem ser expostas diretamente como contratos da API.

## 5. Fluxo de uma requisição

```text
Requisição HTTP
      ↓
  Controller
      ↓
   Service
      ↓
  Repository
      ↓
Banco de dados
```

O controller recebe ou devolve DTOs. O service aplica as regras de negócio e utiliza os repositories necessários. O repository é responsável pelo acesso aos dados.

Quando um módulo precisar de uma operação de outro módulo, deve preferir chamar um serviço público desse módulo, em vez de acessar diretamente seu repository.

## 6. Persistência e banco de dados

O ambiente local utiliza **PostgreSQL**. O acesso aos dados é feito com **Spring Data JPA/Hibernate**, e o **Flyway** controla as alterações da estrutura do banco por migrations.

- `application.properties`: configurações gerais e perfil padrão.
- `application-local.properties`: conexão local com PostgreSQL.
- `application-test.properties`: configuração do H2 em memória para testes.
- `src/main/resources/db/migration/`: scripts SQL versionados pelo Flyway.

No ambiente local, `spring.jpa.hibernate.ddl-auto=validate` faz o Hibernate validar o mapeamento, sem atualizar automaticamente as tabelas. As alterações estruturais devem ser registradas em migrations do Flyway.

Nos testes, o H2 é utilizado em memória e configurado para recriar a estrutura. Ele não substitui o PostgreSQL do ambiente local.

## 7. Convenções de nomenclatura

| Elemento | Convenção | Exemplo |
|---|---|---|
| Pacotes | minúsculos, sem acentos | `usuario.controller` |
| Classes | `PascalCase` | `UsuarioService` |
| Métodos e variáveis | `camelCase` | `buscarPorEmail` |
| Constantes | `UPPER_SNAKE_CASE` | `LIMITE_TENTATIVAS` |
| Controllers | sufixo `Controller` | `UsuarioController` |
| Services | sufixo `Service` | `UsuarioService` |
| Repositories | sufixo `Repository` | `UsuarioRepository` |
| DTOs | finalidade + `Request`/`Response` | `CriarUsuarioRequest` |
| Testes | nome da classe + `Test` | `UsuarioServiceTest` |

Os nomes devem seguir o vocabulário do projeto, em português. Não utilizar acentos ou espaços em nomes de pacotes, classes, métodos ou arquivos. Interfaces não precisam do prefixo `I`.

## 8. Organização dos testes

Os testes ficam em `src/test/java`, mantendo a mesma raiz de pacotes da aplicação. Sempre que possível, devem acompanhar os módulos e as classes testadas.

- Testes de service verificam regras de negócio.
- Testes de controller verificam as respostas HTTP.
- Testes de integração verificam a interação entre componentes e persistência.

O teste de contexto verifica se a aplicação Spring consegue iniciar, mas não substitui os testes das funcionalidades.

## 9. Diretrizes

- Manter cada classe com uma responsabilidade clara.
- Não colocar regras de negócio nos controllers.
- Não expor entities diretamente nos endpoints.
- Manter `common` restrito ao que for compartilhado.
- Não acessar diretamente repositories internos de outros módulos.
- Não versionar senhas, tokens ou credenciais.
- Criar camadas e pacotes conforme as funcionalidades forem implementadas.
- Relacionar testes às regras de negócio correspondentes sempre que possível.
