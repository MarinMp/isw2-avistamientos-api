package co.edu.unbosque.avistamientos_api.adaptador.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio de Spring Data sobre la tabla. Solo lo usa el adaptador de persistencia.
public interface AvistamientoJpaRepository extends JpaRepository<AvistamientoEntity, Long> {
}
