package co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada;

// Se lanza cuando se pide un avistamiento con un id que no existe
public class AvistamientoNoEncontradoException extends RuntimeException {

    public AvistamientoNoEncontradoException(Long id) {
        super("No existe un avistamiento con id " + id);
    }
}
