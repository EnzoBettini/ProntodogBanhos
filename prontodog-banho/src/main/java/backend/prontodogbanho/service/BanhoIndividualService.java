package backend.prontodogbanho.service;

import backend.prontodogbanho.model.BanhoIndividual;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.Usuario;
import backend.prontodogbanho.repository.BanhoIndividualRepository;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import backend.prontodogbanho.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BanhoIndividualService {

    private final BanhoIndividualRepository banhoIndividualRepository;
    private final AnimalServicoRepository animalServicoRepository;
    private final UsuarioRepository usuarioRepository;

    public BanhoIndividualService(BanhoIndividualRepository banhoIndividualRepository,
                                  AnimalServicoRepository animalServicoRepository,
                                  UsuarioRepository usuarioRepository) {
        this.banhoIndividualRepository = banhoIndividualRepository;
        this.animalServicoRepository = animalServicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Listar todos os banhos
    public List<BanhoIndividual> listarTodos() {
        return banhoIndividualRepository.findAll();
    }

    // Buscar por ID
    public Optional<BanhoIndividual> buscarPorId(Long id) {
        return banhoIndividualRepository.findById(id);
    }

    // Buscar banhos de um AnimalServico específico
    public List<BanhoIndividual> buscarPorAnimalServico(Long animalServicoId) {
        return banhoIndividualRepository.findByAnimalServicoIdOrderByNumeroBanho(animalServicoId);
    }

    // Buscar banhos de um animal
    public List<BanhoIndividual> buscarPorAnimal(Long animalId) {
        return banhoIndividualRepository.findByAnimalIdOrderByDataBanhoDesc(animalId);
    }

    // Buscar banhos de um cliente
    public List<BanhoIndividual> buscarPorCliente(Long clienteId) {
        return banhoIndividualRepository.findByClienteIdOrderByDataBanhoDesc(clienteId);
    }

    // Buscar banhos por data
    public List<BanhoIndividual> buscarPorData(LocalDate data) {
        return banhoIndividualRepository.findByDataBanhoOrderByCreatedAt(data);
    }

    // Buscar banhos por período
    public List<BanhoIndividual> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return banhoIndividualRepository.findByDataBanhoBetweenOrderByDataBanhoDesc(dataInicio, dataFim);
    }

    // Registrar um novo banho
    @Transactional
    public BanhoIndividual registrarBanho(Long animalServicoId, LocalDate dataBanho, String observacoes, Long usuarioId) {
        // Busca o AnimalServico
        Optional<AnimalServico> animalServicoOpt = animalServicoRepository.findById(animalServicoId);
        if (animalServicoOpt.isEmpty()) {
            throw new RuntimeException("AnimalServico não encontrado com ID: " + animalServicoId);
        }

        AnimalServico animalServico = animalServicoOpt.get();

        // Verifica se ainda há banhos disponíveis no pacote
        Integer quantidadeTotal = animalServico.getServico().getQuantidade();
        Long banhosJaRealizados = banhoIndividualRepository.countByAnimalServicoId(animalServicoId);

        if (banhosJaRealizados >= quantidadeTotal) {
            throw new RuntimeException("Todos os banhos do pacote já foram utilizados. Total: " + quantidadeTotal);
        }

        // Calcula o próximo número do banho (considera gaps na numeração)
        Integer proximoNumero = calcularProximoNumeroBanho(animalServicoId, quantidadeTotal);

        // Busca o usuário (opcional)
        Usuario usuario = null;
        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isPresent()) {
                usuario = usuarioOpt.get();
            }
        }

        // Cria o novo banho
        BanhoIndividual novoBanho = new BanhoIndividual();
        novoBanho.setAnimalServico(animalServico);
        novoBanho.setDataBanho(dataBanho);
        novoBanho.setNumeroBanho(proximoNumero);
        novoBanho.setObservacoes(observacoes);
        novoBanho.setUsuario(usuario);

        // Salva o banho
        BanhoIndividual banhoSalvo = banhoIndividualRepository.save(novoBanho);

        // Atualiza o contador de banhos usados no AnimalServico
        animalServico.setBanhosUsados(proximoNumero);
        animalServicoRepository.save(animalServico);

        return banhoSalvo;
    }

    // Atualizar um banho existente
    @Transactional
    public BanhoIndividual atualizarBanho(Long id, LocalDate novaData, String novasObservacoes, Long novoUsuarioId) {
        Optional<BanhoIndividual> banhoOpt = banhoIndividualRepository.findById(id);
        if (banhoOpt.isEmpty()) {
            throw new RuntimeException("Banho não encontrado com ID: " + id);
        }

        BanhoIndividual banho = banhoOpt.get();

        // Atualiza os dados
        if (novaData != null) {
            banho.setDataBanho(novaData);
        }
        if (novasObservacoes != null) {
            banho.setObservacoes(novasObservacoes);
        }
        if (novoUsuarioId != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(novoUsuarioId);
            usuarioOpt.ifPresent(banho::setUsuario);
        }

        return banhoIndividualRepository.save(banho);
    }

    // Excluir um banho
    @Transactional
    public void excluirBanho(Long id) {
        Optional<BanhoIndividual> banhoOpt = banhoIndividualRepository.findById(id);
        if (banhoOpt.isEmpty()) {
            throw new RuntimeException("Banho não encontrado com ID: " + id);
        }

        BanhoIndividual banho = banhoOpt.get();
        AnimalServico animalServico = banho.getAnimalServico();

        // Remove o banho
        banhoIndividualRepository.deleteById(id);

        // Atualiza o contador de banhos usados
        Long banhosRestantes = banhoIndividualRepository.countByAnimalServicoId(animalServico.getId());
        animalServico.setBanhosUsados(banhosRestantes.intValue());
        animalServicoRepository.save(animalServico);
    }

    // Verificar quantos banhos restam em um pacote
    public Integer calcularBanhosRestantes(Long animalServicoId) {
        Optional<AnimalServico> animalServicoOpt = animalServicoRepository.findById(animalServicoId);
        if (animalServicoOpt.isEmpty()) {
            return 0;
        }

        AnimalServico animalServico = animalServicoOpt.get();
        Integer quantidadeTotal = animalServico.getServico().getQuantidade();
        Long banhosRealizados = banhoIndividualRepository.countByAnimalServicoId(animalServicoId);

        return quantidadeTotal - banhosRealizados.intValue();
    }

    // Método auxiliar para calcular o próximo número disponível de banho
    private Integer calcularProximoNumeroBanho(Long animalServicoId, Integer quantidadeTotal) {
        // Busca todos os banhos existentes ordenados por número
        List<BanhoIndividual> banhosExistentes = banhoIndividualRepository.findByAnimalServicoIdOrderByNumeroBanho(animalServicoId);

        // Se não há banhos, começa com 1
        if (banhosExistentes.isEmpty()) {
            return 1;
        }

        // Procura o primeiro gap na sequência
        for (int i = 1; i <= quantidadeTotal; i++) {
            final int numeroAtual = i;
            boolean numeroExiste = banhosExistentes.stream()
                    .anyMatch(banho -> banho.getNumeroBanho().equals(numeroAtual));

            if (!numeroExiste) {
                return numeroAtual;  // Encontrou um gap
            }
        }

        // Se chegou aqui, não há gaps e todos os números estão ocupados
        // Isso não deveria acontecer se a validação anterior estiver correta
        throw new RuntimeException("Não há números de banho disponíveis. Verifique a lógica de contagem.");
    }
}
