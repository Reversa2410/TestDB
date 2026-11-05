package gestionclientes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * Presentation Tier (Requerimiento B): ventana "Gestion de Clientes".
 */
public class VentanaClientes extends JFrame {

    private final ClientesRepositorio repo = new ClientesRepositorio();

    // Campos de entrada
    private final JTextField txtNombres = new JTextField();
    private final JTextField txtApellidos = new JTextField();
    private final JTextField txtIdentificacion = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JTextField txtDireccion = new JTextField();
    private final JCheckBox chkEstado = new JCheckBox("Activo", true);

    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnConsultar = new JButton("Consultar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final ModeloClientes modelo = new ModeloClientes();
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblEstado = new JLabel();

    public VentanaClientes() {
        setTitle("Gestion de Clientes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 640);
        setMinimumSize(new Dimension(820, 540));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.add(construirPanelDatos());
        arriba.add(Box.createVerticalStrut(8));
        arriba.add(construirPanelBotones());

        raiz.add(arriba, BorderLayout.NORTH);
        raiz.add(construirTabla(), BorderLayout.CENTER);
        raiz.add(construirBarraEstado(), BorderLayout.SOUTH);
        setContentPane(raiz);

        btnGuardar.addActionListener(e -> guardar());
        btnConsultar.addActionListener(e -> consultar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        getRootPane().setDefaultButton(btnGuardar);

        probarConexionInicial();
        consultar();
    }

    // ---------- Interfaz ----------

    private JPanel construirPanelDatos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));

        // Longitudes alineadas con las de la tabla Clientes
        limitar(txtNombres, 80);
        limitar(txtApellidos, 80);
        limitar(txtIdentificacion, 20);
        limitar(txtTelefono, 15);
        limitar(txtCorreo, 100);
        limitar(txtDireccion, 150);

        fila(panel, 0, "Nombres *", txtNombres, "Apellidos *", txtApellidos);
        fila(panel, 1, "Identificacion *", txtIdentificacion, "Telefono *", txtTelefono);
        fila(panel, 2, "Correo *", txtCorreo, "Direccion *", txtDireccion);
        fila(panel, 3, "Estado", chkEstado, null, null);

        return panel;
    }

    private static void limitar(JTextField campo, int maximo) {
        campo.setDocument(new LimiteTexto(maximo));
    }

    private void fila(JPanel panel, int fila,
                      String etiqueta1, java.awt.Component campo1,
                      String etiqueta2, java.awt.Component campo2) {

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridy = fila;

        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(etiqueta1), g);

        g.gridx = 1;
        g.weightx = 0.5;
        g.fill = GridBagConstraints.HORIZONTAL;
        campo1.setPreferredSize(new Dimension(300, 26));
        panel.add(campo1, g);

        if (etiqueta2 == null || campo2 == null) {
            return;
        }

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta2), g);

        g.gridx = 3;
        g.weightx = 0.5;
        g.fill = GridBagConstraints.HORIZONTAL;
        campo2.setPreferredSize(new Dimension(300, 26));
        panel.add(campo2, g);
    }

    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        for (JButton b : new JButton[] { btnGuardar, btnConsultar, btnLimpiar }) {
            b.setPreferredSize(new Dimension(120, 32));
            panel.add(b);
        }

        btnGuardar.setBackground(new Color(0, 120, 212));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true);
        btnGuardar.setBorderPainted(false);

        return panel;
    }

    private JScrollPane construirTabla() {
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(tabla.getFont().deriveFont(Font.BOLD));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Listado de clientes"));
        return scroll;
    }

    private JLabel construirBarraEstado() {
        lblEstado.setHorizontalAlignment(SwingConstants.LEFT);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
        return lblEstado;
    }

    private void ajustarAnchos() {
        if (tabla.getColumnCount() == 0) {
            return;
        }
        int[] pesos = { 40, 130, 130, 120, 110, 180, 180, 70 };
        for (int i = 0; i < pesos.length && i < tabla.getColumnCount(); i++) {
            TableColumn col = tabla.getColumnModel().getColumn(i);
            col.setPreferredWidth(pesos[i]);
        }
    }

    // ---------- Acciones ----------

    private void probarConexionInicial() {
        try {
            String servidor = repo.probarConexion();
            mensaje("Conexion establecida con " + servidor, true);
        } catch (Exception ex) {
            mensaje("Sin conexion con el servidor de base de datos.", false);
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al SGBD.\n\n" + ex.getMessage()
                            + "\n\nCadena usada:\n" + Conexion.cadenaParaMostrar(),
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultar() {
        try {
            List<Cliente> clientes = repo.consultar();
            modelo.reemplazar(clientes);
            ajustarAnchos();
            mensaje(clientes.size() + " cliente(s) recuperado(s) desde la base de datos.", true);
        } catch (Exception ex) {
            mensaje("Error al consultar los clientes.", false);
            JOptionPane.showMessageDialog(this,
                    "Error al consultar:\n\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardar() {
        String faltante = validar();
        if (faltante != null) {
            mensaje("El campo '" + faltante + "' es obligatorio.", false);
            JOptionPane.showMessageDialog(this,
                    "El campo '" + faltante + "' es obligatorio.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNombres(txtNombres.getText().trim());
        cliente.setApellidos(txtApellidos.getText().trim());
        cliente.setIdentificacion(txtIdentificacion.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setCorreo(txtCorreo.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setEstado(chkEstado.isSelected());

        try {
            int id = repo.guardar(cliente);
            mensaje("Cliente guardado correctamente con IdCliente = " + id + ".", true);
            JOptionPane.showMessageDialog(this,
                    "Registro guardado correctamente.\nIdCliente asignado: " + id,
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            consultar();
        } catch (SQLException ex) {
            if (ClientesRepositorio.esDuplicado(ex)) {
                // Violacion de UNIQUE en Identificacion o Correo
                mensaje("Ya existe un cliente con esa identificacion o correo.", false);
                JOptionPane.showMessageDialog(this,
                        "Ya existe un cliente registrado con esa identificacion o ese correo.\n"
                                + "Ambos campos deben ser unicos.",
                        "Registro duplicado", JOptionPane.WARNING_MESSAGE);
            } else {
                mensaje("Error al guardar el registro.", false);
                JOptionPane.showMessageDialog(this,
                        "No se pudo guardar el registro.\n\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Devuelve el nombre del primer campo obligatorio vacio, o null si todo esta completo. */
    private String validar() {
        Object[][] obligatorios = {
            { "Nombres", txtNombres },
            { "Apellidos", txtApellidos },
            { "Identificacion", txtIdentificacion },
            { "Telefono", txtTelefono },
            { "Correo", txtCorreo },
            { "Direccion", txtDireccion }
        };

        for (Object[] par : obligatorios) {
            JTextField campo = (JTextField) par[1];
            if (campo.getText().trim().isEmpty()) {
                campo.requestFocusInWindow();
                return (String) par[0];
            }
        }
        return null;
    }

    private void limpiarFormulario() {
        for (JTextField t : new JTextField[] { txtNombres, txtApellidos, txtIdentificacion,
                                               txtTelefono, txtCorreo, txtDireccion }) {
            t.setText("");
        }
        chkEstado.setSelected(true);
        txtNombres.requestFocusInWindow();
    }

    private void mensaje(String texto, boolean correcto) {
        lblEstado.setText((correcto ? "OK  -  " : "ERROR  -  ") + texto);
        lblEstado.setForeground(correcto ? new Color(0, 110, 60) : new Color(170, 0, 0));
    }

    // ---------- Modelo de la tabla ----------

    /** Adapta la lista de clientes a lo que la JTable sabe pintar. */
    private static class ModeloClientes extends AbstractTableModel {

        private static final String[] COLUMNAS = {
            "ID", "Nombres", "Apellidos", "Identificacion",
            "Telefono", "Correo", "Direccion", "Estado"
        };

        private List<Cliente> datos = new ArrayList<>();

        void reemplazar(List<Cliente> nuevos) {
            datos = nuevos;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return datos.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNAS.length;
        }

        @Override
        public String getColumnName(int col) {
            return COLUMNAS[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return switch (col) {
                case 0 -> Integer.class;
                case 7 -> Boolean.class;
                default -> String.class;
            };
        }

        @Override
        public Object getValueAt(int fila, int col) {
            Cliente c = datos.get(fila);
            return switch (col) {
                case 0 -> c.getId();
                case 1 -> c.getNombres();
                case 2 -> c.getApellidos();
                case 3 -> c.getIdentificacion();
                case 4 -> c.getTelefono();
                case 5 -> c.getCorreo();
                case 6 -> c.getDireccion();
                case 7 -> c.isEstado();
                default -> "";
            };
        }
    }
}
