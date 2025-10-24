package backend.prontodogbanho.service;

import backend.prontodogbanho.model.FormaPagamento;
import backend.prontodogbanho.model.Maquininha;
import backend.prontodogbanho.repository.FormaPagamentoRepository;
import backend.prontodogbanho.repository.MaquininhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FormaPagamentoService {

    private final FormaPagamentoRepository formaPagamentoRepository;
    private final MaquininhaRepository maquininhaRepository;

    public FormaPagamentoService(FormaPagamentoRepository formaPagamentoRepository, MaquininhaRepository maquininhaRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
        this.maquininhaRepository = maquininhaRepository;
    }

    public List<FormaPagamento> listarTodos() {
        return formaPagamentoRepository.findAll();
    }

    public List<FormaPagamento> listarAtivos() {
        return formaPagamentoRepository.findByAtivoTrue();
    }

    public Optional<FormaPagamento> buscarPorId(Long id) {
        return formaPagamentoRepository.findById(id);
    }

    public FormaPagamento salvar(FormaPagamento formaPagamento) {
        return formaPagamentoRepository.save(formaPagamento);
    }

    public FormaPagamento atualizar(Long id, FormaPagamento formaPagamentoAtualizada) {
        return formaPagamentoRepository.findById(id)
                .map(forma -> {
                    forma.setNome(formaPagamentoAtualizada.getNome());
                    forma.setTipo(formaPagamentoAtualizada.getTipo());
                    forma.setTaxaPercentual(formaPagamentoAtualizada.getTaxaPercentual());
                    forma.setTaxaFixa(formaPagamentoAtualizada.getTaxaFixa());
                    forma.setParcelasMax(formaPagamentoAtualizada.getParcelasMax());
                    forma.setDiasRecebimento(formaPagamentoAtualizada.getDiasRecebimento());
                    forma.setAtivo(formaPagamentoAtualizada.getAtivo());
                    return formaPagamentoRepository.save(forma);
                })
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));
    }

    @Transactional
    public void excluir(Long id) {
        FormaPagamento forma = formaPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));

        // Se tem maquininha vinculada, excluir também
        if (forma.getMaquininha() != null) {
            Maquininha maquininha = forma.getMaquininha();
            maquininhaRepository.delete(maquininha);
        }

        // Excluir forma de pagamento
        formaPagamentoRepository.delete(forma);
    }

    @Transactional
    public void ativar(Long id) {
        formaPagamentoRepository.findById(id).ifPresent(forma -> {
            forma.setAtivo(true);
            formaPagamentoRepository.save(forma);

            // Se tem maquininha vinculada, ativar também
            if (forma.getMaquininha() != null) {
                Maquininha maquininha = forma.getMaquininha();
                maquininha.setAtivo(true);
                maquininhaRepository.save(maquininha);
            }
        });
    }

    @Transactional
    public void desativar(Long id) {
        formaPagamentoRepository.findById(id).ifPresent(forma -> {
            forma.setAtivo(false);
            formaPagamentoRepository.save(forma);

            // Se tem maquininha vinculada, desativar também
            if (forma.getMaquininha() != null) {
                Maquininha maquininha = forma.getMaquininha();
                maquininha.setAtivo(false);
                maquininhaRepository.save(maquininha);
            }
        });
    }

    public List<FormaPagamento> listarPorTipo(String tipo) {
        return formaPagamentoRepository.findByTipoAndAtivoTrue(tipo);
    }

    public List<FormaPagamento> listarComParcelamento() {
        return formaPagamentoRepository.findFormasComParcelamento();
    }
}

