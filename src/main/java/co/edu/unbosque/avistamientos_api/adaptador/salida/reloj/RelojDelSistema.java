package co.edu.unbosque.avistamientos_api.adaptador.salida.reloj;

import co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida.Reloj;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Adaptador de salida: implementa el puerto Reloj con la fecha del sistema
@Component
public class RelojDelSistema implements Reloj {

    @Override
    public LocalDate hoy() {
        return LocalDate.now();
    }
}