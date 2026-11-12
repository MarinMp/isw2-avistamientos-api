package co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada;

// Se lanza cuando los datos de un avistamiento no cumplen las reglas de validación
public class AvistamientoInvalidoException extends RuntimeException {

    public AvistamientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
