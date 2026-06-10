-- Senha: 123456 (BCrypt)
-- UUIDs fixos para dados de seed (evita conflito de sequence)

INSERT INTO unidades (id, nome, endereco) VALUES
    ('11111111-1111-1111-1111-111111111101', 'Raízes Centro', 'Rua das Flores, 100 - Centro, Recife/PE'),
    ('11111111-1111-1111-1111-111111111102', 'Raízes Praia', 'Av. Boa Viagem, 500 - Boa Viagem, Recife/PE');

INSERT INTO usuarios (id, nome, email, senha_hash, role, unidade_id, data_criacao, anonimizado) VALUES
    ('22222222-2222-2222-2222-222222222201', 'Gerente Principal', 'gerente@raizes.com', '$2a$10$AQv.alnrHgatP35Mrniff.PdQ2lBEN5vA/5.fw5eYf8MzZvuSPCBi', 'GERENTE', NULL, NOW(), FALSE),
    ('22222222-2222-2222-2222-222222222202', 'Atendente Centro', 'atendente@raizes.com', '$2a$10$AQv.alnrHgatP35Mrniff.PdQ2lBEN5vA/5.fw5eYf8MzZvuSPCBi', 'ATENDENTE', '11111111-1111-1111-1111-111111111101', NOW(), FALSE),
    ('22222222-2222-2222-2222-222222222203', 'Cozinha Centro', 'cozinha@raizes.com', '$2a$10$AQv.alnrHgatP35Mrniff.PdQ2lBEN5vA/5.fw5eYf8MzZvuSPCBi', 'COZINHA', '11111111-1111-1111-1111-111111111101', NOW(), FALSE);

INSERT INTO produtos (id, nome, descricao, preco, categoria, unidade_id) VALUES
    ('33333333-3333-3333-3333-333333333301', 'Baião de Dois', 'Arroz, feijão de corda, queijo coalho e manteiga de garrafa', 28.90, 'Pratos', '11111111-1111-1111-1111-111111111101'),
    ('33333333-3333-3333-3333-333333333302', 'Carne de Sol', 'Carne de sol desfiada com mandioca e manteiga da terra', 35.50, 'Pratos', '11111111-1111-1111-1111-111111111101'),
    ('33333333-3333-3333-3333-333333333303', 'Tapioca de Queijo', 'Tapioca recheada com queijo coalho', 12.00, 'Lanches', '11111111-1111-1111-1111-111111111101'),
    ('33333333-3333-3333-3333-333333333304', 'Suco de Caju', 'Suco natural de caju 500ml', 8.50, 'Bebidas', '11111111-1111-1111-1111-111111111101'),
    ('33333333-3333-3333-3333-333333333305', 'Cartola', 'Banana, queijo e canela - sobremesa nordestina', 15.00, 'Sobremesas', '11111111-1111-1111-1111-111111111101');

INSERT INTO estoques (id, produto_id, unidade_id, quantidade) VALUES
    ('44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111101', 10),
    ('44444444-4444-4444-4444-444444444402', '33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111101', 10),
    ('44444444-4444-4444-4444-444444444403', '33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111101', 10),
    ('44444444-4444-4444-4444-444444444404', '33333333-3333-3333-3333-333333333304', '11111111-1111-1111-1111-111111111101', 10),
    ('44444444-4444-4444-4444-444444444405', '33333333-3333-3333-3333-333333333305', '11111111-1111-1111-1111-111111111101', 10);

INSERT INTO promocoes (id, nome, tipo, valor_desconto, dia_semana, ativa) VALUES
    ('55555555-5555-5555-5555-555555555501', 'Segunda com 10% off', 'PERCENTUAL_DESCONTO', 10.00, 1, TRUE);
