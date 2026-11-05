using Microsoft.Data.SqlClient;

namespace GestionClientes;

/// <summary>
/// Acceso a datos: unico punto donde la aplicacion habla con el SGBD.
/// </summary>
public class ClientesRepositorio
{
    /// <summary>Prueba de conexion (Requerimiento C, actividad 6).</summary>
    public string ProbarConexion()
    {
        using var cn = new SqlConnection(Conexion.Cadena);
        cn.Open();
        using var cmd = new SqlCommand("SELECT @@SERVERNAME + ' / ' + DB_NAME();", cn);
        return cmd.ExecuteScalar()?.ToString() ?? "(desconocido)";
    }

    /// <summary>Consulta todos los clientes registrados.</summary>
    public List<Cliente> Consultar()
    {
        const string sql = @"
            SELECT IdCliente, Nombres, Apellidos, Identificacion,
                   Telefono, Correo, Direccion, Estado
            FROM dbo.Clientes
            ORDER BY IdCliente;";

        var lista = new List<Cliente>();

        using var cn = new SqlConnection(Conexion.Cadena);
        cn.Open();
        using var cmd = new SqlCommand(sql, cn);
        using var lector = cmd.ExecuteReader();

        while (lector.Read())
        {
            lista.Add(new Cliente
            {
                Id             = lector.GetInt32(0),
                Nombres        = lector.GetString(1),
                Apellidos      = lector.GetString(2),
                Identificacion = lector.GetString(3),
                Telefono       = lector.GetString(4),
                Correo         = lector.GetString(5),
                Direccion      = lector.GetString(6),
                Estado         = lector.GetBoolean(7)
            });
        }

        return lista;
    }

    /// <summary>Inserta un cliente y devuelve el IdCliente generado.</summary>
    public int Guardar(Cliente c)
    {
        const string sql = @"
            INSERT INTO dbo.Clientes
                (Nombres, Apellidos, Identificacion, Telefono, Correo, Direccion, Estado)
            VALUES
                (@Nombres, @Apellidos, @Identificacion, @Telefono, @Correo, @Direccion, @Estado);
            SELECT CAST(SCOPE_IDENTITY() AS INT);";

        using var cn = new SqlConnection(Conexion.Cadena);
        cn.Open();
        using var cmd = new SqlCommand(sql, cn);

        // Parametros: evitan inyeccion SQL y respetan los tipos de la tabla.
        cmd.Parameters.Add("@Nombres",        System.Data.SqlDbType.VarChar, 80).Value  = c.Nombres;
        cmd.Parameters.Add("@Apellidos",      System.Data.SqlDbType.VarChar, 80).Value  = c.Apellidos;
        cmd.Parameters.Add("@Identificacion", System.Data.SqlDbType.VarChar, 20).Value  = c.Identificacion;
        cmd.Parameters.Add("@Telefono",       System.Data.SqlDbType.VarChar, 15).Value  = c.Telefono;
        cmd.Parameters.Add("@Correo",         System.Data.SqlDbType.VarChar, 100).Value = c.Correo;
        cmd.Parameters.Add("@Direccion",      System.Data.SqlDbType.VarChar, 150).Value = c.Direccion;
        cmd.Parameters.Add("@Estado",         System.Data.SqlDbType.Bit).Value          = c.Estado;

        return Convert.ToInt32(cmd.ExecuteScalar());
    }
}
