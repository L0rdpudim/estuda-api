# Estuda — API de apoio aos estudos

O **Estuda** é uma plataforma de apoio ao ensino e à aprendizagem. Este repositório contém o backend do sistema, desenvolvido inicialmente como uma API REST em Java e Spring Boot.

Este README reúne a apresentação do projeto, as regras de negócio, as decisões já tomadas, o estado técnico do código e as orientações de desenvolvimento. Ele também foi escrito para servir como material de contexto em apresentações e em novas conversas sobre o projeto. A equipe de desenvolvimento é formada por três integrantes.

> **Atenção:** este documento diferencia o que já existe no código, o que foi definido como regra de negócio e o que ainda está planejado ou precisa de decisão. Uma funcionalidade descrita aqui não deve ser considerada implementada apenas por estar documentada.

---

## 1. Visão geral

O Estuda é uma plataforma web de apoio ao ensino e à aprendizagem que reúne conteúdos didáticos e atividades organizados por etapa de ensino, ano escolar, disciplina e aula. Seu principal objetivo é facilitar o acesso dos estudantes a materiais de estudo, promover a prática do que foi aprendido e acompanhar o progresso individual. Além disso, possui uma parte dedicada a questões de ENEM e oferece recursos que apoiam os professores na organização e disponibilização de conteúdos, bem como na aplicação de metodologias de ensino.

A plataforma deverá permitir que o estudante selecione seu nível de ensino e encontre conteúdos compatíveis com seu perfil. Ao longo da evolução do projeto, serão acrescentados recursos de personalização, atividades avaliativas, simulados e ferramentas para acompanhar o desempenho.

### Objetivos principais

- Organizar materiais didáticos por etapa de ensino, ano escolar, disciplina e aula.
- Facilitar o estudo e a prática dos conteúdos.
- Permitir que o estudante realize atividades e acompanhe seus resultados.
- Disponibilizar uma área de preparação para o ENEM.
- Permitir a contribuição de professores, mantendo controle editorial sobre o conteúdo publicado.
- Preservar o histórico individual de atividades e resultados.

## 2. Público e perfis

A plataforma considera dois perfis principais:

**Estudante:** acessa conteúdos publicados, realiza atividades, responde questões, consulta seus resultados e acompanha o próprio progresso. Poderá informar preferências para receber recomendações básicas.

**Professor:** após ter sua condição docente validada, poderá cadastrar conteúdos, editar seus próprios rascunhos e enviar sugestões de novos materiais ou de alterações em conteúdos existentes. Suas contribuições passam por avaliação antes de modificar o conteúdo oficial.

A equipe de desenvolvimento e manutenção também exerce uma função administrativa/editorial: analisa a comprovação de docência e avalia as contribuições enviadas pelos professores.

## 3. Fluxos principais previstos

### Fluxo do estudante

1. Acessar a plataforma com sua conta individual.
2. No primeiro acesso, responder opcionalmente ao questionário de preferências ou ignorá-lo.
3. Selecionar etapa de ensino, ano escolar, disciplina e aula.
4. Consultar o conteúdo didático disponibilizado.
5. Responder às questões associadas à aula ou iniciar uma atividade de prática.
6. Solicitar a correção ou finalizar a atividade.
7. Consultar o resultado e acompanhar o próprio histórico e progresso.
8. Acessar a área do ENEM e escolher uma prova de ano específico ou uma disciplina.

### Fluxo do professor

1. Solicitar acesso ao perfil de professor e apresentar comprovação da condição docente.
2. Aguardar a análise da equipe responsável.
3. Após a validação, utilizar as funcionalidades permitidas ao perfil.
4. Cadastrar conteúdos e editar rascunhos próprios.
5. Enviar sugestões de novas aulas ou de melhoria/correção de conteúdos publicados.
6. Aguardar a avaliação editorial da equipe.
7. Receber crédito de contribuição quando uma sugestão for aprovada e incorporada.

## 4. Escopo e funcionalidades

O projeto deve evoluir gradualmente. A primeira prioridade é construir uma base de backend organizada e testável, sem desenvolver simultaneamente todas as funcionalidades nem antecipar a integração com o frontend.

### Funcionalidades previstas para a plataforma

- Contas individuais e diferenciação entre estudante e professor.
- Organização e consulta de conteúdos didáticos.
- Questionário opcional de preferências no primeiro acesso.
- Recomendações básicas de conteúdo.
- Questões fixas associadas às aulas teóricas.
- Atividades de prática com questões selecionadas de um banco.
- Correção de respostas no backend.
- Registro separado de cada tentativa.
- Preparação para o ENEM por prova/ano ou por disciplina.
- Histórico e acompanhamento do progresso.
- Cadastro, edição de rascunhos e envio de sugestões por professores validados.
- Revisão editorial, registro de decisões e créditos de contribuição.

### Limites do MVP e evolução futura

O MVP prevê recomendações simples, que podem considerar preferências por vídeos, textos, atividades práticas, disciplinas e temas de interesse. Recomendações adaptativas avançadas, que alterem continuamente as trilhas com base no histórico e no desempenho, ficam para versões futuras.

As regras de negócio definem o comportamento esperado, mas nem todas as funcionalidades já possuem decisões técnicas ou implementação. As pendências conhecidas estão registradas na seção [Decisões e definições pendentes](#12-decisões-e-definições-pendentes).

## 5. Regras de negócio

As regras abaixo consolidam as regras fornecidas para o projeto. Os identificadores RN devem ser preservados em discussões, tarefas, documentação e implementação, para facilitar rastreabilidade.

### 5.1. Usuários, perfis e permissões

**RN-001 — Contas individuais:** cada usuário deverá possuir uma conta individual, com seus próprios dados, email, senha e histórico.

**RN-002 — Perfis de usuário:** a plataforma deverá distinguir estudantes e professores, disponibilizando funcionalidades de acordo com as permissões de cada perfil.

**RN-003 — Privacidade:** o estudante poderá consultar seu próprio histórico e progresso. Não poderá consultar nem alterar os dados privados de outro estudante.

**RN-004 — Validação do docente:** as permissões de professor somente serão liberadas após a validação da condição docente pela equipe responsável.

### 5.2. Conteúdos didáticos e contribuição dos professores

**RN-005 — Hierarquia dos conteúdos:** os conteúdos serão organizados por etapa de ensino, ano escolar, disciplina e aula.

**RN-006 — Acesso aos conteúdos:** o estudante terá acesso aos conteúdos publicados e disponibilizados na plataforma.

**RN-007 — Cadastro e edição:** o professor poderá cadastrar conteúdos e editar seus rascunhos, além de enviar sugestões de novas aulas e propostas de melhoria ou correção de aulas existentes.

**RN-008 — Avaliação de sugestões:** toda sugestão de novo conteúdo ou alteração de conteúdo compartilhado deverá ser avaliada pela equipe de desenvolvimento e manutenção da plataforma antes de ser publicada.

**RN-009 — Incorporação das sugestões:** uma sugestão aprovada será incorporada ao conteúdo oficial; uma sugestão não aprovada não deverá alterar o material publicado.

**RN-010 — Créditos de contribuição:** o professor que tiver uma contribuição aprovada e incorporada deverá receber crédito de autoria/colaboração junto ao conteúdo correspondente. Os créditos de contribuição deverão ser diferenciados das referências bibliográficas utilizadas na elaboração do material.

**RN-011 — Histórico de alterações:** o histórico de alterações deverá identificar a contribuição recebida e a decisão de aprovação/rejeição, ao menos para fins de controle editorial.

### 5.3. Validação do perfil de professor

**RN-012 — Método de comprovação:** a primeira opção recomendada para comprovação será a Carteira Nacional Docente do Brasil (CNDB), com conferência de autenticidade e validade, inclusive por seu QR Code quando disponível.

**RN-013 — Métodos alternativos:** caso o candidato não apresente CNDB, poderão ser avaliados documentos alternativos, como contrato de trabalho, termo de posse/nomeação, CTPS, contracheque, declaração da instituição de ensino ou carteira funcional digital.

**RN-014 — Registro:** a validação da CNDB ou dos documentos alternativos será responsabilidade da equipe de desenvolvimento e manutenção da plataforma. Qualquer integrante da equipe poderá realizar a análise; não será necessário designar uma única pessoa para essa atividade. A decisão de validação deverá ser registrada, identificando o resultado, a data e o integrante da equipe que realizou a análise.

**RN-015 — Proteção dos dados:** os documentos comprobatórios deverão ter acesso restrito à equipe autorizada e não poderão ser disponibilizados publicamente, nem ser expostos em respostas da API, páginas públicas ou registros de log.

**RN-016 — Prazo da verificação:** a plataforma deverá solicitar somente os dados e documentos necessários para verificar a condição docente. O prazo de retenção e o procedimento de exclusão dos documentos ainda não foram definidos e deverão ser estabelecidos.

### 5.4. Questionário e personalização

**RN-017 — Primeiro acesso:** no primeiro acesso, o estudante poderá responder a um questionário sobre escolaridade, disciplinas de interesse, metodologias preferidas e interesse em questões do ENEM.

**RN-018 — Questionário opcional:** o questionário será opcional. O estudante poderá ignorá-lo e acessar a página inicial normalmente.

**RN-019 — Correção sob solicitação:** as respostas somente deverão ser corrigidas após o estudante acionar a opção de verificar resposta ou finalizar a atividade correspondente. O estudante poderá preencher ou atualizar suas preferências posteriormente.

**RN-020 — Recomendações de conteúdo:** no MVP, as recomendações serão básicas e poderão considerar preferências por vídeos, textos, atividades práticas, disciplinas e conteúdos de interesse.

**RN-021 — Recomendações avançadas:** recomendações adaptativas avançadas, que utilizem histórico e desempenho para alterar continuamente a trilha, ficarão para versões futuras.

### 5.5. Atividades, questões e correção

**RN-022 — Aleatoriedade das questões:** as questões inseridas durante as aulas teóricas serão fixas, mantendo o mesmo conjunto de questões associado à aula. As atividades da área de prática serão selecionadas aleatoriamente a partir de um banco de questões.

**RN-023 — Questões de ENEM:** as questões da área ENEM também serão obtidas do banco de questões, respeitando o filtro escolhido pelo estudante.

**RN-024 — Gabarito e correção:** cada questão objetiva deverá possuir previamente cadastrada uma resposta correta/gabarito, que servirá de base para a correção.

**RN-025 — Confirmação de resposta:** a correção somente será realizada quando o estudante acionar “Verificar resposta” ou a ação de finalizar/corrigir a atividade correspondente.

**RN-026 — Correção:** a correção e o cálculo do resultado deverão ser realizados pela API/backend, utilizando o gabarito e a regra de correção cadastrados. O frontend não deverá definir nem enviar a nota final como fonte de verdade, e o gabarito não deverá ser exposto ao estudante antes da correção.

**RN-027 — Repetição/registro de atividades:** o estudante poderá refazer atividades. Cada tentativa deverá ser registrada separadamente, sem apagar as tentativas anteriores. O registro de cada tentativa deverá conter, no mínimo, o estudante, a atividade/sessão, as questões apresentadas, as respostas enviadas, a data/hora e o resultado calculado.

**RN-028 — Formato da questão:** cada formato de questão deverá possuir tipo de resposta e regra de correção compatíveis com seu gabarito.

### 5.6. Preparação para o ENEM

**RN-029 — Provas anteriores:** o estudante poderá escolher uma prova de um ano específico para resolver.

**RN-030 — Disciplinas:** como alternativa, o estudante poderá selecionar uma disciplina (por exemplo, Matemática) para responder questões daquela área.

**RN-031 — Seleção por ano:** quando selecionar um ano, a plataforma deverá apresentar as questões vinculadas à prova/ano selecionado; quando selecionar uma disciplina, deverá buscar questões do banco que correspondam ao filtro escolhido.

**RN-032 — Resultados:** o resultado da preparação para o ENEM deverá ficar registrado no histórico individual e ser identificável separadamente das atividades das aulas e da prática geral.

### 5.7. Progresso e histórico

**RN-033 — Progresso:** o progresso deverá considerar as atividades concluídas e os resultados registrados no servidor. O estudante poderá consultar seu progresso a qualquer momento após autenticar-se.

**RN-034 — Histórico:** o histórico deverá preservar tentativas anteriores, mesmo quando o estudante refizer a mesma atividade.

**RN-035 — Dados gerais:** a fórmula de progresso e os indicadores exibidos (por exemplo, melhor resultado, última tentativa, média ou taxa de acerto) deverão ser definidos antes de serem implementados.

## 6. Planejamento de desenvolvimento

A ordem abaixo é o planejamento inicial. A equipe poderá revisá-la conforme as dependências técnicas e as prioridades do projeto.

### Etapa 1 — Fundação do backend

- Organizar a arquitetura e os pacotes do projeto.
- Criar o primeiro endpoint REST e seu teste automatizado.
- Definir os modelos de domínio e os contratos da API.
- Definir e configurar o banco de dados.

### Etapa 2 — Funcionalidades principais

- Consultar disciplinas e conteúdos.
- Implementar a seleção do nível de ensino.
- Desenvolver a montagem das trilhas de estudo.
- Implementar atividades, correção de respostas e registro do progresso.

### Etapa 3 — Interface e integração

- Desenvolver a interface web.
- Integrar as telas com os endpoints da API.
- Implementar estados de carregamento, sucesso e erro.

### Etapa 4 — Funcionalidades complementares

- Implementar cadastro e autenticação, conforme a definição final do escopo.
- Desenvolver o painel de acompanhamento.
- Adicionar recomendações personalizadas e outros recursos planejados.

> **Ponto para alinhamento da equipe:** RN-001 e RN-002 já estabelecem contas individuais e perfis distintos, enquanto o planejamento da Etapa 4 menciona cadastro e autenticação como itens a implementar “caso definidos para o escopo”. É necessário decidir em qual etapa esses recursos serão efetivamente desenvolvidos; a regra de negócio, por si só, não resolve essa ordem de implementação.

## 7. Estado atual do backend

Esta seção descreve o conteúdo encontrado no arquivo compactado analisado para a elaboração deste README. Ela deverá ser atualizada conforme o código evoluir.

### Tecnologias e configuração encontradas

| Item | Estado encontrado |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.2 |
| Build e dependências | Maven, com Maven Wrapper |
| Aplicação web | `spring-boot-starter-webmvc` |
| Validação | `spring-boot-starter-validation` |
| Testes | `spring-boot-starter-test` (escopo de teste) |
| Desenvolvimento local | `spring-boot-devtools` (runtime e opcional) |
| Nome da aplicação | `estuda-api` |
| Porta configurada | `8080` |
| Banco de dados | Ainda não configurado no snapshot analisado |
| Persistência/JPA | Dependências ainda não identificadas no `pom.xml` analisado |
| Autenticação/segurança | Ainda não identificada implementação ou dependência de segurança no snapshot analisado |
| Endpoints de negócio | Ainda não identificados no código analisado |

A classe principal é `ApoioEstudosApiApplication.java`, no pacote `br.com.estuda.apoioestudos`. O teste existente `ApoioEstudosApiApplicationTests` contém um teste de carregamento do contexto (`contextLoads`).

**Importante:** o estado acima é uma leitura do ZIP enviado, não uma garantia de que o repositório remoto ou a cópia local permaneçam idênticos. Antes de orientar alterações, deve-se conferir o código atual.


## 8. Direção arquitetural recomendada para [P0] #002

Para organizar o backend neste estágio, recomenda-se uma aplicação única (monólito modular), separada por módulos funcionais (`usuario`, `professor`, `conteudo`, `atividade`, `enem` e `progresso`). Dentro de cada módulo, as classes serão separadas por responsabilidade: `controller`, `service`, `repository`, `entity` e `dto`.

Essa estrutura em camadas é compatível com o Spring Boot e atende ao escopo atual sem introduzir complexidade desnecessária. A recomendação deve ser validada pelos três integrantes da equipe antes de ser registrada como decisão definitiva. A alternativa de Clean Architecture com `presentation`, `application`, `domain` e `infrastructure` permanece como possibilidade futura; não se recomenda misturar as duas estruturas no mesmo módulo.

A documentação detalhada, as responsabilidades, a direção das chamadas e as convenções estão em [`ArqCamadas.md`](ArqCamadas.md).

### Responsabilidade conceitual dos módulos

| Módulo | Responsabilidade esperada |
|---|---|
| `usuario` | Contas, dados de usuário, identidade e informações comuns aos perfis. |
| `professor` | Perfil docente, comprovação/validação e contribuições editoriais. |
| `conteudo` | Etapas de ensino, anos escolares, disciplinas, aulas e materiais publicados. |
| `atividade` | Banco de questões, atividades, tentativas, respostas e correção. |
| `enem` | Montagem e consulta de atividades de preparação para o ENEM por ano ou disciplina. |
| `progresso` | Consulta e consolidação do histórico e dos resultados do estudante. |

Essas responsabilidades são uma referência inicial derivada dos requisitos. As fronteiras entre módulos e os relacionamentos entre seus modelos ainda devem ser detalhados antes da implementação.

### Cuidados ao evoluir a estrutura

- Criar subpacotes internos nos módulos à medida que as classes e responsabilidades surgirem; não criar camadas vazias em todos os módulos apenas para preencher a árvore.
- Manter `common` pequeno, reservado a recursos realmente compartilhados e transversais.
- Evitar que um módulo acesse diretamente os repositórios internos de outro módulo. Preferir serviços ou contratos públicos bem definidos.
- Evitar duplicar entidades e regras de negócio entre `atividade`, `enem` e `progresso`.
- Não misturar DTOs da API com entidades de persistência sem uma decisão consciente.
- Manter a aplicação como um backend organizado; não introduzir microserviços sem uma necessidade concreta e uma decisão da equipe.

## 9. Convenções de desenvolvimento

As convenções abaixo são orientações para as próximas implementações. Devem ser refinadas e adotadas pela equipe à medida que a arquitetura for definida.

1. **Responsabilidade única:** cada classe deve ter uma responsabilidade clara.
2. **Controller enxuto:** controllers devem receber requisições, validar/encaminhar dados e montar respostas; regras de negócio devem ficar na camada apropriada.
3. **Validação de entrada:** validar os dados recebidos pela API e retornar erros consistentes.
4. **DTOs para contratos da API:** definir explicitamente os dados aceitos e devolvidos pelos endpoints, sem expor automaticamente todos os campos internos.
5. **Regras no backend:** decisões de permissão, correção e cálculo de resultados devem ser verificadas no servidor.
6. **Privacidade por padrão:** cada estudante só pode acessar os próprios dados privados; documentos de validação docente precisam de controle de acesso e não podem aparecer em respostas ou logs.
7. **Testes automatizados:** adicionar testes para comportamentos relevantes, regras de negócio e endpoints, além do teste de contexto.
8. **Configuração fora do código:** não versionar senhas, tokens ou credenciais. Utilizar variáveis de ambiente/configuração apropriada quando integrações e banco forem definidos.
9. **Mudanças graduais:** implementar e validar uma parte por vez, mantendo o projeto compilável e testável.
10. **Documentação rastreável:** relacionar funcionalidades e testes aos requisitos RN correspondentes sempre que possível.


## 10. Decisões e definições pendentes

Os itens abaixo ainda precisam ser definidos ou confirmados antes de implementar as partes correspondentes:

- **Banco de dados:** escolher a tecnologia, configurar a conexão e definir a estratégia de persistência.
- **Autenticação e autorização:** definir quando serão implementadas e como serão protegidos os endpoints e os perfis.
- **Escopo de contas no MVP:** alinhar a regra de contas individuais com a posição dessa funcionalidade no planejamento por etapas.
- **Modelos de domínio e contratos da API:** especificar entidades/modelos, relacionamentos, endpoints, formatos de requisição e resposta e códigos de erro.
- **Primeiro endpoint:** escolher um endpoint inicial simples, implementá-lo e criar seu teste automatizado.
- **Questões e correção:** detalhar os tipos de questão, formatos de resposta e regras de correção para cada formato.
- **Progresso:** definir a fórmula de cálculo e quais indicadores serão apresentados, conforme RN-035.
- **Documentos de professor:** definir prazo de retenção, procedimento de exclusão e controles técnicos de armazenamento, conforme RN-016.
- **Validação docente:** definir o procedimento operacional de conferência e registro da análise, respeitando RN-012 a RN-015.
- **Revisão editorial:** definir os estados, responsáveis e fluxo de aprovação/rejeição das sugestões de professores.
- **Personalização:** detalhar como as preferências informadas no questionário serão convertidas em recomendações básicas.

Não assumir valores ou decisões para esses itens sem alinhamento com a equipe.

## 11. Nomenclatura
Elemento	    Padrão	                            Exemplo

Pacotes	        minúsculos	                        usuario.controller
Classes	        PascalCase	                        UsuarioController
Métodos	        camelCase	                        buscarPorEmail()
Variáveis	    camelCase	                        nomeUsuario
Constantes	    UPPER_SNAKE_CASE	                LIMITE_TENTATIVAS
Controllers	    Sufixo Controller	                UsuarioController
Services	    Sufixo Service	                    UsuarioService
Repositories	Sufixo Repository	                UsuarioRepository
DTOs	        Finalidade + Request/Response	    CriarUsuarioRequest


## 12. Como executar o projeto

### Pré-requisitos

- JDK 21 instalado e configurado no `PATH`.
- Git, caso seja necessário clonar ou versionar o repositório.
- Não é necessário instalar o Maven globalmente quando o Maven Wrapper do projeto estiver disponível.

### Windows — PowerShell

Na pasta raiz do projeto:

```powershell
# Executar os testes automatizados
.\mvnw.cmd test

# Iniciar a aplicação
.\mvnw.cmd spring-boot:run

# Gerar o pacote da aplicação
.\mvnw.cmd clean package
```

### Linux ou macOS

Na pasta raiz do projeto:

```bash
# Executar os testes automatizados
./mvnw test

# Iniciar a aplicação
./mvnw spring-boot:run

# Gerar o pacote da aplicação
./mvnw clean package
```

A configuração atual define a porta `8080`. Quando a aplicação estiver em execução, o endereço-base local será `http://localhost:8080`.

> No snapshot analisado ainda não foram identificados endpoints de negócio. Portanto, iniciar a aplicação não significa que os fluxos da plataforma já estejam disponíveis. Os endpoints deverão ser implementados e documentados conforme o backlog.

## 13. Testes

Os testes ficam em `src/test/java`. No snapshot analisado existe `ApoioEstudosApiApplicationTests`, com o teste `contextLoads`, destinado a verificar se o contexto da aplicação Spring consegue iniciar.

À medida que as funcionalidades forem implementadas, devem ser acrescentados testes unitários para regras de negócio e testes de integração para endpoints e persistência, quando esta última estiver configurada. O teste de contexto não substitui a validação dos comportamentos funcionais.

## 14. Material complementar

O planejamento e a apresentação do projeto também podem ser consultados no seguinte material da equipe:

- [Apresentação e planejamento do Estuda no Canva](https://www.canva.com/design/DAHYjcQQGmQ/TiOBHjucKEE0DI1pzeEboQ/edit)
