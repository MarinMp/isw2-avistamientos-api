package co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida;

import co.edu.unbosque.avistamientos_api.dominio.Avistamiento;

import java.util.List;
import java.util.Optional;

// Puerto de salida: lo que la aplicación necesita para guardar y consultar avistamientos.
// La aplicación define esta interfaz; el adaptador de persistencia la implementa.
public interface RepositorioAvistamientos {

    List<Avistamiento> listarTodos();

    Optional<Avistamiento> buscarPorId(Long id);

    Avistamiento guardar(Avistamiento avistamiento);

    boolean existe(Long id);

    void eliminar(Long id);
}
