# Store API – Tienda

API REST para una tienda: catálogo de productos y variantes, inventario, pedidos con flujo de estados, reserva de stock y plantillas de mensaje para WhatsApp.

**Otros idiomas:** [README.md](README.md) (English)

---

## Requisitos

- **Java 21**
- **Maven 3.9+** (o usar el wrapper `mvnw` incluido)
- **PostgreSQL** (por ejemplo Supabase)

---

## Configuración

1. **Base de datos:** En `src/main/resources/application.properties` configura la URL, usuario y contraseña (por ejemplo Supabase):
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

---

## Cómo ejecutar la aplicación

```bash
# Compilar
./mvnw compile

# Ejecutar (puerto 8080 por defecto, o variable de entorno PORT)
./mvnw spring-boot:run
```

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

El log indicará el puerto (por ejemplo `Tomcat started on port(s): 8080`). La aplicación quedará en **http://localhost:8080** (o el puerto mostrado).

---

## Error de Flyway "checksum mismatch"

Si ves **"Migration checksum mismatch for migration version 5"** (u otra versión), es porque una migración ya aplicada fue modificada después. Ejecuta **Flyway repair** una vez para actualizar el historial:

Usa la **misma URL, usuario y contraseña** que en `application.properties`. Desde la raíz del proyecto:

**Windows (CMD):**
```cmd
set FLYWAY_URL=jdbc:postgresql://TU_HOST:5432/store
set FLYWAY_USER=postgres
set FLYWAY_PASSWORD=TU_PASSWORD
mvn flyway:repair
```

**Windows (PowerShell):**
```powershell
$env:FLYWAY_URL="jdbc:postgresql://TU_HOST:5432/store"
$env:FLYWAY_USER="postgres"
$env:FLYWAY_PASSWORD="TU_PASSWORD"
.\mvnw.cmd flyway:repair
```

**Linux / macOS:**
```bash
export FLYWAY_URL=jdbc:postgresql://TU_HOST:5432/store
export FLYWAY_USER=postgres
export FLYWAY_PASSWORD=TU_PASSWORD
./mvnw flyway:repair
```

Después vuelve a arrancar la aplicación. Si no puedes ejecutar repair, el proyecto usa `spring.flyway.validate-on-migrate=false` para que la app pueda arrancar; ejecuta repair cuando sea posible.

---

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

---

## Documentación de la API (Swagger)

Con la aplicación en marcha:

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Ahí puedes probar todos los endpoints (incluido login y Bearer token).

---

## Endpoints públicos (sin token)

- `POST /auth/login`
- `GET /actuator/health` y `GET /actuator/info`
- `GET /swagger-ui.html`, `/v3/api-docs/**`

El resto requieren `Authorization: Bearer <token>`.

---

## Datos de prueba

La migración **V5__seed_data.sql** inserta productos, variantes e inventario de ejemplo. Tras el primer arranque con Flyway ya aplicado tendrás:

- **Usuario:** `admin` / `admin123`
- Productos y variantes de ejemplo con stock

Puedes crear pedidos desde Swagger o con `POST /orders` y probar transiciones y WhatsApp.

---

## Tests

```bash
./mvnw test
```

Usan el perfil `test` con H2 en memoria (no hace falta PostgreSQL). Incluye:

- Carga del contexto Spring
- Tests del login (AuthController)
- Tests de productos (ProductController) con JWT

---

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

---

## Despliegue

- **GitHub Actions:** `.github/workflows/build-and-deploy.yml` compila y ejecuta tests en cada push/PR.
- **Docker:** Usa el `Dockerfile` incluido (por ejemplo en Render). Puerto por defecto 8080; en el entorno puedes definir `PORT` si el servicio lo requiere.
- **Guía paso a paso:** [DESPLIEGUE.md](DESPLIEGUE.md).

---

## Licencia

Uso educativo / interno.
