# 🏦 Banking API — Prueba Técnica Backend

API REST desarrollada en **Java + Spring Boot** siguiendo arquitectura por capas (Controller, Service, Repository, Entity).

Proyecto orientado a la gestión de:

* 👤 Clientes
* 💳 Cuentas bancarias
* 💸 Transacciones (Transferencias)

---

## 🚀 Tecnologías

* Java 17
* Spring Boot 3.4.3
* Spring Web
* Spring Data JPA
* MySQL
* Lombok
* Maven
* JUnit 5 + Mockito + MockMvc

---

## 📦 Arquitectura

El proyecto está organizado por capas:

```
controller/
service/
repository/
entity/
dto/
exception/
```

---

## ⚙️ Configuración Base de Datos

Crear base de datos en MySQL:

```sql
CREATE DATABASE financiera_db;
```

Configurar `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/financiera_db
    username: TU_USUARIO
    password: TU_PASSWORD
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## ▶️ Cómo ejecutar el proyecto

Clonar repositorio:

```
git clone https://github.com/TU_USUARIO/bankingapi.git
```

Entrar al proyecto:

```
cd bankingapi
```

Ejecutar:

```
./mvnw spring-boot:run
```

API disponible en:

```
http://localhost:8080
```

---

## 🧪 Ejecutar tests

```
./mvnw test
```

---

## 📮 Endpoints principales

### 👤 Clientes

Crear cliente:

```
POST /api/clientes
```

Listar clientes:

```
GET /api/clientes
```

Actualizar cliente:

```
PUT /api/clientes/{id}
```

---

### 💳 Cuentas

Crear cuenta:

```
POST /api/cuentas
```

Cancelar cuenta:

```
PATCH /api/cuentas/{id}/cancelar
```

---

### 💸 Transacciones

Transferencia entre cuentas:

```
POST /api/transacciones/transferencia
```

---

## 🧾 Ejemplo Transferencia (Postman)

```json
{
  "cuentaOrigenId": 1,
  "cuentaDestinoId": 2,
  "monto": 10.00
}
```

---

## 📚 Documentación API (Swagger)

Una vez agregado Swagger, acceder a:

```
http://localhost:8080/swagger-ui.html
```

---

## 👨‍💻 Autor

Prueba técnica backend desarrollada por:

**Julián Steven Chavarro Rivera**
