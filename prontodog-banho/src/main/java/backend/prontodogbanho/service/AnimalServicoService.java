package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.CriarAnimalServicoDTO;
import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.BanhoIndividual;
import backend.prontodogbanho.model.Servico;
import backend.prontodogbanho.model.Usuario;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import backend.prontodogbanho.repository.BanhoIndividualRepository;
import backend.prontodogbanho.repository.ServicoRepository;
import backend.prontodogbanho.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnimalServicoService {

    private final AnimalServicoRepository animalServicoRepository;
    private final BanhoIndividualRepository banhoIndividualRepository;
    private final AnimalRepository animalRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;

    public AnimalServicoService(AnimalServicoRepository animalServicoRepository,
                              BanhoIndividualRepository banhoIndividualRepository,
                              AnimalRepository animalRepository,
                              ServicoRepository servicoRepository,
                              UsuarioRepository usuarioRepository) {
        this.animalServicoRepository = animalServicoRepository;
        this.banhoIndividualRepository = banhoIndividualRepository;
        this.animalRepository = animalRepository;
        this.servicoRepository = servicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AnimalServico> listarTodos() {
        return animalServicoRepository.findAll();
    }

    public Optional<AnimalServico> buscarPorId(Long id) {
        return animalServicoRepository.findById(id);
    }

    @Transactional
    public AnimalServico salvar(AnimalServico animalServico) {
        return animalServicoRepository.save(animalServico);
    }

    @Transactional
    public AnimalServico criarComBanhosIndividuais(CriarAnimalServicoDTO dto) {
        System.out.println("🔄 Recebendo DTO: " + dto);

        // Buscar entidades relacionadas
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com ID: " + dto.getAnimalId()));

        Servico servico = servicoRepository.findById(dto.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + dto.getServicoId()));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getUsuarioId()));

        // Validar banhos usados
        if (dto.getBanhosUsados() < 0 || dto.getBanhosUsados() > servico.getQuantidade()) {
            throw new RuntimeException("Banhos usados (" + dto.getBanhosUsados() +
                ") deve estar entre 0 e " + servico.getQuantidade());
        }

        // Converter datas usando métodos seguros
        LocalDate dataServicoLocal = dto.getDataServicoAsLocalDate();
        if (dataServicoLocal == null) {
            throw new RuntimeException("Data do serviço é obrigatória e deve estar no formato YYYY-MM-DD");
        }

        System.out.println("📅 Data do serviço convertida: " + dto.getDataServico() + " -> " + dataServicoLocal);

        // Validar datas dos banhos se banhos usados > 0
        List<LocalDate> datasBanhosLocal = null;
        if (dto.getBanhosUsados() > 0) {
            if (dto.getDatasBanhosRealizados() == null ||
                dto.getDatasBanhosRealizados().size() != dto.getBanhosUsados()) {
                throw new RuntimeException("Deve fornecer exatamente " + dto.getBanhosUsados() +
                    " datas para os banhos já realizados");
            }

            datasBanhosLocal = dto.getDatasBanhosRealizadosAsLocalDate();
            if (datasBanhosLocal.size() != dto.getBanhosUsados()) {
                throw new RuntimeException("Algumas datas dos banhos são inválidas. Formato esperado: YYYY-MM-DD");
            }

            System.out.println("📅 Datas dos banhos convertidas: " + datasBanhosLocal);
        }

        // Converter data de expiração se fornecida
        LocalDate dataExpiracaoLocal = dto.getDataExpiracaoAsLocalDate();
        if (dataExpiracaoLocal != null) {
            System.out.println("📅 Data de expiração convertida: " + dto.getDataExpiracao() + " -> " + dataExpiracaoLocal);
        }

        // Criar AnimalServico
        AnimalServico animalServico = new AnimalServico();
        animalServico.setDataServico(dataServicoLocal);
        animalServico.setBanhosUsados(dto.getBanhosUsados());
        animalServico.setDataExpiracao(dataExpiracaoLocal);
        animalServico.setAnimal(animal);
        animalServico.setServico(servico);
        animalServico.setUsuario(usuario);

        // Salvar AnimalServico primeiro
        AnimalServico animalServicoSalvo = animalServicoRepository.save(animalServico);
        System.out.println("✅ AnimalServico salvo com data: " + animalServicoSalvo.getDataServico());

        // Criar banhos individuais se banhos usados > 0
        if (dto.getBanhosUsados() > 0 && datasBanhosLocal != null) {
            for (int i = 0; i < dto.getBanhosUsados(); i++) {
                BanhoIndividual banho = new BanhoIndividual();
                banho.setAnimalServico(animalServicoSalvo);
                banho.setDataBanho(datasBanhosLocal.get(i));
                banho.setNumeroBanho(i + 1);

                // Adicionar observação se fornecida
                if (dto.getObservacoesBanhos() != null &&
                    i < dto.getObservacoesBanhos().size() &&
                    dto.getObservacoesBanhos().get(i) != null) {
                    banho.setObservacoes(dto.getObservacoesBanhos().get(i));
                }

                banho.setUsuario(usuario);

                BanhoIndividual banhoSalvo = banhoIndividualRepository.save(banho);
                System.out.println("✅ Banho " + (i + 1) + " salvo com data: " + banhoSalvo.getDataBanho());
            }
        }

        return animalServicoSalvo;
    }

    public void deletar(Long id) {
        animalServicoRepository.deleteById(id);
    }

    @Transactional
    public AnimalServico atualizarTudo(Long id, AnimalServico novosDados) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServicoExistente = animalServicoOptional.get();

            animalServicoExistente.setDataServico(novosDados.getDataServico());
            animalServicoExistente.setBanhosUsados(novosDados.getBanhosUsados());
            animalServicoExistente.setDataExpiracao(novosDados.getDataExpiracao());
            animalServicoExistente.setAnimal(novosDados.getAnimal());
            animalServicoExistente.setServico(novosDados.getServico());
            animalServicoExistente.setUsuario(novosDados.getUsuario());

            return animalServicoRepository.save(animalServicoExistente);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    // Métodos para controle de expiração de pacotes

    /**
     * Busca pacotes que vão vencer em até X dias
     * @param dias número de dias para considerar como "vai vencer"
     * @return lista de pacotes que vão vencer
     */
    public List<AnimalServico> buscarPacotesQueVaoVencer(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isAfter(LocalDate.now()))
                .filter(as -> as.getDataExpiracao().isBefore(dataLimite) || as.getDataExpiracao().isEqual(dataLimite))
                .toList();
    }

    /**
     * Busca pacotes já vencidos
     * @return lista de pacotes vencidos
     */
    public List<AnimalServico> buscarPacotesVencidos() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isBefore(hoje))
                .toList();
    }

    /**
     * Busca pacotes válidos (não vencidos ou sem data de expiração)
     * @return lista de pacotes válidos
     */
    public List<AnimalServico> buscarPacotesValidos() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() == null ||
                            as.getDataExpiracao().isAfter(hoje) ||
                            as.getDataExpiracao().isEqual(hoje))
                .toList();
    }

    /**
     * Busca pacotes que expiram exatamente hoje
     * @return lista de pacotes que expiram hoje
     */
    public List<AnimalServico> buscarPacotesQueExpiramHoje() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isEqual(hoje))
                .toList();
    }
}
