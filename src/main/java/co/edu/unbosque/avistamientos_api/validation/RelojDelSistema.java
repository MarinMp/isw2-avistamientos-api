package co.edu.unbosque.avistamientos_api.validation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Implementación real del reloj: devuelve la fecha actual del sistema
@Component
public class RelojDelSistema implements Reloj {

    @Override
    public LocalDate hoy() {
        return LocalDate.now();
    }
}