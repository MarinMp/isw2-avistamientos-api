package co.edu.unbosque.avistamientos_api.controller;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import co.edu.unbosque.avistamientos_api.repository.AvistamientoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable("id") Long id) {
        Optional<Avistamiento> encontrado = repositorio.findById(id);
        if (encontrado.isEmpty()) {
            return noEncontrado(id);
        }
        return ResponseEntity.ok(encontrado.get());
    }

    private ResponseEntity<Map<String, String>> noEncontrado(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe un avistamiento con id " + id));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Avistamiento datos) {
        String error = validar(datos);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        datos.setId(null);
        Avistamiento creado = repositorio.save(datos);
        return ResponseEntity.created(URI.create("/avistamientos/" + creado.getId()))
                .body(creado);
    }

    private String validar(Avistamiento a) {
        List<String> faltantes = new ArrayList<>();
        if (vacio(a.getEspecie())) faltantes.add("especie");
        if (vacio(a.getLugar())) faltantes.add("lugar");
        if (vacio(a.getFecha())) faltantes.add("fecha");
        if (vacio(a.getObservador())) faltantes.add("observador");
        if (!faltantes.isEmpty()) {
            return "Faltan datos obligatorios: " + String.join(", ", faltantes);
        }
        try {
            LocalDate.parse(a.getFecha());
        } catch (DateTimeParseException e) {
            return "La fecha debe tener el formato AAAA-MM-DD";
        }
        if (a.getEspecie().length() > 30) return "La especie no puede tener más de 30 caracteres";
        if (a.getLugar().length() > 50) return "El lugar no puede tener más de 50 caracteres";
        if (a.getObservador().length() > 30) return "El observador no puede tener más de 30 caracteres";
        return null;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}