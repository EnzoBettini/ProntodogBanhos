package backend.prontodogbanho.service;

import backend.prontodogbanho.model.FormaPagamento;
import backend.prontodogbanho.repository.FormaPagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormaPagamentoService {

    private final FormaPagamentoRepository formaPagamentoRepository;

    public FormaPagamentoService(FormaPagamentoRepository formaPagamentoRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
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

    public void excluir(Long id) {
        formaPagamentoRepository.deleteById(id);
    }

    public void ativar(Long id) {
        formaPagamentoRepository.findById(id).ifPresent(forma -> {
            forma.setAtivo(true);
            formaPagamentoRepository.save(forma);
        });
    }

    public void desativar(Long id) {
        formaPagamentoRepository.findById(id).ifPresent(forma -> {
            forma.setAtivo(false);
            formaPagamentoRepository.save(forma);
        });
    }

    public List<FormaPagamento> listarPorTipo(String tipo) {
        return formaPagamentoRepository.findByTipoAndAtivoTrue(tipo);
    }

    public List<FormaPagamento> listarComParcelamento() {
        return formaPagamentoRepository.findFormasComParcelamento();
    }
}

