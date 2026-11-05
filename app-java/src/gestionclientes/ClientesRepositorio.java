package gestionclientes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos: unico punto donde la aplicacion habla con el SGBD.
 */
public class ClientesRepositorio {

    /** Codigos de SQL Server para violacion de restriccion UNIQUE. */
    public static boolean esDuplicado(SQLException e) {
        return e.getErrorCode() == 2601 || e.getErrorCode() == 2627;
    }

    /** Prueba de conexion (Requerimiento C, actividad 6). */
    public String probarConexion() throws SQLException {
        String sql = "SELECT @@SERVERNAME + ' / ' + DB_NAME();";
        try (Connection cn = Conexion.abrir();
             PreparedStatement cmd = cn.prepareStatement(sql);
             ResultSet r = cmd.executeQuery()) {

            return r.next() ? r.getString(1) : "(desconocido)";
        }
    }

    /** Consulta todos los clientes registrados. */
    public List<Cliente> consultar() throws SQLException {
        String sql = """
                SELECT IdCliente, Nombres, Apellidos, Identificacion,
                       Telefono, Correo, Direccion, Estado
                FROM dbo.Clientes
                ORDER BY IdCliente""";

        List<Cliente> lista = new ArrayList<>();

        try (Connection cn = Conexion.abrir();
             PreparedStatement cmd = cn.prepareStatement(sql);
             ResultSet r = cmd.executeQuery()) {

            while (r.next()) {
                Cliente c = new Cliente();
                c.setId(r.getInt(1));
                c.setNombres(r.getString(2));
                c.setApellidos(r.getString(3));
                c.setIdentificacion(r.getString(4));
                c.setTelefono(r.getString(5));
                c.setCorreo(r.getString(6));
                c.setDireccion(r.getString(7));
                c.setEstado(r.getBoolean(8));
                lista.add(c);
            }
        }

        return lista;
    }

    /** Inserta un cliente y devuelve el IdCliente generado. */
    public int guardar(Cliente c) throws SQLException {
        String sql = """
                INSERT INTO dbo.Clientes
                    (Nombres, Apellidos, Identificacion, Telefono, Correo, Direccion, Estado)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?)""";

        try (Connection cn = Conexion.abrir();
             PreparedStatement cmd = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Parametros: evitan inyeccion SQL y respetan los tipos de la tabla.
            cmd.setString(1, c.getNombres());
            cmd.setString(2, c.getApellidos());
            cmd.setString(3, c.getIdentificacion());
            cmd.setString(4, c.getTelefono());
            cmd.setString(5, c.getCorreo());
            cmd.setString(6, c.getDireccion());
            cmd.setObject(7, c.isEstado(), Types.BIT);

            cmd.executeUpdate();

            try (ResultSet claves = cmd.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        }
    }
}
