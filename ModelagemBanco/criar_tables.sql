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
