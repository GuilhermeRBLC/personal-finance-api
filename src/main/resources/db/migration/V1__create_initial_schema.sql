
-- Criação da tabela de Usuários
CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Criação da tabela de Contas
CREATE TABLE tb_accounts (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(50) NOT NULL,
     balance NUMERIC(19, 2) NOT NULL,
     user_id BIGINT NOT NULL,
     CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE
);

-- Criação da tabela de Transações
CREATE TABLE tb_transactions (
     id BIGSERIAL PRIMARY KEY,
     description VARCHAR(150) NOT NULL,
     amount NUMERIC(19, 2) NOT NULL,
     date DATE NOT NULL,
     type VARCHAR(15) NOT NULL,
     category VARCHAR(50),
     account_id BIGINT NOT NULL,
     CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES tb_accounts (id) ON DELETE CASCADE
);