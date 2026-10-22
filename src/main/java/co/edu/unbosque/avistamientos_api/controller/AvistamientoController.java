package co.edu.unbosque.avistamientos_api.controller;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import co.edu.unbosque.avistamientos_api.repository.AvistamientoRepository;
import co.edu.unbosque.avistamientos_api.validation.ValidadorDeAvistamiento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repositorio;
    private final ValidadorDeAvistamiento validador;

    public AvistamientoController(AvistamientoRepository repositorio, ValidadorDeAvistamiento validador) {
        this.repositorio = repositorio;
        this.validador = validador;
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
        String error = validador.validar(datos);
        if (error != null) {
            return datosInvalidos(error);
        }
        normalizar(datos);
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
        String error = validador.validar(datos);
        if (error != null) {
            return datosInvalidos(error);
        }
        normalizar(datos);
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

    // Bonus - Resumen avistamientos por especie
    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return repositorio.findAll().stream()
                .collect(Collectors.groupingBy(Avistamiento::getEspecie, TreeMap::new, Collectors.counting()));
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

    // Quita espacios sobrantes y pasa la especie a minúsculas
    private void normalizar(Avistamiento a) {
        a.setEspecie(a.getEspecie().trim().toLowerCase());
        a.setLugar(a.getLugar().trim());
        a.setFecha(a.getFecha().trim());
        a.setObservador(a.getObservador().trim());
    }
}