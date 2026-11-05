using System.Text.Json;

namespace GestionClientes;

/// <summary>
/// Requerimiento C: lectura de la cadena de conexion desde appsettings.json.
/// </summary>
public static class Conexion
{
    private static string? _cadena;

    public static string Cadena
    {
        get
        {
            if (_cadena is not null) return _cadena;

            var ruta = Path.Combine(AppContext.BaseDirectory, "appsettings.json");
            if (!File.Exists(ruta))
                throw new FileNotFoundException("No se encontro appsettings.json con la cadena de conexion.", ruta);

            using var doc = JsonDocument.Parse(File.ReadAllText(ruta));
            if (!doc.RootElement.TryGetProperty("ConnectionString", out var valor))
                throw new InvalidOperationException("appsettings.json no contiene la clave 'ConnectionString'.");

            _cadena = valor.GetString()
                      ?? throw new InvalidOperationException("La cadena de conexion esta vacia.");
            return _cadena;
        }
    }

    /// <summary>Version segura para mostrar en pantalla (oculta la contrasena).</summary>
    public static string CadenaParaMostrar()
    {
        var partes = Cadena.Split(';', StringSplitOptions.RemoveEmptyEntries);
        var visibles = partes.Select(p =>
        {
            var clave = p.Split('=')[0].Trim().ToLowerInvariant();
            return clave is "password" or "pwd" ? clave + "=********" : p.Trim();
        });
        return string.Join(";", visibles) + ";";
    }
}
