package co.edu.unbosque.avistamientos_api;

import co.edu.unbosque.avistamientos_api.repository.AvistamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas de integración
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AvistamientoApiTest {

    // Datos de prueba
    private static final String COLIBRI =
            "{\"especie\":\"colibri\",\"lugar\":\"Bogota\",\"fecha\":\"2026-09-29\",\"observador\":\"Paula\"}";
    private static final String TUCAN =
            "{\"especie\":\"tucan\",\"lugar\":\"Leticia\",\"fecha\":\"2026-09-30\",\"observador\":\"Andres\"}";

    // Puerto donde Spring levantó la API para la prueba
    @Value("${local.server.port}")
    private int puerto;

    @Autowired
    private AvistamientoRepository repositorio;

    private final HttpClient cliente = HttpClient.newHttpClient();

    // Variar la tabla antes de la prueba
    @BeforeEach
    void limpiarBaseDeDatos() {
        repositorio.deleteAll();
    }

    // Pruebas con GET

    @Test
    void listarSinDatosDevuelve200YListaVacia() throws Exception {
        HttpResponse<String> r = enviar("GET", "/avistamientos", null);
        assertEquals(200, r.statusCode());
        assertEquals("[]", r.body());
    }

    @Test
    void verUnoDevuelve200SiExisteY404SiNo() throws Exception {
        long id = crear(COLIBRI);
        assertEquals(200, enviar("GET", "/avistamientos/" + id, null).statusCode());
        assertEquals(404, enviar("GET", "/avistamientos/3010", null).statusCode());
    }

    // Pruebas con POST

    @Test
    void registrarDevuelve201ConElAvistamientoCreado() throws Exception {
        HttpResponse<String> r = enviar("POST", "/avistamientos", COLIBRI);
        assertEquals(201, r.statusCode());
        assertTrue(r.body().contains("\"especie\":\"colibri\""));
        assertTrue(r.body().contains("\"id\":"));
    }

    @Test
    void registrarSinDatosDevuelve400() throws Exception {
        HttpResponse<String> r = enviar("POST", "/avistamientos", "{\"especie\":\"colibri\"}");
        assertEquals(400, r.statusCode());
        assertTrue(r.body().contains("Faltan datos obligatorios"));
    }

    @Test
    void registrarConFechaInvalidaDevuelve400() throws Exception {
        assertEquals(400, enviar("POST", "/avistamientos", COLIBRI.replace("2026-09-29", "prueba")).statusCode());
        assertEquals(400, enviar("POST", "/avistamientos", COLIBRI.replace("2026-09-29", "2026-02-30")).statusCode());
    }

    @Test
    void registrarConTextoMuyLargoDevuelve400() throws Exception {
        String observadorLargo = "Tiene mas caracteres de los permitidos";
        HttpResponse<String> r = enviar("POST", "/avistamientos", COLIBRI.replace("Paula", observadorLargo));
        assertEquals(400, r.statusCode());
    }

    @Test
    void registrarNormalizaLaEspecie() throws Exception {
        HttpResponse<String> r = enviar("POST", "/avistamientos", COLIBRI.replace("\"colibri\"", "\"  COLIBRI  \""));
        assertEquals(201, r.statusCode());
        assertTrue(r.body().contains("\"especie\":\"colibri\""));
    }

    // Pruebas PUT

    @Test
    void actualizarDevuelve200SiExiste404SiNoY400SiFaltanDatos() throws Exception {
        long id = crear(COLIBRI);
        String cambio = COLIBRI.replace("Bogota", "Villa de Leyva");

        HttpResponse<String> r = enviar("PUT", "/avistamientos/" + id, cambio);
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("Villa de Leyva"));

        assertEquals(404, enviar("PUT", "/avistamientos/3010", cambio).statusCode());
        assertEquals(400, enviar("PUT", "/avistamientos/" + id, "{\"especie\":\"colibri\"}").statusCode());
    }

    // Pruebas DELETE

    @Test
    void eliminarDevuelve200SiExisteY404SiNo() throws Exception {
        long id = crear(COLIBRI);
        HttpResponse<String> r = enviar("DELETE", "/avistamientos/" + id, null);
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("eliminado correctamente"));

        assertEquals(404, enviar("GET", "/avistamientos/" + id, null).statusCode());
        assertEquals(404, enviar("DELETE", "/avistamientos/" + id, null).statusCode());
    }

    // Bonus - Pruebas RESUMEN

    @Test
    void resumenCuentaAvistamientosPorEspecie() throws Exception {
        crear(COLIBRI);
        crear(COLIBRI.replace("\"colibri\"", "\"Colibri\""));
        crear(TUCAN);
        HttpResponse<String> r = enviar("GET", "/avistamientos/resumen", null);
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"colibri\":2"));
        assertTrue(r.body().contains("\"tucan\":1"));
    }

    // Métodos auxiliares de validación

    // Petición HTTP a la API + respuesta
    private HttpResponse<String> enviar(String metodo, String ruta, String json) throws Exception {
        HttpRequest.Builder peticion = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Accept", "application/json");
        if (json == null) {
            peticion.method(metodo, HttpRequest.BodyPublishers.noBody());
        } else {
            peticion.header("Content-Type", "application/json")
                    .method(metodo, HttpRequest.BodyPublishers.ofString(json));
        }
        return cliente.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
    }

    // Crea un avistamiento con POST y devuelve el id asignado
    private long crear(String json) throws Exception {
        HttpResponse<String> r = enviar("POST", "/avistamientos", json);
        assertEquals(201, r.statusCode());
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(r.body());
        assertTrue(m.find(), "No trae id: " + r.body());
        return Long.parseLong(m.group(1));
    }
}