using System.Runtime.InteropServices;

namespace GestionClientes;

internal static class Program
{
    // Una aplicacion WinExe no tiene consola propia: la tomamos prestada
    // de la ventana de comandos que la invoco para el modo --prueba.
    [DllImport("kernel32.dll")]
    private static extern bool AttachConsole(int dwProcessId);

    [STAThread]
    private static int Main(string[] args)
    {
        // Modo de verificacion por consola: GestionClientes.exe --prueba
        // Ejecuta prueba de conexion + insercion + consulta sin abrir la ventana.
        if (args.Length > 0 && (args[0] == "--prueba" || args[0] == "--selftest"))
            return PruebaConsola();

        ApplicationConfiguration.Initialize();
        Application.Run(new FormClientes());
        return 0;
    }

    private static int PruebaConsola()
    {
        AttachConsole(-1); // -1 = ATTACH_PARENT_PROCESS
        var repo = new ClientesRepositorio();

        try
        {
            Console.WriteLine("Cadena de conexion : " + Conexion.CadenaParaMostrar());
            Console.WriteLine("Servidor / BD      : " + repo.ProbarConexion());

            var antes = repo.Consultar().Count;
            Console.WriteLine($"Clientes antes     : {antes}");

            var marca = DateTime.Now.ToString("HHmmssfff");
            var id = repo.Guardar(new Cliente
            {
                Nombres        = "Prueba",
                Apellidos      = "Consola",
                Identificacion = "T" + marca,
                Telefono       = "3000000000",
                Correo         = $"prueba{marca}@correo.com",
                Direccion      = "Registro creado por la prueba automatica",
                Estado         = true
            });
            Console.WriteLine($"Insertado IdCliente: {id}");

            var despues = repo.Consultar();
            Console.WriteLine($"Clientes despues   : {despues.Count}");

            var ok = despues.Count == antes + 1 && despues.Any(c => c.Id == id);
            Console.WriteLine(ok ? "RESULTADO: OK" : "RESULTADO: FALLO");
            return ok ? 0 : 1;
        }
        catch (Exception ex)
        {
            Console.WriteLine("RESULTADO: FALLO - " + ex.Message);
            return 1;
        }
    }
}
