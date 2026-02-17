# Store API – Tienda (Inventario, Pedidos, WhatsApp)

API REST para una tienda: catálogo de productos y variantes, inventario, pedidos con flujo de estados, reserva de stock y plantillas de mensaje para WhatsApp.

## Requisitos

- **Java 21**
- **Maven 3.9+** (o usar el wrapper `mvnw` incluido)
- **PostgreSQL** (por ejemplo Supabase)

## Si la aplicación no arranca: error de Flyway "checksum mismatch"

Si ves **"Migration checksum mismatch for migration version 5"** (o otra versión), es porque una migración ya aplicada en la base de datos fue modificada después. Debes ejecutar **Flyway repair** una vez para actualizar el historial:

1. Usa la **misma URL, usuario y contraseña** que en `application.properties`.
2. En la raíz del proyecto (donde está `pom.xml`):

**Windows (CMD):**
```cmd
set FLYWAY_URL=jdbc:postgresql://db.cwkzmwxyddsqjiiovflt.supabase.co:5432/store
set FLYWAY_USER=postgres
set FLYWAY_PASSWORD=TuPasswordDeSupabase
mvn flyway:repair
```

**Windows (PowerShell):**
```powershell
$env:FLYWAY_URL="jdbc:postgresql://db.cwkzmwxyddsqjiiovflt.supabase.co:5432/store"
$env:FLYWAY_USER="postgres"
$env:FLYWAY_PASSWORD="TuPasswordDeSupabase"
mvn flyway:repair
```

**Linux / macOS:**
```bash
export FLYWAY_URL=jdbc:postgresql://db.cwkzmwxyddsqjiiovflt.supabase.co:5432/store
export FLYWAY_USER=postgres
export FLYWAY_PASSWORD=TuPasswordDeSupabase
./mvnw flyway:repair
```

Después vuelve a arrancar la aplicación con `./mvnw spring-boot:run` (o `.\mvnw.cmd spring-boot:run`).

**Si no puedes ejecutar repair** (ej. no tienes Maven en PATH): puedes descomentar en `application.properties` la línea `spring.flyway.validate-on-migrate=false` para que la app arranque sin validar checksums. Es temporal; lo correcto es ejecutar `flyway:repair` cuando puedas.

---

## Configuración

1. **Base de datos**: En `src/main/resources/application.properties` están la URL, usuario y contraseña de Supabase. Ajusta si usas otro servidor:
   ```properties
   spring.datasource.url=jdbc:postgresql://HOST:5432/store
   spring.datasource.username=postgres
   spring.datasource.password=TU_PASSWORD
   ```

2. **Crear la base `store`** en PostgreSQL/Supabase si no existe:
   ```sql
   CREATE DATABASE store;
   ```

3. Al arrancar, **Flyway** crea las tablas y el usuario por defecto.

## Cómo ejecutar la aplicación

```bash
# Compilar
./mvnw compile

# Ejecutar (puerto automático si server.port=0)
./mvnw spring-boot:run
```

En Windows:

```cmd
.\mvnw.cmd spring-boot:run
```

El log indicará el puerto (por ejemplo `Tomcat started on port 63386`). La aplicación quedará en **http://localhost:&lt;puerto&gt;**.

## Login y uso de la API

### 1. Obtener token (login)

```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta de ejemplo:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

### 2. Llamar a los endpoints protegidos

Incluye el token en la cabecera:

```http
Authorization: Bearer <token>
```

Ejemplos:

- `GET /products` – Listar productos
- `POST /products` – Crear producto
- `GET /orders` – Listar pedidos
- `POST /orders` – Crear pedido
- `POST /orders/{id}/transition` – Cambiar estado (ej. confirmar, enviar)
- `GET /inventory/balance` – Ver inventario
- `GET /whatsapp/order/{id}/message` – Mensaje WhatsApp para un pedido
- `GET /reports/weekly-sales?weekStart=2025-02-10` – Ventas semanales
- `GET /reports/top-products?limit=10` – Productos más vendidos

## Documentación de la API (Swagger)

Con la aplicación en marcha:

- **Swagger UI**: http://localhost:&lt;puerto&gt;/swagger-ui.html  
- **OpenAPI JSON**: http://localhost:&lt;puerto&gt;/v3/api-docs  

Ahí puedes probar todos los endpoints (incluido login y Bearer token).

## Endpoints públicos (sin token)

- `POST /auth/login`
- `GET /actuator/health` y `GET /actuator/info`
- `GET /swagger-ui.html`, `/v3/api-docs/**`

El resto requieren `Authorization: Bearer <token>`.

## Datos de prueba

La migración **V5__seed_data.sql** inserta productos, variantes e inventario de ejemplo. Tras el primer arranque con Flyway ya aplicado, tendrás:

- **Usuario**: `admin` / `admin123`
- Productos y variantes de ejemplo con stock

Puedes crear pedidos desde Swagger o con `POST /orders` y probar transiciones y WhatsApp.

## Tests

```bash
./mvnw test
```

Incluye:

- Carga del contexto Spring
- Tests del login (AuthController)
- Tests de productos (ProductController) con JWT

## Estructura del proyecto

```
src/main/java/com/alterna/store/store/
├── config/           # CORS, OpenAPI, JPA Auditing
├── shared/           # Excepciones, utilidades, AuditableEntity
├── security/         # JWT, Auth, User
├── catalog/          # Productos y variantes
├── inventory/        # Stock y ajustes
├── orders/           # Pedidos, estados, historial, dirección
├── messaging/        # Plantilla WhatsApp
└── reports/          # Ventas semanales, top productos
```

## Despliegue con GitHub Actions

Hay un workflow en **`.github/workflows/build-and-deploy.yml`** que:

1. Compila el proyecto
2. Ejecuta los tests
3. (Opcional) Despliega en un servicio externo si configuras los secretos

Guía detallada paso a paso: **[DESPLIEGUE.md](DESPLIEGUE.md)**.

## Licencia

Uso educativo / interno.
