# API REST de Gestión de Pólizas

API REST Spring Boot para consultar y administrar pólizas y riesgos con persistencia H2, validación de datos y reglas de negocio del módulo 2.

## Tecnologías

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- H2 Database
- Maven Wrapper

## Estructura

```text
src/main/java/com/pruebatecnica/polizas
├── config
├── controller
├── dto
├── exception
├── model
├── repository
└── service
```

## Ejecución

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación se inicia en `http://localhost:8080` y utiliza una base de datos H2 en memoria. Los datos iniciales incluyen una póliza individual activa, una colectiva activa y una póliza cancelada.

## Seguridad

Todas las rutas requieren el encabezado:

```text
x-api-key: 123456
```

Las solicitudes sin el encabezado o con una clave diferente responden `401 Unauthorized`.

## Endpoints

| Método | Ruta | Resultado |
|---|---|---|
| GET | `/polizas` | Lista pólizas |
| GET | `/polizas?tipo=COLECTIVA&estado=ACTIVA` | Filtra pólizas |
| GET | `/polizas/{id}/riesgos` | Consulta riesgos |
| POST | `/polizas/{id}/renovar` | Renueva una póliza |
| POST | `/polizas/{id}/cancelar` | Cancela una póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Crea un riesgo; responde `201` |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo |
| POST | `/core-mock/evento` | Registra un evento CORE simulado |

Las operaciones exitosas responden `200`, excepto la creación de riesgos, que responde `201`. Las validaciones responden `400`, los recursos inexistentes `404` y las operaciones incompatibles con las reglas de negocio `409`.

## Ejemplos

Renovar con un IPC de 5,2 %:

```http
POST /polizas/1/renovar
x-api-key: 123456
Content-Type: application/json

{
  "ipc": 5.2
}
```

Agregar un riesgo a una póliza colectiva:

```http
POST /polizas/2/riesgos
x-api-key: 123456
Content-Type: application/json

{
  "inmueble": "Apartamento 302"
}
```

Cancelar una póliza:

```http
POST /polizas/2/cancelar
x-api-key: 123456
```

Registrar un evento en el CORE Mock:

```http
POST /core-mock/evento
x-api-key: 123456
Content-Type: application/json

{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

## Pruebas

```powershell
.\mvnw.cmd clean test
```

## Empaquetado

```powershell
.\mvnw.cmd clean package
```
