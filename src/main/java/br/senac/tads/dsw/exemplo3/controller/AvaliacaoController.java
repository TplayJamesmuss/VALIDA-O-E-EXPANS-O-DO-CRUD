package br.senac.tads.dsw.exemplo3.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.senac.tads.dsw.exemplo3.model.Avaliacao;
import br.senac.tads.dsw.exemplo3.repository.AvaliacaoRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoRepository repository;

    public AvaliacaoController(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Avaliacao> listarTodas() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscarPorId(@PathVariable Long id) {
        Optional<Avaliacao> avaliacaoBuscada = repository.findById(id);

        if (avaliacaoBuscada.isPresent()) {
            return ResponseEntity.ok(avaliacaoBuscada.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Avaliacao> criarAvaliacao(@Valid @RequestBody Avaliacao avaliacao) {
        Avaliacao avaliacaoSalva = repository.save(avaliacao);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(avaliacaoSalva.getId())
                .toUri();

        return ResponseEntity.created(location).body(avaliacaoSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> atualizarAvaliacao(@PathVariable Long id,
            @Valid @RequestBody Avaliacao avaliacaoAtualizada) {
        Optional<Avaliacao> avaliacaoBuscada = repository.findById(id);

        if (avaliacaoBuscada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Avaliacao avaliacaoExistente = avaliacaoBuscada.get();
        BeanUtils.copyProperties(avaliacaoAtualizada, avaliacaoExistente, "id");
        return ResponseEntity.ok(repository.save(avaliacaoExistente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarAvaliacao(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}