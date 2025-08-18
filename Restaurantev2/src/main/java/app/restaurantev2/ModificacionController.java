package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ModificacionController {
    public static int idCuentaEditar = -1;
    public static int idSolicitudEditar = -1; // para marcar la solicitud como editada

    @FXML private BorderPane rootPane;
    @FXML private Label lblTitulo, lblMesa, lblEstado;
    @FXML private TableView<DetalleCuenta> tablaDetallesCuenta;
    @FXML private TableColumn<DetalleCuenta, String> colPlatillo;
    @FXML private TableColumn<DetalleCuenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleCuenta, Double> colPrecio;
    @FXML private Button btnAgregarPlatillo, btnEditarPlatillo, btnEliminarPlatillo, btnEnviarModificacion, btnRegresar;
    @FXML private TableView<Platillo> tablaMenuPlatillos;
    @FXML private TableColumn<Platillo, String> colNombreMenuPlatillo;
    @FXML private TableColumn<Platillo, Double> colPrecioMenuPlatillo;
    @FXML private TableColumn<Platillo, String> colCategoriaMenuPlatillo;

    private MainApp.ConexionBaseDatos db;
    private ObservableList<DetalleCuenta> listaDetalles = FXCollections.observableArrayList();
    private ObservableList<Platillo> listaMenuPlatillos = FXCollections.observableArrayList();

    // Modelo para los detalles de la cuenta
    public static class DetalleCuenta {
        public String platillo;
        public int cantidad;
        public double precio;
        public int idDetalle;
        public DetalleCuenta(int idDetalle, String platillo, int cantidad, double precio) {
            this.idDetalle = idDetalle;
            this.platillo = platillo;
            this.cantidad = cantidad;
            this.precio = precio;
        }
        public String getPlatillo() { return platillo; }
        public int getCantidad() { return cantidad; }
        public double getPrecio() { return precio; }
        public int getIdDetalle() { return idDetalle; }
    }

    // Modelo para los platillos del menú
    public static class Platillo {
        public int idPlatillo;
        public String nombre;
        public double precio;
        public String categoria;
        public Platillo(int idPlatillo, String nombre, double precio, String categoria) {
            this.idPlatillo = idPlatillo;
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }
        public int getIdPlatillo() { return idPlatillo; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public String getCategoria() { return categoria; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();

        colPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlatillo()));
        colCantidad.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        tablaDetallesCuenta.setItems(listaDetalles);

        colNombreMenuPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colPrecioMenuPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        colCategoriaMenuPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategoria()));
        tablaMenuPlatillos.setItems(listaMenuPlatillos);

        cargarDatosCuenta();
        cargarMenuPlatillos();

        btnAgregarPlatillo.setOnAction(e -> agregarPlatilloSeleccionado());
        btnEditarPlatillo.setOnAction(e -> editarPlatilloSeleccionado());
        btnEliminarPlatillo.setOnAction(e -> eliminarPlatilloSeleccionado());
        btnEnviarModificacion.setOnAction(e -> enviarModificacion());
        btnRegresar.setOnAction(e -> regresar());
    }

    private void cargarDatosCuenta() {
        if (idCuentaEditar == -1) {
            mostrarAlerta("Error", "No se recibió el id de cuenta para editar.");
            return;
        }
        String sqlCuenta = "SELECT M.NUMERO_MESA, C.ESTADO FROM CUENTAS C JOIN MESAS M ON C.ID_MESA = M.ID_MESA WHERE C.ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sqlCuenta)) {
            pstmt.setInt(1, idCuentaEditar);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lblMesa.setText("Mesa: " + rs.getInt("NUMERO_MESA"));
                lblEstado.setText("Estado: " + rs.getString("ESTADO"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar datos de la cuenta: " + e.getMessage());
        }

        listaDetalles.clear();
        String sqlDetalle = "SELECT DC.ID_DETALLE, P.NOMBRE AS PLATILLO, DC.CANTIDAD, P.PRECIO " +
                "FROM DETALLES_CUENTA DC JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO " +
                "WHERE DC.ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
            pstmt.setInt(1, idCuentaEditar);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaDetalles.add(new DetalleCuenta(
                        rs.getInt("ID_DETALLE"),
                        rs.getString("PLATILLO"),
                        rs.getInt("CANTIDAD"),
                        rs.getDouble("PRECIO")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar detalles de la cuenta: " + e.getMessage());
        }
    }

    private void cargarMenuPlatillos() {
        listaMenuPlatillos.clear();
        String sql = "SELECT ID_PLATILLO, NOMBRE, PRECIO, (SELECT NOMBRE FROM CATEGORIAS WHERE ID_CATEGORIA = P.ID_CATEGORIA) AS CATEGORIA FROM PLATILLOS P";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaMenuPlatillos.add(new Platillo(
                        rs.getInt("ID_PLATILLO"),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getString("CATEGORIA")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar el menú de platillos: " + e.getMessage());
        }
    }

    private void agregarPlatilloSeleccionado() {
        Platillo platillo = tablaMenuPlatillos.getSelectionModel().getSelectedItem();
        if (platillo == null) {
            mostrarAlerta("Agregar Platillo", "Selecciona un platillo del menú para agregar.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Agregar platillo");
        dialog.setContentText("Cantidad:");
        int cantidad = 1;
        try {
            dialog.showAndWait();
            cantidad = Integer.parseInt(dialog.getEditor().getText());
            if (cantidad < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser un número entero mayor a 0.");
            return;
        }

        String sql = "INSERT INTO DETALLES_CUENTA (ID_CUENTA, ID_PLATILLO, CANTIDAD, NOTAS) VALUES (?, ?, ?, '')";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuentaEditar);
            pstmt.setInt(2, platillo.getIdPlatillo());
            pstmt.setInt(3, cantidad);
            pstmt.executeUpdate();
            cargarDatosCuenta();
            mostrarAlerta("Platillo agregado", "Platillo agregado correctamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el platillo: " + e.getMessage());
        }
    }

    private void editarPlatilloSeleccionado() {
        DetalleCuenta detalle = tablaDetallesCuenta.getSelectionModel().getSelectedItem();
        if (detalle == null) {
            mostrarAlerta("Editar Platillo", "Selecciona un platillo de la cuenta para editar.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(detalle.getCantidad()));
        dialog.setTitle("Editar cantidad");
        dialog.setHeaderText("Editar cantidad de platillo");
        dialog.setContentText("Nueva cantidad:");
        try {
            dialog.showAndWait();
            int nuevaCantidad = Integer.parseInt(dialog.getEditor().getText());
            if (nuevaCantidad < 1) throw new NumberFormatException();
            String sql = "UPDATE DETALLES_CUENTA SET CANTIDAD = ? WHERE ID_DETALLE = ?";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nuevaCantidad);
                pstmt.setInt(2, detalle.getIdDetalle());
                pstmt.executeUpdate();
                cargarDatosCuenta();
                mostrarAlerta("Cantidad editada", "Cantidad editada correctamente.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser mayor que cero.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo editar el platillo: " + e.getMessage());
        }
    }

    private void eliminarPlatilloSeleccionado() {
        DetalleCuenta detalle = tablaDetallesCuenta.getSelectionModel().getSelectedItem();
        if (detalle == null) {
            mostrarAlerta("Eliminar Platillo", "Selecciona un platillo para eliminar.");
            return;
        }
        String sql = "DELETE FROM DETALLES_CUENTA WHERE ID_DETALLE = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detalle.getIdDetalle());
            pstmt.executeUpdate();
            cargarDatosCuenta();
            mostrarAlerta("Platillo eliminado", "Platillo eliminado correctamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar el platillo: " + e.getMessage());
        }
    }

    private void enviarModificacion() {
        // Cambia la cuenta a MODIFICADA y luego ABIERTA
        String sqlModificada = "UPDATE CUENTAS SET ESTADO = 'MODIFICADA' WHERE ID_CUENTA = ?";
        String sqlAbrir = "UPDATE CUENTAS SET ESTADO = 'ABIERTA' WHERE ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt1 = conn.prepareStatement(sqlModificada)) {
            pstmt1.setInt(1, idCuentaEditar);
            pstmt1.executeUpdate();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo marcar la cuenta como MODIFICADA: " + e.getMessage());
            return;
        }
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt2 = conn.prepareStatement(sqlAbrir)) {
            pstmt2.setInt(1, idCuentaEditar);
            pstmt2.executeUpdate();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo reabrir la cuenta: " + e.getMessage());
            return;
        }
        // Marca la solicitud como EDITADA
        if (idSolicitudEditar != -1) {
            String sqlSolicitud = "UPDATE SOLICITUDES_MODIFICACION SET ESTADO = 'EDITADA' WHERE ID_SOLICITUD = ?";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sqlSolicitud)) {
                pstmt.setInt(1, idSolicitudEditar);
                pstmt.executeUpdate();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo marcar la solicitud como EDITADA: " + e.getMessage());
            }
        }
        mostrarAlerta("Modificación enviada", "La cuenta fue modificada, reabierta y la solicitud marcada como editada.");
    }

    private void regresar() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/app/restaurantev2/Mesero.fxml"));
            stage.setScene(new Scene(root, 1600, 900));
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo regresar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}