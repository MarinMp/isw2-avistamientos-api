package co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida;

import java.time.LocalDate;

// Puerto de salida: la aplicación pide la fecha de hoy sin saber de dónde sale
public interface Reloj {
    LocalDate hoy();
}