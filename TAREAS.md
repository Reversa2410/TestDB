# Unidad V — Qué hay que hacer (resumen operativo)

**Objetivo:** app cliente-servidor mínima: formulario que inserta y lista clientes desde un SGBD real.
**Tiempo:** 180 min. **Total:** 100 pts.

## 1. Base de datos (30 pts)
BD: `BD_Comercializadora`. Tabla `Clientes`:

| Campo | Tipo | Restricción |
|---|---|---|
| IdCliente | INT | PK, autoincremental |
| Nombres | VARCHAR(80) | NOT NULL |
| Apellidos | VARCHAR(80) | NOT NULL |
| Identificacion | VARCHAR(20) | UNIQUE |
| Telefono | VARCHAR(15) | NOT NULL |
| Correo | VARCHAR(100) | UNIQUE |
| Direccion | VARCHAR(150) | NOT NULL |
| Estado | BIT/BOOLEAN | DEFAULT 1 (activo) |

Tareas: crear BD → crear tabla → PK → restricciones → insertar ≥5 registros de prueba → `SELECT * FROM Clientes`.

## 2. App / Presentation Tier (35 pts)
Formulario único: **"Gestión de Clientes"**.
- Campos de entrada para los 8 datos del cliente.
- Botón **Guardar** (INSERT).
- Botón **Consultar** (SELECT → tabla/grilla).
- Validar que los obligatorios no estén vacíos.
- Mensajes de éxito y de error.

## 3. Cadena de conexión (25 pts)
Configurada dentro de la app, con servidor + puerto + BD + usuario + contraseña. Debe incluir prueba de conexión.

```
SQL Server: Server=localhost;Database=BD_Comercializadora;User Id=sa;Password=TuClave;TrustServerCertificate=True;
MySQL:      Server=localhost;Port=3306;Database=BD_Comercializadora;Uid=root;Pwd=TuClave;
```

## 4. Pruebas (10 pts)
Abrir app → conexión OK → registrar cliente nuevo desde la UI → consultar listado → verificar el registro directamente en el SGBD.

## Entregables
1. Script SQL (BD + tabla).
2. Proyecto de la app.
3. Evidencia de la cadena de conexión.
4. Captura de la app funcionando.
5. Evidencia del registro hecho desde la app y guardado en la BD.

## NO hace falta
UPDATE/DELETE · login · API REST / servicios web · arquitectura en capas · procedimientos almacenados · lógica de negocio.
Sí se permiten componentes visuales y asistentes del IDE. La conexión debe ser real, no simulada.

## Rúbrica
BD 20 · campos y restricciones 10 · interfaz 15 · registro y consulta 20 · cadena de conexión 15 · comunicación app↔SGBD 10 · pruebas 10.
