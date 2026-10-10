package co.edu.unbosque.avistamientos_api.controller;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import co.edu.unbosque.avistamientos_api.repository.AvistamientoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repositorio;

    public AvistamientoController(AvistamientoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<Avistamiento> listar() {
        return repositorio.findAll();
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Avistamiento datos) {
        datos.setId(null);
        Avistamiento creado = repositorio.save(datos);
        return ResponseEntity.created(URI.create("/avistamientos/" + creado.getId()))
                .body(creado);
    }
}