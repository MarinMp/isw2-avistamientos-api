package co.edu.unbosque.avistamientos_api.configuracion;

import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida.RepositorioAvistamientos;
import co.edu.unbosque.avistamientos_api.aplicacion.servicio.ServicioAvistamientos;
import co.edu.unbosque.avistamientos_api.validation.ValidadorDeAvistamiento;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Punto de composición: aquí, fuera del hexágono, se conectan los puertos con sus implementaciones.
// Así la aplicación no necesita anotaciones de Spring.
@Configuration
public class ConfiguracionAplicacion {

    @Bean
    public GestionarAvistamientos gestionarAvistamientos(RepositorioAvistamientos repositorio,
                                                         ValidadorDeAvistamiento validador) {
        return new ServicioAvistamientos(repositorio, validador);
    }
}
