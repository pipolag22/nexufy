# Nexufy Backend

## Introducción
Nexufy es una plataforma que actúa como un nexo entre productores y proveedores de insumos, como plásticos, vidrio y otros materiales. Este backend maneja la lógica de negocio y la API para las interacciones en la plataforma, permitiendo la gestión de productos, usuarios y roles, con características de seguridad y autenticación.

## Tecnologías Utilizadas
- **Framework**: Spring Boot
- **Base de Datos**: MongoDB
- **Autenticación**: JWT (Json Web Token)
- **Generación de Reportes**: JasperReports
- **Seguridad**: Spring Security
- **Documentación de API**: Swagger

## Estructura de la Base de Datos
La base de datos MongoDB se organiza en las siguientes colecciones:
- **comments**: Para almacenar comentarios.
- **customers**: Contiene información sobre los clientes registrados, incluyendo roles y productos asociados.
- **products**: Almacena los productos publicados.
- **roles**: Define los roles del sistema (ROLE_USER, ROLE_ADMIN, ROLE_SUPERADMIN).

## Roles y Permisos
- **Usuario sin registrar (Visitante)**: Navegar y ver publicaciones.
- **Usuario Registrado (ROLE_USER)**: Ver detalles de otros usuarios.
- **Administrador (ROLE_ADMIN)**: Publicar y gestionar sus propios productos.
- **Superadministrador (ROLE_SUPERADMIN)**: Gestionar todos los usuarios, productos y ver estadísticas.

## Guía de Instalación y Configuración
### Requisitos Previos
- Java 17
- MongoDB
- Git

### Clonar el Repositorio
```bash
git clone https://github.com/GasparTorres/nexufy.git
cd nexufy
Configuración
Edita el archivo src/main/resources/application.properties con tus credenciales de MongoDB y configuraciones de JWT:

properties
Copiar código
spring.application.name=nexufy
spring.data.mongodb.database=Nexufy
spring.data.mongodb.uri=mongodb+srv://<usuario>:<contraseña>@nexufy.mongodb.net/
nexufy.app.jwtSecret=YourJWTSecretKey
nexufy.app.jwtExpirationMs=86400000
server.port=8081
spring.main.allow-circular-references=true
Ejecutar la Aplicación
bash
Copiar código
./mvnw install
./mvnw spring-boot:run
La aplicación estará disponible en http://localhost:8081.

Arquitectura del Proyecto
Controladores: Gestionan las solicitudes HTTP.
Servicios: Contienen la lógica de negocio.
Repositorios: Manejan la interacción con la base de datos.
Seguridad: Configura CORS y JWT en SecurityConfig.
API Endpoints
Autenticación
POST /api/auth/login: Iniciar sesión.
POST /api/auth/register: Registro de nuevos usuarios.
Clientes
GET /api/customer/all: Obtener todos los clientes.
PUT /api/customer/{id}: Actualizar cliente.
DELETE /api/customer/{id}: Eliminar cliente.
Productos
GET /api/customer/{id}/products: Obtener productos de un cliente.
POST /api/products/customer/{customerId}: Crear producto.
Usuarios y Roles
PUT /api/user/promote/admin: Promover usuario a administrador.
Seguridad (CORS y JWT)
La configuración de seguridad en SecurityConfig permite:

Acceso público: login, registro y acceso a productos.
Protección: Rutas de superadministradores y reportes requieren permisos específicos.
