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
  cliente?: Cliente // Relacionamento com cliente (opcional para evitar referência circular)
  servicos: Servico[]
}

// 🛁 Serviço realizado no animal
export interface Servico {
  id: number
  dataServico: string // formato: "YYYY-MM-DD"
  banhosUsados: number
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
