# Ecommerce API

API REST de e-commerce simplificado, desarrollada como proyecto de portfolio para practicar Java y Spring Boot a nivel profesional: arquitectura en capas, autenticación JWT, transaccionalidad, control de concurrencia y testing en múltiples niveles.

## Stack

- **Java 17** + **Spring Boot 4.1.1**
- **Spring Web**, **Spring Data JPA**, **Spring Security**
- **PostgreSQL** (producción/desarrollo) + **H2** (tests)
- **Flyway** — migraciones de base de datos versionadas
- **JWT** (jjwt) — autenticación stateless
- **Swagger / OpenAPI** (springdoc 3.0.0) — documentación interactiva
- **JUnit 5 + Mockito** — testing unitario, de integración y de concurrencia

## Funcionalidades

- Catálogo de productos y categorías, con filtros combinables (categoría, rango de precio) y paginación.
- Autenticación con JWT y autorización por rol (`ADMIN` / `USER`).
- Carrito y creación de órdenes: cálculo de totales en el backend, validación de stock, transaccionalidad completa.
- **Control de concurrencia real** (optimistic locking) para evitar sobreventa, demostrado con un test de hilos concurrentes.
- Historial de órdenes por usuario, con protección contra acceso a recursos ajenos (IDOR).
- Manejo global de errores con respuestas HTTP consistentes.

## Variables de entorno

Las credenciales de base de datos se configuran por variables de entorno, con valores por defecto para desarrollo local con Docker Compose:

| Variable | Default | Descripción |
|---|---|---|
| `DB_USERNAME` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `postgres` | Contraseña de PostgreSQL |

## Cómo levantar el proyecto

### Requisitos
- Java 17+
- Maven (o usar el wrapper incluido, `mvnw`)
- Docker (para la base de datos)

### 1. Levantar la base de datos

docker compose up -d

### 2. Configurar las credenciales

Revisá `src/main/resources/application.properties` y ajustá `spring.datasource.username`/`password` si cambiaste algo en el `docker-compose.yml`.

### 3. Ejecutar la aplicación

./mvnw spring-boot:run

Las migraciones de Flyway se aplican automáticamente al arrancar (incluye un usuario `admin` precargado, ver sección siguiente).

### 4. Documentación interactiva

Con la app corriendo, abrí:

http://localhost:8080/swagger-ui/index.html


Para probar endpoints protegidos: logueate con `POST /api/auth/login`, copiá el `token` de la respuesta, y pegalo (sin el prefijo `Bearer`) en el botón **Authorize** de la interfaz.

## Usuario administrador por defecto

| Campo | Valor |
|---|---|
| Username | `admin` |
| Password | `Admin123!` |

Cualquier otro usuario registrado por `/api/auth/register` recibe el rol `USER` automáticamente.

## Tests

./mvnw test


Incluye tests unitarios (Mockito), de persistencia (`@DataJpaTest`), de la capa web (`@WebMvcTest`), de integración completa (`@SpringBootTest`), y un test de concurrencia con hilos reales que verifica que el optimistic locking evita la sobreventa de stock.

## Decisiones de diseño relevantes

- **El total de una orden siempre se calcula en el backend**, nunca se confía en un valor que mande el cliente.
- **El primer usuario `ADMIN` se crea por migración de datos**, no por un endpoint de registro — evita que cualquiera pueda autoasignarse privilegios de administrador.
- **Control de acceso a nivel de objeto**: un usuario solo puede ver sus propias órdenes, incluso si tiene un token válido, protegiendo contra IDOR. Este chequeo aplica también a usuarios con rol `ADMIN` (decisión deliberada: ver cualquier orden requeriría un endpoint o rol separado, no implementado en esta versión).

## Troubleshooting conocido

- **Spring Boot 4 / Jackson 3**: varios paquetes de testing y de Jackson cambiaron respecto a versiones anteriores (`@MockBean` → `@MockitoBean`, `ObjectMapper` → `JsonMapper` en `tools.jackson`).
- **Swagger UI + `Pageable`**: el parámetro `sort` requiere `springdoc.default-flat-param-object=true` en `application.properties` para serializarse correctamente desde la interfaz.

