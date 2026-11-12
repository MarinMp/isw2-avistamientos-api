package co.edu.unbosque.avistamientos_api.adaptador.salida.persistencia;

import co.edu.unbosque.avistamientos_api.aplicacion.puerto.salida.RepositorioAvistamientos;
import co.edu.unbosque.avistamientos_api.dominio.Avistamiento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Adaptador de salida: implementa el puerto RepositorioAvistamientos con JPA y H2.
// Traduce entre el Avistamiento del dominio y la AvistamientoEntity de la tabla.
@Component
public class RepositorioAvistamientosJpa implements RepositorioAvistamientos {

    private final AvistamientoJpaRepository jpa;

    public RepositorioAvistamientosJpa(AvistamientoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Avistamiento> listarTodos() {
        return jpa.findAll().stream().map(this::aDominio).toList();
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        return aDominio(jpa.save(aEntidad(avistamiento)));
    }

    @Override
    public boolean existe(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private Avistamiento aDominio(AvistamientoEntity e) {
        Avistamiento a = new Avistamiento();
        a.setId(e.getId());
        a.setEspecie(e.getEspecie());
        a.setLugar(e.getLugar());
        a.setFecha(e.getFecha());
        a.setObservador(e.getObservador());
        return a;
    }

    private AvistamientoEntity aEntidad(Avistamiento a) {
        AvistamientoEntity e = new AvistamientoEntity();
        e.setId(a.getId());
        e.setEspecie(a.getEspecie());
        e.setLugar(a.getLugar());
        e.setFecha(a.getFecha());
        e.setObservador(a.getObservador());
        return e;
    }
}
