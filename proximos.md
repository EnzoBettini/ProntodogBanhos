# Próximos Passos - Projeto Banho & Tosa

## 1️⃣ Refinar e Organizar o Banco
- Revisar se todas as colunas estão corretas e necessárias.
- Adicionar constraints extras se necessário (`NOT NULL`, checks, etc.).
- Documentar relacionamentos entre tabelas (clientes, animais, serviços, animal_servico, telefones, emails).

## 2️⃣ Conectar Backend Java ao Banco
- Criar projeto Java (Spring Boot ou JDBC puro).
- Configurar conexão com PostgreSQL local:
  `jdbc:postgresql://localhost:5432/banhoetosa`
- Testar conexão com queries simples via Java.

## 3️⃣ Criar APIs para o Front-End
- Criar endpoints REST básicos:
  - `/clientes` - CRUD
  - `/animais` - CRUD
  - `/servicos` - CRUD
  - `/animal_servico` - CRUD
- Permitir manipulação de dados via backend.

## 4️⃣ Criar o Front-End em Vue 3 + Tailwind
- Iniciar projeto Vue 3 com Vite:
  ```bash
  npm create vite@latest banhoetosa-frontend --template vue
  cd banhoetosa-frontend
  npm install
````

* Instalar e configurar Tailwind CSS.
* Criar páginas básicas:

  * Página de clientes
  * Página de animais do cliente
  * Página de registro de serviços
* Consumir APIs do backend para mostrar e inserir dados.

## 5️⃣ Funcionalidades Extras

* Automatizar envio de mensagens WhatsApp (via API externa, como Twilio).
* Relatórios:

  * Quantos banhos cada animal já fez
  * Animais sem serviços
  * Histórico de serviços por cliente
* Planejar integração futura com SimplesVet.

## 6️⃣ Testes e Versionamento

* Testar todas as queries via backend e front-end.
* Criar backups do banco regularmente.
* Guardar scripts `.sql` para recriar o banco em outro PC.
