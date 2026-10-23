package co.edu.unbosque.avistamientos_api;

import co.edu.unbosque.avistamientos_api.model.Avistamiento;
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
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pruebas de integración
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AvistamientoApiTest {

    // Datos de prueba
    private static final String GUACAMAYA =
            "{\"especie\":\"guacamaya\",\"lugar\":\"Leticia\",\"fecha\":\"2026-09-20\",\"observador\":\"Paula\"}";
    private static final String TUCAN =
            "{\"especie\":\"tucan\",\"lugar\":\"Mocoa\",\"fecha\":\"2026-09-21\",\"observador\":\"Andres\"}";

    // Puerto donde Spring levantó la API para la prueba
    @Value("${local.server.port}")
    private int puerto;

    @Autowired
    private AvistamientoRepository repositorio;

    private final HttpClient cliente = HttpClient.newHttpClient();

    // Cada prueba empieza con la base vacía: así ninguna depende de otra ni del orden
    @BeforeEach
    void limpiarBaseDeDatos() {
        repositorio.deleteAll();
    }

    // ===================== GET /avistamientos =====================

    @Test
    void listarSinDatosResponde200ConListaVacia() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("GET", "/avistamientos", null);

        // Assert
        assertEquals(200, respuesta.statusCode());
        assertEquals("[]", respuesta.body());
    }

    @Test
    void listarConDatosResponde200ConTodosLosAvistamientos() throws Exception {
        // Arrange
        crear(GUACAMAYA);
        crear(TUCAN);

        // Act
        HttpResponse<String> respuesta = enviar("GET", "/avistamientos", null);

        // Assert
        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains("\"especie\":\"guacamaya\""));
        assertTrue(respuesta.body().contains("\"especie\":\"tucan\""));
        assertEquals(2, repositorio.count());
    }

    // ===================== GET /avistamientos/{id} =====================

    @Test
    void verUnoQueExisteResponde200ConTodosSusCampos() throws Exception {
        // Arrange
        long id = crear(GUACAMAYA);

        // Act
        HttpResponse<String> respuesta = enviar("GET", "/avistamientos/" + id, null);

        // Assert
        assertEquals(200, respuesta.statusCode());
        assertCampos(respuesta.body(), id, "guacamaya", "Leticia", "2026-09-20", "Paula");
    }

    @Test
    void verUnoQueNoExisteResponde404ConMensaje() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("GET", "/avistamientos/3010", null);

        // Assert
        assertEquals(404, respuesta.statusCode());
        assertEquals("{\"error\":\"No existe un avistamiento con id 3010\"}", respuesta.body());
    }

    // ===================== POST /avistamientos =====================

    @Test
    void registrarAvistamientoValidoResponde201YQuedaGuardado() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", GUACAMAYA);

        // Assert: la respuesta
        assertEquals(201, respuesta.statusCode());
        long id = extraerId(respuesta.body());
        assertCampos(respuesta.body(), id, "guacamaya", "Leticia", "2026-09-20", "Paula");

        // Assert: el efecto (un GET al nuevo id lo devuelve)
        HttpResponse<String> consulta = enviar("GET", "/avistamientos/" + id, null);
        assertEquals(200, consulta.statusCode());
        assertCampos(consulta.body(), id, "guacamaya", "Leticia", "2026-09-20", "Paula");
    }

    @Test
    void registrarNormalizaLaEspecieAntesDeGuardar() throws Exception {
        // Arrange
        String especieSinNormalizar = GUACAMAYA.replace("\"guacamaya\"", "\"  GUACAMAYA  \"");

        // Act
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", especieSinNormalizar);

        // Assert
        assertEquals(201, respuesta.statusCode());
        long id = extraerId(respuesta.body());
        assertEquals("guacamaya", repositorio.findById(id).orElseThrow().getEspecie());
    }

    @Test
    void registrarSinDatosObligatoriosResponde400YNoGuardaNada() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", "{\"especie\":\"guacamaya\"}");

        // Assert
        assertEquals(400, respuesta.statusCode());
        assertEquals("{\"error\":\"Faltan datos obligatorios: lugar, fecha, observador\"}", respuesta.body());
        assertEquals(0, repositorio.count());
    }

    @Test
    void registrarConEspecieDeDosCaracteresResponde400() throws Exception {
        // Arrange
        String especieCorta = GUACAMAYA.replace("\"guacamaya\"", "\"ab\"");

        // Act
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", especieCorta);

        // Assert
        assertEquals(400, respuesta.statusCode());
        assertEquals("{\"error\":\"La especie debe tener entre 3 y 50 caracteres\"}", respuesta.body());
        assertEquals(0, repositorio.count());
    }

    @Test
    void registrarConFechaFuturaResponde400() throws Exception {
        // Arrange: aquí se usa el reloj REAL de la API, así que "mañana" se calcula al ejecutar
        String manana = LocalDate.now().plusDays(1).toString();
        String fechaFutura = GUACAMAYA.replace("2026-09-20", manana);

        // Act
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", fechaFutura);

        // Assert
        assertEquals(400, respuesta.statusCode());
        assertEquals("{\"error\":\"La fecha no puede ser posterior a hoy\"}", respuesta.body());
        assertEquals(0, repositorio.count());
    }

    // ===================== PUT /avistamientos/{id} =====================

    @Test
    void actualizarAvistamientoQueExisteResponde200YGuardaLosCambios() throws Exception {
        // Arrange
        long id = crear(GUACAMAYA);
        String cambio = GUACAMAYA.replace("Leticia", "Villa de Leyva");

        // Act
        HttpResponse<String> respuesta = enviar("PUT", "/avistamientos/" + id, cambio);

        // Assert: la respuesta
        assertEquals(200, respuesta.statusCode());
        assertCampos(respuesta.body(), id, "guacamaya", "Villa de Leyva", "2026-09-20", "Paula");

        // Assert: el efecto (un GET muestra el cambio)
        HttpResponse<String> consulta = enviar("GET", "/avistamientos/" + id, null);
        assertCampos(consulta.body(), id, "guacamaya", "Villa de Leyva", "2026-09-20", "Paula");
    }

    @Test
    void actualizarAvistamientoQueNoExisteResponde404() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("PUT", "/avistamientos/3010", GUACAMAYA);

        // Assert
        assertEquals(404, respuesta.statusCode());
        assertEquals("{\"error\":\"No existe un avistamiento con id 3010\"}", respuesta.body());
    }

    @Test
    void actualizarConDatosInvalidosResponde400YNoCambiaNada() throws Exception {
        // Arrange
        long id = crear(GUACAMAYA);

        // Act
        HttpResponse<String> respuesta = enviar("PUT", "/avistamientos/" + id, "{\"especie\":\"guacamaya\"}");

        // Assert
        assertEquals(400, respuesta.statusCode());
        assertEquals("{\"error\":\"Faltan datos obligatorios: lugar, fecha, observador\"}", respuesta.body());
        assertEquals("Leticia", repositorio.findById(id).orElseThrow().getLugar());
    }

    // ===================== DELETE /avistamientos/{id} =====================

    @Test
    void eliminarAvistamientoQueExisteResponde200YYaNoSePuedeConsultar() throws Exception {
        // Arrange
        long id = crear(GUACAMAYA);

        // Act
        HttpResponse<String> respuesta = enviar("DELETE", "/avistamientos/" + id, null);

        // Assert: la respuesta
        assertEquals(200, respuesta.statusCode());
        assertEquals("{\"mensaje\":\"Avistamiento con id " + id + " eliminado correctamente\"}", respuesta.body());

        // Assert: el efecto (un GET responde 404)
        assertEquals(404, enviar("GET", "/avistamientos/" + id, null).statusCode());
    }

    @Test
    void eliminarAvistamientoQueNoExisteResponde404() throws Exception {
        // Act
        HttpResponse<String> respuesta = enviar("DELETE", "/avistamientos/3010", null);

        // Assert
        assertEquals(404, respuesta.statusCode());
        assertEquals("{\"error\":\"No existe un avistamiento con id 3010\"}", respuesta.body());
    }

    // ===================== BONUS: GET /avistamientos/resumen =====================

    @Test
    void resumenCuentaAvistamientosPorEspecieNormalizada() throws Exception {
        // Arrange
        crear(GUACAMAYA);
        crear(GUACAMAYA.replace("\"guacamaya\"", "\"Guacamaya\""));
        crear(TUCAN);

        // Act
        HttpResponse<String> respuesta = enviar("GET", "/avistamientos/resumen", null);

        // Assert
        assertEquals(200, respuesta.statusCode());
        assertEquals("{\"guacamaya\":2,\"tucan\":1}", respuesta.body());
    }

    // ===================== AUXILIARES =====================

    // Hace una petición HTTP a la API y devuelve la respuesta
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

    // Crea un avistamiento con POST y devuelve el id que le asignó la API
    private long crear(String json) throws Exception {
        HttpResponse<String> respuesta = enviar("POST", "/avistamientos", json);
        assertEquals(201, respuesta.statusCode());
        return extraerId(respuesta.body());
    }

    // Saca el id del JSON de respuesta
    private long extraerId(String cuerpo) {
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(cuerpo);
        assertTrue(m.find(), "La respuesta no trae id: " + cuerpo);
        return Long.parseLong(m.group(1));
    }

    // Verifica que el JSON tenga cada campo con el valor esperado
    private void assertCampos(String cuerpo, long id, String especie, String lugar, String fecha, String observador) {
        assertEquals(id, extraerId(cuerpo));
        assertTrue(cuerpo.contains("\"especie\":\"" + especie + "\""), "especie incorrecta: " + cuerpo);
        assertTrue(cuerpo.contains("\"lugar\":\"" + lugar + "\""), "lugar incorrecto: " + cuerpo);
        assertTrue(cuerpo.contains("\"fecha\":\"" + fecha + "\""), "fecha incorrecta: " + cuerpo);
        assertTrue(cuerpo.contains("\"observador\":\"" + observador + "\""), "observador incorrecto: " + cuerpo);
    }
}