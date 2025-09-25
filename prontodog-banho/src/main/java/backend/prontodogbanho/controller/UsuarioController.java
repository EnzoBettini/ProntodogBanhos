package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Usuario;
import backend.prontodogbanho.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private UsuarioService usuarioService;

    public  UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return this.usuarioService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = this.usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> adicionarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = this.usuarioService.salvar(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        this.usuarioService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
