package co.edu.unbosque.avistamientos_api.validation;

import java.time.LocalDate;

// Fuente de la fecha de hoy
public interface Reloj {
    LocalDate hoy();
}