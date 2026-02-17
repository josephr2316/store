# Cómo usar Supabase con este proyecto

## Cómo funciona Supabase (resumen)

- **Un proyecto = una base de datos PostgreSQL.** Al crear un proyecto en Supabase se crea automáticamente una base llamada `postgres`.
- **Usuario por defecto:** `postgres` (con la contraseña que pusiste al crear el proyecto).
- **“Store” es el nombre de tu aplicación**, no hace falta crear otra base con ese nombre. Usas la base `postgres` y dentro creas tus tablas (con Flyway o JPA). La “base” para tu app sigue siendo esa misma.

---

## Cambiar la contraseña de la base de datos

1. Entra en [Supabase](https://supabase.com) → tu proyecto.
2. **Project Settings** (icono de engranaje, abajo a la izquierda).
3. **Database** (menú izquierdo).
4. En **Database password** usa **Reset database password**, pon la nueva y guárdala.
5. Actualiza en tu proyecto el archivo `src/main/resources/application-supabase.properties` con la nueva contraseña en `spring.datasource.password=...`.

---

## ¿Crear otra base de datos llamada “store”?

**No es lo habitual.** En Supabase suele usarse una sola base por proyecto (`postgres`). Tu aplicación “store” es solo el nombre del proyecto; las tablas (productos, pedidos, etc.) se crean dentro de `postgres`, por ejemplo en el schema `public`.

Si por algún motivo necesitas **otra base de datos** llamada `store`:

1. En Supabase: **SQL Editor**.
2. Ejecuta: `CREATE DATABASE store;`
3. En la conexión tendrías que usar la base `store` en lugar de `postgres`.  
   En muchos planes de Supabase la conexión que te dan solo permite usar la base `postgres`; bases nuevas no siempre están expuestas en el connection string. Por eso lo normal es **seguir usando la base `postgres`** y no crear una base “store”.

Recomendación: **dejar la base `postgres`** y usar Flyway/JPA para crear en ella las tablas de tu tienda (store).

---

## Cómo arrancar la app con Supabase

```bash
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=supabase"
```

Las credenciales están en `src/main/resources/application-supabase.properties` (este archivo está en `.gitignore` para no subir la contraseña).
