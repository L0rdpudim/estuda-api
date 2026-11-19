-- Migration inicial para validação do controle de versão do banco de dados (Flyway)
CREATE TABLE IF NOT EXISTS tb_teste_conexao (
    id BIGSERIAL PRIMARY KEY,
    mensagem VARCHAR(255) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);