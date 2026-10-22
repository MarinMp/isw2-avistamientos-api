package co.edu.unbosque.avistamientos_api.validation;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidadorDeAvistamiento {

    private final Reloj reloj;

    // Recibe el reloj desde afuera
    public ValidadorDeAvistamiento(Reloj reloj) {
        this.reloj = reloj;
    }

    // Devuelve el mensaje de error, o null si el avistamiento es válido
    public String validar(Avistamiento a) {
        // 1. Campos obligatorios
        List<String> faltantes = new ArrayList<>();
        if (vacio(a.getEspecie())) faltantes.add("especie");
        if (vacio(a.getLugar())) faltantes.add("lugar");
        if (vacio(a.getFecha())) faltantes.add("fecha");
        if (vacio(a.getObservador())) faltantes.add("observador");
        if (!faltantes.isEmpty()) {
            return "Faltan datos obligatorios: " + String.join(", ", faltantes);
        }

        // Especie: entre 3 y 50 caracteres (sin contar espacios al inicio y al final)
        int largoEspecie = a.getEspecie().trim().length();
        if (largoEspecie < 3 || largoEspecie > 50) {
            return "La especie debe tener entre 3 y 50 caracteres";
        }

        // Fecha: formato AAAA-MM-DD y no posterior a hoy
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(a.getFecha().trim());
        } catch (DateTimeParseException e) {
            return "La fecha debe tener el formato AAAA-MM-DD";
        }
        if (fecha.isAfter(reloj.hoy())) {
            return "La fecha no puede ser posterior a hoy";
        }

        // Largos máximos de lugar y observador (coinciden con la entidad)
        if (a.getLugar().trim().length() > 100) return "El lugar no puede tener más de 100 caracteres";
        if (a.getObservador().trim().length() > 30) return "El observador no puede tener más de 30 caracteres";

        return null;
    }

    // Indica si un texto es null, vacío o solo espacios
    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}