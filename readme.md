Proyecto CRUD de Invitaciones con Spring Boot + MongoDB + JWT + Código QR

Dependencias sugeridas (en pom.xml si usas Maven):
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-mongodb
- jjwt (JWT auth)
- zxing (generación de QR)

📁 Estructura de carpetas
-------------------------------

├── config/            ← Seguridad y JWT

├── controller/        ← Controladores REST

├── dto/               ← Clases para login y registro

├── model/             ← User, Invitation

├── repository/        ← Mongo Repositories

├── service/           ← Lógica de negocio

└── Application.java
