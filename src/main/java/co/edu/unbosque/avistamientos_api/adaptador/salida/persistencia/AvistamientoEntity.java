package co.edu.unbosque.avistamientos_api.adaptador.salida.persistencia;

import jakarta.persistence.*;

// Adaptador de salida: así se guarda un avistamiento en la tabla de la base de datos (JPA)
@Entity
@Table(name = "avistamientos")
public class AvistamientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "especie", nullable = false, length = 50)
    private String especie;

    @Column(name = "lugar", nullable = false, length = 100)
    private String lugar;

    @Column(name = "fecha", nullable = false, length = 10)
    private String fecha;

    @Column(name = "observador", nullable = false, length = 30)
    private String observador;

    public AvistamientoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObservador() {
        return observador;
    }

    public void setObservador(String observador) {
        this.observador = observador;
    }
}