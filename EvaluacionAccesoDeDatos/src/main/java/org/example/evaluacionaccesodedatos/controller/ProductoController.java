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
import java.time.LocalDate;

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
        cmbCategoria.getItems().addAll("Papeleria","Tecnologia","Accesorios");
        cargarDatos();

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
        if (!validarFormulario()) {
            return;
        }

        String sql = "INSERT INTO catalogos.producto " +
                "(codigo, nombre, categoria, precio, stock)"+
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
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

        String sql = "DELETE FROM catalogos.producto WHERE codigo = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, productoSeleccionado.getCodigo());

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
                "WHERE codigo = ?";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.setString(6, productoSeleccionado.getCodigo());
            int filas = statement.executeUpdate();
            statement.executeUpdate();
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro de producto",
                    "Producto guardado",
                    "El producto se registró correctamente."
            );


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

    public void productosExistentes(ActionEvent actionEvent) {

    }

    @FXML
    private boolean validarFormulario() {

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
            double precio = Double.parseDouble(
                    txtPrecio.getText().trim());

            int stock = Integer.parseInt(
                    txtStock.getText().trim());

            if (precio < 0 || stock < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Dato inválido",
                        "Valores negativos",
                        "El precio y el stock no pueden ser negativos."
                );
                return false;
            }

        } catch (NumberFormatException ex) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Dato inválido",
                    "Precio o stock incorrecto",
                    "El precio debe ser numérico y el stock entero."
            );
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

    private void filtro1(){
        productos.clear();
        String sql = "SELECT * FROM cataglogs.productos where nombre like '%Lenovo%'";

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
}
