# Induztek Protocolos

Aplicación móvil Android (Kotlin, Jetpack Compose) con backend de microservicios (Spring Boot) para registrar y gestionar protocolos de pruebas eléctricas en terreno de Induztek Ingeniería SpA.

Asignatura: DSY1105 Desarrollo de Aplicaciones Móviles, Duoc UC. Evaluación Parcial 4.

## 1. Nombre del proyecto

Induztek Protocolos

## 2. Integrantes

| Integrante | Rol |
| --- | --- |
| Vicente Bravo | Único integrante: app móvil, microservicios, pruebas y publicación |

## 3. Funcionalidades

La app cubre el flujo de una visita técnica, desde el inicio de sesión hasta la formalización del protocolo.

- **Inicio de sesión con correo y contraseña.** La contraseña se guarda en el servidor con hash PBKDF2WithHmacSHA256 y sal.
- **Cuatro roles con permisos distintos:** técnico de terreno, ingeniero de pruebas, jefe técnico y usuario cliente.
- **Visitas:** crear una visita, ver su detalle y cerrarla.
- **Protocolos de prueba:** iniciar un protocolo por equipo, registrar condiciones ambientales, adjuntar evidencia fotográfica y cambiar su estado (borrador, registrado en terreno, observado, formalizado, anulado).
- **Pruebas por equipo:** resistencia de aislación, relación de transformación (TTR), resistencia de devanados, resistencia de contactos y operación de relé, con resultado aprobado, observado o rechazado.
- **Catálogo:** instalaciones, equipos (transformador, interruptor, celda de media tensión, cable de poder, relé de protección) e instrumentos.
- **Historial** de protocolos por equipo y **revisión** de protocolos pendientes.
- **Vista de cliente:** el usuario cliente consulta solo los equipos de su empresa.
- **Clima actual desde una API externa (Open-Meteo)** para completar la temperatura y la humedad del protocolo.
- **Servidor configurable** desde la pantalla de inicio de sesión.

Arquitectura: la app habla únicamente con el API Gateway, que enruta hacia tres microservicios registrados en Eureka. Cada microservicio tiene su propia base de datos MySQL.

| Componente | Puerto | Función |
| --- | --- | --- |
| eureka-server | 8761 | Registro de servicios |
| usuarios-service | 8081 | Usuarios y autenticación |
| catalogo-service | 8082 | Instalaciones, equipos e instrumentos |
| protocolos-service | 8083 | Visitas, protocolos y pruebas |
| api-gateway | 8080 | Punto de entrada único para la app |

## 4. Endpoints utilizados

### Microservicios propios (vía API Gateway, `http://HOST:8080`)

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/usuarios` | Lista usuarios (filtros `rol`, `activos`) |
| GET | `/api/usuarios/{id}` | Obtiene un usuario |
| POST | `/api/usuarios/login` | Inicia sesión con correo y contraseña |
| PUT | `/api/usuarios/{id}` | Actualiza un usuario |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario |
| GET | `/api/instalaciones` | Lista instalaciones (filtro `idCliente`) |
| GET | `/api/instalaciones/{id}` | Obtiene una instalación |
| PUT | `/api/instalaciones/{id}` | Actualiza una instalación |
| DELETE | `/api/instalaciones/{id}` | Elimina una instalación |
| GET | `/api/equipos` | Lista equipos (filtro `idInstalacion`) |
| GET | `/api/equipos/{id}` | Obtiene un equipo |
| POST | `/api/equipos` | Crea un equipo |
| PUT | `/api/equipos/{id}` | Actualiza un equipo |
| DELETE | `/api/equipos/{id}` | Elimina un equipo |
| GET | `/api/instrumentos` | Lista instrumentos |
| GET | `/api/instrumentos/{id}` | Obtiene un instrumento |
| POST | `/api/instrumentos` | Crea un instrumento |
| PUT | `/api/instrumentos/{id}` | Actualiza un instrumento |
| DELETE | `/api/instrumentos/{id}` | Elimina un instrumento |
| GET | `/api/visitas` | Lista visitas |
| GET | `/api/visitas/{id}` | Obtiene una visita |
| POST | `/api/visitas` | Crea una visita |
| PUT | `/api/visitas/{id}/cierre` | Cierra una visita |
| GET | `/api/protocolos` | Lista protocolos (filtros `idVisita`, `idEquipo`, `estado`) |
| GET | `/api/protocolos/{id}` | Obtiene un protocolo |
| GET | `/api/protocolos/{id}/detalle` | Detalle completo de un protocolo |
| GET | `/api/protocolos/equipo/{idEquipo}/detalle` | Historial de protocolos de un equipo |
| POST | `/api/protocolos` | Inicia un protocolo |
| PUT | `/api/protocolos/{id}/ambiente` | Guarda condiciones ambientales |
| PUT | `/api/protocolos/{id}/evidencia` | Guarda evidencia fotográfica |
| GET | `/api/protocolos/{id}/foto` | Obtiene la foto de evidencia |
| PUT | `/api/protocolos/{id}/estado` | Cambia el estado del protocolo |
| POST | `/api/protocolos/{id}/pruebas` | Registra una prueba |
| PUT | `/api/pruebas/{id}/resultado` | Fija el resultado de una prueba |

Documentación Swagger de cada servicio: `http://localhost:PUERTO/swagger-ui.html`.

### API externa

La app consume **Open-Meteo** (https://open-meteo.com), un servicio público de clima que no requiere clave. Se llama con Retrofit, de forma independiente de los microservicios propios, y no escribe ni modifica datos del servidor por sí sola.

| Método | URL | Parámetros |
| --- | --- | --- |
| GET | `https://api.open-meteo.com/v1/forecast` | `latitude`, `longitude`, `current=temperature_2m,relative_humidity_2m` |

Dónde se usa: en la pantalla del protocolo, sección **Condiciones ambientales**, el botón **Obtener clima actual del lugar** consulta el clima en las coordenadas registradas con el GPS del protocolo. La temperatura y la humedad recibidas se muestran en la interfaz y rellenan los campos del formulario, y el técnico puede corregirlas antes de guardar. Si el servicio no responde, la app avisa y permite escribir los valores a mano.

## 5. Pasos para ejecutar

### Requisitos

- Laragon (MySQL, usuario `root` sin contraseña)
- JDK 21
- Visual Studio Code con Extension Pack for Java y Spring Boot Dashboard
- Android Studio, con emulador o teléfono con depuración USB
- Maven, solo para ejecutar `mvn clean verify`

### Backend

1. Iniciar MySQL desde Laragon con **Start All**. Las bases `induztek_usuarios`, `induztek_catalogo` e `induztek_protocolos` se crean automáticamente, junto con los datos iniciales.
2. Abrir la carpeta de microservicios en Visual Studio Code.
3. Iniciar los servicios desde Spring Boot Dashboard, en este orden y esperando a que cada uno muestre su puerto:
   1. `eureka-server`
   2. `usuarios-service`
   3. `catalogo-service`
   4. `protocolos-service`
   5. `api-gateway`
4. Comprobar en `http://localhost:8761` que estén registrados los tres servicios.

### Aplicación móvil

1. Abrir la carpeta de la app en Android Studio y esperar la sincronización de Gradle.
2. Ejecutar la app con **Run**.
3. En la pantalla de inicio de sesión, el servidor por defecto es `http://10.0.2.2:8080/`, válido para el emulador. Si el backend corre en otro computador o se usa un teléfono físico, escribir `http://IP_DEL_SERVIDOR:8080/` (misma red Wi-Fi, puerto 8080 permitido en el firewall) y pulsar **Guardar y reconectar**.
4. Iniciar sesión con una cuenta de prueba:

| Correo | Contraseña |
| --- | --- |
| `acontreras@induztek.ejemplo` | `Induztek2026` |

Todas las cuentas de prueba usan esa contraseña inicial.

### Pruebas

- App: `./gradlew testDebugUnitTest` (en Windows, `.\gradlew testDebugUnitTest`).
- Microservicios: `mvn clean verify` dentro de cada servicio. El informe de cobertura JaCoCo queda en `target/site/jacoco/index.html`.

### Generar el APK y el AAB firmados

```
.\gradlew assembleRelease bundleRelease
```

Salida: `app/build/outputs/apk/release/app-release.apk` y `app/build/outputs/bundle/release/app-release.aab`. La firma usa `keystore/induztek-release.jks`, alias `induztek`.

## 6. Captura del APK firmado y del .jks

![APK firmado y archivo .jks](docs/apk-firmado-y-jks.png)

Archivos entregados en la carpeta de firma: `InduztekProtocolos.apk`, `InduztekProtocolos.aab` e `InduztekProtocolos.jks`.

## Planificación y control de versiones

- Tablero de Trello: Al trabajar de manera individual no se utilizo plataforma de organización interactiva.
- Repositorio de la app móvil: https://github.com/vicbravoe/App-InduztekProtocolos
- Repositorio de los microservicios: https://github.com/vicbravoe/MS-InduztekProtocolos
