namespace GestionClientes;

/// <summary>Representa una fila de la tabla Clientes.</summary>
public class Cliente
{
    public int Id { get; set; }
    public string Nombres { get; set; } = "";
    public string Apellidos { get; set; } = "";
    public string Identificacion { get; set; } = "";
    public string Telefono { get; set; } = "";
    public string Correo { get; set; } = "";
    public string Direccion { get; set; } = "";
    public bool Estado { get; set; } = true;
}
