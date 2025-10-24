
# Próximos Passos - Projeto Banho & Tosa (atualizado)

## 1️⃣ Refinar e Organizar o Banco

* Revisar todas as tabelas e colunas (clientes, animais, serviços, animal\_servico, telefones, emails).
* Adicionar constraints (`NOT NULL`, checks) se necessário.
* Documentar relacionamentos entre tabelas.

## 2️⃣ Backend Java com Spring Boot

* Projeto já criado.
* Conexão com PostgreSQL local configurada.
* Testar queries simples via Java para confirmar comunicação com o banco.

## 3️⃣ Repositórios JPA


  * ClienteRepository
  * TelefoneRepository
  * EmailRepository
  * AnimalRepository
  * ServicoRepository
  * AnimalServicoRepository
  * UsuarioRepository


## 4️⃣ Service Layer (próximo passo)

* Criar classes de serviço para lógica de negócios:

  * Cadastrar cliente com telefones e emails.
  * Registrar animal e vincular ao cliente.
  * Registrar serviços feitos por animais com usuário responsável.
  * Consultar histórico de serviços por animal ou cliente.
* Por enquanto, focar em apenas **um teste de inserção** para validar se o fluxo funciona.

## 5️⃣ Criar APIs REST

* Criar endpoints básicos para cada entidade:

  * GET, POST, PUT, DELETE
  * Ex.: `/clientes`, `/animais`, `/servicos`, `/animal_servico`
* Consumir dados via backend (testar com Postman ou Insomnia).

## 6️⃣ Front-End em Vue 3 + Tailwind

* Iniciar projeto Vue 3 com Vite (se ainda não feito):

  ```bash
  npm create vite@latest banhoetosa-frontend --template vue
  cd banhoetosa-frontend
  npm install
  ```
* Configurar Tailwind CSS.
* Criar páginas básicas: clientes, animais, registro de serviços.
* Consumir APIs do backend.

## 7️⃣ Funcionalidades Extras

* Automatizar envio de mensagens (opcional por enquanto).
* Relatórios: banhos feitos, animais sem serviços, histórico de serviços por cliente.

## 8️⃣ Testes e Versionamento

* Testar CRUD de todas as entidades via backend e frontend.
* Criar backups do banco.
* Guardar scripts `.sql` para recriar o banco.
