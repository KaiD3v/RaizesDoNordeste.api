CREATE TABLE unidades (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(500) NOT NULL
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    unidade_id UUID REFERENCES unidades(id),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    anonimizado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE produtos (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    unidade_id UUID NOT NULL REFERENCES unidades(id)
);

CREATE TABLE estoques (
    id UUID PRIMARY KEY,
    produto_id UUID NOT NULL REFERENCES produtos(id),
    unidade_id UUID NOT NULL REFERENCES unidades(id),
    quantidade INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_estoque_produto_unidade UNIQUE (produto_id, unidade_id)
);

CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    unidade_id UUID NOT NULL REFERENCES unidades(id),
    canal_pedido VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    desconto_promocao DECIMAL(10, 2) DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    estoque_baixado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE itens_pedido (
    id UUID PRIMARY KEY,
    pedido_id UUID NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL
);

CREATE TABLE pagamentos (
    id UUID PRIMARY KEY,
    pedido_id UUID NOT NULL REFERENCES pedidos(id),
    status VARCHAR(50) NOT NULL,
    transacao_mock_id VARCHAR(255),
    data TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE fidelidades (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE REFERENCES usuarios(id),
    pontos INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE historico_resgates (
    id UUID PRIMARY KEY,
    fidelidade_id UUID NOT NULL REFERENCES fidelidades(id),
    pontos_usados INTEGER NOT NULL,
    valor_desconto DECIMAL(10, 2) NOT NULL,
    data TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE promocoes (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    valor_desconto DECIMAL(10, 2),
    dia_semana INTEGER,
    produto_gratis_id UUID,
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE logs_auditoria (
    id UUID PRIMARY KEY,
    usuario_id UUID,
    acao VARCHAR(255) NOT NULL,
    dados_antes TEXT,
    dados_depois TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    ip VARCHAR(50)
);

CREATE TABLE consentimentos (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    finalidade VARCHAR(255) NOT NULL,
    data_consentimento TIMESTAMP NOT NULL DEFAULT NOW(),
    ip_origem VARCHAR(50)
);

CREATE INDEX idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX idx_pedidos_unidade ON pedidos(unidade_id);
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_canal ON pedidos(canal_pedido);
CREATE INDEX idx_produtos_unidade ON produtos(unidade_id);
CREATE INDEX idx_estoques_unidade ON estoques(unidade_id);
