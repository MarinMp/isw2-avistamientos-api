package co.edu.unbosque.avistamientos_api.validation;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// Pruebas unitarias del validador: no levantan la API ni usan la base de datos.
class ValidadorDeAvistamientoTest {

    // "Hoy" fijo para todas las pruebas: 27 de octubre de 2026
    private static final LocalDate HOY = LocalDate.of(2026, 10, 27);

    private ValidadorDeAvistamiento validador;

    @BeforeEach
    void prepararValidador() {
        validador = new ValidadorDeAvistamiento(new RelojFijo(HOY));
    }

    // ===================== ESPECIE: valores límite =====================

    @Test
    void especieDeTresCaracteresEsValida() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("abc");

        String error = validador.validar(avistamiento);

        assertNull(error);
    }

    @Test
    void especieDeDosCaracteresEsRechazada() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("ab");

        String error = validador.validar(avistamiento);

        assertEquals("La especie debe tener entre 3 y 50 caracteres", error);
    }

    @Test
    void especieDeCincuentaCaracteresEsValida() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("a".repeat(50));

        String error = validador.validar(avistamiento);

        assertNull(error);
    }

    @Test
    void especieDeCincuentaYUnCaracteresEsRechazada() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("a".repeat(51));

        String error = validador.validar(avistamiento);

        assertEquals("La especie debe tener entre 3 y 50 caracteres", error);
    }

    // ===================== FECHA: valores límite =====================

    @Test
    void fechaDeHoyEsValida() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-10-27");

        String error = validador.validar(avistamiento);

        assertNull(error);
    }

    @Test
    void fechaDeMananaEsRechazada() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-10-28");

        String error = validador.validar(avistamiento);

        assertEquals("La fecha no puede ser posterior a hoy", error);
    }

    // ===================== AUXILIAR =====================

    // Crea un avistamiento con todos los campos válidos.
    // Cada prueba cambia solo el campo que quiere probar.
    private Avistamiento avistamientoValido() {
        Avistamiento a = new Avistamiento();
        a.setEspecie("guacamaya");
        a.setLugar("Leticia, Amazonas");
        a.setFecha("2026-09-15");
        a.setObservador("Paula");
        return a;
    }
}