# AD1-EV

# 🏠 API Inmobiliaria

API desarrollada con **Spring Boot** para la gestión de un sistema inmobiliario. Permite administrar Agencias, Propietarios, Inmuebles, Clientes y Visitas, incluyendo validaciones, manejo de excepciones, filtrado de datos y gestión de errores.


##  Tecnologías Utilizadas

* **Java 21** 
* **Spring Boot 3.5.6** (Web, JPA, Validation)
* **Base de Datos:** MariaDB (dockerizada)
* **Mapeo:** ModelMapper
* **Documentación:** OpenAPI 3.0 (SpringDoc / Swagger UI)
* **Testing/Mocking:** WireMock 
* **Gestión de dependencias:** Maven
* **Pruebas Api: Postman

##  Modelo de Datos

El sistema gestiona las siguientes entidades relacionadas:

1.  **Agencia:** Entidad principal que gestiona inmuebles.
2.  **Propietario:** Dueño de los inmuebles.
3.  **Inmueble:** Pisos/Casas asociados a una agencia y un propietario (Relación N:1).
4.  **Cliente:** Usuario interesado en comprar/alquilar.
5.  **Visita:** Cita concertada entre un Cliente y un Inmueble (Relación N:1).

##  Arquitectura del Proyecto

El código está organizado en capas:

1. **Capa Controller**: Gestiona las peticiones HTTP, valida los DTOs de entrada y devuelve las respuestas.
2. **Capa Service**: Contiene toda la lógica de negocio
3. **Capa Repository**: Se encarga del acceso a la base de datos
4. **Modelos de datos (Domain y DTO)**: Domain representa los datos de la base de datos. DTOs para representar los datos de entrad y de salida.
5. **Manejo de excepciones**: Se capturan errores y se devuelven respuestas JSON estandarizadas.

##  Pruebas de test:

1. **Tests de capa Service**: Probamos la lógica de negocio. Para ellos utilizamos Mockito para mockear los respositorios.
2. **Tests de capa Controller**: Probamos que los endpoints responden a los códigos correctos ( 200, 201, 400, 404...) Para ello ultizamos MockMvc.

##  Instrucciones de Ejecución

### 1. Requisitos previos
* Tener instalado JDK 17+.
* Tener Maven instalado.
* Un IDE (IntelliJ IDEA, Visual Studio Code,...).
* Tener Docker instalado.
* Postman.

### 2. Levantar la base de datos (Docker)
Antes de iniciar la aplicación es necesario lanzar el contenedor de MariaDB. Ejecuta el siguiente comando:

```bash
docker compose docker-compose.dev.yaml up -d
```

### 3. Arrancar la API Principal
Ejecuta el siguiente comando ( la api está disponible en  http://localhost:8080):

```bash
mvn spring-boot:run
```

### 4. Ejecutar los Tests
Para arrancar todos los tests y ver que son correctos ejecuta el comando:

```bash
mvn test
```

### 5. Pruebas de la API con Postman
Se incluye una colección de Postman con todos los endpoints y ejemplos.

```bash
API Inmobiliaria.postman_collection.json
```
Se añade otra colección Postman para que pueda ser ejecutada con el Runner de Postman y poder realizar una pruebla completa de la API

```bash
API Inmobiliaria - Run Test.postman_collection.json
```

Para pobrar las colecciones abre Postman.

Haz clic en Import > Upload Files y selecciona el archivo de la colección.

### 6. Proyecto Auxiliar
Se dispone de un repositorio secundario ("API Inmobiliaria WireMock") que fue utilizado durante la fase de diseño para la generación de datos de prueba. No es necesario ejecutar dicho repositorio para el funcionamiento de esta API, ya que este proyecto es totalmente independiente. Para la realización de esta API se utilizó Wiremock 

```bash
https://github.com/raulpra/API-INMOB-WIREMOCK.git
```

##
