package gestionclientes;

import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {

    public static void main(String[] args) {
        // Modo de verificacion por consola: java ... gestionclientes.Main --prueba
        // Ejecuta prueba de conexion + insercion + consulta sin abrir la ventana.
        if (args.length > 0 && (args[0].equals("--prueba") || args[0].equals("--selftest"))) {
            System.exit(pruebaConsola());
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorado) {
            // Si el aspecto del sistema no esta disponible se usa el de Java.
        }

        SwingUtilities.invokeLater(() -> new VentanaClientes().setVisible(true));
    }

    private static int pruebaConsola() {
        ClientesRepositorio repo = new ClientesRepositorio();

        try {
            System.out.println("Cadena de conexion : " + Conexion.cadenaParaMostrar());
            System.out.println("Servidor / BD      : " + repo.probarConexion());

            int antes = repo.consultar().size();
            System.out.println("Clientes antes     : " + antes);

            String marca = String.valueOf(System.currentTimeMillis() % 1000000000L);

            Cliente c = new Cliente();
            c.setNombres("Prueba");
            c.setApellidos("Consola");
            c.setIdentificacion("T" + marca);
            c.setTelefono("3000000000");
            c.setCorreo("prueba" + marca + "@correo.com");
            c.setDireccion("Registro creado por la prueba automatica");
            c.setEstado(true);

            int id = repo.guardar(c);
            System.out.println("Insertado IdCliente: " + id);

            List<Cliente> despues = repo.consultar();
            System.out.println("Clientes despues   : " + despues.size());

            boolean ok = despues.size() == antes + 1
                    && despues.stream().anyMatch(x -> x.getId() == id);

            System.out.println(ok ? "RESULTADO: OK" : "RESULTADO: FALLO");
            return ok ? 0 : 1;

        } catch (Exception ex) {
            System.out.println("RESULTADO: FALLO - " + ex.getMessage());
            return 1;
        }
    }
}
