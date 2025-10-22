package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.CriarMaquininhaDTO;
import backend.prontodogbanho.dto.AtualizarMaquininhaDTO;
import backend.prontodogbanho.dto.MaquininhaCompletoDTO;
import backend.prontodogbanho.dto.MaquininhaResumoDTO;
import backend.prontodogbanho.model.*;
import backend.prontodogbanho.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaquininhaService {

    private final MaquininhaRepository maquininhaRepository;
    private final AdquirenteRepository adquirenteRepository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final BandeiraRepository bandeiraRepository;
    private final MaquininhaTaxaRepository taxaRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;

    public MaquininhaService(
            MaquininhaRepository maquininhaRepository,
            AdquirenteRepository adquirenteRepository,
            ContaBancariaRepository contaBancariaRepository,
            BandeiraRepository bandeiraRepository,
            MaquininhaTaxaRepository taxaRepository,
            FormaPagamentoRepository formaPagamentoRepository
    ) {
        this.maquininhaRepository = maquininhaRepository;
        this.adquirenteRepository = adquirenteRepository;
        this.contaBancariaRepository = contaBancariaRepository;
        this.bandeiraRepository = bandeiraRepository;
        this.taxaRepository = taxaRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    // Listar todas
    public List<Maquininha> listarTodas() {
        return maquininhaRepository.findAll();
    }

    // Listar apenas ativas
    public List<Maquininha> listarAtivas() {
        return maquininhaRepository.findByAtivoTrue();
    }

    // Listar ativas com resumo
    public List<MaquininhaResumoDTO> listarAtivasResumo() {
        return listarAtivas().stream()
                .map(MaquininhaResumoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Buscar por ID com detalhes
    public Maquininha buscarPorId(Long id) {
        return maquininhaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Maquininha não encontrada com ID: " + id));
    }

    // Buscar por ID com DTO completo
    public MaquininhaCompletoDTO buscarPorIdCompleto(Long id) {
        Maquininha maquininha = buscarPorId(id);
        return MaquininhaCompletoDTO.fromEntity(maquininha);
    }

    // Criar maquininha com taxas
    @Transactional
    public Maquininha criar(CriarMaquininhaDTO dto) {
        // Validar adquirente
        Adquirente adquirente = adquirenteRepository.findById(dto.getAdquirenteId())
                .orElseThrow(() -> new RuntimeException("Adquirente não encontrado"));

        // Validar conta bancária
        ContaBancaria conta = contaBancariaRepository.findById(dto.getContaBancariaId())
                .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada"));

        // Validar conta PIX se informada
        ContaBancaria contaPix = null;
        if (dto.getContaPixId() != null) {
            contaPix = contaBancariaRepository.findById(dto.getContaPixId())
                    .orElseThrow(() -> new RuntimeException("Conta PIX não encontrada"));
        }

        // Criar maquininha
        Maquininha maquininha = new Maquininha();
        maquininha.setNome(dto.getNome());
        maquininha.setAdquirente(adquirente);
        maquininha.setContaBancaria(conta);
        maquininha.setPrazoRecebimentoDebito(dto.getPrazoRecebimentoDebito());
        maquininha.setPrazoRecebimentoCredito(dto.getPrazoRecebimentoCredito());
        maquininha.setAceitaAntecipacao(dto.getAceitaAntecipacao());
        maquininha.setAntecipacaoAutomatica(dto.getAntecipacaoAutomatica());
        maquininha.setTaxaAntecipacaoMensal(dto.getTaxaAntecipacaoMensal());
        maquininha.setAceitaPix(dto.getAceitaPix());
        maquininha.setContaPix(contaPix);
        maquininha.setTaxaPix(dto.getTaxaPix());
        maquininha.setPrazoRecebimentoPix(dto.getPrazoRecebimentoPix());

        // Salvar maquininha
        maquininha = maquininhaRepository.save(maquininha);

        // Criar forma de pagamento automaticamente
        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setNome(maquininha.getNome());
        formaPagamento.setTipo("credito"); // Tipo padrão, pode ser ajustado
        formaPagamento.setMaquininha(maquininha);
        formaPagamento.setAtivo(true);
        formaPagamento.setParcelasMax(12); // Padrão para maquininhas
        formaPagamento.setTaxaPercentual(BigDecimal.ZERO); // Taxa será calculada dinamicamente
        formaPagamento.setTaxaFixa(BigDecimal.ZERO);
        formaPagamento.setDiasRecebimento(maquininha.getPrazoRecebimentoCredito() != null ? maquininha.getPrazoRecebimentoCredito() : 30);
        formaPagamentoRepository.save(formaPagamento);

        // Adicionar taxas se informadas
        if (dto.getTaxas() != null && !dto.getTaxas().isEmpty()) {
            for (CriarMaquininhaDTO.TaxaMaquininhaDTO taxaDto : dto.getTaxas()) {
                adicionarTaxa(maquininha.getId(), taxaDto);
            }
        }

        return maquininha;
    }

    // Atualizar maquininha
    @Transactional
    public Maquininha atualizar(Long id, AtualizarMaquininhaDTO dto) {
        Maquininha maquininha = buscarPorId(id);

        if (dto.getNome() != null) maquininha.setNome(dto.getNome());

        if (dto.getAdquirenteId() != null) {
            Adquirente adquirente = adquirenteRepository.findById(dto.getAdquirenteId())
                    .orElseThrow(() -> new RuntimeException("Adquirente não encontrado"));
            maquininha.setAdquirente(adquirente);
        }

        if (dto.getContaBancariaId() != null) {
            ContaBancaria conta = contaBancariaRepository.findById(dto.getContaBancariaId())
                    .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada"));
            maquininha.setContaBancaria(conta);
        }

        if (dto.getPrazoRecebimentoDebito() != null) maquininha.setPrazoRecebimentoDebito(dto.getPrazoRecebimentoDebito());
        if (dto.getPrazoRecebimentoCredito() != null) maquininha.setPrazoRecebimentoCredito(dto.getPrazoRecebimentoCredito());
        if (dto.getAceitaAntecipacao() != null) maquininha.setAceitaAntecipacao(dto.getAceitaAntecipacao());
        if (dto.getAntecipacaoAutomatica() != null) maquininha.setAntecipacaoAutomatica(dto.getAntecipacaoAutomatica());
        if (dto.getTaxaAntecipacaoMensal() != null) maquininha.setTaxaAntecipacaoMensal(dto.getTaxaAntecipacaoMensal());
        if (dto.getAceitaPix() != null) maquininha.setAceitaPix(dto.getAceitaPix());

        if (dto.getContaPixId() != null) {
            ContaBancaria contaPix = contaBancariaRepository.findById(dto.getContaPixId())
                    .orElseThrow(() -> new RuntimeException("Conta PIX não encontrada"));
            maquininha.setContaPix(contaPix);
        }

        if (dto.getTaxaPix() != null) maquininha.setTaxaPix(dto.getTaxaPix());
        if (dto.getPrazoRecebimentoPix() != null) maquininha.setPrazoRecebimentoPix(dto.getPrazoRecebimentoPix());
        if (dto.getAtivo() != null) maquininha.setAtivo(dto.getAtivo());

        return maquininhaRepository.save(maquininha);
    }

    // Adicionar taxa
    @Transactional
    public MaquininhaTaxa adicionarTaxa(Long maquininhaId, CriarMaquininhaDTO.TaxaMaquininhaDTO taxaDto) {
        Maquininha maquininha = buscarPorId(maquininhaId);

        Bandeira bandeira = bandeiraRepository.findById(taxaDto.getBandeiraId())
                .orElseThrow(() -> new RuntimeException("Bandeira não encontrada"));

        MaquininhaTaxa taxa = new MaquininhaTaxa();
        taxa.setMaquininha(maquininha);
        taxa.setBandeira(bandeira);
        taxa.setTipoTransacao(taxaDto.getTipoTransacao());
        taxa.setNumeroParcelas(taxaDto.getNumeroParcelas());
        taxa.setTaxaPercentual(taxaDto.getTaxaPercentual());
        taxa.setTaxaFixa(taxaDto.getTaxaFixa());

        return taxaRepository.save(taxa);
    }

    // Buscar taxa específica
    public MaquininhaTaxa buscarTaxa(Long maquininhaId, Long bandeiraId, String tipoTransacao, Integer numeroParcelas) {
        return taxaRepository.findTaxaExata(maquininhaId, bandeiraId, tipoTransacao, numeroParcelas)
                .orElse(null);
    }

    // Calcular taxa de uma transação
    public BigDecimal calcularTaxa(Long maquininhaId, Long bandeiraId, String tipoTransacao, Integer numeroParcelas, BigDecimal valor) {
        MaquininhaTaxa taxa = buscarTaxa(maquininhaId, bandeiraId, tipoTransacao, numeroParcelas);

        if (taxa == null) {
            return BigDecimal.ZERO;
        }

        return taxa.calcularTaxa(valor);
    }

    // Calcular data de recebimento
    public LocalDate calcularDataRecebimento(Long maquininhaId, String tipoTransacao, LocalDate dataTransacao) {
        Maquininha maquininha = buscarPorId(maquininhaId);
        return maquininha.calcularDataRecebimento(tipoTransacao, dataTransacao);
    }

    // Calcular valor com taxa (repasse ao cliente)
    public Map<String, Object> calcularValorComTaxa(
            Long maquininhaId,
            Long bandeiraId,
            String tipoTransacao,
            Integer numeroParcelas,
            BigDecimal valorOriginal) {

        // Buscar taxa
        BigDecimal valorTaxa = calcularTaxa(maquininhaId, bandeiraId, tipoTransacao, numeroParcelas, valorOriginal);

        // Calcular valor líquido (valor que você recebe = valor - taxa)
        BigDecimal valorLiquido = valorOriginal.subtract(valorTaxa);

        // Calcular data de recebimento
        LocalDate dataRecebimento = calcularDataRecebimento(maquininhaId, tipoTransacao, LocalDate.now());

        // Montar resposta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("valorOriginal", valorOriginal);     // Valor que cliente paga
        resultado.put("valorTaxa", valorTaxa);              // Taxa da maquininha (despesa)
        resultado.put("valorFinal", valorLiquido);          // Valor líquido que você recebe
        resultado.put("dataRecebimento", dataRecebimento);

        // Adicionar info da taxa
        MaquininhaTaxa taxa = buscarTaxa(maquininhaId, bandeiraId, tipoTransacao, numeroParcelas);
        if (taxa != null) {
            resultado.put("taxaPercentual", taxa.getTaxaPercentual());
            resultado.put("taxaFixa", taxa.getTaxaFixa());
        }

        return resultado;
    }

    // Excluir (hard delete)
    @Transactional
    public void excluir(Long id) {
        Maquininha maquininha = buscarPorId(id);

        // Excluir também a forma de pagamento vinculada
        formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
            formaPagamentoRepository.delete(forma);
        });

        // Excluir maquininha
        maquininhaRepository.delete(maquininha);
    }

    // Ativar
    @Transactional
    public void ativar(Long id) {
        Maquininha maquininha = buscarPorId(id);
        maquininha.setAtivo(true);
        maquininhaRepository.save(maquininha);

        // Ativar também a forma de pagamento vinculada
        formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
            forma.setAtivo(true);
            formaPagamentoRepository.save(forma);
        });
    }
}

