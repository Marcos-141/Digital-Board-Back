# DigitalBoard - RESTful API

**DigitalBoard** es una API RESTful desarrollada con **Spring Boot** y **Java 25** diseñada para gestionar un sistema de tablero digital con usuarios, temáticas (topics), publicaciones (posts) y comentarios.

El proyecto implementa una **arquitectura por capas** (Controller, Service, Repository, Entity, DTO, Mapper) garantizando una separación clara de responsabilidades, validación estricta de datos de entrada y manejo estandarizado de respuestas HTTP.

---

## Tecnologías Utilizadas

* **Lenguaje:** Java 25
* **Framework:** Spring Boot 3.x
* **Persistencia & Mapeo:** Spring Data JPA / Hibernate
* **Base de Datos:** H2 Database (En memoria para desarrollo)
* **Boilerplate & Utilidades:** Lombok
* **Validación:** Jakarta Validation (`@Valid`)
* **Servidor Embebido:** Apache Tomcat

---

## Estructura del Proyecto

```text
com.DigitalBoardMembers.DigitalBoard/
├── controllers/      # Endpoints REST (@RestController)
├── service/          # Interfaces de lógica de negocio
│   └── impl/         # Implementaciones (@Service, @Transactional)
├── repository/       # Interfaces de acceso a datos (JpaRepository)
├── entity/           # Modelos de dominio / Entidades JPA (@Entity)
├── dto/              # Objetos de Transferencia de Datos
│   ├── request/      # DTOs de entrada con validaciones (@Valid)
│   └── response/     # DTOs de salida serializados a JSON
├── mappers/          # Componentes para conversión Entidad <-> DTO
└── DigitalBoardApplication.java