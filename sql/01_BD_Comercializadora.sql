/* =========================================================
   Unidad V - Arquitectura Cliente-Servidor
   Script de creacion: BD_Comercializadora (MariaDB)
   ========================================================= */

-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS BD_Comercializadora
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BD_Comercializadora;

-- 2. Crear la tabla Clientes (3. llave primaria y 4. restricciones incluidas)
DROP TABLE IF EXISTS Clientes;

CREATE TABLE Clientes
(
    IdCliente      INT           NOT NULL AUTO_INCREMENT,
    Nombres        VARCHAR(80)   NOT NULL,
    Apellidos      VARCHAR(80)   NOT NULL,
    Identificacion VARCHAR(20)   NOT NULL,
    Telefono       VARCHAR(15)   NOT NULL,
    Correo         VARCHAR(100)  NOT NULL,
    Direccion      VARCHAR(150)  NOT NULL,
    Estado         TINYINT(1)    NOT NULL DEFAULT 1,

    CONSTRAINT PK_Clientes         PRIMARY KEY (IdCliente),
    CONSTRAINT UQ_Clientes_Identif UNIQUE (Identificacion),
    CONSTRAINT UQ_Clientes_Correo  UNIQUE (Correo)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- 5. Insertar registros de prueba (minimo cinco)
INSERT INTO Clientes (Nombres, Apellidos, Identificacion, Telefono, Correo, Direccion)
VALUES
    ('Ana',    'Lopez',    '1001001001', '3001112233', 'ana.lopez@correo.com',    'Calle 10 # 5-20'),
    ('Carlos', 'Ruiz',     '1002002002', '3002223344', 'carlos.ruiz@correo.com',  'Avenida 7 # 45-10'),
    ('Maria',  'Gomez',    '1003003003', '3003334455', 'maria.gomez@correo.com',  'Carrera 15 # 80-30'),
    ('Jorge',  'Martinez', '1004004004', '3004445566', 'jorge.martinez@correo.com','Diagonal 22 # 12-05'),
    ('Lucia',  'Pena',     '1005005005', '3005556677', 'lucia.pena@correo.com',   'Transversal 9 # 33-40');

-- 6. Consulta de verificacion: todos los clientes registrados
SELECT IdCliente,
       Nombres,
       Apellidos,
       Identificacion,
       Telefono,
       Correo,
       Direccion,
       Estado
FROM Clientes
ORDER BY IdCliente;
