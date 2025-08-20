package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MeseroCuentasController {

    @FXML private BorderPane rootPane;
    @FXML private ComboBox<Integer> comboCuentaActiva;
    @FXML private TableView<DetalleCuenta> tablaDetallesCuenta;
    @FXML private TableColumn<DetalleCuenta, Integer> colMesaCuenta;
    @FXML private TableColumn<DetalleCuenta, String> colPlatilloCuenta;
    @FXML private TableColumn<DetalleCuenta, Double> colPrecioPlatilloCuenta;
    @FXML private TextArea mostrarTotalCuenta;
    @FXML private Button btnCalcularTotal;
    @FXML private Button btnPagarCuenta;

    @FXML private Button btnHorariolaboral;
    @FXML private Button btnMesasasignadas;
    @FXML private Button btnTomarorden;
    @FXML private Button btnSalir;

    @FXML private Label lblTitulo, lblTitulo1;

    // NUEVOS PARA SOLICITUD MODIFICACION
    @FXML private TextArea txtMotivo;
    @FXML private Button btnSolicitarEdicion;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Integer> listaCuentas = FXCollections.observableArrayList();
    public ObservableList<DetalleCuenta> listaDetalles = FXCollections.observableArrayList();

    private int idMesero;

    // Modelo DetalleCuenta
    public static class DetalleCuenta {
        public int mesa;
        public String platillo;
        public double precio;
        public DetalleCuenta(int mesa, String platillo, double precio) {
            this.mesa = mesa;
            this.platillo = platillo;
            this.precio = precio;
        }
        public int getMesa() { return mesa; }
        public String getPlatillo() { return platillo; }
        public double getPrecio() { return precio; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();
        idMesero = LoginController.currentUserId;

        colMesaCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesa()).asObject());
        colPlatilloCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlatillo()));
        colPrecioPlatilloCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());

        tablaDetallesCuenta.setItems(listaDetalles);
        comboCuentaActiva.setItems(listaCuentas);

        cargarCuentasActivas();

        comboCuentaActiva.setOnAction(e -> cargarDetallesCuenta());
        btnCalcularTotal.setOnAction(e -> calcularTotal());
        btnPagarCuenta.setOnAction(e -> pagarCuenta());

        // NUEVO: Solicitar modificación
        if (btnSolicitarEdicion != null) {
            btnSolicitarEdicion.setOnAction(e -> solicitarModificacion());
        }

        // Navegación menú lateral
        btnHorariolaboral.setOnAction(e -> cambiarPantalla("Mesero.fxml"));
        btnMesasasignadas.setOnAction(e -> cambiarPantalla("Mesero_NuevaOrden.fxml"));
        btnTomarorden.setOnAction(e -> cambiarPantalla("Mesero_Cuentas.fxml"));
        btnSalir.setOnAction(e -> salir());
    }

    private void cargarCuentasActivas() {
        listaCuentas.clear();
        String sql = "SELECT C.ID_CUENTA FROM CUENTAS C WHERE C.ID_MESERO = ? AND C.ESTADO = 'ABIERTA'";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaCuentas.add(rs.getInt("ID_CUENTA"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar cuentas: " + e.getMessage());
        }
    }

    private void cargarDetallesCuenta() {
        listaDetalles.clear();
        Integer idCuenta = comboCuentaActiva.getValue();
        if (idCuenta == null) return;
        String sql = "SELECT M.NUMERO_MESA, P.NOMBRE, P.PRECIO " +
                "FROM DETALLES_CUENTA DC " +
                "JOIN CUENTAS C ON DC.ID_CUENTA = C.ID_CUENTA " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO " +
                "WHERE DC.ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuenta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaDetalles.add(new DetalleCuenta(
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar detalles: " + e.getMessage());
        }
    }

    private void calcularTotal() {
        double total = 0;
        for (DetalleCuenta detalle : listaDetalles) {
            total += detalle.getPrecio();
        }
        mostrarTotalCuenta.setText("Total: $" + total);
    }

    private void pagarCuenta() {
        Integer idCuenta = comboCuentaActiva.getValue();
        if (idCuenta == null) {
            mostrarAlerta("Selecciona una cuenta activa.");
            return;
        }
        String sqlCerrarCuenta = "UPDATE CUENTAS SET ESTADO = 'CERRADA' WHERE ID_CUENTA = ?";
        String sqlLiberarMesa = "UPDATE MESAS SET ESTADO = 'DISPONIBLE' WHERE ID_MESA = (SELECT ID_MESA FROM CUENTAS WHERE ID_CUENTA = ?)";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmtCerrar = conn.prepareStatement(sqlCerrarCuenta);
             PreparedStatement pstmtLiberar = conn.prepareStatement(sqlLiberarMesa)) {
            // Cerrar cuenta
            pstmtCerrar.setInt(1, idCuenta);
            pstmtCerrar.executeUpdate();

            // Liberar mesa
            pstmtLiberar.setInt(1, idCuenta);
            pstmtLiberar.executeUpdate();

            mostrarAlerta("Cuenta pagada y cerrada correctamente. La mesa ahora está disponible.");
            cargarCuentasActivas();
            listaDetalles.clear();
            mostrarTotalCuenta.clear();

            // Mostrar ventana de calificación
            mostrarVentanaCalificacion(idMesero, idCuenta);
        } catch (Exception e) {
            mostrarAlerta("Error al pagar cuenta: " + e.getMessage());
        }
    }

    // NUEVO: Solicitar Modificación
    private void solicitarModificacion() {
        Integer idCuenta = comboCuentaActiva.getValue();
        String motivo = txtMotivo.getText();
        if (idCuenta == null || motivo == null || motivo.trim().isEmpty()) {
            mostrarAlerta("Faltan datos", "Selecciona una cuenta y escribe el motivo.");
            return;
        }
        String sql = "INSERT INTO SOLICITUDES_MODIFICACION (ID_CUENTA, ID_MESERO, MOTIVO, ESTADO) VALUES (?, ?, ?, 'PENDIENTE')";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuenta);
            pstmt.setInt(2, idMesero);
            pstmt.setString(3, motivo);
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Solicitud enviada correctamente.");
            txtMotivo.clear();
            comboCuentaActiva.setValue(null);
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo enviar la solicitud: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(String contenido) {
        mostrarAlerta("Error", contenido);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // Navegación
    private void cambiarPantalla(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) btnSalir.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            mostrarAlerta("Error al cambiar pantalla: " + e.getMessage());
        }
    }

    private void salir() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }

    // Ventana emergente de calificación
    private void mostrarVentanaCalificacion(int idMesero, int idCuenta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CalificacionMesero.fxml"));
            Parent root = loader.load();

            CalificacionMeseroController controller = loader.getController();
            controller.inicializarDatos(idMesero, idCuenta);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Califica al Mesero");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al mostrar calificación: " + e.getMessage());
        }
    }
}