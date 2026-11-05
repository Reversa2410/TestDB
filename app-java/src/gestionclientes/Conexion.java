package gestionclientes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Requerimiento C: lectura de la cadena de conexion desde conexion.properties
 * y apertura de conexiones contra el SGBD.
 */
public final class Conexion {

    private static final String ARCHIVO = "conexion.properties";

    private static String url;
    private static String usuario;
    private static String clave;

    private Conexion() {
    }

    private static synchronized void cargar() {
        if (url != null) {
            return;
        }

        Properties p = new Properties();

        // Se busca primero junto al programa y, si no esta, dentro del propio jar.
        Path local = Path.of(ARCHIVO);
        try {
            if (Files.exists(local)) {
                try (Reader r = Files.newBufferedReader(local, StandardCharsets.UTF_8)) {
                    p.load(r);
                }
            } else {
                try (InputStream in = Conexion.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
                    if (in == null) {
                        throw new IllegalStateException(
                                "No se encontro " + ARCHIVO + " con la cadena de conexion.");
                    }
                    p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + ARCHIVO, e);
        }

        url = p.getProperty("jdbc.url", "").trim();
        if (url.isEmpty()) {
            throw new IllegalStateException(ARCHIVO + " no contiene la clave 'jdbc.url'.");
        }

        usuario = p.getProperty("jdbc.usuario", "").trim();
        clave = p.getProperty("jdbc.clave", "").trim();
    }

    /** Abre una conexion nueva contra el servidor de base de datos. */
    public static Connection abrir() throws SQLException {
        cargar();
        if (usuario.isEmpty()) {
            // Autenticacion de Windows: las credenciales van dentro de la URL.
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, usuario, clave);
    }

    /** Version segura para mostrar en pantalla (oculta la contrasena). */
    public static String cadenaParaMostrar() {
        cargar();

        StringBuilder sb = new StringBuilder();
        for (String parte : url.split(";")) {
            if (parte.isBlank()) {
                continue;
            }
            String nombre = parte.split("=")[0].trim().toLowerCase();
            sb.append(nombre.equals("password") ? "password=********" : parte.trim()).append(';');
        }

        if (!usuario.isEmpty()) {
            sb.append(" [usuario=").append(usuario).append("; clave=********]");
        }
        return sb.toString();
    }
}
