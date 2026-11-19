# Estuda — Backend API

O **Estuda** é uma plataforma web de apoio ao ensino e à aprendizagem. Este repositório contém o backend da aplicação, desenvolvido em **Java 21** e **Spring Boot**, responsável por disponibilizar a API, aplicar regras de negócio e acessar os dados armazenados.

A plataforma organiza conteúdos e atividades por etapa de ensino, ano escolar, disciplina e aula. O estudante poderá estudar os materiais, responder questões, acompanhar seu desempenho e acessar atividades de preparação para o ENEM. Professores com perfil validado poderão contribuir com conteúdos, sujeitos à revisão da equipe responsável.

> **Estado do projeto:** o backend está em desenvolvimento. A API disponibiliza um endpoint básico de status e já possui a estrutura de persistência para contas de estudantes e professores. O cadastro via API, autenticação e demais funcionalidades ainda não estão implementados.

## Tecnologias

- **Java 21** — linguagem de programação.
- **Spring Boot 4.1.2** — estrutura principal da aplicação.
- **Spring Web MVC** — criação dos endpoints REST.
- **Spring Data JPA / Hibernate** — integração e mapeamento de persistência.
- **PostgreSQL** — único SGBD utilizado pela aplicação e pelos testes automatizados.
- **Flyway** — controle de versões e execução de alterações no banco (migrations).
- **Maven Wrapper** — execução dos comandos Maven sem exigir uma instalação global do Maven.

## Pré-requisitos

Antes de executar o projeto, tenha instalado e configurado:

1. **JDK 21** — necessário para compilar e executar a aplicação. Verifique com `java -version`.
2. **PostgreSQL** — servidor do banco de dados instalado e em execução.
3. **Git** — necessário apenas para clonar o repositório.
4. **Visual Studio Code** ou outra IDE Java — recomendado para editar e executar o projeto.

O Maven não precisa ser instalado separadamente, pois o repositório possui Maven Wrapper (`mvnw` e `mvnw.cmd`). Na primeira execução, o Wrapper poderá baixar a distribuição do Maven automaticamente; portanto, será necessário acesso à internet.

## Como configurar o banco de dados

A aplicação e os testes utilizam exclusivamente PostgreSQL. A aplicação usa o perfil `local`; os testes usam o perfil `test`. Ambos usam as mesmas variáveis de conexão e, por padrão, o banco `estuda`.

### 1. Iniciar o PostgreSQL

Certifique-se de que o serviço do PostgreSQL esteja em execução. A configuração padrão do projeto considera:

| Configuração | Valor padrão |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Banco de dados | `estuda` |
| Usuário | `postgres` |
| Senha | A senha definida na instalação do PostgreSQL |

### 2. Criar o banco `estuda`

É possível criar o banco pelo pgAdmin 4:

1. Conecte-se ao servidor PostgreSQL.
2. Clique com o botão direito em **Databases**.
3. Selecione **Create → Database**.
4. Informe `estuda` no campo **Database**.
5. Selecione `postgres` como **Owner** e mantenha as demais opções padrão.
6. Salve.

Também é possível executar o comando abaixo pelo Query Tool do pgAdmin ou pelo `psql`:

```sql
CREATE DATABASE estuda;
```

**Não é necessário criar tabelas manualmente.** Ao iniciar a aplicação, o Flyway executa as migrations localizadas em `src/main/resources/db/migration`. A migration `V1` cria a tabela `tb_teste_conexao`; a `V2` cria as tabelas `tb_usuario` e `tb_perfil_usuario`, além da restrição de unicidade de email sem diferenciar maiúsculas de minúsculas. O Hibernate está configurado para validar o mapeamento, não para atualizar a estrutura automaticamente.

### 3. Configurar a senha do PostgreSQL

A senha é lida pela variável de ambiente `DB_PASSWORD` e não deve ser publicada no repositório.

**Windows — PowerShell:**

```powershell
$env:DB_PASSWORD = "SUA_SENHA_DO_POSTGRES"
```

**Linux ou macOS — terminal:**

```bash
export DB_PASSWORD="SUA_SENHA_DO_POSTGRES"
```

Substitua `SUA_SENHA_DO_POSTGRES` pela senha definida para o usuário PostgreSQL.

As variáveis `DB_USERNAME` e `DB_URL` são opcionais. Quando não forem definidas, o projeto utilizará os valores padrão `postgres` e `jdbc:postgresql://localhost:5432/estuda`, respectivamente.

Exemplo opcional no PowerShell:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_URL = "jdbc:postgresql://localhost:5432/estuda"
```

A variável `DB_PASSWORD` precisa ser definida novamente quando um novo terminal do PowerShell for aberto, a menos que tenha sido configurada permanentemente no sistema.

## Como executar a aplicação

Abra o terminal na pasta raiz do projeto, onde está localizado o arquivo `pom.xml`.

### Windows — PowerShell

```powershell
# Iniciar a API
.\mvnw.cmd spring-boot:run
```

### Linux ou macOS

```bash
# Necessário apenas se o arquivo não tiver permissão de execução
chmod +x mvnw

# Iniciar a API
./mvnw spring-boot:run
```

A aplicação será iniciada na porta `8080`.

### Verificar se a API está funcionando

Com a aplicação em execução, acesse pelo navegador ou por uma ferramenta de requisições HTTP:

```text
http://localhost:8080/api/v1/status
```

Resposta esperada:

```json
{
  "status": "online",
  "message": "API funcionando corretamente"
}
```

O endereço `http://localhost:8080/` pode retornar erro 404 porque não existe uma rota configurada para a raiz `/`. Para verificar a API, utilize o endpoint `/api/v1/status`.

### Encerrar a aplicação

No terminal em que o Spring Boot está sendo executado, pressione **Ctrl + C**. Isso encerra a aplicação, mas não desliga o servidor PostgreSQL. Os dados armazenados no PostgreSQL permanecem salvos.

Para iniciar novamente, execute o comando `spring-boot:run` indicado acima.

## Testes e build

Os testes automatizados utilizam PostgreSQL com o perfil `test`. Antes de executá-los, mantenha o serviço PostgreSQL ligado, o banco `estuda` criado e `DB_PASSWORD` configurada no terminal.

**Windows — PowerShell:**

```powershell
# Executar os testes automatizados
.\mvnw.cmd test

# Limpar e gerar o pacote da aplicação
.\mvnw.cmd clean package
```

**Linux ou macOS:**

```bash
# Executar os testes automatizados
./mvnw test

# Limpar e gerar o pacote da aplicação
./mvnw clean package
```

O teste `DatabaseConnectionTest` verifica se a conexão está ativa e confirma que o SGBD conectado é PostgreSQL. Não há banco alternativo ou banco em memória no projeto.

## Como o backend está organizado

O backend está sendo organizado em módulos e camadas. O endpoint de status está dividido entre os pacotes `controllers`, `services` e `dtos`. O módulo `usuario` já contém as entidades `Usuario` e `PerfilUsuario`, os enums de tipo e validação de perfil e o `UsuarioRepository`. A estrutura de persistência é versionada pelo Flyway; os endpoints de cadastro e as camadas de service/controller do módulo ainda serão implementados.

O fluxo previsto para operações que acessam dados é: **requisição HTTP → Controller → Service → Repository → banco de dados**. A organização detalhada dos pacotes, as responsabilidades e as convenções de desenvolvimento estão descritas em [`ArqCamadas.md`](ArqCamadas.md).

## Regras de negócio

As regras abaixo representam o comportamento definido para a plataforma. Elas devem orientar a implementação do backend e dos testes. O modelo e a persistência de contas/perfis das regras RN-001, RN-002 e RN-004 estão implementados; as operações de cadastro, autenticação e liberação de permissões ainda não estão disponíveis na API.

### Usuários, perfis e permissões

- **RN-001 — Contas individuais:** cada usuário deverá possuir uma conta individual, com seus próprios dados, email, senha e histórico.
- **RN-002 — Perfis de usuário:** a plataforma deverá distinguir estudantes e professores, disponibilizando funcionalidades de acordo com as permissões de cada perfil.
- **RN-003 — Privacidade:** o estudante poderá consultar seu próprio histórico e progresso. Não poderá consultar nem alterar os dados privados de outro estudante.
- **RN-004 — Validação do docente:** as permissões de professor somente serão liberadas após a validação da condição docente pela equipe responsável.

### Conteúdos didáticos e contribuição dos professores

- **RN-005 — Hierarquia dos conteúdos:** os conteúdos serão organizados por etapa de ensino, ano escolar, disciplina e aula.
- **RN-006 — Acesso aos conteúdos:** o estudante terá acesso aos conteúdos publicados e disponibilizados na plataforma.
- **RN-007 — Cadastro e edição:** o professor poderá cadastrar conteúdos e editar seus rascunhos, além de enviar sugestões de novas aulas e propostas de melhoria ou correção de aulas existentes.
- **RN-008 — Avaliação de sugestões:** toda sugestão de novo conteúdo ou alteração de conteúdo compartilhado deverá ser avaliada pela equipe de desenvolvimento e manutenção da plataforma antes de ser publicada.
- **RN-009 — Incorporação das sugestões:** uma sugestão aprovada será incorporada ao conteúdo oficial; uma sugestão não aprovada não deverá alterar o material publicado.
- **RN-010 — Créditos de contribuição:** o professor que tiver uma contribuição aprovada e incorporada deverá receber crédito de autoria/colaboração junto ao conteúdo correspondente. Os créditos de contribuição deverão ser diferenciados das referências bibliográficas utilizadas na elaboração do material.
- **RN-011 — Histórico de alterações:** o histórico de alterações deverá identificar a contribuição recebida e a decisão de aprovação/rejeição, ao menos para fins de controle editorial.

### Validação do perfil de professor

- **RN-012 — Método de comprovação:** a primeira opção recomendada para comprovação será a Carteira Nacional Docente do Brasil (CNDB), com conferência de autenticidade e validade, inclusive por seu QR Code quando disponível.
- **RN-013 — Métodos alternativos:** caso o candidato não apresente CNDB, poderão ser avaliados documentos alternativos, como contrato de trabalho, termo de posse/nomeação, CTPS, contracheque, declaração da instituição de ensino ou carteira funcional digital.
- **RN-014 — Registro:** a validação da CNDB ou dos documentos alternativos será responsabilidade da equipe de desenvolvimento e manutenção da plataforma. Qualquer integrante da equipe poderá realizar a análise. A decisão deverá registrar o resultado, a data e o integrante da equipe responsável pela análise.
- **RN-015 — Proteção dos dados:** os documentos comprobatórios deverão ter acesso restrito à equipe autorizada e não poderão ser disponibilizados publicamente, nem ser expostos em respostas da API, páginas públicas ou registros de log.
- **RN-016 — Prazo da verificação:** a plataforma deverá solicitar somente os dados e documentos necessários para verificar a condição docente. O prazo de retenção e o procedimento de exclusão dos documentos ainda deverão ser definidos.

### Questionário e personalização

- **RN-017 — Primeiro acesso:** no primeiro acesso, o estudante poderá responder a um questionário sobre escolaridade, disciplinas de interesse, metodologias preferidas e interesse em questões do ENEM.
- **RN-018 — Questionário opcional:** o questionário será opcional. O estudante poderá ignorá-lo e acessar a página inicial normalmente.
- **RN-019 — Correção sob solicitação:** as respostas somente deverão ser corrigidas após o estudante acionar a opção de verificar resposta ou finalizar a atividade correspondente. O estudante poderá preencher ou atualizar suas preferências posteriormente.
- **RN-020 — Recomendações de conteúdo:** no MVP, as recomendações serão básicas e poderão considerar preferências por vídeos, textos, atividades práticas, disciplinas e conteúdos de interesse.
- **RN-021 — Recomendações avançadas:** recomendações adaptativas avançadas, que utilizem histórico e desempenho para alterar continuamente a trilha, ficarão para versões futuras.

### Atividades, questões e correção

- **RN-022 — Aleatoriedade das questões:** as questões inseridas durante as aulas teóricas serão fixas, mantendo o mesmo conjunto de questões associado à aula. As atividades da área de prática serão selecionadas aleatoriamente a partir de um banco de questões.
- **RN-023 — Questões de ENEM:** as questões da área ENEM também serão obtidas do banco de questões, respeitando o filtro escolhido pelo estudante.
- **RN-024 — Gabarito e correção:** cada questão objetiva deverá possuir previamente cadastrada uma resposta correta/gabarito, que servirá de base para a correção.
- **RN-025 — Confirmação de resposta:** a correção somente será realizada quando o estudante acionar “Verificar resposta” ou a ação de finalizar/corrigir a atividade correspondente.
- **RN-026 — Correção:** a correção e o cálculo do resultado deverão ser realizados pela API/backend, utilizando o gabarito e a regra de correção cadastrados. O frontend não deverá definir nem enviar a nota final como fonte de verdade, e o gabarito não deverá ser exposto ao estudante antes da correção.
- **RN-027 — Repetição/registro de atividades:** o estudante poderá refazer atividades. Cada tentativa deverá ser registrada separadamente, sem apagar as tentativas anteriores. O registro deverá conter, no mínimo, o estudante, a atividade/sessão, as questões apresentadas, as respostas enviadas, a data/hora e o resultado calculado.
- **RN-028 — Formato da questão:** cada formato de questão deverá possuir tipo de resposta e regra de correção compatíveis com seu gabarito.

### Preparação para o ENEM

- **RN-029 — Provas anteriores:** o estudante poderá escolher uma prova de um ano específico para resolver.
- **RN-030 — Disciplinas:** como alternativa, o estudante poderá selecionar uma disciplina (por exemplo, Matemática) para responder questões daquela área.
- **RN-031 — Seleção por ano:** quando selecionar um ano, a plataforma deverá apresentar as questões vinculadas à prova/ano selecionado; quando selecionar uma disciplina, deverá buscar questões do banco que correspondam ao filtro escolhido.
- **RN-032 — Resultados:** o resultado da preparação para o ENEM deverá ficar registrado no histórico individual e ser identificável separadamente das atividades das aulas e da prática geral.

### Progresso e histórico

- **RN-033 — Progresso:** o progresso deverá considerar as atividades concluídas e os resultados registrados no servidor. O estudante poderá consultar seu progresso a qualquer momento após autenticar-se.
- **RN-034 — Histórico:** o histórico deverá preservar tentativas anteriores, mesmo quando o estudante refizer a mesma atividade.
- **RN-035 — Dados gerais:** a fórmula de progresso e os indicadores exibidos (por exemplo, melhor resultado, última tentativa, média ou taxa de acerto) deverão ser definidos antes de serem implementados.

## Links do projeto

- [Apresentação e planejamento do Estuda no Canva](https://www.canva.com/design/DAHYjcQQGmQ/TiOBHjucKEE0DI1pzeEboQ/edit)
- [Arquitetura e organização do backend](ArqCamadas.md)

---

Desenvolvido como projeto de especialização em backend.
