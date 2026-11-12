package co.edu.unbosque.avistamientos_api.aplicacion.servicio;

import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.AvistamientoNoEncontradoException;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida.RepositorioAvistamientos;
import co.edu.unbosque.avistamientos_api.dominio.Avistamiento;
import co.edu.unbosque.avistamientos_api.validation.ValidadorDeAvistamiento;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Implementación de los casos de uso. Solo conoce el dominio y los puertos: nada de Spring ni de JPA.
public class ServicioAvistamientos implements GestionarAvistamientos {

    private final RepositorioAvistamientos repositorio;
    private final ValidadorDeAvistamiento validador;

    public ServicioAvistamientos(RepositorioAvistamientos repositorio, ValidadorDeAvistamiento validador) {
        this.repositorio = repositorio;
        this.validador = validador;
    }

    @Override
    public List<Avistamiento> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public Avistamiento obtener(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new AvistamientoNoEncontradoException(id));
    }

    @Override
    public Avistamiento registrar(Avistamiento datos) {
        validar(datos);
        datos.normalizar();
        datos.setId(null);
        return repositorio.guardar(datos);
    }

    @Override
    public Avistamiento actualizar(Long id, Avistamiento datos) {
        Avistamiento existente = obtener(id);
        validar(datos);
        datos.normalizar();
        existente.setEspecie(datos.getEspecie());
        existente.setLugar(datos.getLugar());
        existente.setFecha(datos.getFecha());
        existente.setObservador(datos.getObservador());
        return repositorio.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!repositorio.existe(id)) {
            throw new AvistamientoNoEncontradoException(id);
        }
        repositorio.eliminar(id);
    }

    @Override
    public Map<String, Long> resumenPorEspecie() {
        return repositorio.listarTodos().stream()
                .collect(Collectors.groupingBy(Avistamiento::getEspecie, TreeMap::new, Collectors.counting()));
    }

    private void validar(Avistamiento datos) {
        String error = validador.validar(datos);
        if (error != null) {
            throw new AvistamientoInvalidoException(error);
        }
    }
}
