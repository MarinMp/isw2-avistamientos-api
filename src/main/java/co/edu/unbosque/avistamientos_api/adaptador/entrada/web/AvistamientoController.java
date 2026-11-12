package co.edu.unbosque.avistamientos_api.adaptador.entrada.web;

import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.AvistamientoNoEncontradoException;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos_api.dominio.Avistamiento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

// Adaptador de entrada: traduce las peticiones HTTP a llamadas al puerto GestionarAvistamientos
// y el resultado (o la excepción) a una respuesta HTTP. No tiene reglas de negocio.
@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final GestionarAvistamientos casosDeUso;

    public AvistamientoController(GestionarAvistamientos casosDeUso) {
        this.casosDeUso = casosDeUso;
    }

    // Listar todos los avistamientos
    @GetMapping
    public List<Avistamiento> listar() {
        return casosDeUso.listar();
    }

    // Ver un avistamiento
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(casosDeUso.obtener(id));
        } catch (AvistamientoNoEncontradoException e) {
            return noEncontrado(e);
        }
    }

    // Registrar un avistamiento
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Avistamiento datos) {
        try {
            Avistamiento creado = casosDeUso.registrar(datos);
            return ResponseEntity.created(URI.create("/avistamientos/" + creado.getId()))
                    .body(creado);
        } catch (AvistamientoInvalidoException e) {
            return datosInvalidos(e);
        }
    }

    // Actualizar un avistamiento
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable("id") Long id, @RequestBody Avistamiento datos) {
        try {
            return ResponseEntity.ok(casosDeUso.actualizar(id, datos));
        } catch (AvistamientoNoEncontradoException e) {
            return noEncontrado(e);
        } catch (AvistamientoInvalidoException e) {
            return datosInvalidos(e);
        }
    }

    // Eliminar un avistamiento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable("id") Long id) {
        try {
            casosDeUso.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Avistamiento con id " + id + " eliminado correctamente"));
        } catch (AvistamientoNoEncontradoException e) {
            return noEncontrado(e);
        }
    }

    // Bonus - Resumen avistamientos por especie
    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return casosDeUso.resumenPorEspecie();
    }

    // Respuesta cuando el avistamiento no existe
    private ResponseEntity<Map<String, String>> noEncontrado(AvistamientoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    // Respuesta con el mensaje de error de validación
    private ResponseEntity<Map<String, String>> datosInvalidos(AvistamientoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
