package org.example.evaluacionaccesodedatos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.evaluacionaccesodedatos.database.DatabaseConnection;
import org.example.evaluacionaccesodedatos.model.Productos;
import java.sql.*;
import java.util.Objects;
import java.math.BigDecimal;

public class ProductoController {
    @FXML
    public TextField txtCodigo;
    @FXML
    public TextField txtNombre;
    @FXML
    public TextField txtPrecio;
    @FXML
    public TableView<Productos> tblProuctos;
    @FXML
    public TextField txtStock;
    @FXML
    public TableColumn<Productos, String> colCodigo;
    @FXML
    public TableColumn<Productos, String> colNombre;
    @FXML
    public TableColumn<Productos,String> colCategoria;
    @FXML
    public TableColumn<Productos,Double> colPrecio;
    @FXML
    public TableColumn<Productos,Integer> colStock;
    @FXML
    public Button btnEliminar;
    @FXML
    public Button btnGuardar;
    @FXML
    public Button btnActualizar;
    @FXML
    public Button btnLimpiar;
    @FXML
    public ComboBox<String> cmbFiltros;
    @FXML
    public CheckBox chkProductosExistentes;
    @FXML
    public ComboBox<String> cmbCategoria;
    @FXML
    public TableColumn<Productos, Integer> colId;

    private final ObservableList<Productos> productos =
            FXCollections.observableArrayList();
    private Productos productoSeleccionado;


    @FXML
    private void initialize(){
        configurarTabla();
        tblProuctos.setItems(productos);
        cmbCategoria.getItems().addAll(
                "Papelería",
                "Tecnología",
                "Accesorios");
        cargarDatos();
        cmbFiltros.getItems().addAll( "B7 - Limpiar filtros","B1 - Buscar cuadernos","B2 - Productos código T00","B3 - Categoría Papelería",
                "B4 - Productos con existencias","B5 - USB + Tecnología + Stock", "B6 - Sin coincidencias");

        tblProuctos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarProducto(actual);
                    }
                });
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String encabezado,
            String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public void guardarFormulario(ActionEvent actionEvent) {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        if (codigoDuplicado(codigo, -1)) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "Producto existente",
                    "Ya existe un producto con ese código."
            );
            return;
        }
        if (!validarFormulario()) {
            return;
        }

        String sql = "INSERT INTO catalogos.producto " +
                "(codigo, nombre, categoria, precio, stock)"+
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim().toUpperCase());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setBigDecimal(4, new BigDecimal(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.executeUpdate();
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro de producto",
                    "Producto guardado",
                    "El producto se registró correctamente."
            );

            cargarDatos();

        } catch (SQLException ex) {
            mostrarError(ex);
        }

    }
    public void eliminarProductos(ActionEvent actionEvent) {

        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Eliminar producto",
                    "Producto no seleccionado",
                    "Seleccione un producto de la tabla."
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar producto");
        confirmacion.setHeaderText("Confirmar eliminación");
        confirmacion.setContentText("¿Desea eliminar permanentemente este producto?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM catalogos.producto WHERE id_producto = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productoSeleccionado.getId());

            int filas = statement.executeUpdate();
            if (filas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Eliminar producto",
                        "Producto eliminado",
                        "El producto fue eliminado permanentemente."
                );

                limpiarFormulario(null);
                cargarDatos();

            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Eliminar producto",
                        "Producto no encontrado",
                        "No se encontró el producto para eliminar."
                );
            }

        } catch (SQLException ex) {
            mostrarError(ex);
        }
    }
    public void limpiarFormulario(ActionEvent actionEvent) {
        txtNombre.clear();
        txtCodigo.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbCategoria.setValue(null);
        productoSeleccionado = null;
        tblProuctos.getSelectionModel().clearSelection();

    }

    public void actualizarRegistro(ActionEvent actionEvent) {

        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Actualizar producto",
                    "Producto no seleccionado",
                    "Seleccione un producto de la tabla."
            );
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        String sql = "UPDATE catalogos.producto SET " +
                "codigo = ?," +
                "nombre = ?, " +
                "categoria = ?, " +
                "precio = ?, " +
                "stock = ? " +
                "WHERE id_producto = ?";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim().toUpperCase());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setBigDecimal(4, new BigDecimal(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.setInt(6, productoSeleccionado.getId());
            int filas = statement.executeUpdate();
            if (filas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Actualizar producto",
                        "Producto actualizado",
                        "Los datos se actualizaron correctamente."
                );
                cargarDatos();
            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Actualizar producto",
                        "Producto no encontrado",
                        "No se encontró un producto activo."
                );
            }

        } catch (SQLException ex) {
            mostrarError(ex);
        }

    }
    @FXML
    public void productosExistentes(ActionEvent actionEvent) {
        aplicarFiltro();
    }
    private boolean codigoDuplicado(String codigo, int idProducto) {

        String sql = "SELECT COUNT(*) FROM catalogos.producto " +
                "WHERE codigo = ? AND id_producto <> ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codigo.trim().toUpperCase());
            statement.setInt(2, idProducto);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            mostrarError(ex);
            return true;
        }

        return false;
    }

    @FXML
    private boolean validarFormulario() {
        String categoria = cmbCategoria.getValue();

        if (categoria == null ||
                !cmbCategoria.getItems().contains(categoria)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Categoría inválida",
                    "Error en la categoría",
                    "Debe seleccionar Papelería, Tecnología o Accesorios."
            );
            return false;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.length() < 3 || nombre.length() > 80) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nombre inválido",
                    "Error en el nombre",
                    "El nombre debe tener entre 3 y 80 caracteres."
            );
            return false;
        }
        String codigo = txtCodigo.getText().trim().toUpperCase();

        if (codigo.isEmpty() || codigo.length() > 10) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código inválido",
                    "Error en el código",
                    "El código debe tener entre 1 y 10 caracteres."
            );
            return false;
        }

        if (txtCodigo.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtStock.getText().trim().isEmpty()
                || cmbCategoria.getValue() == null)
        {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Registro incompleto",
                    "Datos faltantes",
                    "Complete todos los campos obligatorios."
            );
            return false;
        }

        try {
            BigDecimal precio;

            try {
                precio = new BigDecimal(txtPrecio.getText().trim());

                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio inválido",
                            "Precio fuera de rango",
                            "El precio debe ser mayor que cero."
                    );
                    return false;
                }

                if (precio.scale() > 2 ||
                        precio.compareTo(new BigDecimal("99999999.99")) > 0) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio inválido",
                            "Formato o rango incorrecto",
                            "El precio debe tener máximo 2 decimales y no superar C$99,999,999.99."
                    );
                    return false;
                }

            } catch (NumberFormatException ex) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "Formato incorrecto",
                        "Ingrese un precio numérico válido."
                );
                return false;
            }
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (stock < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock inválido",
                        "Cantidad incorrecta",
                        "El stock no puede ser negativo."
                );
                return false;
            }
        }
        catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Dato inválido",
                    "Precio o stock incorrecto",
                    "El precio debe ser numérico y el stock entero.");
            return false;
        }

        return true;

    }

    public void cargarDatos() {
        productos.clear();
        String sql = "SELECT * FROM catalogos.producto";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ){
            while(resultSet.next()){
                Productos productos = new Productos();
                productos.setId(resultSet.getInt("id_producto"));
                productos.setCodigo(resultSet.getString("codigo"));
                productos.setNombre(resultSet.getString("nombre"));
                productos.setCategoria(resultSet.getString("categoria"));
                productos.setPrecio(resultSet.getDouble("precio"));
                productos.setStock(resultSet.getInt("stock"));

                this.productos.add(productos);
            }
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Carga de Datos",
                    "Datos Cargados Correctamente",
                    "Se han cargado los datos de la base de datos correctamente."
            );
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    private void mostrarError(SQLException ex) {
        ex.printStackTrace();

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de base de datos",
                "No se pudo realizar la operación",
                ex.getMessage()
        );
    }
    private void seleccionarProducto(Productos producto) {
        productoSeleccionado = producto;
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));
        cmbCategoria.setValue(producto.getCategoria());
    }

    @FXML
    private void aplicarFiltro(){


        String consultaSeleccionada = cmbFiltros.getValue();
        String sql = "SELECT * FROM catalogos.producto";
        if (Objects.equals(consultaSeleccionada, "B7 - Limpiar filtros")) {
            chkProductosExistentes.setSelected(false);
            cmbFiltros.setValue(null);
            sql = "SELECT * FROM catalogos.producto";
        }
        if(Objects.equals(consultaSeleccionada, "B1 - Buscar cuadernos")) {
            sql = "SELECT * FROM catalogos.producto WHERE nombre  LIKE '%Cuaderno%'";

        }
        if(Objects.equals(consultaSeleccionada, "B2 - Productos código T00")) {
            sql = "SELECT * FROM catalogos.producto WHERE codigo LIKE 'T00%'";

        }
        if(Objects.equals(consultaSeleccionada, "B3 - Categoría Papelería")) {
            sql = "SELECT * FROM catalogos.producto WHERE categoria = 'Papelería'";

        }
        if(Objects.equals(consultaSeleccionada, "B4 - Productos con existencias")) {
            sql = "SELECT * FROM catalogos.producto WHERE stock > 0";
        }
        if (Objects.equals(consultaSeleccionada, "B5 - USB + Tecnología + Stock")) {
            sql = "SELECT * FROM catalogos.producto WHERE nombre ILIKE '%USB%' AND categoria = 'Tecnología' AND stock > 0";
        }
        if(Objects.equals(consultaSeleccionada, "B6 - Sin coincidencias")) {
            sql = "SELECT * FROM catalogos.producto WHERE nombre = 'ZZZ999'";
            mostrarAlerta(Alert.AlertType.INFORMATION, "No se encontraron coincidencias", "No se han encontrado coincidencias para la busqueda", "Que triste");

        }

        if (chkProductosExistentes.isSelected()) {

            if (sql.toUpperCase().contains(" WHERE ")) {
                sql += " AND stock > 0";
            } else {
                sql += " WHERE stock > 0";
            }
        }
        ObservableList<Productos> nuevosProductos = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {

                Productos papeleria = new Productos();
                papeleria.setId(resultSet.getInt("id_producto"));
                papeleria.setCodigo(resultSet.getString("codigo"));
                papeleria.setNombre(resultSet.getString("nombre"));
                papeleria.setCategoria(resultSet.getString("categoria"));
                papeleria.setPrecio(resultSet.getDouble("precio"));
                papeleria.setStock(resultSet.getInt("stock"));
                nuevosProductos.add(papeleria);
            }
            productos.setAll(nuevosProductos);

            System.out.println("Productos cargados: " + productos.size());

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Error al cargar productos",
                    ex.getMessage()
            );
        }
    }
}