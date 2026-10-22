package co.edu.unbosque.avistamientos_api.validation;

import java.time.LocalDate;

// Doble de prueba de tipo STUB: siempre responde la misma fecha.
// Así "hoy" no depende del día en que se ejecuten las pruebas.
public class RelojFijo implements Reloj {

    private final LocalDate fecha;

    public RelojFijo(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public LocalDate hoy() {
        return fecha;
    }
}