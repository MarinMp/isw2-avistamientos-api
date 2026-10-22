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
        // Se le entrega el stub del reloj en lugar del reloj real
        validador = new ValidadorDeAvistamiento(new RelojFijo(HOY));
    }

    // ===================== ESPECIE: valores límite =====================

    @Test
    void especieDeTresCaracteresEsValida() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("abc");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertNull(error);
    }

    @Test
    void especieDeDosCaracteresEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("ab");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("La especie debe tener entre 3 y 50 caracteres", error);
    }

    @Test
    void especieDeCincuentaCaracteresEsValida() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("a".repeat(50));

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertNull(error);
    }

    @Test
    void especieDeCincuentaYUnCaracteresEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setEspecie("a".repeat(51));

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("La especie debe tener entre 3 y 50 caracteres", error);
    }

    // ===================== FECHA: valores límite =====================

    @Test
    void fechaDeHoyEsValida() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-10-27");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertNull(error);
    }

    @Test
    void fechaDeMananaEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-10-28");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
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
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-09-15");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertNull(error);
    }

    @Test
    void fechaConFormatoDiaMesAnioEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("27/10/2026");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("La fecha debe tener el formato AAAA-MM-DD", error);
    }

    @Test
    void fechaInexistenteEsRechazada() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setFecha("2026-02-30");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("La fecha debe tener el formato AAAA-MM-DD", error);
    }

    // ===================== LUGAR Y OBSERVADOR =====================

    @Test
    void lugarVacioSeConsideraFaltante() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setLugar("");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
        assertEquals("Faltan datos obligatorios: lugar", error);
    }

    @Test
    void observadorVacioSeConsideraFaltante() {
        // Arrange
        Avistamiento avistamiento = avistamientoValido();
        avistamiento.setObservador("");

        // Act
        String error = validador.validar(avistamiento);

        // Assert
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