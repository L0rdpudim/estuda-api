# Arquitetura do backend — Estuda

## 1. Visão geral

O Estuda utiliza uma aplicação única (monólito modular), organizada em camadas. A proposta é separar as funcionalidades por módulo e manter, dentro de cada módulo, as responsabilidades de controller, service, repository, entity e DTO.

Essa organização evita complexidade desnecessária neste estágio e permite ampliar o backend conforme as funcionalidades forem implementadas.

## 2. Estrutura atual

A raiz dos pacotes Java é `br.com.estuda.apoioestudos`. A classe `ApoioEstudosApiApplication` fica diretamente nessa raiz para permitir que o Spring Boot encontre os componentes dos subpacotes.

Estrutura implementada:

```text
src/main/java/br/com/estuda/apoioestudos/
├── ApoioEstudosApiApplication.java
├── common/
│   └── ApiExceptionHandler.java
├── config/
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── controllers/
│   └── StatusController.java
├── dtos/
│   └── StatusResponse.java
├── services/
│   └── StatusService.java
└── usuario/
    ├── controller/
    │   └── UsuarioController.java
    ├── dto/
    │   ├── AutenticacaoResponse.java
    │   ├── CadastroUsuarioRequest.java
    │   ├── LoginRequest.java
    │   └── UsuarioAutenticadoResponse.java
    ├── entity/
    │   ├── PerfilUsuario.java
    │   ├── StatusValidacaoProfessor.java
    │   ├── TipoPerfil.java
    │   └── Usuario.java
    ├── repository/
    │   └── UsuarioRepository.java
    └── service/
        ├── CredenciaisInvalidasException.java
        ├── EmailJaCadastradoException.java
        ├── JwtService.java
        ├── UsuarioAutenticado.java
        └── UsuarioService.java
```

Os endpoints de status e cadastro/autenticação estão implementados. O módulo `usuario` mantém o domínio, DTOs, service e persistência de contas/perfis. A camada `config` valida tokens JWT Bearer, torna a API stateless e associa permissões efetivas ao usuário autenticado.

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
│   └── ApiExceptionHandler.java
└── config/
```

Os módulos `professor`, `conteudo`, `atividade`, `enem` e `progresso` deverão receber subpacotes conforme suas primeiras classes forem criadas. Não é necessário criar pacotes vazios antecipadamente.

No módulo `usuario`, cada `Usuario` possui um `PerfilUsuario` associado um-para-um. O perfil distingue `ESTUDANTE` e `PROFESSOR`; estudantes não possuem status de validação docente e professores iniciam com status `PENDENTE`. A estrutura física correspondente é criada pela migration `V2__usuarios.sql`.

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

O filtro JWT em `config` valida o token Bearer e define o principal/permissões no contexto do Spring Security antes do controller. O `UsuarioService` cria contas, verifica credenciais e emite tokens; o `ApiExceptionHandler` converte erros esperados para respostas HTTP. Rotas sob `/api/v1/professores/**` exigem `ROLE_PROFESSOR` e rotas sob `/api/v1/estudantes/**` exigem `ROLE_ESTUDANTE`; endpoints docentes também podem restringir métodos com `@PreAuthorize`. Todo usuário recebe `ROLE_ESTUDANTE`, enquanto professores só recebem a permissão docente após validação.

## 6. Persistência e banco de dados

O projeto utiliza **exclusivamente PostgreSQL**, tanto na execução da aplicação quanto nos testes automatizados. O acesso é feito com **Spring Data JPA/Hibernate**, e o **Flyway** controla as alterações estruturais por migrations.

- `application.properties`: configurações gerais e perfil padrão `local`.
- `application-local.properties`: conexão com PostgreSQL e configurações de JPA/Flyway.
- `application-test.properties`: conexão de teste com PostgreSQL e configurações de JPA/Flyway.
- `src/main/resources/db/migration/`: scripts SQL versionados pelo Flyway.

A migration `V1` cria a tabela usada no teste inicial de conexão. A migration `V2` cria `tb_usuario` e `tb_perfil_usuario`, com validações para os tipos de perfil e estados de validação docente. O email possui unicidade sem distinção entre maiúsculas e minúsculas.

Senhas são persistidas com BCrypt. Tokens JWT são assinados usando segredo `JWT_SECRET` fornecido pelo ambiente e expiram após uma hora por padrão; não se deve versionar o segredo.

O Hibernate utiliza `spring.jpa.hibernate.ddl-auto=validate`: valida o mapeamento sem criar ou atualizar tabelas. Toda alteração estrutural deve ser registrada em uma nova migration Flyway. Não utilizar bancos em memória ou outro SGBD nos testes; as classes de teste ativam o perfil `test` e exigem o PostgreSQL disponível.

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
- Testes de integração verificam a interação entre componentes e persistência no PostgreSQL.

Os testes exigem o PostgreSQL ligado e as mesmas variáveis de conexão usadas pela aplicação. O teste de contexto verifica a inicialização do Spring, mas não substitui os testes das regras de negócio.

## 9. Diretrizes

- Manter cada classe com uma responsabilidade clara.
- Não colocar regras de negócio nos controllers.
- Não expor entities diretamente nos endpoints.
- Manter `common` restrito ao que for compartilhado.
- Não acessar diretamente repositories internos de outros módulos.
- Não versionar senhas, tokens ou credenciais.
- Criar camadas e pacotes conforme as funcionalidades forem implementadas.
- Relacionar testes às regras de negócio correspondentes sempre que possível.
