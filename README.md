# Store API

REST API for a store: product and variant catalog, inventory, orders with status workflow, stock reservation, and WhatsApp message templates.

**Other languages:** [README.es.md](README.es.md) (Spanish)

---

## Requirements

- **Java 21**
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **PostgreSQL** (e.g. Supabase)

---

## Configuration

1. **Database:** In `src/main/resources/application.properties` set your database URL, username and password (e.g. Supabase):
   ```properties
   spring.datasource.url=jdbc:postgresql://HOST:5432/store
   spring.datasource.username=postgres
   spring.datasource.password=YOUR_PASSWORD
   ```

2. **Create the `store` database** in PostgreSQL/Supabase if it does not exist:
   ```sql
   CREATE DATABASE store;
   ```

3. On startup, **Flyway** creates tables and the default user.

---

## Running the application

```bash
# Build
./mvnw compile

# Run (default port 8080, or use PORT env var)
./mvnw spring-boot:run
```

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

The log will show the port (e.g. `Tomcat started on port(s): 8080`). The app is available at **http://localhost:8080** (or the port shown).

---

## Flyway "checksum mismatch" error

If you see **"Migration checksum mismatch for migration version 5"** (or another version), a migration was changed after it had already been applied. Run **Flyway repair** once to update the schema history:

Use the **same URL, user and password** as in `application.properties`. From the project root:

**Windows (CMD):**
```cmd
set FLYWAY_URL=jdbc:postgresql://YOUR_HOST:5432/store
set FLYWAY_USER=postgres
set FLYWAY_PASSWORD=YOUR_PASSWORD
mvn flyway:repair
```

**Windows (PowerShell):**
```powershell
$env:FLYWAY_URL="jdbc:postgresql://YOUR_HOST:5432/store"
$env:FLYWAY_USER="postgres"
$env:FLYWAY_PASSWORD="YOUR_PASSWORD"
.\mvnw.cmd flyway:repair
```

**Linux / macOS:**
```bash
export FLYWAY_URL=jdbc:postgresql://YOUR_HOST:5432/store
export FLYWAY_USER=postgres
export FLYWAY_PASSWORD=YOUR_PASSWORD
./mvnw flyway:repair
```

Then start the application again. If you cannot run repair, the project uses `spring.flyway.validate-on-migrate=false` so the app can still start; run repair when possible.

---

## Login and API usage

### 1. Get token (login)

```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Example response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN"
}
```

### 2. Call protected endpoints

Include the token in the header:

```http
Authorization: Bearer <token>
```

Examples:

- `GET /products` – List products
- `POST /products` – Create product
- `GET /orders` – List orders
- `POST /orders` – Create order
- `POST /orders/{id}/transition` – Change order status (e.g. confirm, ship)
- `GET /inventory/balance` – View inventory
- `GET /whatsapp/order/{id}/message` – WhatsApp message for an order
- `GET /reports/weekly-sales?weekStart=2025-02-10` – Weekly sales
- `GET /reports/top-products?limit=10` – Top products

---

## API documentation (Swagger)

With the app running:

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

You can try all endpoints there (including login and Bearer token).

---

## Public endpoints (no token)

- `POST /auth/login`
- `GET /actuator/health` and `GET /actuator/info`
- `GET /swagger-ui.html`, `/v3/api-docs/**`

All other endpoints require `Authorization: Bearer <token>`.

---

## Seed data

Migration **V5__seed_data.sql** inserts sample products, variants and inventory. After the first run with Flyway applied you get:

- **User:** `admin` / `admin123`
- Sample products and variants with stock

You can create orders via Swagger or `POST /orders` and test transitions and WhatsApp.

---

## Tests

```bash
./mvnw test
```

Uses the `test` profile with H2 in-memory (no PostgreSQL needed). Includes:

- Spring context load
- AuthController login tests
- ProductController tests with JWT

---

## Project structure

```
src/main/java/com/alterna/store/store/
├── config/           # CORS, OpenAPI, JPA Auditing
├── shared/           # Exceptions, utilities, AuditableEntity
├── security/         # JWT, Auth, User
├── catalog/          # Products and variants
├── inventory/        # Stock and adjustments
├── orders/           # Orders, status, history, address
├── messaging/        # WhatsApp template
└── reports/          # Weekly sales, top products
```

---

## Deployment

- **GitHub Actions:** `.github/workflows/build-and-deploy.yml` compiles and runs tests on push/PR.
- **Docker:** Use the included `Dockerfile` (e.g. for Render). Default port 8080; set `PORT` in the environment if required.
- **Step-by-step guide:** [DESPLIEGUE.md](DESPLIEGUE.md) (Spanish).

---

## License

Educational / internal use.
