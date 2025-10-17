package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.ServicoAdicional;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import backend.prontodogbanho.repository.ServicoAdicionalRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalServicoRepository animalServicoRepository;
    private final ServicoAdicionalRepository servicoAdicionalRepository;

    public AnimalService(AnimalRepository animalRepository,
                        AnimalServicoRepository animalServicoRepository,
                        ServicoAdicionalRepository servicoAdicionalRepository) {
        this.animalRepository = animalRepository;
        this.animalServicoRepository = animalServicoRepository;
        this.servicoAdicionalRepository = servicoAdicionalRepository;
    }

    public List<Animal> listarTodos() {
        return animalRepository.findAll();
    }

    /**
     * 🔍 Buscar animais com filtros e paginação
     */
    public Page<Animal> buscarAnimaisComFiltros(String nome, String tipo, String raca,
                                               String codigoSimplesVet, String clienteNome,
                                               int page, int size, String sortBy, String sortDir) {

        System.out.println("🔍 Iniciando busca filtrada de animais...");

        // Criar ordenação
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Criar paginação
        Pageable pageable = PageRequest.of(page, size, sort);

        // Normalizar filtros (null se vazio)
        nome = (nome != null && nome.trim().isEmpty()) ? null : nome;
        tipo = (tipo != null && tipo.trim().isEmpty()) ? null : tipo;
        raca = (raca != null && raca.trim().isEmpty()) ? null : raca;
        codigoSimplesVet = (codigoSimplesVet != null && codigoSimplesVet.trim().isEmpty()) ? null : codigoSimplesVet;
        clienteNome = (clienteNome != null && clienteNome.trim().isEmpty()) ? null : clienteNome;

        // Converter código para Long se for numérico
        Long codigoNumerico = null;
        if (codigoSimplesVet != null) {
            try {
                codigoNumerico = Long.parseLong(codigoSimplesVet);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Código não é numérico, busca por código será ignorada: " + codigoSimplesVet);
                codigoSimplesVet = null; // Se não é número, ignora o filtro
            }
        }

        System.out.println("🔍 Filtros normalizados:");
        System.out.println("  - Nome: " + nome);
        System.out.println("  - Tipo: " + tipo);
        System.out.println("  - Raça: " + raca);
        System.out.println("  - Código String: " + codigoSimplesVet);
        System.out.println("  - Código Numérico: " + codigoNumerico);
        System.out.println("  - Cliente: " + clienteNome);

        // Executar busca
        Page<Animal> resultado = animalRepository.findAnimaisComFiltros(
            nome, tipo, raca, codigoSimplesVet, codigoNumerico, clienteNome, pageable
        );

        System.out.println("✅ Busca concluída: " + resultado.getTotalElements() +
                         " animais encontrados em " + resultado.getTotalPages() + " páginas");

        return resultado;
    }
    public Optional<Animal> buscarPorId(Long id) {
        return animalRepository.findById(id);
    }

    @Transactional
    public Animal salvar(Animal animal) {
        if (animal.getCodigoAnimalSistema() == null) {
            Long maxCodigo = animalRepository.findMaxCodigoAnimalSistema();
            animal.setCodigoAnimalSistema(maxCodigo != null ? maxCodigo + 1 : 1L);
        }
        return animalRepository.save(animal);
    }

    public void deletar(Long id) {
        animalRepository.deleteById(id);
    }

    @Transactional
    public Animal atualizarNome(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setNome(novosDados.getNome());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }

    @Transactional
    public Animal atualizarCodigoSimplesVet(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado");
        }
    }

    @Transactional
    public Animal atualizarRaca(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setRaca(novosDados.getRaca());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }

    @Transactional
    public Animal atualizarPeso(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setPeso(novosDados.getPeso());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }

    @Transactional
    public Animal atualizarCompleto(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();

            // Atualizar apenas os campos que não são chaves ou referências
            if (novosDados.getNome() != null) {
                animalExistente.setNome(novosDados.getNome());
            }
            if (novosDados.getTipo() != null) {
                animalExistente.setTipo(novosDados.getTipo());
            }
            if (novosDados.getRaca() != null) {
                animalExistente.setRaca(novosDados.getRaca());
            }
            if (novosDados.getPeso() != null) {
                animalExistente.setPeso(novosDados.getPeso());
            }
            if (novosDados.getCodigoSimplesVet() != null) {
                animalExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
            }

            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }

    /**
     * 📊 Buscar histórico completo de um animal
     * Retorna dados do animal, resumo estatístico e lista de todos os serviços
     */
    public Map<String, Object> buscarHistoricoCompleto(Long animalId) {
        System.out.println("📊 Iniciando busca de histórico para animal ID: " + animalId);

        // Verificar se animal existe
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com ID: " + animalId));

        System.out.println("🐕 Animal encontrado: " + animal.getNome() + " (" + animal.getTipo() + ")");

        // Buscar todos os serviços do animal (ordenados por data mais recente primeiro)
        List<AnimalServico> servicosDoAnimal = animalServicoRepository
                .findByAnimalIdOrderByDataServicoDesc(animalId);

        System.out.println("🔍 Encontrados " + servicosDoAnimal.size() + " serviços para o animal");

        // Calcular resumo estatístico
        Map<String, Object> resumo = calcularResumoEstatistico(servicosDoAnimal);
        System.out.println("📈 Resumo calculado: " + resumo);

        // Converter serviços para estrutura de resposta
        List<Map<String, Object>> servicosFormatados = servicosDoAnimal.stream()
                .map(this::formatarServicoParaHistorico)
                .collect(Collectors.toList());

        // Montar resposta final
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("animal", formatarAnimalParaHistorico(animal));
        resposta.put("resumo", resumo);
        resposta.put("servicos", servicosFormatados);

        System.out.println("✅ Histórico completo montado com sucesso!");
        return resposta;
    }

    /**
     * 📈 Calcular estatísticas resumidas dos serviços do animal
     */
    private Map<String, Object> calcularResumoEstatistico(List<AnimalServico> servicos) {
        if (servicos.isEmpty()) {
            Map<String, Object> resumoVazio = new HashMap<>();
            resumoVazio.put("totalGasto", 0.0);
            resumoVazio.put("totalServicos", 0);
            resumoVazio.put("primeiroServico", null);
            resumoVazio.put("ultimoServico", null);
            resumoVazio.put("temServicoAtivo", false);
            return resumoVazio;
        }

        // Calcular total gasto (serviço principal + adicionais)
        double totalGasto = servicos.stream()
                .mapToDouble(servico -> {
                    double valorPrincipal = servico.getServico().getValor() != null ? servico.getServico().getValor() : 0.0;

                    // Buscar serviços adicionais
                    List<ServicoAdicional> adicionais = servicoAdicionalRepository
                            .findByAnimalServicoPrincipalId(servico.getId());

                    double valorAdicionais = adicionais.stream()
                            .mapToDouble(adicional -> adicional.getValorTotal() != null ?
                                    adicional.getValorTotal().doubleValue() : 0.0)
                            .sum();

                    return valorPrincipal + valorAdicionais;
                })
                .sum();

        // Encontrar primeiro e último serviço
        LocalDate primeiroServico = servicos.stream()
                .map(AnimalServico::getDataServico)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate ultimoServico = servicos.stream()
                .map(AnimalServico::getDataServico)
                .max(LocalDate::compareTo)
                .orElse(null);

        // Verificar se tem serviço ativo (não completo)
        boolean temServicoAtivo = servicos.stream()
                .anyMatch(servico -> {
                    // Considerar ativo se não é serviço único e ainda tem banhos restantes
                    if (servico.getServico().podeSerUsadoComoAdicional()) {
                        return false; // Serviços únicos não têm conceito de "ativo"
                    }
                    return servico.getBanhosUsados() < servico.getServico().getQuantidade();
                });

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalGasto", totalGasto);
        resumo.put("totalServicos", servicos.size());
        resumo.put("primeiroServico", primeiroServico != null ? primeiroServico.toString() : null);
        resumo.put("ultimoServico", ultimoServico != null ? ultimoServico.toString() : null);
        resumo.put("temServicoAtivo", temServicoAtivo);

        return resumo;
    }

    /**
     * 🐕 Formatar dados do animal para histórico
     */
    private Map<String, Object> formatarAnimalParaHistorico(Animal animal) {
        Map<String, Object> animalFormatado = new HashMap<>();
        animalFormatado.put("id", animal.getId());
        animalFormatado.put("nome", animal.getNome());
        animalFormatado.put("tipo", animal.getTipo());
        animalFormatado.put("raca", animal.getRaca());
        animalFormatado.put("peso", animal.getPeso());
        animalFormatado.put("codigoSimplesVet", animal.getCodigoSimplesVet());
        animalFormatado.put("codigoAnimalSistema", animal.getCodigoAnimalSistema());
        return animalFormatado;
    }

    /**
     * 🛁 Formatar um serviço para exibição no histórico
     */
    private Map<String, Object> formatarServicoParaHistorico(AnimalServico animalServico) {
        Map<String, Object> servicoFormatado = new HashMap<>();

        // Dados básicos do serviço
        servicoFormatado.put("id", animalServico.getId());
        servicoFormatado.put("dataServico", animalServico.getDataServico().toString());
        servicoFormatado.put("dataExpiracao", animalServico.getDataExpiracao() != null ?
                animalServico.getDataExpiracao().toString() : null);

        // Dados do serviço principal
        Map<String, Object> servico = new HashMap<>();
        servico.put("id", animalServico.getServico().getId());
        servico.put("nome", animalServico.getServico().getNome());
        servico.put("quantidade", animalServico.getServico().getQuantidade());
        servico.put("podeSerAdicional", animalServico.getServico().podeSerUsadoComoAdicional());
        servicoFormatado.put("servico", servico);

        // Status do serviço (completo, em andamento, etc.)
        String statusServico;
        if (animalServico.getServico().podeSerUsadoComoAdicional()) {
            // Serviços únicos são sempre "realizado" ou "pendente"
            statusServico = animalServico.getBanhosUsados() > 0 ? "realizado" : "pendente";
        } else {
            // Pacotes têm lógica de progresso
            if (animalServico.getBanhosUsados() >= animalServico.getServico().getQuantidade()) {
                statusServico = "completo";
            } else if (animalServico.getBanhosUsados() > 0) {
                statusServico = "em_andamento";
            } else {
                statusServico = "nao_iniciado";
            }
        }
        servicoFormatado.put("statusServico", statusServico);

        servicoFormatado.put("banhosRealizados", animalServico.getBanhosUsados());
        servicoFormatado.put("valorTotal", animalServico.getServico().getValor() != null ?
                animalServico.getServico().getValor() : 0.0);
        servicoFormatado.put("statusPagamento", animalServico.getStatusPagamento());
        servicoFormatado.put("dataPagamento", animalServico.getDataPagamento() != null ?
                animalServico.getDataPagamento().toString() : null);

        // Dados do usuário
        if (animalServico.getUsuario() != null) {
            Map<String, Object> usuario = new HashMap<>();
            usuario.put("id", animalServico.getUsuario().getId());
            usuario.put("nome", animalServico.getUsuario().getNome());
            servicoFormatado.put("usuario", usuario);
        }

        // Buscar serviços adicionais
        List<ServicoAdicional> servicosAdicionais = servicoAdicionalRepository
                .findByAnimalServicoPrincipalId(animalServico.getId());

        List<Map<String, Object>> adicionaisFormatados = servicosAdicionais.stream()
                .map(this::formatarServicoAdicionalParaHistorico)
                .collect(Collectors.toList());

        servicoFormatado.put("servicosAdicionais", adicionaisFormatados);

        return servicoFormatado;
    }

    /**
     * ➕ Formatar um serviço adicional para histórico
     */
    private Map<String, Object> formatarServicoAdicionalParaHistorico(ServicoAdicional servicoAdicional) {
        Map<String, Object> adicionalFormatado = new HashMap<>();

        adicionalFormatado.put("id", servicoAdicional.getId());
        adicionalFormatado.put("nome", servicoAdicional.getServicoAdicional().getNome());
        adicionalFormatado.put("quantidade", servicoAdicional.getQuantidadeAdicional());
        adicionalFormatado.put("valorUnitario", servicoAdicional.getValorUnitario() != null ?
                servicoAdicional.getValorUnitario().doubleValue() : 0.0);
        adicionalFormatado.put("valorTotal", servicoAdicional.getValorTotal() != null ?
                servicoAdicional.getValorTotal().doubleValue() : 0.0);
        adicionalFormatado.put("observacoes", servicoAdicional.getObservacoes());

        return adicionalFormatado;
    }
}
