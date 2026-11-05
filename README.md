# Unidad V — Gestión de Clientes (Cliente-Servidor)

Implementación de la práctica descrita en `Unidad V.pdf`. Resumen de requisitos en [TAREAS.md](TAREAS.md).

Para reconstruir el proyecto desde cero (pasos, comandos, código completo y errores frecuentes): [GUIA-REPLICACION.txt](GUIA-REPLICACION.txt).

**Stack:** SQL Server + Java 17+ con Swing y el driver JDBC oficial de Microsoft (`mssql-jdbc`).

## Estructura

```
sql/01_BD_Comercializadora.sql   Script de BD, tabla, restricciones y 5 registros
app-java/                        Proyecto de la aplicación (Presentation Tier)
  conexion.properties            Cadena de conexión
  compilar.bat / ejecutar.bat    Compilar y ejecutar sin Maven ni IDE
  pom.xml                        Alternativa para quien use Maven o un IDE
  lib/                           Driver JDBC y DLL de autenticación de Windows
  src/gestionclientes/
    Cliente.java                 Modelo de una fila de la tabla
    Conexion.java                Lectura de la cadena de conexión
    ClientesRepositorio.java     Acceso a datos: probar / consultar / guardar
    VentanaClientes.java         Ventana "Gestión de Clientes"
    LimiteTexto.java             Limita la longitud de los campos de texto
    Main.java                    Arranque + modo de prueba por consola
evidencias/                      Capturas y salidas para la entrega
```

## Puesta en marcha

Crear la base de datos:

```bash
sqlcmd -S localhost -E -C -i "sql/01_BD_Comercializadora.sql"
```

Compilar y ejecutar (doble clic también funciona):

```bash
cd app-java && compilar.bat
```

```bash
cd app-java && ejecutar.bat
```

Prueba de conexión por consola, sin abrir la ventana:

```bash
cd app-java && ejecutar.bat --prueba
```

## Requisito del servidor: TCP/IP

El driver JDBC **solo habla TCP/IP**. SQL Server trae ese protocolo deshabilitado por defecto en muchas instalaciones, y entonces la app falla con *"No se pudo realizar la conexión TCP/IP al host localhost, puerto 1433"*.

Para habilitarlo: Administrador de configuración de SQL Server → Configuración de red de SQL Server → Protocolos de *(instancia)* → TCP/IP → Habilitado = Sí, y reiniciar el servicio SQL Server.

En esta máquina ya quedó habilitado y escuchando en el puerto 1433.

## Cadena de conexión

Está en `app-java/conexion.properties`, clave `jdbc.url`. El archivo se lee en tiempo de ejecución, así que se puede cambiar de servidor sin recompilar.

```
jdbc:sqlserver://localhost;databaseName=BD_Comercializadora;integratedSecurity=true;encrypt=true;trustServerCertificate=true
```

El mismo archivo incluye comentada la variante con usuario y contraseña de SQL Server, por si el laboratorio entrega credenciales `sa`:

```
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=BD_Comercializadora;encrypt=true;trustServerCertificate=true
jdbc.usuario=sa
jdbc.clave=TuClave
```

La contraseña nunca se muestra en pantalla: `Conexion.cadenaParaMostrar()` la enmascara antes de incluirla en los mensajes de error.

La autenticación de Windows necesita `mssql-jdbc_auth-13.6.2.x64.dll`, que está en `lib/` y que `ejecutar.bat` pasa a la JVM con `-Djava.library.path`. Con usuario y contraseña esa DLL no hace falta.

## Cobertura de la rúbrica

| Requisito | Dónde |
|---|---|
| BD, tabla, PK, restricciones, 5 registros, consulta | `sql/01_BD_Comercializadora.sql` |
| Ventana "Gestión de Clientes" | `VentanaClientes.java` |
| Botón Guardar (INSERT) | `VentanaClientes.guardar()` → `ClientesRepositorio.guardar()` |
| Botón Consultar (SELECT) | `VentanaClientes.consultar()` → `ClientesRepositorio.consultar()` |
| Botón Mostrar / Ocultar tabla | `VentanaClientes.alternarTabla()` |
| Resultados en grilla | `JTable` con un `AbstractTableModel` sobre `List<Cliente>` |
| Validación de obligatorios | `VentanaClientes.validar()` |
| Mensajes de éxito y error | `JOptionPane` + barra de estado inferior |
| Cadena de conexión | `conexion.properties` + `Conexion.java` |
| Prueba de conexión | `ClientesRepositorio.probarConexion()`, al abrir la ventana y en `--prueba` |

## Evidencias

| Archivo | Contenido |
|---|---|
| `evidencias/app-inicio-sin-tabla.png` | Estado al abrir: conectada, con el listado aún oculto |
| `evidencias/app-funcionando.png` | Tras pulsar "Mostrar tabla": los clientes recuperados de la BD |
| `evidencias/prueba-conexion.txt` | Cadena usada, servidor/BD, inserción y conteo antes/después |
| `evidencias/verificacion-sgbd.txt` | `SELECT` ejecutado directamente en el SGBD con el registro creado desde la app |

El registro `Prueba / Consola` que aparece en esas salidas lo insertó la aplicación y se confirmó después consultando el SGBD por separado: es la evidencia del punto D de la práctica. Se eliminó al terminar para dejar la tabla con los 5 registros del script.

## Notas de implementación

- Todas las consultas usan `PreparedStatement` con parámetros, no concatenación de cadenas.
- La URL lleva `sendStringParametersAsUnicode=false`. Sin eso el driver envía cada `String` como `nvarchar(4000)`, lo que obliga a SQL Server a una conversión implícita contra las columnas `varchar` y, en un `WHERE`, impide aprovechar el índice. Con el ajuste los parámetros viajan como `varchar`, verificable en `sys.dm_exec_cached_plans`.
- El `IdCliente` generado se recupera con `getGeneratedKeys()`, y es lo que la app muestra al guardar.
- La violación de `UNIQUE` en `Identificacion` o `Correo` (errores 2601/2627) se traduce a un mensaje legible en vez de mostrar la excepción de SQL Server.
- Las longitudes máximas de los campos de texto coinciden con las de las columnas de la tabla.
- El listado no se carga al abrir la ventana: aparece al pulsar "Mostrar tabla" o "Consultar", y el mismo botón lo vuelve a ocultar. La zona central usa un `CardLayout` que alterna entre un aviso y la grilla. Guardar un cliente solo refresca la grilla si ya estaba visible.
- Al ejecutar con JDK 22 o superior aparecen avisos `WARNING ... restricted method`: son del JDK al cargar la DLL de autenticación y no afectan al funcionamiento.
- No se implementan UPDATE, DELETE, autenticación ni API REST: la práctica los excluye expresamente.

## Historial

La primera versión de esta práctica se hizo en C# con WinForms; está en el commit `84e22dd` por si hiciera falta consultarla. El script SQL no cambió al pasar a Java.
