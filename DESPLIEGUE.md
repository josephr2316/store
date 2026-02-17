# Despliegue de la aplicación Store (paso a paso)

Esta guía explica cómo hacer que la aplicación se compile y se pruebe con **GitHub Actions** y, opcionalmente, cómo desplegarla en un servicio en la nube.

---

## 1. Subir el proyecto a GitHub

1. Crea un repositorio nuevo en GitHub (por ejemplo `mi-tienda-store`).
2. En tu máquina, inicializa Git en la carpeta del proyecto (si aún no lo has hecho):
   ```bash
   cd store
   git init
   git add .
   git commit -m "Initial commit - Store API"
   ```
3. Conecta el repositorio remoto y sube:
   ```bash
   git remote add origin https://github.com/TU_USUARIO/mi-tienda-store.git
   git branch -M main
   git push -u origin main
   ```

---

## 2. GitHub Actions: compilar y ejecutar tests

El workflow ya está en el proyecto en:

**`.github/workflows/build-and-deploy.yml`**

### Qué hace

- Se ejecuta en cada **push** y en cada **pull request** a las ramas `main` o `master`.
- Usa **Java 21** (Temurin).
- Ejecuta:
  ```bash
  ./mvnw -B verify -DskipTests=false
  ./mvnw -B test
  ```
  Es decir: compila, ejecuta tests y verifica que todo pase.

### Cómo ver que funciona

1. Después de hacer `git push`, abre tu repositorio en GitHub.
2. Ve a la pestaña **Actions**.
3. Verás el workflow **"Build and Test"** en ejecución o ya finalizado.
4. Si el icono está en verde, la compilación y los tests han pasado.

### Si los tests fallan en GitHub

- Los tests usan la base de datos configurada en `application.properties`. En GitHub Actions **no** hay Supabase por defecto.
- Para que los tests no dependan de la base real:
  - Crea un perfil de test con H2 en memoria, por ejemplo `src/test/resources/application-test.properties`:
    ```properties
    spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    spring.datasource.driver-class-name=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.flyway.baseline-on-migrate=true
    ```
  - Y en los tests que cargan el contexto completo, activa el perfil `test` (por ejemplo con `@ActiveProfiles("test")`).
- O bien configura en GitHub **Secrets** la URL y credenciales de una base de pruebas (por ejemplo otra base en Supabase) y úsalas en el workflow con un perfil `test` que lea variables de entorno.

---

## 3. Desplegar la aplicación (fuera de localhost)

Para que la API esté accesible por internet (y no solo en localhost), hay que desplegarla en un servicio. Tres opciones típicas:

### Opción A: Railway

1. Entra en [railway.app](https://railway.app) y crea un proyecto.
2. Conecta el repositorio de GitHub (Deploy from GitHub repo).
3. Railway detecta Maven/Java y construye con `mvnw` (o el comando que configures).
4. Añade un **servicio PostgreSQL** en Railway o usa una base externa (por ejemplo Supabase).
5. En **Variables** del servicio, define:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   (Spring Boot las usa por defecto si existen.)
6. Guarda y despliega. Railway te dará una URL pública (ej. `https://tu-app.railway.app`).

Para desplegar desde GitHub Actions con Railway CLI, puedes añadir un job `deploy` que use `RAILWAY_TOKEN` en los secretos del repo.

### Opción B: Render

1. Entra en [render.com](https://render.com) y crea un **Web Service**.
2. Conecta el repositorio de GitHub.
3. Configuración típica:
   - **Build command:** `./mvnw -DskipTests package`
   - **Start command:** `java -jar target/store-0.0.1-SNAPSHOT.jar`
4. Añade **Environment Variables** con la URL y credenciales de la base de datos.
5. Despliega. Render te asigna una URL (ej. `https://store-xxx.onrender.com`).

### Opción C: Otra máquina (VPS) con GitHub Actions

1. Tienes un servidor (Ubuntu, etc.) con Java 21 y la aplicación desplegada (por ejemplo un JAR o un servicio systemd).
2. En GitHub, en el repositorio: **Settings → Secrets and variables → Actions**.
3. Añade secretos, por ejemplo:
   - `SSH_PRIVATE_KEY`: clave privada para conectarte por SSH al servidor.
   - `HOST`: IP o dominio del servidor.
   - `USER`: usuario SSH.
4. En `.github/workflows/build-and-deploy.yml`, añade un job que:
   - Haga `./mvnw -B package -DskipTests` (o `verify` si quieres tests).
   - Conecte por SSH al servidor y suba el JAR (o los artefactos).
   - Reinicie el servicio en el servidor (por ejemplo `systemctl restart store`).

Ejemplo mínimo de paso de despliegue por SSH (sustituye según tu caso):

```yaml
- name: Deploy to server
  uses: appleboy/ssh-action@v1.0.0
  with:
    host: ${{ secrets.HOST }}
    username: ${{ secrets.USER }}
    key: ${{ secrets.SSH_PRIVATE_KEY }}
    script: |
      cd /opt/store
      # aquí copias el JAR (por ejemplo con scp en un paso anterior) y reinicias
      sudo systemctl restart store
```

---

## 4. Resumen rápido

| Paso | Acción |
|------|--------|
| 1 | Subir código a GitHub (`git push`). |
| 2 | Comprobar en **Actions** que el workflow **Build and Test** pase (verde). |
| 3 | (Opcional) Configurar tests con H2 en perfil `test` para no depender de Supabase en CI. |
| 4 | Elegir un servicio (Railway, Render, VPS, etc.) y configurar base de datos y variables de entorno. |
| 5 | (Opcional) Añadir un job `deploy` en el workflow usando los secretos necesarios. |

Si indicas en qué servicio quieres desplegar (Railway, Render, VPS con SSH, etc.), se puede detallar solo ese flujo (comandos y ejemplo de workflow concreto).
