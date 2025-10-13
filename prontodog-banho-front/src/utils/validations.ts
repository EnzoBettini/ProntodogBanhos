// 📁 utils/validations.ts
// Funções de validação para formulários
// Cada função retorna string com erro ou null se válido

// 📧 VALIDAÇÃO DE EMAIL
export const validarEmail = (email: string): string | null => {
  if (!email.trim()) {
    return null // Email é opcional
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email)) {
    return 'Formato de email inválido.'
  }

  if (email.length > 100) {
    return 'Email deve ter no máximo 100 caracteres.'
  }

  return null
}

// 📋 VALIDAÇÃO DE CPF
export const validarCpf = (cpf: string): string | null => {
  // Remove caracteres não numéricos
  const cpfLimpo = cpf.replace(/\D/g, '')

  // Verifica se tem 11 dígitos
  if (cpfLimpo.length !== 11) {
    return 'CPF deve ter 11 dígitos'
  }

  // Verifica se não são todos números iguais
  if (/^(\d)\1{10}$/.test(cpfLimpo)) {
    return 'CPF inválido'
  }

  // Algoritmo de validação do CPF
  let soma = 0
  let resto

  // Valida primeiro dígito
  for (let i = 1; i <= 9; i++) {
    soma += parseInt(cpfLimpo.substring(i - 1, i)) * (11 - i)
  }

  resto = (soma * 10) % 11
  if (resto === 10 || resto === 11) resto = 0
  if (resto !== parseInt(cpfLimpo.substring(9, 10))) {
    return 'CPF inválido'
  }

  // Valida segundo dígito
  soma = 0
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(cpfLimpo.substring(i - 1, i)) * (12 - i)
  }

  resto = (soma * 10) % 11
  if (resto === 10 || resto === 11) resto = 0
  if (resto !== parseInt(cpfLimpo.substring(10, 11))) {
    return 'CPF inválido'
  }

  return null // CPF válido
}

// 📞 VALIDAÇÃO DE TELEFONE
export const validarTelefone = (telefone: string): string | null => {
  // Remove caracteres não numéricos
  const telLimpo = telefone.replace(/\D/g, '')

  // Verifica se tem pelo menos 10 dígitos (fixo) ou 11 (celular)
  if (telLimpo.length < 10) {
    return 'Telefone deve ter pelo menos 10 dígitos'
  }

  if (telLimpo.length > 11) {
    return 'Telefone deve ter no máximo 11 dígitos'
  }

  // Verifica se o DDD é válido (11-99)
  const ddd = parseInt(telLimpo.substring(0, 2))
  if (ddd < 11 || ddd > 99) {
    return 'DDD inválido'
  }

  return null // Telefone válido
}

// 👤 VALIDAÇÃO DE NOME COMPLETO
export const validarNomeCompleto = (nome: string): string | null => {
  if (!nome || nome.trim().length === 0) {
    return 'Nome é obrigatório'
  }

  if (nome.trim().length < 3) {
    return 'Nome deve ter pelo menos 3 caracteres'
  }

  if (nome.trim().length > 100) {
    return 'Nome deve ter no máximo 100 caracteres'
  }

  // Verifica se tem pelo menos um espaço (nome e sobrenome)
  if (!nome.trim().includes(' ')) {
    return 'Digite nome e sobrenome'
  }

  // Verifica se contém apenas letras, espaços e acentos
  const nomeRegex = /^[a-zA-ZÀ-ÿ\s]+$/
  if (!nomeRegex.test(nome.trim())) {
    return 'Nome deve conter apenas letras'
  }

  return null // Nome válido
}

// 🔢 VALIDAÇÃO DE CÓDIGO SIMPLES VET
export const validarCodigoSimplesVet = (codigo: string): string | null => {
  if (!codigo || codigo.trim().length === 0) {
    return 'Código é obrigatório'
  }

  const codigoNumero = parseInt(codigo)

  if (isNaN(codigoNumero)) {
    return 'Código deve ser um número'
  }

  if (codigoNumero <= 0) {
    return 'Código deve ser maior que zero'
  }

  if (codigoNumero > 999999999) {
    return 'Código muito grande'
  }

  return null // Código válido
}

// 🐕 VALIDAÇÃO DE NOME DO ANIMAL
export const validarNomeAnimal = (nome: string): string | null => {
  if (!nome || nome.trim().length === 0) {
    return 'Nome do animal é obrigatório'
  }

  if (nome.trim().length < 2) {
    return 'Nome deve ter pelo menos 2 caracteres'
  }

  if (nome.trim().length > 50) {
    return 'Nome deve ter no máximo 50 caracteres'
  }

  // Permite letras, números, espaços e alguns caracteres especiais
  const nomeRegex = /^[a-zA-ZÀ-ÿ0-9\s\-_.]+$/
  if (!nomeRegex.test(nome.trim())) {
    return 'Nome contém caracteres inválidos'
  }

  return null // Nome válido
}

// 🎯 VALIDAÇÃO DE TIPO DE ANIMAL
export const validarTipoAnimal = (tipo: string): string | null => {
  if (!tipo || tipo.trim().length === 0) {
    return 'Tipo de animal é obrigatório'
  }

  return null // Tipo válido (já validado pelo select)
}

// 🧹 FUNÇÕES UTILITÁRIAS DE FORMATAÇÃO

// Formatar CPF para exibição
export const formatarCpf = (cpf: string): string => {
  const cpfLimpo = cpf.replace(/\D/g, '')
  return cpfLimpo.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

// Formatar telefone para exibição
export const formatarTelefone = (telefone: string): string => {
  const telLimpo = telefone.replace(/\D/g, '')

  if (telLimpo.length === 11) {
    return telLimpo.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
  } else if (telLimpo.length === 10) {
    return telLimpo.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
  }

  return telefone
}

// Limpar formatação (manter apenas números)
export const limparFormatacao = (valor: string): string => {
  return valor.replace(/\D/g, '')
}
