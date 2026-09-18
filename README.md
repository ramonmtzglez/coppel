# TV Maze Middleware API

API intermediaria que consume el servicio público de [TV Maze](https://www.tvmaze.com/api) y expone endpoints para buscar shows, consultar su detalle (con caché en MongoDB) y guardar calificaciones/comentarios.

## Stack

- Java 17
- Spring Boot 3.5
- Spring Web (MVC) + WebClient (WebFlux)
- Spring Data MongoDB
- Lombok, Jakarta Validation
- MongoDB Atlas

## Requisitos previos

- JDK 17
- Maven (incluye wrapper `mvnw` / `mvnw.cmd`)
- Una instancia de MongoDB Atlas (gratuita, sin restricción de IP)

## Configuración

La conexión a MongoDB se define con la variable de entorno `MONGODB_URI`. Si no se define, la app intenta conectarse a `mongodb://localhost:27017/tvmaze_middleware`.

Formato de la URI de Atlas:

```
mongodb+srv://<USUARIO>:<PASSWORD>@<CLUSTER>/<DATABASE>?retryWrites=true&w=majority&appName=<APP_NAME>
```

Ejemplo (sustituye con tus credenciales):

```
mongodb+srv://mi_usuario:mi_password@cluster0.xxxxx.mongodb.net/tvmaze_middleware?retryWrites=true&w=majority&appName=Cluster0
```

> No subas credenciales al repositorio; usa variables de entorno.

## Cómo ejecutar

```powershell
# Windows (PowerShell)
$env:MONGODB_URI = "mongodb+srv://<USUARIO>:<PASSWORD>@<CLUSTER>/<DATABASE>?retryWrites=true&w=majority"
.\mvnw.cmd spring-boot:run
```

```bash
# Linux / macOS / Git Bash
export MONGODB_URI="mongodb+srv://<USUARIO>:<PASSWORD>@<CLUSTER>/<DATABASE>?retryWrites=true&w=majority"
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

Para dejar la variable fija en Windows:

```powershell
setx MONGODB_URI "mongodb+srv://<USUARIO>:<PASSWORD>@<CLUSTER>/<DATABASE>?retryWrites=true&w=majority"
```

## Tests

```bash
.\mvnw.cmd test
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/shows/search?q={query}` | Busca shows y devuelve arreglo con sus comentarios |
| GET | `/api/v1/shows/{id}` | Detalle del show (con caché en MongoDB) y sus comentarios |
| POST | `/api/v1/shows/{id}/comments` | Guarda un comentario y calificación (0-5) del show |

### 1. Buscar shows

```bash
curl --location "http://localhost:8080/api/v1/shows/search?q=girls"
```

Respuesta (resumen):

```json
[
  {
    "id": 139,
    "name": "Girls",
    "channel": "HBO",
    "summary": "<p>...</p>",
    "genres": ["Drama", "Romance"],
    "comments": [
      { "comment": "Excelente serie", "rating": 5 }
    ]
  }
]
```

### 2. Detalle del show

```bash
curl --location "http://localhost:8080/api/v1/shows/139"
```

Devuelve el objeto completo (`status`, `runtime`, `premiered`, `ended`, `rating`, `image`, `language`, `officialSite`, `url`) más el arreglo `comments`.

### 3. Guardar comentario

```bash
curl --location --request POST "http://localhost:8080/api/v1/shows/139/comments" \
  --header "Content-Type: application/json" \
  --data "{\"comment\":\"Excelente serie\",\"rating\":5}"
```

Respuesta `201 Created`:

```json
{
  "id": "6aac89d57d63aa309f4333aa",
  "showId": 139,
  "comment": "Excelente serie",
  "rating": 5,
  "createdAt": "2026-09-18T00:46:13.793Z"
}
```

Validaciones del payload:

- `comment`: obligatorio, no vacío (`@NotBlank`).
- `rating`: obligatorio, entre `0` y `5` (`@Min(0)` / `@Max(5)`).
- `showId` (path): debe ser positivo (`@Positive`).

## Caché

El endpoint `GET /api/v1/shows/{id}` implementa caché *cache-aside* en MongoDB:

1. Busca el show en la colección `shows` por `_id`.
2. Si existe, lo retorna desde caché.
3. Si no existe, consume TV Maze, guarda el resultado y lo retorna.

Los documentos cacheados expiran automáticamente a las 24 horas mediante un índice TTL sobre el campo `cachedAt`.

## Estructura del proyecto

```
src/main/java/com/tvmaze/middleware
├── client/        # Cliente HTTP (WebClient) hacia TV Maze
├── config/        # Configuración de beans (WebClient)
├── controller/    # Endpoints REST
├── document/      # Documentos MongoDB (ShowDocument, CommentDocument)
├── dto/           # DTOs de entrada/salida y de TV Maze
├── exception/     # Manejo global de errores (@RestControllerAdvice)
├── mapper/        # Mapeo entre DTOs y documentos
├── repository/    # Repositorios Spring Data MongoDB
└── service/       # Lógica de negocio
```
