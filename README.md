# Unidad V — Gestión de Clientes (Cliente-Servidor)

Implementación de la práctica descrita en `Unidad V.pdf`. Resumen de requisitos en [TAREAS.md](TAREAS.md).

Para reconstruir el proyecto desde cero (pasos, comandos, código completo y errores frecuentes): [GUIA-REPLICACION.txt](GUIA-REPLICACION.txt). La guía conserva la versión original con SQL Server por motivos históricos.

**Stack:** MariaDB 13 + Java 17+ con Swing y el conector JDBC oficial de MariaDB (`mariadb-java-client`, administrado por Maven).

## Estructura

```
sql/01_BD_Comercializadora.sql   Script de BD, tabla, restricciones y 5 registros
app-java/                        Proyecto de la aplicación (Presentation Tier)
  conexion.properties            Cadena de conexión
  compilar.sh / ejecutar.sh      Compilar y ejecutar en Linux/macOS
  compilar.bat / ejecutar.bat    Compilar y ejecutar en Windows
  pom.xml                        Dependencias y configuración de Maven
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

Requisitos: JDK 17 o superior, Maven y MariaDB en marcha.

Crear la base de datos y cargar los 5 registros de prueba:

```bash
mariadb -u root -p < "sql/01_BD_Comercializadora.sql"
```

Revisar `app-java/conexion.properties` y ajustar usuario y contraseña:

```properties
jdbc.url=jdbc:mariadb://localhost:3306/BD_Comercializadora
jdbc.usuario=root
jdbc.clave=TuClave
```

Compilar y ejecutar:

```bash
cd app-java && ./compilar.sh
```

```bash
cd app-java && ./ejecutar.sh
```

Prueba de conexión por consola, sin abrir la ventana:

```bash
cd app-java && ./ejecutar.sh --prueba
```

También se puede usar Maven directamente:

```bash
cd app-java
mvn compile
mvn exec:java -Dexec.args="--prueba"
mvn exec:java
```

En Windows, usar `compilar.bat` y `ejecutar.bat`.

## Cadena de conexión

Está en `app-java/conexion.properties`, clave `jdbc.url`. El archivo se lee en tiempo de ejecución, así que se puede cambiar de servidor sin recompilar.

```
jdbc:mariadb://localhost:3306/BD_Comercializadora
```

El usuario y la contraseña se leen de `jdbc.usuario` y `jdbc.clave`. La contraseña nunca se muestra en pantalla: `Conexion.cadenaParaMostrar()` la enmascara antes de incluirla en los mensajes de error.

> No subir al repositorio una contraseña real. Si se va a publicar el proyecto, dejar un valor de ejemplo y configurar la credencial localmente.

## Cobertura de la rúbrica

| Requisito | Dónde |
|---|---|
| BD, tabla, PK, restricciones, 5 registros, consulta | `sql/01_BD_Comercializadora.sql` |
| Ventana "Gestión de Clientes" | `VentanaClientes.java` |
| Botón Guardar (INSERT) | `VentanaClientes.guardar()` → `ClientesRepositorio.guardar()` |
| Botón Consultar (SELECT) | `VentanaClientes.consultar()` → `ClientesRepositorio.consultar()` |
| Resultados en grilla | `JTable` con un `AbstractTableModel` sobre `List<Cliente>` |
| Validación de obligatorios | `VentanaClientes.validar()` |
| Mensajes de éxito y error | `JOptionPane` + barra de estado inferior |
| Cadena de conexión | `conexion.properties` + `Conexion.java` |
| Prueba de conexión | `ClientesRepositorio.probarConexion()`, al abrir la ventana y en `--prueba` |

## Evidencias

| Archivo | Contenido |
|---|---|
| `evidencias/app-funcionando.png` | Aplicación abierta, conectada y listando clientes |
| `evidencias/prueba-conexion.txt` | Cadena usada, servidor/BD, inserción y conteo antes/después |
| `evidencias/verificacion-sgbd.txt` | `SELECT` ejecutado directamente en el SGBD con el registro creado desde la app |

El registro `Prueba / Consola` que aparece en esas salidas lo insertó la aplicación y se confirmó después consultando el SGBD por separado: es la evidencia del punto D de la práctica.

## Notas de implementación

- Todas las consultas usan `PreparedStatement` con parámetros, no concatenación de cadenas.
- Las columnas de texto son `VARCHAR` y `Estado` es `TINYINT(1)`, equivalente al `BIT` de SQL Server.
- El `IdCliente` generado se recupera con `getGeneratedKeys()`, y es lo que la app muestra al guardar.
- La violación de `UNIQUE` en `Identificacion` o `Correo` (error 1062 de MariaDB) se traduce a un mensaje legible en vez de mostrar la excepción del SGBD.
- Las longitudes máximas de los campos de texto coinciden con las de las columnas de la tabla.
- No se implementan UPDATE, DELETE, autenticación ni API REST: la práctica los excluye expresamente.

## Historial

La primera versión de esta práctica se hizo en C# con WinForms; después se portó a Java con SQL Server y finalmente se migró a MariaDB. Las versiones anteriores están en el historial de Git.
