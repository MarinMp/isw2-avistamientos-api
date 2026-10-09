package co.edu.unbosque.avistamientos_api.repository;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
        
}