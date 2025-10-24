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
  statusPagamento: string // "pago", "em_aberto", "parcial", "cancelado"
  dataPagamento?: string // formato: "YYYY-MM-DD" (opcional)
  dataRealizacao?: string | null // formato: "YYYY-MM-DD" (opcional) - Data em que o serviço foi realizado
  statusServico?: string // "pendente", "realizado" - Status do serviço único
  valorTotalServico?: number // Valor total do serviço principal (calcula automaticamente com desconto se em venda)
  valorCobrado?: number // Valor efetivamente cobrado quando o serviço está em uma venda
  animalId?: number    // ID do animal (não vem o objeto completo devido ao @JsonBackReference)
  servicoId?: number   // ID do serviço (não vem o objeto completo devido ao @JsonBackReference)
  usuarioId?: number   // ID do usuário (não vem o objeto completo devido ao @JsonBackReference)
  vendaId?: number     // ID da venda vinculada (se houver)
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
  observacoes?: string
  usuarioId?: number
  // ❌ Removidos: statusPagamento e dataPagamento (herdam do pai automaticamente)
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

// =====================================================
// 💰 SISTEMA DE VENDAS
// =====================================================

// 💳 Forma de Pagamento
export interface FormaPagamento {
  id: number
  nome: string
  tipo: 'dinheiro' | 'debito' | 'credito' | 'pix' | 'boleto' | 'outro'
  taxaPercentual: number
  taxaFixa: number
  parcelasMax: number
  diasRecebimento: number
  ativo: boolean
  createdAt: string
  maquininhaId?: number | null // ID da maquininha vinculada (se for pagamento via maquininha)
}

// 🛒 Venda (Resumo - para listagens)
export interface VendaResumo {
  id: number
  codigoVenda: number
  dataVenda: string
  tipoVenda: 'presencial' | 'agendamento' | 'busca_entrega'
  statusVenda: 'em_aberto' | 'pago' | 'parcial' | 'cancelado'
  clienteId: number
  clienteNome: string
  usuarioId: number
  usuarioNome: string
  valorTotal: number
  valorPago: number
  valorPendente: number
  quantidadeItens: number
  quantidadeBaixas: number
  percentualPago: number
}

// 🛒 Item da Venda (Detalhado)
export interface VendaItemDetalhado {
  id: number
  animalServicoId: number
  animalId: number
  animalNome: string
  animalTipo: string
  animalRaca?: string
  servicoId: number
  servicoNome: string
  valorItem: number
  descontoItem: number
  valorFinalItem: number
  valorAdicionais: number
  quantidadeAdicionais: number
  observacoes?: string
}

// 💸 Baixa da Venda (Pagamento Detalhado)
export interface VendaBaixaDetalhada {
  id: number
  dataBaixa: string
  formaPagamentoId: number
  formaPagamentoNome: string
  formaPagamentoTipo: string
  valorBaixa: number
  valorTaxa: number
  valorLiquido: number
  numeroParcelas: number
  dataPrimeiraParcela?: string
  valorParcela: number
  usuarioId: number
  usuarioNome: string
  observacoes?: string
}

// 🛒 Venda (Completa - para detalhes)
export interface VendaCompleta {
  id: number
  codigoVenda: number
  dataVenda: string
  tipoVenda: 'presencial' | 'agendamento' | 'busca_entrega'
  statusVenda: 'em_aberto' | 'pago' | 'parcial' | 'cancelado'
  clienteId: number
  clienteNome: string
  clienteCpf?: string
  usuarioId: number
  usuarioNome: string
  valorBruto: number
  desconto: number
  valorTotal: number
  valorPago: number
  valorPendente: number
  percentualPago: number
  quantidadeItens: number
  quantidadeBaixas: number
  observacoes?: string
  canceladoEm?: string
  motivoCancelamento?: string
  itens: VendaItemDetalhado[]
  baixas: VendaBaixaDetalhada[]
}

// 📝 Criar Venda - Item
export interface CriarVendaItem {
  // Para serviços que já existem como AnimalServico
  animalServicoId?: number

  // Para criar novos serviços na hora
  animalId?: number // ID do animal
  servicoId?: number // ID do serviço do catálogo

  // Valores personalizados
  valorItem?: number // Opcional, usa valor do serviço se não informado
  descontoItem?: number
  observacoes?: string
}

// 📝 Criar Venda
export interface CriarVenda {
  clienteId: number
  usuarioId: number
  tipoVenda?: 'presencial' | 'agendamento' | 'busca_entrega'
  desconto?: number
  observacoes?: string
  animalServicoIds?: number[] // Opção 1: lista simples de IDs
  itens?: CriarVendaItem[] // Opção 2: lista detalhada com valores customizados
}

// 📝 Atualizar Venda
export interface AtualizarVenda {
  tipoVenda?: string
  desconto?: number
  observacoes?: string
  statusVenda?: string
}

// 📝 Cancelar Venda
export interface CancelarVenda {
  motivoCancelamento: string
}

// 📝 Criar Baixa (Registrar Pagamento)
export interface CriarVendaBaixa {
  vendaId: number
  formaPagamentoId: number
  valorBaixa: number
  numeroParcelas?: number
  dataPrimeiraParcela?: string // formato: "YYYY-MM-DD"
  observacoes?: string
  usuarioId: number
}

// 📊 Tipos de venda
export const TIPOS_VENDA = ['presencial', 'agendamento', 'busca_entrega'] as const
export type TipoVenda = typeof TIPOS_VENDA[number]

// 📊 Status de venda
export const STATUS_VENDA = ['em_aberto', 'pago', 'parcial', 'cancelado'] as const
export type StatusVenda = typeof STATUS_VENDA[number]

// 📊 Tipos de forma de pagamento
export const TIPOS_FORMA_PAGAMENTO = ['dinheiro', 'debito', 'credito', 'pix', 'boleto', 'outro'] as const
export type TipoFormaPagamento = typeof TIPOS_FORMA_PAGAMENTO[number]

// 🏦 Sistema de Maquininhas

// Conta Bancária
export interface ContaBancaria {
  id: number
  nome: string
  banco: string
  agencia?: string
  conta?: string
  tipo: 'corrente' | 'poupanca' | 'pagamento'
  pixChave?: string
  ativo: boolean
}

// Adquirente
export interface Adquirente {
  id: number
  nome: string
  codigo: string
  ativo: boolean
}

// Bandeira
export interface Bandeira {
  id: number
  nome: string
  codigo: string
  ativo: boolean
}

// Taxa de Maquininha
export interface MaquininhaTaxa {
  id?: number
  bandeiraId: number
  bandeiraNome?: string
  tipoTransacao: 'debito' | 'credito_avista' | 'credito_parcelado'
  tipoTransacaoDescricao?: string
  numeroParcelas?: number | null
  taxaPercentual: number
  taxaFixa?: number
}

// Maquininha Completa
export interface Maquininha {
  id: number
  nome: string
  ativo: boolean

  // Adquirente
  adquirenteId: number
  adquirenteNome: string

  // Conta bancária
  contaBancariaId: number
  contaBancariaNome: string

  // Prazos
  prazoRecebimentoDebito: number
  prazoRecebimentoCredito: number

  // Antecipação
  aceitaAntecipacao: boolean
  antecipacaoAutomatica: boolean
  taxaAntecipacaoMensal: number

  // PIX
  aceitaPix: boolean
  contaPixId?: number
  contaPixNome?: string
  taxaPix: number
  prazoRecebimentoPix: number

  // Taxas
  taxas?: MaquininhaTaxa[]
  totalTaxasConfiguradas?: number
  totalBandeirasConfiguradas?: number

  // Datas
  createdAt?: string
  updatedAt?: string
}

// Nova Maquininha (para criar)
export interface NovaMaquininha {
  nome: string
  adquirenteId: number
  contaBancariaId: number
  prazoRecebimentoDebito: number
  prazoRecebimentoCredito: number
  aceitaAntecipacao: boolean
  antecipacaoAutomatica: boolean
  taxaAntecipacaoMensal: number
  aceitaPix: boolean
  contaPixId?: number | null
  taxaPix: number
  prazoRecebimentoPix: number
  taxas?: MaquininhaTaxa[]
}

// Maquininha Resumo (para dropdowns)
export interface MaquininhaResumo {
  id: number
  nome: string
  adquirenteNome: string
  contaBancariaNome: string
  ativo: boolean
  aceitaPix: boolean
}
