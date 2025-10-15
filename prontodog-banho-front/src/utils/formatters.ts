// 🔧 Utilitários de formatação centralizados
// Evita duplicação de código entre componentes

/**
 * Formata um CPF no padrão brasileiro (###.###.###-##)
 */
export const formatarCpf = (cpf: string): string => {
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

/**
 * Formata um telefone no padrão brasileiro
 * Suporta formatos de 10 e 11 dígitos
 */
export const formatarTelefone = (telefone: string): string => {
  // Remove caracteres não numéricos
  const numeros = telefone.replace(/\D/g, '')

  // Formata conforme o tamanho
  if (numeros.length === 11) {
    return numeros.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
  } else if (numeros.length === 10) {
    return numeros.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
  }

  return telefone
}

/**
 * Formata horário no padrão brasileiro (HH:MM)
 */
export const formatarHorario = (): string => {
  return new Date().toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 🗓️ FORMATAÇÃO SEGURA DE DATAS
 * Evita problemas de timezone ao exibir datas
 */
export const formatarDataSegura = (dataString: string): string => {
  if (!dataString) return ''

  try {
    // Parse seguro: adicionar horário para evitar timezone UTC
    const [ano, mes, dia] = dataString.split('-')

    // Criar data local (não UTC)
    const dataLocal = new Date(Number(ano), Number(mes) - 1, Number(dia))

    return dataLocal.toLocaleDateString('pt-BR')
  } catch (error) {
    console.warn('⚠️ Erro ao formatar data:', dataString, error)
    return dataString // Retorna string original se der erro
  }
}

/**
 * 📅 FORMATAÇÃO DE DATA ESTENDIDA
 * Formato: "15 de outubro de 2025"
 */
export const formatarDataEstendida = (dataString: string): string => {
  if (!dataString) return ''

  try {
    const [ano, mes, dia] = dataString.split('-')
    const dataLocal = new Date(Number(ano), Number(mes) - 1, Number(dia))

    return dataLocal.toLocaleDateString('pt-BR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    })
  } catch (error) {
    console.warn('⚠️ Erro ao formatar data estendida:', dataString, error)
    return dataString
  }
}

/**
 * Cores para tipos de animais
 * Centraliza as definições de cores para consistência
 */
export const getCorTipoAnimal = (tipo: string): string => {
  const cores: Record<string, string> = {
    'Cachorro': 'bg-blue-100 text-blue-800',
    'Gato': 'bg-purple-100 text-purple-800',
    'Pássaro': 'bg-yellow-100 text-yellow-800',
    'Peixe': 'bg-cyan-100 text-cyan-800',
    'Hamster': 'bg-pink-100 text-pink-800',
    'Coelho': 'bg-green-100 text-green-800'
  }
  return cores[tipo] || 'bg-gray-100 text-gray-800'
}

/**
 * Ícones para tipos de animais
 * Centraliza os emojis para consistência
 */
export const getIconeTipoAnimal = (tipo: string): string => {
  const icones: Record<string, string> = {
    'Cachorro': '🐕',
    'Gato': '🐱',
    'Pássaro': '🐦',
    'Peixe': '🐟',
    'Hamster': '🐹',
    'Coelho': '🐰',
    'Outro': '🐾'
  }
  return icones[tipo] || '🐾'
}
