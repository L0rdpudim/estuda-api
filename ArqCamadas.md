# Arquitetura e organização do backend — Estuda

## 1. Direção arquitetural recomendada

Para a etapa **[P0] #002 — Organizar a estrutura do backend**, recomenda-se uma aplicação única (monólito modular), organizada primeiro por módulo funcional e, dentro de cada módulo, por responsabilidade.

Essa abordagem mantém próximas as classes que implementam uma mesma funcionalidade e separa os papéis de controller, service, repository, entity e DTO. É uma arquitetura em camadas tradicional do ecossistema Spring, suficiente para o estágio atual do Estuda e mais simples de manter do que introduzir desde já todas as divisões de Clean Architecture/Ports and Adapters.

A adoção definitiva dessa direção deve ser validada pelos três integrantes da equipe. A alternativa com `presentation`, `application`, `domain` e `infrastructure` pode ser reavaliada se a complexidade do sistema justificar essa separação no futuro. Não se recomenda misturar as duas convenções no mesmo módulo.

## 2. Estrutura de pacotes

A raiz dos pacotes Java é `br.com.estuda.apoioestudos`. A classe `ApoioEstudosApiApplication` permanece diretamente nessa raiz para que o Spring Boot encontre os componentes localizados nos subpacotes por meio do component scan padrão.

```text
src/
├── main/
│   ├── java/br/com/estuda/apoioestudos/
│   │   ├── ApoioEstudosApiApplication.java
│   │   ├── atividade/
│   │   ├── conteudo/
│   │   ├── enem/
│   │   ├── professor/
│   │   ├── progresso/
│   │   ├── usuario/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── common/
│   │   │   ├── exception/
│   │   │   └── validation/
│   │   └── config/
│   └── resources/
│       └── application.properties
└── test/java/br/com/estuda/apoioestudos/
    └── ApoioEstudosApiApplicationTests.java
```

Os módulos `atividade`, `conteudo`, `enem`, `professor` e `progresso` já representam áreas funcionais identificadas nos requisitos. Seus subpacotes internos devem ser criados à medida que suas primeiras classes forem implementadas; não é necessário criar pastas vazias em todos eles antecipadamente.

O módulo `usuario` serve como primeiro exemplo da divisão interna. Os arquivos `package-info.java` documentam e mantêm os pacotes no controle de versão, sem criar entidades, endpoints ou regras de negócio fictícias.

## 3. Responsabilidades das partes

### `controller` — entrada HTTP

Recebe as requisições da API, lê parâmetros e DTOs, aciona o serviço responsável e transforma o resultado em uma resposta HTTP.

Não deve conter regras de negócio, fazer consultas diretamente ao banco de dados ou decidir permissões sem consultar a camada responsável.

### `service` — operações e regras de negócio

Implementa ou coordena os casos de uso do módulo. É onde devem ficar validações de negócio, verificações de existência, aplicação de regras e coordenação dos repositories necessários.

Um service não deve depender de controllers. Quando uma funcionalidade de outro módulo for necessária, deve-se preferir uma operação pública do service daquele módulo em vez de acessar diretamente seu repository.

### `repository` — acesso a dados

Centraliza as operações de consulta e persistência. As interfaces e tecnologias específicas serão definidas quando a equipe escolher o banco de dados e a estratégia de persistência.

Como o snapshot atual ainda não possui banco de dados nem dependência de persistência, não se deve criar repository ou adicionar JPA apenas para preencher a estrutura.

### `entity` — modelos e entidades

Representa os dados e conceitos do domínio do módulo. Quando a estratégia de persistência for definida, as classes que precisarem ser persistidas poderão receber o mapeamento correspondente.

Entidades não devem ser usadas automaticamente como contratos da API e não devem depender de DTOs ou controllers.

### `dto` — contratos de entrada e saída

Define os dados que a API recebe e devolve. DTOs limitam a exposição dos dados internos e permitem validar as entradas.

Quando houver necessidade, os nomes devem indicar sua finalidade, por exemplo `CriarUsuarioRequest` e `UsuarioResponse`. Não se deve receber ou devolver a entidade diretamente por conveniência.

### `config` — configuração técnica

Agrupa configurações do Spring e criação de beans compartilhados. Não deve concentrar regras de negócio nem substituir os módulos funcionais.

### `common` — recursos transversais

Deve permanecer pequeno. É reservado a recursos genuinamente compartilhados, como tratamento global de exceções e validadores reutilizados por mais de um módulo. Uma regra exclusiva de `usuario`, por exemplo, deve permanecer dentro de `usuario`.

## 4. Direção das chamadas

O fluxo habitual de uma operação deve ser:

```text
Requisição HTTP
      ↓
 Controller
      ↓
   Service
      ↓
  Repository
      ↓
  Persistência
```

O controller também trabalha com DTOs de entrada e saída. O service aplica as regras e coordena o acesso aos dados. O repository não deve conhecer HTTP nem DTOs da API.

Regras práticas:

- Controller chama service; não chama repository diretamente.
- Service concentra as regras de negócio e chama os repositories necessários.
- Repository cuida de acesso a dados; não contém regras de fluxo da aplicação.
- DTOs representam contratos da API; entities representam o modelo interno/persistido.
- Um módulo não deve manipular diretamente o repository interno de outro módulo.
- Classes compartilhadas só vão para `common` quando houver reutilização real.

## 5. Convenções de nomenclatura

| Elemento | Convenção | Exemplo |
|---|---|---|
| Pacotes | minúsculos, sem acentos | `usuario.controller` |
| Classes e interfaces | `PascalCase` | `UsuarioService` |
| Métodos e variáveis | `camelCase` | `buscarPorEmail` |
| Constantes | `UPPER_SNAKE_CASE` | `LIMITE_TENTATIVAS` |
| Controller | sufixo `Controller` | `UsuarioController` |
| Service | sufixo `Service` | `UsuarioService` |
| Repository | sufixo `Repository` | `UsuarioRepository` |
| DTO | finalidade + `Request`/`Response` | `CriarUsuarioRequest` |
| Testes | nome da classe + `Test` | `UsuarioServiceTest` |

Os nomes devem ficar em português, acompanhando o vocabulário funcional já utilizado no projeto. Não usar acentos ou espaços em nomes de pacotes, classes, métodos ou arquivos. Interfaces não precisam receber prefixo `I`.

## 6. Organização dos testes

Os testes devem acompanhar os módulos e as classes testadas dentro de `src/test/java`, mantendo a mesma raiz de pacotes da aplicação. Exemplos:

```text
src/test/java/br/com/estuda/apoioestudos/
├── ApoioEstudosApiApplicationTests.java
└── usuario/
    ├── service/UsuarioServiceTest.java
    └── controller/UsuarioControllerTest.java
```

Testes de service devem verificar regras de negócio. Testes de controller devem verificar o comportamento HTTP. Testes de integração devem ser adicionados quando houver endpoints e persistência implementados.

## 7. Limites desta etapa

Nesta etapa não serão implementados cadastro de usuários, autenticação, banco de dados, endpoints de negócio nem integração com a interface web. O objetivo é estabelecer a organização e as convenções para que essas funcionalidades possam ser desenvolvidas depois, sem antecipar decisões ainda pendentes.

No ZIP analisado, além da classe principal e do teste `contextLoads`, não existem classes funcionais de controller, service, repository, entity ou DTO. Portanto, ainda não há classes dessas camadas para mover ou separar. A estrutura documentada e os `package-info.java` deixam os pacotes preparados, mas a implementação real das camadas ocorrerá junto com cada funcionalidade.