// 📁 types/api.ts
// Este arquivo contém todas as interfaces (tipos) que nossa API retorna
// Isso nos ajuda a ter código mais seguro e com autocompletar

// 📞 Telefone do cliente
export interface Telefone {
  id: number
  telefone: string
}

// 📧 Email do cliente
export interface EmailCliente {
  id: number
  email: string
}

// 🐕 Animal do cliente
export interface Animal {
  id: number
  codigoAnimalSistema: number
  nome: string
  codigoSimplesVet: number
  tipo: string
  raca?: string | null
  peso?: number | null
  cliente?: Cliente // Relacionamento com cliente (opcional para evitar referência circular)
  servicos: AnimalServico[]
}

// 🛁 Serviço realizado no animal (tabela intermediária AnimalServico)
export interface AnimalServico {
  id: number
  dataServico: string // formato: "YYYY-MM-DD"
  dataExpiracao?: string // formato: "YYYY-MM-DD" (opcional)
  banhosUsados: number
  statusPagamento: string // "pago", "em_aberto", "cancelado"
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  valorTotalServico?: number // Valor total do serviço principal (novo campo)
  animalId?: number    // ID do animal (não vem o objeto completo devido ao @JsonBackReference)
  servicoId?: number   // ID do serviço (não vem o objeto completo devido ao @JsonBackReference)
  usuarioId?: number   // ID do usuário (não vem o objeto completo devido ao @JsonBackReference)
  animal?: Animal      // Para compatibilidade, mas pode ser undefined
  servico?: ServicoCompleto // Para compatibilidade, mas pode ser undefined
  servicosAdicionais?: ServicoAdicional[] // Lista de serviços adicionais associados
}

// 💼 Serviço principal (entity Servico)
export interface ServicoCompleto {
  id: number
  nome: string
  descricao: string
  quantidade: number
  valor: number
  podeSerAdicional?: boolean // Nova propriedade para indicar se pode ser adicional
  categoria?: string // Nova propriedade para categorizar serviços
  servicosAnimais?: AnimalServico[]
}

// 🔧 Serviço Adicional (entity ServicoAdicional)
export interface ServicoAdicional {
  id: number
  animalServicoPrincipalId: number
  servicoAdicional: ServicoCompleto
  quantidade: number
  valorUnitario: number
  valorTotal: number
  statusPagamento: string // "pago", "em_aberto", "cancelado"
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  observacoes?: string
  usuario?: Usuario
  createdAt: string
  updatedAt: string
}

// 👤 Usuário do sistema (vendedores/funcionários)
export interface Usuario {
  id: number
  nome: string
  email: string
  role: string
  animalServicos?: AnimalServico[] // Relacionamento com animal serviços
}

// 👤 Cliente principal
export interface Cliente {
  id: number
  codigoClienteSistema: number
  nomeCompleto: string
  cpf: string
  codigoSimplesVet: number
  telefones: Telefone[]
  emailClientes: EmailCliente[]
  animais: Animal[]
}

// 📊 Resposta da API (lista de clientes)
export type ClientesResponse = Cliente[]

// 📊 Resposta da API (lista de serviços)
export type ServicosResponse = ServicoCompleto[]

// 📊 Resposta da API (lista de usuários)
export type UsuariosResponse = Usuario[]

// 🔄 Estados para controle de loading e erros
export interface ApiState<T> {
  data: T | null
  loading: boolean
  error: string | null
}

// 📝 INTERFACES PARA CRIAÇÃO (sem IDs)

// 📞 Telefone para criação (sem ID)
export interface NovoTelefone {
  telefone: string
}

// 📧 Email para criação (sem ID)
export interface NovoEmailCliente {
  email: string
}

// 🐕 Animal para criação (sem ID e sem serviços)
export interface NovoAnimal {
  nome: string
  codigoSimplesVet: number
  tipo: string
  raca?: string | null
  peso?: number | null
}

// 👤 Cliente para criação (sem ID, sem emails por enquanto)
export interface NovoCliente {
  nomeCompleto: string
  cpf: string
  codigoSimplesVet: number
  telefones: NovoTelefone[]
  emailClientes: NovoEmailCliente[]
  animais: NovoAnimal[]
}

// 💼 Serviço para criação (sem ID)
export interface NovoServico {
  nome: string
  descricao: string
  quantidade: number // Representa banhos por pacote (1 = banho único, 4 = pacote 4 banhos)
  valor: number // Valor total do pacote/serviço
  podeSerAdicional?: boolean // Indica se pode ser usado como serviço adicional
  categoria?: string // Categoria do serviço (ex: "banho", "tosa", "perfumaria")
}

// 🛁 Animal Serviço para criação (sem ID)
export interface NovoAnimalServico {
  dataServico: string // formato: "YYYY-MM-DD"
  dataExpiracao?: string // formato: "YYYY-MM-DD" (opcional)
  banhosUsados: number
  statusPagamento?: string // "pago", "em_aberto", "cancelado" (padrão: "em_aberto")
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  animal: { id: number } // Referência ao animal
  servico: { id: number } // Referência ao serviço
  usuario: { id: number } // Referência ao usuário
}

// 🛁 Animal Serviço completo para criação (com banhos individuais)
export interface CriarAnimalServicoCompleto {
  dataServico: string // formato: "YYYY-MM-DD"
  dataExpiracao?: string // formato: "YYYY-MM-DD" (opcional)
  banhosUsados: number
  statusPagamento?: string // "pago", "em_aberto", "cancelado" (padrão: "em_aberto")
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  animalId: number
  servicoId: number
  usuarioId: number
  // Datas dos banhos já realizados (se banhosUsados > 0)
  datasBanhosRealizados?: string[] // formato: "YYYY-MM-DD"
  // Observações para cada banho (opcional)
  observacoesBanhos?: string[]
  // Serviços adicionais para criar junto
  servicosAdicionais?: NovoServicoAdicional[]
}

// 🔧 Serviço Adicional para criação (sem ID)
export interface NovoServicoAdicional {
  servicoAdicionalId: number
  quantidade: number
  valorUnitario: number
  statusPagamento: string // "pago", "em_aberto", "cancelado"
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  observacoes?: string
  usuarioId?: number
}

// 📋 Dados do formulário (estrutura interna do componente)
export interface FormularioCliente {
  nomeCompleto: string
  cpf: string
  codigoSimplesVet: string // String no form, convertido para number na API
  telefones: string[] // Array de strings no form
  emailClientes: string[] // Array de strings no form (emails opcionais)
  animais: {
    nome: string
    codigoSimplesVet: string // String no form, convertido para number na API
    tipo: string
    raca?: string | null
    peso?: string | null // String no form, convertido para number na API
  }[]
}

// 🎯 Tipos de animais disponíveis
export const TIPOS_ANIMAIS = [
  'Cachorro',
  'Gato',
  'Pássaro',
  'Hamster',
  'Coelho',
  'Outro'
] as const

export type TipoAnimal = typeof TIPOS_ANIMAIS[number]

// 💳 Status de pagamento disponíveis
export const STATUS_PAGAMENTO = [
  'em_aberto',
  'pago',
  'cancelado'
] as const

export type StatusPagamento = typeof STATUS_PAGAMENTO[number]

// 💼 Tipos de serviços comuns em petshops
export const TIPOS_SERVICOS = [
  // Serviços Únicos
  'Banho Simples',
  'Banho e Tosa',
  'Tosa Higiênica',
  'Tosa Completa',
  'Escovação de Pelos',
  'Corte de Unhas',
  'Limpeza de Ouvidos',
  'Escovação Dental',
  'Hidratação',
  'Perfume',
  'Transporte',
  // Pacotes de Banhos
  'Pacote Banho Simples',
  'Pacote Banho e Tosa',
  'Pacote Premium',
  'Pacote Completo',
  'Outro'
] as const

export type TipoServico = typeof TIPOS_SERVICOS[number]
