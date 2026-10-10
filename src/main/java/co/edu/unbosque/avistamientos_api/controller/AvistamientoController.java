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

    // Listar todos los avistamientos
    @GetMapping
    public List<Avistamiento> listar() {
        return repositorio.findAll();
    }

    // Ver un avistamiento
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable("id") Long id) {
        Optional<Avistamiento> encontrado = repositorio.findById(id);
        if (encontrado.isEmpty()) {
            return noEncontrado(id);
        }
        return ResponseEntity.ok(encontrado.get());
    }

    // Registrar un avistamiento
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Avistamiento datos) {
        String error = validar(datos);
        if (error != null) {
            return datosInvalidos(error);
        }
        datos.setId(null);
        Avistamiento creado = repositorio.save(datos);
        return ResponseEntity.created(URI.create("/avistamientos/" + creado.getId()))
                .body(creado);
    }

    // Actualizar un avistamiento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody Avistamiento datos) {
        Optional<Avistamiento> encontrado = repositorio.findById(id);
        if (encontrado.isEmpty()) {
            return noEncontrado(id);
        }
        String error = validar(datos);
        if (error != null) {
            return datosInvalidos(error);
        }
        Avistamiento existente = encontrado.get();
        existente.setEspecie(datos.getEspecie());
        existente.setLugar(datos.getLugar());
        existente.setFecha(datos.getFecha());
        existente.setObservador(datos.getObservador());
        return ResponseEntity.ok(repositorio.save(existente));
    }

    // Eliminar un avistamiento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Long id) {
        if (!repositorio.existsById(id)) {
            return noEncontrado(id);
        }
        repositorio.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Avistamiento con id " + id + " eliminado correctamente"));
    }

    // Respuesta cuando el avistamiento no existe
    private ResponseEntity<Map<String, String>> noEncontrado(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No existe un avistamiento con id " + id));
    }

    // Respuesta con el mensaje de error de validación
    private ResponseEntity<Map<String, String>> datosInvalidos(String mensaje) {
        return ResponseEntity.badRequest().body(Map.of("error", mensaje));
    }

    // Valida los datos recibidos
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
        if (a.getEspecie().length() > 100) return "La especie no puede tener más de 100 caracteres";
        if (a.getLugar().length() > 100) return "El lugar no puede tener más de 100 caracteres";
        if (a.getObservador().length() > 30) return "El observador no puede tener más de 30 caracteres";
        return null;
    }

    // Indica si un texto es null, vacío o solo espacios
    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}