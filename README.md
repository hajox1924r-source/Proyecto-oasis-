# Farmacia Oasis

Aplicativo web para la gestión de inventario y facturación en la Farmacia Oasis, con énfasis en el **control de medicamentos vencidos** (alertas y notificaciones de fecha de vencimiento). Proyecto de aula — Tecnología en Desarrollo de Software.

## Tecnologías

- **Java + Spring Boot** (patrón MVC)
- **Thymeleaf** para las vistas (HTML + CSS puro, sin JavaScript)
- **MySQL** como base de datos
- **Spring Security** para el inicio de sesión (sesiones + contraseñas con BCrypt)
- **Maven** para la gestión de dependencias y la construcción

## Arquitectura (MVC)

El navegador solo recibe HTML y CSS. Cada acción (buscar, guardar, vender) es un formulario o un enlace que recarga la página.

- **Modelo:** las entidades (`model/`) y el acceso a datos (`repository/`), más la lógica de negocio (`service/`).
- **Vista:** las plantillas Thymeleaf en `resources/templates/`.
- **Controlador:** las clases de `controller/`, que reciben la petición y devuelven el nombre de la vista con sus datos.

## Estructura del proyecto

```
Proyecto-oasis-/
├── src/
│   ├── main/
│   │   ├── java/com/farmaciaoasis/
│   │   │   ├── FarmaciaOasisApplication.java   # clase que arranca Spring Boot
│   │   │   ├── config/
│   │   │   │   └── SeguridadConfig.java         # configuración de Spring Security
│   │   │   ├── model/                           # entidades = tablas de la base de datos
│   │   │   │   ├── Administrador.java
│   │   │   │   ├── Cajero.java
│   │   │   │   ├── Categoria.java
│   │   │   │   ├── Producto.java
│   │   │   │   ├── Proveedor.java
│   │   │   │   ├── Lote.java                    # incluye la regla de vencimientos
│   │   │   │   ├── Venta.java
│   │   │   │   └── DetalleVenta.java
│   │   │   ├── repository/                      # acceso a datos (Spring Data JPA)
│   │   │   │   ├── AdministradorRepository.java
│   │   │   │   ├── CajeroRepository.java
│   │   │   │   ├── ProductoRepository.java
│   │   │   │   ├── ProveedorRepository.java
│   │   │   │   ├── LoteRepository.java
│   │   │   │   └── VentaRepository.java
│   │   │   ├── service/                         # lógica de negocio
│   │   │   │   ├── ProductoService.java
│   │   │   │   ├── LoteService.java             # control de vencimientos y alertas
│   │   │   │   └── VentaService.java
│   │   │   └── controller/                      # reciben la petición y eligen la vista
│   │   │       ├── AuthController.java          # inicio de sesión
│   │   │       ├── DashboardController.java
│   │   │       ├── ProductoController.java
│   │   │       ├── LoteController.java
│   │   │       ├── VencimientoController.java   # panel de medicamentos por vencer
│   │   │       └── VentaController.java
│   │   └── resources/
│   │       ├── templates/                       # vistas Thymeleaf (.html)
│   │       │   ├── fragmentos.html              # <head> y menú que se repiten en todas las páginas
│   │       │   ├── login.html
│   │       │   ├── dashboard.html
│   │       │   ├── productos/lista.html
│   │       │   ├── lotes/lista.html
│   │       │   ├── vencimientos/lista.html
│   │       │   └── ventas/lista.html
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── estilos.css
│   │       └── application.properties           # conexión a MySQL
│   └── test/java/com/farmaciaoasis/
│       └── model/LoteTest.java                  # prueba de la regla de vencimientos
├── bd/
│   └── farmacia_oasis.sql                       # script de creación de la base de datos
├── docs/
│   ├── modelo_entidad_relacion.png
│   └── modelo_relacional.png
├── .gitignore
├── pom.xml                                       # dependencias (Maven)
└── README.md
```

## Cómo ejecutar

Se necesita Java 17 o superior, Maven y MySQL 8.

1. Crear la base de datos corriendo el script `bd/farmacia_oasis.sql` en MySQL.
2. El proyecto se conecta a MySQL con el usuario `root` sin contraseña. Si tu MySQL usa otros datos, crea en la raíz del proyecto un archivo `application-local.properties` (no se sube al repositorio) con:

   ```
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_clave
   ```

3. Desde la carpeta del proyecto: `mvn spring-boot:run`.
4. Abrir en el navegador: `http://localhost:8080`.

Para correr las pruebas: `mvn test`.

## Usuarios

- **Administrador:** gestiona productos, lotes y proveedores (registra la mercancía que ingresa).
- **Cajero:** registra las ventas.

El script de la base de datos crea dos usuarios de prueba:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@farmaciaoasis.com` | `admin123` |
| Cajero | `cajero@farmaciaoasis.com` | `cajero123` |

## Integrantes

- Ruben Arrellano Marrugo
- Mayleth Sofia Cabarcas Suárez
- Jady Luz Ramírez Benítez
- Carlos Arturo Sánchez Villarreal
