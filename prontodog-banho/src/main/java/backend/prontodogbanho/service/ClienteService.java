package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.repository.ClienteRespository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Service
@RequestMapping("/cliente")
public class ClienteService {
    private final ClienteRespository clienteRespository;

    public ClienteService(ClienteRespository clienteRespository) {
        this.clienteRespository = clienteRespository;
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<Optional<Cliente>> procurarPorId (@PathVariable ("id") Long id) {
        Optional<Cliente> cliente = clienteRespository.findById(id);
        return ResponseEntity.ok(cliente);
    }
}
