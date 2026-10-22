package co.edu.unbosque.avistamientos_api.controller;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import co.edu.unbosque.avistamientos_api.repository.AvistamientoRepository;
import co.edu.unbosque.avistamientos_api.validation.RelojFijo;
import co.edu.unbosque.avistamientos_api.validation.ValidadorDeAvistamiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias del controlador
class AvistamientoControllerTest {

    private AvistamientoRepository repositorio;
    private AvistamientoController controlador;

    @BeforeEach
    void prepararControlador() {
        // Doble del repositorio: un mock creado por Mockito
        repositorio = mock(AvistamientoRepository.class);
        // Validador real, con el stub del reloj (hoy = 2026-10-27)
        ValidadorDeAvistamiento validador = new ValidadorDeAvistamiento(new RelojFijo(LocalDate.of(2026, 10, 27)));
        controlador = new AvistamientoController(repositorio, validador);
    }

    @Test
    void registrarAvistamientoValidoLoGuardaUnaVezYResponde201() {
        Avistamiento guardado = avistamientoValido();
        guardado.setId(1L);
        when(repositorio.save(any(Avistamiento.class))).thenReturn(guardado);

        ResponseEntity<?> respuesta = controlador.registrar(avistamientoValido());

        assertEquals(201, respuesta.getStatusCode().value());
        verify(repositorio, times(1)).save(any(Avistamiento.class));
    }

    @Test
    void registrarAvistamientoInvalidoResponde400YNoLoGuarda() {
        Avistamiento invalido = avistamientoValido();
        invalido.setEspecie("ab");

        ResponseEntity<?> respuesta = controlador.registrar(invalido);

        assertEquals(400, respuesta.getStatusCode().value());
        assertEquals(Map.of("error", "La especie debe tener entre 3 y 50 caracteres"), respuesta.getBody());
        verify(repositorio, never()).save(any(Avistamiento.class));
    }

    @Test
    void obtenerAvistamientoQueNoExisteResponde404() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> respuesta = controlador.obtener(99L);

        assertEquals(404, respuesta.getStatusCode().value());
        assertEquals(Map.of("error", "No existe un avistamiento con id 99"), respuesta.getBody());
    }

    // Crea un avistamiento con todos los campos válidos
    private Avistamiento avistamientoValido() {
        Avistamiento a = new Avistamiento();
        a.setEspecie("guacamaya");
        a.setLugar("Leticia, Amazonas");
        a.setFecha("2026-09-15");
        a.setObservador("Paula");
        return a;
    }
}