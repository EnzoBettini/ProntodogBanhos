create table banhoetosa.clientes (
    id bigserial primary key,
    codigo_cliente_sistema bigserial unique,
    nome_completo varchar not null,
    cpf varchar(11) unique,
    codigo_simplesvet bigint unique
);

create table banhoetosa.animais (
    id bigserial primary key,
    codigo_animal_sistema bigserial unique,
    nome varchar not null,
    codigo_simplesvet bigint unique,
    tipo varchar,
    cliente_id bigint not null,
    constraint fk_cliente foreign key (cliente_id) references banhoetosa.clientes (id)
);

create table banhoetosa.servicos (
    id bigserial primary key,
    nome varchar not null unique,
    descricao text,
    quantidade int,
    valor numeric(10, 2) not null
);

create table banhoetosa.animal_servico (
    id bigserial primary key,
    data_servico date not null,
    banhos_usados int not null,
    animal_id bigint not null,
    servico_id bigint not null,
    constraint fk_animal_id foreign key (animal_id) references banhoetosa.animais (id),
    constraint fk_servico_id foreign key (servico_id) references banhoetosa.servicos (id)
);

create table banhoetosa.telefones_clientes (
    id bigserial primary key,
    cliente_id bigint not null,
    telefone varchar not null,
    constraint fk_cliente_id_telefone foreign key (cliente_id) references banhoetosa.clientes (id)
);

create table banhoetosa.email_clientes (
    id bigserial primary key,
    cliente_id bigint not null,
    email varchar not null,
    constraint fk_cliente_id_email foreign key (cliente_id) references banhoetosa.clientes (id)
);

create table banhoetosa.usuarios (
    id bigserial primary key,
    nome varchar(100) not null,
    email varchar(100) not null unique,
    senha varchar(255) not null,
    role varchar(50) not null
);

ALTER TABLE banhoetosa.animal_servico ADD COLUMN usuario_id BIGINT;

ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES banhoetosa.usuarios (id);

ALTER TABLE banhoetosa.animais
ADD COLUMN status VARCHAR(20) DEFAULT 'vivo' NOT NULL CHECK (
    status IN ('vivo', 'falecido')
);

-- Tabela para registrar cada banho individual de um pacote
CREATE TABLE banhoetosa.banhos_individuais (
    id BIGSERIAL PRIMARY KEY,
    animal_servico_id BIGINT NOT NULL,
    data_banho DATE NOT NULL,
    numero_banho INTEGER NOT NULL,
    observacoes TEXT,
    usuario_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_animal_servico FOREIGN KEY (animal_servico_id)
        REFERENCES banhoetosa.animal_servico (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_banho FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios (id),
    CONSTRAINT unique_banho_por_servico
        UNIQUE (animal_servico_id, numero_banho)
);

-- Adicionar novos atributos na tabela de animais
ALTER TABLE banhoetosa.animais
ADD COLUMN raca VARCHAR(100);

ALTER TABLE banhoetosa.animais
ADD COLUMN peso NUMERIC(5, 2);
