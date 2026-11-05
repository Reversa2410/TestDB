using System.ComponentModel;

namespace GestionClientes;

/// <summary>
/// Presentation Tier (Requerimiento B): formulario "Gestion de Clientes".
/// </summary>
public class FormClientes : Form
{
    private readonly ClientesRepositorio _repo = new();

    // Campos de entrada
    private readonly TextBox _txtNombres        = new();
    private readonly TextBox _txtApellidos      = new();
    private readonly TextBox _txtIdentificacion = new();
    private readonly TextBox _txtTelefono       = new();
    private readonly TextBox _txtCorreo         = new();
    private readonly TextBox _txtDireccion      = new();
    private readonly CheckBox _chkEstado        = new() { Text = "Activo", Checked = true };

    private readonly Button _btnGuardar   = new() { Text = "Guardar" };
    private readonly Button _btnConsultar = new() { Text = "Consultar" };
    private readonly Button _btnLimpiar   = new() { Text = "Limpiar" };

    private readonly DataGridView _grid = new();
    private readonly Label _lblEstado   = new();

    public FormClientes()
    {
        Text = "Gestion de Clientes";
        Size = new Size(1020, 640);
        MinimumSize = new Size(820, 540);
        StartPosition = FormStartPosition.CenterScreen;
        Font = new Font("Segoe UI", 9F);

        Controls.Add(ConstruirLayout());

        _btnGuardar.Click   += (_, _) => Guardar();
        _btnConsultar.Click += (_, _) => Consultar();
        _btnLimpiar.Click   += (_, _) => LimpiarFormulario();

        Load += (_, _) => { ProbarConexionInicial(); Consultar(); };
    }

    // ---------- Interfaz ----------

    private Control ConstruirLayout()
    {
        var raiz = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            Padding = new Padding(12),
            ColumnCount = 1,
            RowCount = 4
        };
        raiz.RowStyles.Add(new RowStyle(SizeType.AutoSize));      // datos
        raiz.RowStyles.Add(new RowStyle(SizeType.AutoSize));      // botones
        raiz.RowStyles.Add(new RowStyle(SizeType.Percent, 100));  // grilla
        raiz.RowStyles.Add(new RowStyle(SizeType.AutoSize));      // estado

        raiz.Controls.Add(ConstruirPanelDatos(), 0, 0);
        raiz.Controls.Add(ConstruirPanelBotones(), 0, 1);
        raiz.Controls.Add(ConstruirGrilla(), 0, 2);
        raiz.Controls.Add(ConstruirBarraEstado(), 0, 3);
        return raiz;
    }

    private Control ConstruirPanelDatos()
    {
        var grupo = new GroupBox
        {
            Text = "Datos del cliente",
            Dock = DockStyle.Top,
            AutoSize = true,
            AutoSizeMode = AutoSizeMode.GrowAndShrink,
            Padding = new Padding(10)
        };

        var tabla = new TableLayoutPanel
        {
            Dock = DockStyle.Top,
            AutoSize = true,
            ColumnCount = 4,
            RowCount = 4
        };
        tabla.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 135));
        tabla.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
        tabla.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 110));
        tabla.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));

        void Fila(int fila, string etiqueta1, Control campo1, string? etiqueta2, Control? campo2)
        {
            tabla.Controls.Add(new Label
            {
                Text = etiqueta1,
                AutoSize = true,
                Anchor = AnchorStyles.Left,
                Margin = new Padding(3, 8, 3, 3)
            }, 0, fila);

            campo1.Dock = DockStyle.Fill;
            campo1.Margin = new Padding(3, 4, 12, 4);
            tabla.Controls.Add(campo1, 1, fila);

            if (etiqueta2 is null || campo2 is null) return;

            tabla.Controls.Add(new Label
            {
                Text = etiqueta2,
                AutoSize = true,
                Anchor = AnchorStyles.Left,
                Margin = new Padding(3, 8, 3, 3)
            }, 2, fila);

            campo2.Dock = DockStyle.Fill;
            campo2.Margin = new Padding(3, 4, 3, 4);
            tabla.Controls.Add(campo2, 3, fila);
        }

        // Longitudes alineadas con las de la tabla Clientes
        _txtNombres.MaxLength        = 80;
        _txtApellidos.MaxLength      = 80;
        _txtIdentificacion.MaxLength = 20;
        _txtTelefono.MaxLength       = 15;
        _txtCorreo.MaxLength         = 100;
        _txtDireccion.MaxLength      = 150;

        Fila(0, "Nombres *",        _txtNombres,        "Apellidos *", _txtApellidos);
        Fila(1, "Identificacion *", _txtIdentificacion, "Telefono *",  _txtTelefono);
        Fila(2, "Correo *",         _txtCorreo,         "Direccion *", _txtDireccion);
        Fila(3, "Estado",           _chkEstado,         null,          null);

        grupo.Controls.Add(tabla);
        return grupo;
    }

    private Control ConstruirPanelBotones()
    {
        var panel = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            AutoSize = true,
            FlowDirection = FlowDirection.LeftToRight,
            Padding = new Padding(0, 8, 0, 8)
        };

        foreach (var b in new[] { _btnGuardar, _btnConsultar, _btnLimpiar })
        {
            b.AutoSize = false;
            b.Size = new Size(110, 32);
            b.Margin = new Padding(0, 0, 8, 0);
            panel.Controls.Add(b);
        }

        _btnGuardar.BackColor = Color.FromArgb(0, 120, 212);
        _btnGuardar.ForeColor = Color.White;
        _btnGuardar.FlatStyle = FlatStyle.Flat;

        return panel;
    }

    private Control ConstruirGrilla()
    {
        _grid.Dock = DockStyle.Fill;
        _grid.ReadOnly = true;
        _grid.AllowUserToAddRows = false;
        _grid.AllowUserToDeleteRows = false;
        _grid.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
        _grid.MultiSelect = false;
        _grid.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
        _grid.RowHeadersVisible = false;
        _grid.BackgroundColor = Color.White;
        _grid.BorderStyle = BorderStyle.FixedSingle;
        _grid.EnableHeadersVisualStyles = false;
        _grid.ColumnHeadersDefaultCellStyle.Font = new Font("Segoe UI", 9F, FontStyle.Bold);
        _grid.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(243, 243, 243);
        _grid.AlternatingRowsDefaultCellStyle.BackColor = Color.FromArgb(250, 250, 250);

        var grupo = new GroupBox
        {
            Text = "Listado de clientes",
            Dock = DockStyle.Fill,
            Padding = new Padding(10)
        };
        grupo.Controls.Add(_grid);
        return grupo;
    }

    private Control ConstruirBarraEstado()
    {
        _lblEstado.Dock = DockStyle.Top;
        _lblEstado.AutoSize = false;
        _lblEstado.Height = 24;
        _lblEstado.TextAlign = ContentAlignment.MiddleLeft;
        _lblEstado.ForeColor = Color.DimGray;
        return _lblEstado;
    }

    // ---------- Acciones ----------

    private void ProbarConexionInicial()
    {
        try
        {
            var servidor = _repo.ProbarConexion();
            Mensaje($"Conexion establecida con {servidor}", correcto: true);
        }
        catch (Exception ex)
        {
            Mensaje("Sin conexion con el servidor de base de datos.", correcto: false);
            MessageBox.Show(
                $"No se pudo conectar al SGBD.\n\n{ex.Message}\n\nCadena usada:\n{Conexion.CadenaParaMostrar()}",
                "Error de conexion", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void Consultar()
    {
        try
        {
            var clientes = _repo.Consultar();
            _grid.DataSource = new BindingList<Cliente>(clientes);
            FormatearColumnas();
            Mensaje($"{clientes.Count} cliente(s) recuperado(s) desde la base de datos.", correcto: true);
        }
        catch (Exception ex)
        {
            Mensaje("Error al consultar los clientes.", correcto: false);
            MessageBox.Show($"Error al consultar:\n\n{ex.Message}", "Error",
                MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void Guardar()
    {
        if (!Validar(out var faltante))
        {
            Mensaje($"El campo '{faltante}' es obligatorio.", correcto: false);
            MessageBox.Show($"El campo '{faltante}' es obligatorio.", "Validacion",
                MessageBoxButtons.OK, MessageBoxIcon.Warning);
            return;
        }

        var cliente = new Cliente
        {
            Nombres        = _txtNombres.Text.Trim(),
            Apellidos      = _txtApellidos.Text.Trim(),
            Identificacion = _txtIdentificacion.Text.Trim(),
            Telefono       = _txtTelefono.Text.Trim(),
            Correo         = _txtCorreo.Text.Trim(),
            Direccion      = _txtDireccion.Text.Trim(),
            Estado         = _chkEstado.Checked
        };

        try
        {
            var id = _repo.Guardar(cliente);
            Mensaje($"Cliente guardado correctamente con IdCliente = {id}.", correcto: true);
            MessageBox.Show($"Registro guardado correctamente.\nIdCliente asignado: {id}",
                "Exito", MessageBoxButtons.OK, MessageBoxIcon.Information);
            LimpiarFormulario();
            Consultar();
        }
        catch (Microsoft.Data.SqlClient.SqlException ex) when (ex.Number is 2601 or 2627)
        {
            // Violacion de UNIQUE en Identificacion o Correo
            Mensaje("Ya existe un cliente con esa identificacion o correo.", correcto: false);
            MessageBox.Show(
                "Ya existe un cliente registrado con esa identificacion o ese correo.\n" +
                "Ambos campos deben ser unicos.",
                "Registro duplicado", MessageBoxButtons.OK, MessageBoxIcon.Warning);
        }
        catch (Exception ex)
        {
            Mensaje("Error al guardar el registro.", correcto: false);
            MessageBox.Show($"No se pudo guardar el registro.\n\n{ex.Message}", "Error",
                MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private bool Validar(out string faltante)
    {
        var obligatorios = new (string Etiqueta, TextBox Campo)[]
        {
            ("Nombres",        _txtNombres),
            ("Apellidos",      _txtApellidos),
            ("Identificacion", _txtIdentificacion),
            ("Telefono",       _txtTelefono),
            ("Correo",         _txtCorreo),
            ("Direccion",      _txtDireccion)
        };

        foreach (var (etiqueta, campo) in obligatorios)
        {
            if (string.IsNullOrWhiteSpace(campo.Text))
            {
                faltante = etiqueta;
                campo.Focus();
                return false;
            }
        }

        faltante = "";
        return true;
    }

    private void LimpiarFormulario()
    {
        foreach (var t in new[] { _txtNombres, _txtApellidos, _txtIdentificacion,
                                  _txtTelefono, _txtCorreo, _txtDireccion })
            t.Clear();

        _chkEstado.Checked = true;
        _txtNombres.Focus();
    }

    private void FormatearColumnas()
    {
        if (_grid.Columns.Count == 0) return;

        var titulos = new Dictionary<string, string>
        {
            ["Id"]             = "ID",
            ["Nombres"]        = "Nombres",
            ["Apellidos"]      = "Apellidos",
            ["Identificacion"] = "Identificacion",
            ["Telefono"]       = "Telefono",
            ["Correo"]         = "Correo",
            ["Direccion"]      = "Direccion",
            ["Estado"]         = "Estado"
        };

        foreach (DataGridViewColumn col in _grid.Columns)
            if (titulos.TryGetValue(col.Name, out var titulo))
                col.HeaderText = titulo;

        _grid.Columns["Id"]!.FillWeight = 28;
        _grid.Columns["Estado"]!.FillWeight = 45;
        _grid.Columns["Telefono"]!.FillWeight = 78;
        _grid.Columns["Identificacion"]!.FillWeight = 85;
    }

    private void Mensaje(string texto, bool correcto)
    {
        _lblEstado.Text = (correcto ? "OK  -  " : "ERROR  -  ") + texto;
        _lblEstado.ForeColor = correcto ? Color.FromArgb(0, 110, 60) : Color.FromArgb(170, 0, 0);
    }
}
