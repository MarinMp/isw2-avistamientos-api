# 🦅 Taller API REST - Registro de avistamientos de aves

Taller sobre API REST para registrar los avistamientos de aves que las personas observan en el campo. Recibe y responde JSON y guarda los datos en una base de datos H2 embebida.

Cada avistamiento tiene:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | número | Lo asigna el sistema |
| `especie` | texto | Nombre de la especie, por ejemplo `colibrí` |
| `lugar` | texto | Dónde se observó |
| `fecha` | texto | Fecha en formato `AAAA-MM-DD` |
| `observador` | texto | Quién lo registró |

---

## Tecnologías utilizadas

La estructura inicial del proyecto (`pom.xml`, Maven Wrapper y clase principal) se generó con **Spring Initializr** (https://start.spring.io).

- **Java:** El proyecto compila para Java 21.
- **Spring Boot:** Spring Web y Spring Data JPA.
- **H2 Database:** Embebida. La carpeta ./data se crea al arrancar la API.
- **Maven Wrapper:** `mvnw` y `mvnw.cmd`. No es necesario instalar Maven.
- **JUnit 5 y Mockito:** Pruebas unitarias y dobles de prueba.
- **JaCoCo:** Medición de la cobertura de las pruebas.

## Requisitos para ejecutar el proyecto

- El **JDK** instalado debe ser 21 o superior. Para verificarlo en consola el comando es:
  ```bash
  java -version
  ```
- Se requiere **Git**, para poder clonar el repositorio.

---

## Instalación y ejecución

**Nota:** En Windows utilizar Git Bash para todos los pasos, esta terminal ya viene incluida al instalar Git. Así se usa la misma terminal para encender la API y para ejecutar los ejemplos con curl, ya que no funcionan correctamente en CMD ni en PowerShell. Para abrir la terminal: 
- Dar clic derecho dentro de la carpeta donde se quiera descargar el proyecto.
- Dar clic en **Open Git Bash here**.

1. Como primer paso se debe clonar el repositorio y entrar a la carpeta del proyecto. En una terminal (preferiblemente Git Bash) se ejecutan los siguientes comandos:
   ```bash
   git clone https://github.com/MarinMp/isw2-avistamientos-api.git
   cd isw2-avistamientos-api
   ```
   El primer comando descarga el proyecto y el segundo entra a su carpeta. Los siguientes comandos deben ejecutarse dentro de esa carpeta.

2. Después se debe encender la API. El comando utilizado depende de la terminal que se esté utilizando:

   | Sistema | Comando |
   |---|---|
   | Linux / macOS / Git Bash | `./mvnw spring-boot:run` |
   | Windows (CMD o PowerShell) | `.\mvnw.cmd spring-boot:run` |

   Este comando es para utilizar el Maven Wrapper incluido en el proyecto para descargar las dependencias, compilar el código y arrancar la API.

   La API está lista cuando en la terminal aparece una línea con el texto `Started AvistamientosApiApplication`. A partir de ese momento la API queda disponible en **http://localhost:8080** y se le pueden hacer peticiones (con curl, Postman o el navegador).

   Una manera sencilla de comprobar que la API que funciona, es abriendo el navegador **http://localhost:8080/avistamientos**: debe mostrar `[]` (lista vacía) o la lista de avistamientos registrados.

   Mientras la API está encendida, esa terminal queda ocupada. Para probar los ejemplos con curl se debe abrir otra ventana de Git Bash.

4. Para apagar la API, se hace desde la terminal donde se está ejecutando. Presionar `Ctrl + C`.

- **Alternativa con un IDE:** También se puede abrir la carpeta del proyecto en IntelliJ IDEA (o en otro IDE con soporte para Maven) y ejecutar la clase `AvistamientosApiApplication`.
  
- Los datos se guardan en la carpeta `data/` del proyecto, así que se conservan aunque se apague la API. Para empezar con la base vacía, debe borrar esa carpeta con la API apagada.

---

## Endpoints

| Acción | Petición | Respuesta esperada |
|---|---|---|
| Listar todos los avistamientos | `GET /avistamientos` | `200` con la lista JSON |
| Ver un avistamiento | `GET /avistamientos/{id}` | `200` con el avistamiento - `404` si no existe |
| Registrar un avistamiento | `POST /avistamientos` | `201` con el avistamiento creado - `400` si faltan datos o son inválidos |
| Actualizar un avistamiento | `PUT /avistamientos/{id}` | `200` con el avistamiento actualizado - `404` si no existe - `400` si faltan datos o son inválidos |
| Eliminar un avistamiento | `DELETE /avistamientos/{id}` | `200` con mensaje de confirmación - `404` si no existe |
| Resumen por especie (bonus) | `GET /avistamientos/resumen` | `200` con la cantidad de avistamientos por especie |

### Reglas de validación (respuesta `400`)

- `especie`, `lugar`, `fecha` y `observador` son obligatorios y no pueden estar vacíos los campos.
- `fecha` debe ser una fecha real con formato `AAAA-MM-DD`.
- `especie` y `lugar`: debe tener máximo 100 caracteres.
- `observador`: debe tener máximo 30 caracteres.

### Normalización

Antes de guardar, la API quita los espacios sobrantes de todos los campos y pasa la especie a minúsculas. Así `"Guacamaya"`, `" guacamaya "` y `"GUACAMAYA"` se guardan igual y el resumen los cuenta juntos.

### Formato de los errores

Los errores responden JSON con la forma:
```json
{"error": "mensaje que explica el problema"}
```

---

## Ejemplos con curl

Antes de probar los ejemplos se debe tener en cuenta:
 
- La API debe estar encendida (paso 2 de Instalación y ejecución). Como esa terminal queda ocupada mientras la API corre, los ejemplos se ejecutan en otra terminal.
- En Windows se deben ejecutar en **Git Bash**, que es la terminal que viene con Git. En PowerShell, `curl` es otro programa y las comillas del JSON se interpretan distinto.
- Cada ejemplo está escrito para poder copiarlo y pegarlo directamente en la terminal.

### Registrar un avistamiento - `201`
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya","lugar":"Humedal La Conejera, Bogota","fecha":"2026-09-20","observador":"Paula"}'
```
Respuesta:
```json
{"id":1,"especie":"guacamaya","lugar":"Humedal La Conejera, Bogota","fecha":"2026-09-20","observador":"Paula"}
```

### Registrar un avistamiento con datos faltantes - `400`
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya"}'
```
Respuesta:
```json
{"error":"Faltan datos obligatorios: lugar, fecha, observador"}
```

### Listar todos los avistamientos - `200`
```bash
curl -i http://localhost:8080/avistamientos
```

### Ver un avistamiento - `200`
```bash
curl -i http://localhost:8080/avistamientos/1
```
Respuesta:
```json
{"id":1,"especie":"guacamaya","lugar":"Humedal La Conejera, Bogota","fecha":"2026-09-20","observador":"Paula"}
```

### Ver un avistamiento que no existe - `404`
```bash
curl -i http://localhost:8080/avistamientos/3010
```
Respuesta:
```json
{"error":"No existe un avistamiento con id 3010"}
```

### Actualizar un avistamiento - `200`
```bash
curl -i -X PUT http://localhost:8080/avistamientos/1 \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya","lugar":"Jardin Botanico, Bogota","fecha":"2026-09-21","observador":"Paula"}'
```
Respuesta:
```json
{"id":1,"especie":"guacamaya","lugar":"Jardin Botanico, Bogota","fecha":"2026-09-21","observador":"Paula"}
```

### Actualizar un avistamiento que no existe - `404`
```bash
curl -i -X PUT http://localhost:8080/avistamientos/3010 \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya","lugar":"Jardin Botanico, Bogota","fecha":"2026-09-21","observador":"Paula"}'
```
Respuesta:
```json
{"error":"No existe un avistamiento con id 3010"}
```

### Actualizar un avistamiento con datos faltantes - `400`
```bash
curl -i -X PUT http://localhost:8080/avistamientos/1 \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya"}'
```
Respuesta:
```json
{"error":"Faltan datos obligatorios: lugar, fecha, observador"}
```

### Eliminar un avistamiento - `200`
```bash
curl -i -X DELETE http://localhost:8080/avistamientos/1
```
Respuesta:
```json
{"mensaje":"Avistamiento con id 1 eliminado correctamente"}
```

### Eliminar un avistamiento que no existe - `404`
```bash
curl -i -X DELETE http://localhost:8080/avistamientos/3010
```
Respuesta:
```json
{"error":"No existe un avistamiento con id 3010"}
```

### Resumen por especie (bonus) - `200`

Los siguientes comandos se ejecutan uno por uno con el fin de poder visualizar el resumen por especie. A continuación, se registrarán varias especies:

```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"guacamaya","lugar":"Jardin Botanico","fecha":"2026-09-22","observador":"Juana"}'
```
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"Guacamaya","lugar":"Leticia, Amazonas","fecha":"2026-09-23","observador":"Valentina"}'
```
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"  GUACAMAYA  ","lugar":"Acacias, Meta","fecha":"2026-09-24","observador":"Paula"}'
```
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"tucan","lugar":"Reserva Rio Claro, Antioquia","fecha":"2026-09-25","observador":"Camilo"}'
```
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"Tucan","lugar":"Mocoa, Putumayo","fecha":"2026-09-26","observador":"Sofia"}'
```
```bash
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"toche","lugar":"Villa de Leyva, Boyaca","fecha":"2026-09-27","observador":"Paula"}'
```

Cada uno responde `201`. Después se consulta el resumen con el siguiente comando:

```bash
curl -i http://localhost:8080/avistamientos/resumen
```
Respuesta:
```json
{"guacamaya":3,"toche":1,"tucan":2}
```

---

## Pruebas automáticas (bonus)

El proyecto incluye pruebas automáticas que revisan que la API funcione bien, sin necesidad de usar Postman ni curl. Hay pruebas en dos niveles:

| Nivel | Clase | Pruebas | Qué revisa |
|---|---|---|---|
| Unitario | `validation/ValidadorDeAvistamientoTest` | 15 | La regla de negocio del validador, con valores límite (especie de 2, 3, 50 y 51 caracteres; y fecha de hoy y de mañana). Usa un reloj con fecha fija (`RelojFijo`, un stub). |
| Unitario | `controller/AvistamientoControllerTest` | 3 | El controlador sin levantar la API, con un mock del repositorio (Mockito). |
| Integración | `AvistamientoApiTest` | 15 | La API completa por HTTP: los 5 endpoints y el resumen, con casos de éxito y de error (200, 201, 400 y 404), el cuerpo de la respuesta y el efecto en la base de datos. |

Las pruebas de integración encienden la API de forma temporal y usan una base de datos aparte, en memoria, así que **no afectan los avistamientos guardados en `data/`**.
 
Antes de ejecutar las pruebas se debe **apagar la API** si está encendida. Los comandos se ejecutan dentro de la carpeta del proyecto.

`src/test/java/co/edu/unbosque/avistamientos_api/AvistamientoApiTest.java`

### Ejecutar las pruebas
 
| Sistema | Comando |
|---|---|
| Linux / macOS / Git Bash (recomendado) | `./mvnw test` |
| Windows (CMD o PowerShell) | `.\mvnw.cmd test` |

Si todas las pruebas pasan, al final aparece `Tests run: 34, Failures: 0, Errors: 0` y el mensaje `BUILD SUCCESS`. Si alguna falla, aparece `BUILD FAILURE` junto con el nombre de la prueba que falló.

### Generar el reporte de cobertura
 
La cobertura se mide con **JaCoCo**. Este comando ejecuta todas las pruebas y luego genera el reporte:
 
| Sistema | Comando |
|---|---|
| Linux / macOS / Git Bash (recomendado) | `./mvnw verify` |
| Windows (CMD o PowerShell) | `.\mvnw.cmd verify` |

Al terminar, el reporte queda en **`target/site/jacoco/index.html`**. Para verlo, se abre ese archivo con el navegador (doble clic desde el explorador de archivos). En el reporte, verde significa código ejecutado por las pruebas, rojo código que nunca se ejecutó y amarillo una condición en la que solo se probó uno de sus caminos.
 
### Documento de justificación
 
Las decisiones sobre qué probar, en qué nivel, con qué dobles y la lectura de la cobertura están explicadas en **`docs/justificacion-pruebas.pdf`**.

---

## Estructura del proyecto

```
docs/
└── justificacion-pruebas.pdf                      # Documento de justificación de las pruebas
src/
├── main/
│   ├── java/co/edu/unbosque/avistamientos_api/
│   │   ├── controller/AvistamientoController.java # Endpoints REST
│   │   ├── model/Avistamiento.java                # Entidad JPA (tabla "avistamientos")
│   │   ├── repository/AvistamientoRepository.java # Acceso a la base de datos
│   │   ├── validation/
│   │   │   ├── ValidadorDeAvistamiento.java       # Regla de negocio (validación)
│   │   │   ├── Reloj.java                         # Interfaz para obtener la fecha de hoy
│   │   │   └── RelojDelSistema.java               # Reloj real (LocalDate.now())
│   │   └── AvistamientosApiApplication.java       # Clase principal que arranca la API
│   └── resources/application.properties           # Configuración de H2 y del servidor
└── test/
    ├── java/co/edu/unbosque/avistamientos_api/
    │   ├── validation/
    │   │   ├── ValidadorDeAvistamientoTest.java   # Pruebas unitarias del validador
    │   │   └── RelojFijo.java                     # Doble de prueba (stub) con fecha fija
    │   ├── controller/
    │   │   └── AvistamientoControllerTest.java    # Pruebas unitarias con mock del repositorio
    │   └── AvistamientoApiTest.java               # Pruebas de integración
    └── resources/application.properties           # H2 en memoria solo para las pruebas
```

---

## Uso de IA

Para este trabajo utilicé **Claude (Anthropic)**, modelo **Claude Opus 5.5**.

Donde más me apoyé fue en las pruebas de integración (`AvistamientoApiTest.java`). No tenía mucho conocimiento sobre cómo hacerlas, así que la IA generó la clase de pruebas y me explicó cómo funciona cada parte: cómo se enciende la API temporalmente, cómo se hacen las peticiones y cómo se verifican los códigos de estado. Además:

- En el resto del proyecto la IA me sirvió como guía para ir construyéndolo paso a paso.
- Me orientó en la configuración del entorno (JDK, IntelliJ, Maven, Git y GitHub).
- Me ayudó a revisar el código que fui escribiendo y me sugirió mejoras.
- Por último, me apoyó en la redacción de este README para poder entregar una buena guía.

En el **taller de pruebas unitarias y de integración** también usé Claude (Claude Opus 5.5) como guía para:
 
- Extraer la validación a la clase `ValidadorDeAvistamiento` y crear la interfaz `Reloj`.
- Generar la clase de pruebas del controlador con Mockito.
- Reescribir las pruebas de integración.
- Configurar JaCoCo e interpretar el reporte de cobertura.
- Por último, me apoyó en la redacción de lo que me faltaba para una buena guía en el README..
---

## Información Académica 

- **Universidad: Universidad El Bosque**
- **Asignatura: Ingeniería de Software 2**
- **Autor: María Paula Marín Soler**
- **Docente: Jonathan Briceño**
