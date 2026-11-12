package co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada;

import co.edu.unbosque.avistamientos_api.dominio.Avistamiento;

import java.util.List;
import java.util.Map;

// Puerto de entrada: los casos de uso que la aplicación ofrece hacia afuera.
// El adaptador web (el controlador) los llama sin saber cómo están implementados.
public interface GestionarAvistamientos {

    List<Avistamiento> listar();

    // Lanza AvistamientoNoEncontradoException si no existe
    Avistamiento obtener(Long id);

    // Lanza AvistamientoInvalidoException si los datos no son válidos
    Avistamiento registrar(Avistamiento datos);

    // Lanza AvistamientoNoEncontradoException o AvistamientoInvalidoException
    Avistamiento actualizar(Long id, Avistamiento datos);

    // Lanza AvistamientoNoEncontradoException si no existe
    void eliminar(Long id);

    // Cantidad de avistamientos por especie, ordenado por especie
    Map<String, Long> resumenPorEspecie();
}
