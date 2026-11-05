# Unidad V — Gestión de Clientes (Cliente-Servidor)

Implementación de la práctica descrita en `Unidad V.pdf`. Resumen de requisitos en [TAREAS.md](TAREAS.md).

Para reconstruir el proyecto desde cero (pasos, comandos, código completo y errores frecuentes): [GUIA-REPLICACION.txt](GUIA-REPLICACION.txt).

**Stack:** SQL Server 2025 (local) + C# WinForms sobre .NET 10, con `Microsoft.Data.SqlClient`.

## Estructura

```
sql/01_BD_Comercializadora.sql   Script de BD, tabla, restricciones y 5 registros
app/GestionClientes/             Proyecto de la aplicación (Presentation Tier)
  appsettings.json               Cadena de conexión
  Conexion.cs                    Lectura de la cadena de conexión
  ClientesRepositorio.cs         Acceso a datos: probar / consultar / guardar
  FormClientes.cs                Formulario "Gestión de Clientes"
  Program.cs                     Arranque + modo de prueba por consola
evidencias/                      Capturas y salidas para la entrega
```

## Puesta en marcha

Crear la base de datos:

```bash
sqlcmd -S localhost -E -C -i "sql/01_BD_Comercializadora.sql"
```

Ejecutar la aplicación:

```bash
dotnet run --project app/GestionClientes
```

Prueba de conexión por consola (sin abrir la ventana):

```bash
dotnet run --project app/GestionClientes -- --prueba
```

## Cadena de conexión

Está en `app/GestionClientes/appsettings.json`, clave `ConnectionString`. Se copia al directorio de salida en cada compilación, así que se puede cambiar de servidor sin recompilar.

```
Server=localhost;Database=BD_Comercializadora;Integrated Security=True;TrustServerCertificate=True;
```

El mismo archivo incluye, en `ConnectionStringSqlAuth`, la variante con usuario y contraseña de SQL Server por si el laboratorio entrega credenciales `sa`:

```
Server=localhost;Database=BD_Comercializadora;User Id=sa;Password=TuClave;TrustServerCertificate=True;
```

Para usarla basta con intercambiar el valor de las dos claves. La contraseña nunca se muestra en pantalla: `Conexion.CadenaParaMostrar()` la enmascara antes de incluirla en los mensajes de error.

## Cobertura de la rúbrica

| Requisito | Dónde |
|---|---|
| BD, tabla, PK, restricciones, 5 registros, consulta | `sql/01_BD_Comercializadora.sql` |
| Formulario "Gestión de Clientes" | `FormClientes.cs` |
| Botón Guardar (INSERT) | `FormClientes.Guardar()` → `ClientesRepositorio.Guardar()` |
| Botón Consultar (SELECT) | `FormClientes.Consultar()` → `ClientesRepositorio.Consultar()` |
| Resultados en grilla | `DataGridView` enlazado a `List<Cliente>` |
| Validación de obligatorios | `FormClientes.Validar()` |
| Mensajes de éxito y error | `MessageBox` + barra de estado inferior |
| Cadena de conexión | `appsettings.json` + `Conexion.cs` |
| Prueba de conexión | `ClientesRepositorio.ProbarConexion()`, al abrir el formulario y en `--prueba` |

## Evidencias

| Archivo | Contenido |
|---|---|
| `evidencias/app-funcionando.png` | Aplicación abierta, conectada y listando clientes |
| `evidencias/prueba-conexion.txt` | Cadena usada, servidor/BD, inserción y conteo antes/después |
| `evidencias/verificacion-sgbd.txt` | `SELECT` ejecutado directamente en el SGBD con los registros creados desde la app |

Los registros `Prueba / Consola` de la tabla fueron insertados por la aplicación y confirmados después consultando el SGBD por separado: son la evidencia del punto D de la práctica.

## Notas de implementación

- Todas las consultas usan parámetros tipados, no concatenación de cadenas.
- La violación de `UNIQUE` en `Identificacion` o `Correo` (errores 2601/2627) se traduce a un mensaje legible en vez de mostrar la excepción de SQL Server.
- Las longitudes máximas de los cuadros de texto coinciden con las de las columnas de la tabla.
- No se implementan UPDATE, DELETE, autenticación ni API REST: la práctica los excluye expresamente.
