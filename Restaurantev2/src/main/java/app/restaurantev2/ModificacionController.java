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

    @FXML private BorderPane rootPane;
    @FXML private Label lblTitulo, lblMesa, lblEstado;
    @FXML private TableView<DetalleCuenta> tablaDetallesCuenta;
    @FXML private TableColumn<DetalleCuenta, String> colPlatillo;
    @FXML private TableColumn<DetalleCuenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleCuenta, Double> colPrecio;
    @FXML private Button btnAgregarPlatillo, btnEditarPlatillo, btnEliminarPlatillo, btnEnviarModificacion, btnRegresar;

    private MainApp.ConexionBaseDatos db;
    private ObservableList<DetalleCuenta> listaDetalles = FXCollections.observableArrayList();

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

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();

        colPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlatillo()));
        colCantidad.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());

        tablaDetallesCuenta.setItems(listaDetalles);

        cargarDatosCuenta();

        btnAgregarPlatillo.setOnAction(e -> agregarPlatillo());
        btnEditarPlatillo.setOnAction(e -> editarPlatillo());
        btnEliminarPlatillo.setOnAction(e -> eliminarPlatillo());
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

    private void agregarPlatillo() {
        mostrarAlerta("Agregar Platillo", "Funcionalidad de agregar platillo pendiente de implementación.");
    }

    private void editarPlatillo() {
        mostrarAlerta("Editar Platillo", "Funcionalidad de editar platillo pendiente de implementación.");
    }

    private void eliminarPlatillo() {
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
        mostrarAlerta("Enviar Modificación", "Funcionalidad de enviar modificación pendiente de implementación.");
    }

    private void regresar() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("Mesero.fxml"));
            stage.setScene(new Scene(root, 1600, 900));
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo regresar: " + ex.getMessage());
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