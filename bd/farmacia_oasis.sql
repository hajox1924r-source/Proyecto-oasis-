-- =====================================================================
-- Base de datos: Aplicativo web para la Farmacia Oasis (MySQL 8)
-- Enfoque: control de medicamentos vencidos y notificaciones de vencimiento.
-- La venta/facturación queda en segundo plano (mínima).
-- =====================================================================

DROP DATABASE IF EXISTS farmacia_oasis;
CREATE DATABASE farmacia_oasis;
USE farmacia_oasis;

-- ---------------------------------------------------------------------
-- Usuarios: solo hay dos tipos, cada uno con su propia tabla
--   administrador -> registra los lotes que ingresan
--   cajero        -> registra las ventas
-- ---------------------------------------------------------------------
CREATE TABLE administrador (
    id_administrador INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    correo           VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash  VARCHAR(255) NOT NULL,            -- la genera el aplicativo (BCrypt)
    activo           BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE cajero (
    id_cajero        INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    correo           VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash  VARCHAR(255) NOT NULL,            -- la genera el aplicativo (BCrypt)
    activo           BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Medicamentos
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(60) NOT NULL UNIQUE           -- Analgésicos, Antibióticos...
) ENGINE=InnoDB;

CREATE TABLE producto (
    id_producto   INT AUTO_INCREMENT PRIMARY KEY,
    codigo        VARCHAR(30)  NOT NULL UNIQUE,
    nombre        VARCHAR(100) NOT NULL,
    presentacion  VARCHAR(60),                          -- Tableta 500 mg x 10, Jarabe 120 ml...
    precio_venta  DECIMAL(10,2) NOT NULL CHECK (precio_venta >= 0),
    id_categoria  INT NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE=InnoDB;

CREATE TABLE proveedor (
    id_proveedor  INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    nit           VARCHAR(20) UNIQUE,
    telefono      VARCHAR(20),
    correo        VARCHAR(100)
) ENGINE=InnoDB;

-- La fecha de vencimiento va en el LOTE: un mismo medicamento puede
-- tener varios lotes que vencen en fechas distintas.
CREATE TABLE lote (
    id_lote            INT AUTO_INCREMENT PRIMARY KEY,
    numero_lote        VARCHAR(30) NOT NULL,
    id_producto        INT NOT NULL,
    id_proveedor       INT NOT NULL,
    id_administrador   INT NOT NULL,                       -- quién registró el ingreso del lote
    fecha_ingreso      DATE NOT NULL,
    fecha_vencimiento  DATE NOT NULL,
    cantidad           INT NOT NULL CHECK (cantidad >= 0),   -- unidades disponibles del lote
    precio_compra      DECIMAL(10,2) NOT NULL CHECK (precio_compra >= 0),
    CONSTRAINT uq_lote_producto UNIQUE (id_producto, numero_lote),
    CONSTRAINT ck_lote_fechas CHECK (fecha_vencimiento > fecha_ingreso),
    CONSTRAINT fk_lote_producto  FOREIGN KEY (id_producto)  REFERENCES producto(id_producto),
    CONSTRAINT fk_lote_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    CONSTRAINT fk_lote_administrador FOREIGN KEY (id_administrador) REFERENCES administrador(id_administrador),
    INDEX idx_lote_vencimiento (fecha_vencimiento)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Ventas (mínimo, en segundo plano)
-- ---------------------------------------------------------------------
CREATE TABLE venta (
    id_venta    INT AUTO_INCREMENT PRIMARY KEY,
    fecha       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total       DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    id_cajero   INT NOT NULL,                           -- quién registró la venta
    CONSTRAINT fk_venta_cajero FOREIGN KEY (id_cajero) REFERENCES cajero(id_cajero)
) ENGINE=InnoDB;

-- Se vende desde un lote concreto: así se descuenta del lote que vence primero (PEPS/FIFO).
CREATE TABLE detalle_venta (
    id_detalle       INT AUTO_INCREMENT PRIMARY KEY,
    id_venta         INT NOT NULL,
    id_lote          INT NOT NULL,
    cantidad         INT NOT NULL CHECK (cantidad > 0),
    precio_unitario  DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal         DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_lote  FOREIGN KEY (id_lote)  REFERENCES lote(id_lote)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Vista de vencimientos: base de las notificaciones y de la vista gráfica.
-- "Próximo a vencer" = 30 días o menos (se puede cambiar).
-- ---------------------------------------------------------------------
CREATE VIEW vista_vencimientos AS
SELECT  l.id_lote,
        p.codigo,
        p.nombre                                   AS producto,
        l.numero_lote,
        l.cantidad,
        l.fecha_vencimiento,
        DATEDIFF(l.fecha_vencimiento, CURDATE())   AS dias_restantes,
        CASE
            WHEN l.fecha_vencimiento <  CURDATE()                          THEN 'VENCIDO'
            WHEN l.fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 'PROXIMO A VENCER'
            ELSE 'VIGENTE'
        END                                        AS estado
FROM lote l
JOIN producto p ON p.id_producto = l.id_producto
WHERE l.cantidad > 0;

-- =====================================================================
-- Datos de ejemplo (las fechas se calculan desde hoy para que la vista
-- siempre muestre los tres estados)
-- =====================================================================
INSERT INTO administrador (nombre, correo, contrasena_hash) VALUES
 ('Administrador Farmacia Oasis', 'admin@farmaciaoasis.com', '$2a$10$F6CEXbvSxwKnTOf0Zp.lPuZS6nqS0fw4VEMG08TorW/fcFgHOd89u');  -- clave de prueba: admin123

INSERT INTO cajero (nombre, correo, contrasena_hash) VALUES
 ('Cajero de mostrador', 'cajero@farmaciaoasis.com', '$2a$10$MNQwIZeP5FWpviHAZu3.ruxZ4UvK3jaPVjxpeoyn0jCZdYx40AoBi');  -- clave de prueba: cajero123

INSERT INTO categoria (nombre) VALUES ('Analgésicos'), ('Antibióticos'), ('Antialérgicos'), ('Hidratación');

INSERT INTO producto (codigo, nombre, presentacion, precio_venta, id_categoria) VALUES
 ('MED-001', 'Acetaminofén',  'Tableta 500 mg x 10',  3500, 1),
 ('MED-002', 'Ibuprofeno',    'Tableta 400 mg x 10',  4500, 1),
 ('MED-003', 'Amoxicilina',   'Cápsula 500 mg x 10', 12000, 2),
 ('MED-004', 'Loratadina',    'Tableta 10 mg x 10',   5000, 3),
 ('MED-005', 'Suero oral',    'Sobre x 1',            2500, 4);

INSERT INTO proveedor (nombre, nit, telefono, correo) VALUES
 ('Distribuidora Ejemplo S.A.S.', '900000001-1', '6050000001', 'ventas@distribuidoraejemplo.com'),
 ('Droguería Mayorista Ejemplo',  '900000002-2', '6050000002', 'pedidos@mayoristaejemplo.com');

INSERT INTO lote (numero_lote, id_producto, id_proveedor, id_administrador, fecha_ingreso, fecha_vencimiento, cantidad, precio_compra) VALUES
 ('A-1001', 1, 1, 1, DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 8,  2000),  -- vencido
 ('A-1002', 1, 1, 1, DATE_SUB(CURDATE(), INTERVAL 20 DAY),  DATE_ADD(CURDATE(), INTERVAL 400 DAY), 40, 2000),  -- vigente
 ('B-2001', 2, 2, 1, DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 12, 2800),  -- próximo a vencer
 ('C-3001', 3, 1, 1, DATE_SUB(CURDATE(), INTERVAL 60 DAY),  DATE_ADD(CURDATE(), INTERVAL 25 DAY), 6,  8000),  -- próximo a vencer
 ('D-4001', 4, 2, 1, DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_ADD(CURDATE(), INTERVAL 250 DAY), 30, 3000),  -- vigente
 ('E-5001', 5, 2, 1, DATE_SUB(CURDATE(), INTERVAL 400 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY),   5,  1200);  -- vencido

INSERT INTO venta (total, id_cajero) VALUES (11500, 1);
INSERT INTO detalle_venta (id_venta, id_lote, cantidad, precio_unitario, subtotal) VALUES
 (1, 3, 1, 4500, 4500),
 (1, 2, 2, 3500, 7000);

-- Ejemplo de consulta para las notificaciones:
-- SELECT * FROM vista_vencimientos WHERE estado <> 'VIGENTE' ORDER BY dias_restantes;
