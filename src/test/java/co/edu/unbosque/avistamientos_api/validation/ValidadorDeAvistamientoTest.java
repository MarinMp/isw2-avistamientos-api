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

    // ===================== ESPECIE: otros casos =====================

    @Test
    void especieRepresentativaEsValida() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("guacamaya");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertNull(error);
    }

    @Test
    void especieConSoloEspaciosSeConsideraFaltante() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("   ");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("Faltan datos obligatorios: especie", error);
    }

    @Test
    void especieDeDosCaracteresRodeadaDeEspaciosEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("  ab  ");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("La especie debe tener entre 3 y 50 caracteres", error);
    }

    // ===================== FECHA: otros casos =====================

    @Test
    void fechaPasadaEsValida() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-09-15");

        String error = validador.validar(avistamiento);

        assertNull(error);
    }

    @Test
    void fechaConFormatoDiaMesAnioEsRechazada() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("27/10/2026");

        String error = validador.validar(avistamiento);

        assertEquals("La fecha debe tener el formato AAAA-MM-DD", error);
    }

    @Test
    void fechaInexistenteEsRechazada() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-02-30");

        String error = validador.validar(avistamiento);

        assertEquals("La fecha debe tener el formato AAAA-MM-DD", error);
    }

    // ===================== LUGAR Y OBSERVADOR =====================

    @Test
    void lugarVacioSeConsideraFaltante() {
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setLugar("");

        String error = validador.validar(avistamiento);

        assertEquals("Faltan datos obligatorios: lugar", error);
    }

    @Test
    void observadorVacioSeConsideraFaltante() {

        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setObservador("");

        String error = validador.validar(avistamiento);

        assertEquals("Faltan datos obligatorios: observador", error);
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