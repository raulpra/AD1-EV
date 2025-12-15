# AD1-EV

# 🏠 API Inmobiliaria

API desarrollada con **Spring Boot** para la gestión de un sistema inmobiliario. Permite administrar Agencias, Propietarios, Inmuebles, Clientes y Visitas, incluyendo validaciones, manejo de excepciones y filtrado de datos.


##  Tecnologías Utilizadas

* **Java 21** 
* **Spring Boot 3.x** (Web, JPA, Validation)
* **Base de Datos:** H2 (En memoria, temporalmente) / Próxima migración a PostgreSQL/MariaDB.
* **Mapeo:** ModelMapper
* **Documentación:** OpenAPI 3.0 (SpringDoc / Swagger UI)
* **Testing/Mocking:** WireMock (Standalone)
* **Gestión de dependencias:** Maven

##  Modelo de Datos

El sistema gestiona las siguientes entidades relacionadas:

1.  **Agencia:** Entidad principal que gestiona inmuebles.
2.  **Propietario:** Dueño de los inmuebles.
3.  **Inmueble:** Pisos/Casas asociados a una agencia y un propietario (Relación N:1).
4.  **Cliente:** Usuario interesado en comprar/alquilar.
5.  **Visita:** Cita concertada entre un Cliente y un Inmueble (Relación N:1).

##  Instrucciones de Ejecución

### 1. Requisitos previos
* Tener instalado JDK 17+.
* Tener Maven instalado.
* Un IDE (IntelliJ IDEA, Eclipse, VS Code).

### 2. Arrancar la API Principal
Ejecuta el siguiente comando en la raíz del proyecto:

```bash
mvn spring-boot:run
