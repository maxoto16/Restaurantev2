package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MeseroCuentasController {

    @FXML private BorderPane rootPane;
    @FXML private Button btnHorariolaboral, btnMesasasignadas, btnTomarorden, btnSalir;
    @FXML private Button btnSolicitarEdicion, btnCalcularTotal, btnPagarCuenta;
    @FXML private ImageView imagenlogo;
    @FXML private Label lblTitulo1, lblTitulo;
    @FXML private ComboBox<Integer> comboMesaConCUENTAACTIVA;
    @FXML private ComboBox<Integer> comboCuentaActiva;
    @FXML private TextArea txtMotivo, mostrarTotalCuenta;
    @FXML private TableView<DetalleCuenta> tablaDetallesCuenta;
    @FXML private TableColumn<DetalleCuenta, Integer> colMesaCuenta;
    @FXML private TableColumn<DetalleCuenta, String> colPlatilloCuenta;
    @FXML private TableColumn<DetalleCuenta, Double> colPrecioPlatilloCuenta;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<DetalleCuenta> listaDetallesCuenta = FXCollections.observableArrayList();

    public static class DetalleCuenta {
        private Integer mesa;
        private String platillo;
        private Double precio;
        public DetalleCuenta(Integer mesa, String platillo, Double precio) {
            this.mesa = mesa;
            this.platillo = platillo;
            this.precio = precio;
        }
        public Integer getMesa() { return mesa; }
        public String getPlatillo() { return platillo; }
        public Double getPrecio() { return precio; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();

        colMesaCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesa()).asObject());
        colPlatilloCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlatillo()));
        colPrecioPlatilloCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        tablaDetallesCuenta.setItems(listaDetallesCuenta);

        cargarMesasConCuentaActiva();

        comboMesaConCUENTAACTIVA.setOnAction(e -> cargarCuentasActivasPorMesa());
        comboCuentaActiva.setOnAction(e -> actualizarDetallesCuentaYTotal());

        btnSolicitarEdicion.setOnAction(e -> enviarSolicitudModificacion());
        btnCalcularTotal.setOnAction(e -> calcularTotalCuenta());
        btnPagarCuenta.setOnAction(e -> pagarCuentaYLiberarMesaYCalificar());
        btnSalir.setOnAction(e -> ((Stage)btnSalir.getScene().getWindow()).close());

        btnHorariolaboral.setOnAction(e -> navegar("/app/restaurantev2/Mesero.fxml"));
        btnMesasasignadas.setOnAction(e -> navegar("/app/restaurantev2/Mesero_NuevaOrden.fxml"));
        btnTomarorden.setOnAction(e -> navegar("/app/restaurantev2/Mesero_Cuentas.fxml"));
    }

    private void navegar(String fxmlPath) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root, 1600, 900));
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al cambiar pantalla: " + ex.getMessage());
        }
    }

    private void cargarMesasConCuentaActiva() {
        comboMesaConCUENTAACTIVA.getItems().clear();
        String sql = "SELECT DISTINCT M.NUMERO_MESA " +
                "FROM MESAS M " +
                "JOIN CUENTAS C ON M.ID_MESA = C.ID_MESA " +
                "WHERE C.ESTADO = 'ABIERTA' ORDER BY M.NUMERO_MESA";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comboMesaConCUENTAACTIVA.getItems().add(rs.getInt("NUMERO_MESA"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar mesas: " + e.getMessage());
        }
        comboCuentaActiva.getItems().clear();
        listaDetallesCuenta.clear();
        mostrarTotalCuenta.clear();
    }

    private void cargarCuentasActivasPorMesa() {
        comboCuentaActiva.getItems().clear();
        Integer mesaSeleccionada = comboMesaConCUENTAACTIVA.getValue();
        if (mesaSeleccionada == null) return;
        String sql = "SELECT C.ID_CUENTA " +
                "FROM CUENTAS C " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "WHERE M.NUMERO_MESA = ? AND C.ESTADO = 'ABIERTA'";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mesaSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comboCuentaActiva.getItems().add(rs.getInt("ID_CUENTA"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar cuentas activas: " + e.getMessage());
        }
        actualizarDetallesCuentaYTotal();
    }

    private void actualizarDetallesCuentaYTotal() {
        listaDetallesCuenta.clear();
        mostrarTotalCuenta.clear();
        Integer cuentaSeleccionada = comboCuentaActiva.getValue();
        Integer mesaSeleccionada = comboMesaConCUENTAACTIVA.getValue();
        if (cuentaSeleccionada == null || mesaSeleccionada == null) return;
        String sql = "SELECT M.NUMERO_MESA, P.NOMBRE AS PLATILLO, P.PRECIO " +
                "FROM DETALLES_CUENTA DC " +
                "JOIN CUENTAS C ON DC.ID_CUENTA = C.ID_CUENTA " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO " +
                "WHERE DC.ID_CUENTA = ?";
        double total = 0;
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cuentaSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String platillo = rs.getString("PLATILLO");
                double precio = rs.getDouble("PRECIO");
                listaDetallesCuenta.add(new DetalleCuenta(mesaSeleccionada, platillo, precio));
                total += precio;
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar detalles de la cuenta: " + e.getMessage());
        }
        mostrarTotalCuenta.setText("Total: $" + String.format("%.2f", total));
    }

    private void calcularTotalCuenta() {
        actualizarDetallesCuentaYTotal();
    }

    private void enviarSolicitudModificacion() {
        String motivo = txtMotivo.getText();
        Integer cuentaSeleccionada = comboCuentaActiva.getValue();
        int idMesero = LoginController.currentUserId;
        if (cuentaSeleccionada == null || motivo == null || motivo.trim().isEmpty()) {
            mostrarAlerta("Error", "El motivo y la cuenta son obligatorios.");
            return;
        }
        String sql = "INSERT INTO SOLICITUDES_MODIFICACION (ID_CUENTA, ID_MESERO, MOTIVO, FECHA_SOLICITUD) VALUES (?, ?, ?, SYSDATE)";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cuentaSeleccionada);
            pstmt.setInt(2, idMesero);
            pstmt.setString(3, motivo);
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Solicitud enviada correctamente.");
            txtMotivo.clear();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al enviar solicitud: " + e.getMessage());
        }
    }

    private void pagarCuentaYLiberarMesaYCalificar() {
        Integer cuentaSeleccionada = comboCuentaActiva.getValue();
        Integer mesaSeleccionada = comboMesaConCUENTAACTIVA.getValue();
        if (cuentaSeleccionada == null || mesaSeleccionada == null) {
            mostrarAlerta("Error", "Seleccione una Mesa y una Cuenta activa.");
            return;
        }
        String sqlCerrarCuenta = "UPDATE CUENTAS SET ESTADO = 'CERRADA' WHERE ID_CUENTA = ?";
        String sqlLiberarMesa = "UPDATE MESAS SET ESTADO = 'LIBRE' WHERE NUMERO_MESA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmtCerrar = conn.prepareStatement(sqlCerrarCuenta);
             PreparedStatement pstmtLiberar = conn.prepareStatement(sqlLiberarMesa)) {
            pstmtCerrar.setInt(1, cuentaSeleccionada);
            pstmtCerrar.executeUpdate();
            pstmtLiberar.setInt(1, mesaSeleccionada);
            pstmtLiberar.executeUpdate();
            mostrarAlerta("Éxito", "Cuenta pagada y mesa liberada.");
            cargarMesasConCuentaActiva();
            abrirVentanaCalificacionMesero(cuentaSeleccionada);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al pagar cuenta y liberar mesa: " + e.getMessage());
        }
    }

    private void abrirVentanaCalificacionMesero(Integer idCuenta) {
        try {
            int idMesero = obtenerIdMeseroPorCuenta(idCuenta);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/restaurantev2/CalificacionMesero.fxml"));
            Parent root = loader.load();
            CalificacionMeseroController controller = loader.getController();
            controller.inicializarDatos(idMesero, idCuenta);
            Stage stage = new Stage();
            stage.setTitle("Calificar Mesero");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de calificación: " + e.getMessage());
        }
    }

    private int obtenerIdMeseroPorCuenta(int idCuenta) {
        int idMesero = -1;
        String sql = "SELECT ID_MESERO FROM CUENTAS WHERE ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuenta);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                idMesero = rs.getInt("ID_MESERO");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo obtener el mesero de la cuenta: " + e.getMessage());
        }
        return idMesero;
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}