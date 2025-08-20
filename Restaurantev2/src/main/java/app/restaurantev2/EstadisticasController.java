package app.restaurantev2;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class EstadisticasController {

    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;
    @FXML private ImageView logoImage, iconMesas, iconEstadisticas, iconCategorias, iconPlatillos, iconUsuarios, iconSalir;
    @FXML private Label adminName;
    @FXML private Label lblTitulo;
    @FXML private Label lblTotalDinero, lblCuentasCerradas, lblCuentasAbiertas, lblSolicitudesModificacion;
    @FXML private TableView<DetalleDineroCuenta> tablaDetallesDinero;
    @FXML private TableColumn<DetalleDineroCuenta, String> colFechaCuenta;
    @FXML private TableColumn<DetalleDineroCuenta, Integer> colMesaCuenta;
    @FXML private TableColumn<DetalleDineroCuenta, String> colMeseroCuenta;
    @FXML private TableColumn<DetalleDineroCuenta, Double> colTotalCuenta;
    @FXML private TableColumn<DetalleDineroCuenta, String> colEstadoCuenta;
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFin;
    @FXML private Button btnFiltrar;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<DetalleDineroCuenta> listaDetallesDinero = FXCollections.observableArrayList();

    public static class DetalleDineroCuenta {
        private String fecha;
        private int mesa;
        private String mesero;
        private double total;
        private String estado;
        public DetalleDineroCuenta(String fecha, int mesa, String mesero, double total, String estado) {
            this.fecha = fecha;
            this.mesa = mesa;
            this.mesero = mesero;
            this.total = total;
            this.estado = estado;
        }
        public String getFecha() { return fecha; }
        public int getMesa() { return mesa; }
        public String getMesero() { return mesero; }
        public double getTotal() { return total; }
        public String getEstado() { return estado; }
    }

    @FXML
    public void initialize() {
        animateIconOnHover(btnMesas, iconMesas);
        animateIconOnHover(btnEstadisticas, iconEstadisticas);
        animateIconOnHover(btnCategorias, iconCategorias);
        animateIconOnHover(btnPlatillos, iconPlatillos);
        animateIconOnHover(btnUsuarios, iconUsuarios);
        animateIconOnHover(btnSalir, iconSalir);

        // Navegación entre pantallas
        btnMesas.setOnAction(e -> {
            Stage stageActual = (Stage) btnMesas.getScene().getWindow();
            cambiarPantalla(stageActual, "/app/restaurantev2/Admin.fxml", "Admin - Restaurante", 1600, 900);
        });
        btnEstadisticas.setOnAction(e -> {
            Stage stageActual = (Stage) btnEstadisticas.getScene().getWindow();
            cambiarPantalla(stageActual, "/app/restaurantev2/Estadisticas.fxml", "Estadísticas - Restaurante", 1600, 900);
        });
        btnCategorias.setOnAction(e -> {
            Stage stageActual = (Stage) btnCategorias.getScene().getWindow();
            cambiarPantalla(stageActual, "/app/restaurantev2/Categorias.fxml", "Categorias - Restaurante", 1600, 900);
        });
        btnPlatillos.setOnAction(e -> {
            Stage stageActual = (Stage) btnPlatillos.getScene().getWindow();
            cambiarPantalla(stageActual, "/app/restaurantev2/Platillos.fxml", "Platillos - Restaurante", 1600, 900);
        });
        btnUsuarios.setOnAction(e -> {
            Stage stageActual = (Stage) btnUsuarios.getScene().getWindow();
            cambiarPantalla(stageActual, "/app/restaurantev2/Usuarios.fxml", "Usuarios - Restaurante", 1600, 900);
        });
        btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());

        db = new MainApp.ConexionBaseDatos();
        colFechaCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha()));
        colMesaCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesa()).asObject());
        colMeseroCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMesero()));
        colTotalCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotal()).asObject());
        colEstadoCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstado()));
        tablaDetallesDinero.setItems(listaDetallesDinero);

        // Filtro inicial con el mes actual
        LocalDate hoy = LocalDate.now();
        datePickerInicio.setValue(hoy.withDayOfMonth(1));
        datePickerFin.setValue(hoy);
        filtrarEstadisticas();

        btnFiltrar.setOnAction(e -> filtrarEstadisticas());
    }

    private void animateIconOnHover(Button button, ImageView icon) {
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(250), icon);
            st.setToX(1.16);
            st.setToY(1.16);
            st.play();
        });
        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(250), icon);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    // Método funcional: suma los registros filtrados que aparecen en la tabla
    private void filtrarEstadisticas() {
        listaDetallesDinero.clear();

        LocalDate fechaInicio = datePickerInicio.getValue();
        LocalDate fechaFin = datePickerFin.getValue();
        String fechaIniStr = fechaInicio != null ? fechaInicio.toString() : "";
        String fechaFinStr = fechaFin != null ? fechaFin.toString() : "";

        double totalDinero = 0;
        int cuentasCerradas = 0;
        int cuentasAbiertas = 0;
        int solicitudesModificacion = 0;

        String sqlDetalles = "SELECT C.ID_CUENTA, TO_CHAR(C.FECHA_APERTURA, 'YYYY-MM-DD') AS FECHA, " +
                "M.NUMERO_MESA, U.NOMBRE AS MESERO, C.ESTADO, " +
                "(SELECT NVL(SUM(P.PRECIO * DC.CANTIDAD),0) FROM DETALLES_CUENTA DC JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO WHERE DC.ID_CUENTA = C.ID_CUENTA) AS TOTAL " +
                "FROM CUENTAS C " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "JOIN USUARIOS U ON C.ID_MESERO = U.ID_USUARIO " +
                "WHERE TO_CHAR(C.FECHA_APERTURA, 'YYYY-MM-DD') BETWEEN ? AND ? " +
                "ORDER BY C.FECHA_APERTURA DESC";

        String sqlSolicitudes =
                "SELECT COUNT(*) AS SOLICITUDES " +
                        "FROM SOLICITUDES_MODIFICACION SM " +
                        "JOIN CUENTAS CQ ON SM.ID_CUENTA = CQ.ID_CUENTA " +
                        "WHERE TO_CHAR(CQ.FECHA_APERTURA, 'YYYY-MM-DD') BETWEEN ? AND ?";

        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmtDetalles = conn.prepareStatement(sqlDetalles);
             PreparedStatement pstmtSolicitudes = conn.prepareStatement(sqlSolicitudes)) {

            pstmtDetalles.setString(1, fechaIniStr);
            pstmtDetalles.setString(2, fechaFinStr);
            ResultSet rs = pstmtDetalles.executeQuery();

            while (rs.next()) {
                double total = rs.getDouble("TOTAL");
                String estado = rs.getString("ESTADO");

                listaDetallesDinero.add(new DetalleDineroCuenta(
                        rs.getString("FECHA"),
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("MESERO"),
                        total,
                        estado
                ));

                totalDinero += total;
                if ("CERRADA".equalsIgnoreCase(estado)) cuentasCerradas++;
                if ("ABIERTA".equalsIgnoreCase(estado)) cuentasAbiertas++;
            }

            // Solicitudes de modificación
            pstmtSolicitudes.setString(1, fechaIniStr);
            pstmtSolicitudes.setString(2, fechaFinStr);
            ResultSet rsSol = pstmtSolicitudes.executeQuery();
            if (rsSol.next()) {
                solicitudesModificacion = rsSol.getInt("SOLICITUDES");
            }

            lblTotalDinero.setText("$" + String.format("%.2f", totalDinero));
            lblCuentasCerradas.setText(String.valueOf(cuentasCerradas));
            lblCuentasAbiertas.setText(String.valueOf(cuentasAbiertas));
            lblSolicitudesModificacion.setText(String.valueOf(solicitudesModificacion));

        } catch (Exception e) {
            mostrarAlerta("Error al filtrar estadísticas: " + e.getMessage());
        }
    }

    public void mostrarAlerta(String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void cambiarPantalla(Stage stageActual, String fxml, String titulo, int ancho, int alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Scene nuevaEscena = new Scene(root, ancho, alto);
            stageActual.setTitle(titulo);
            stageActual.setScene(nuevaEscena);
        } catch (Exception e) {
            mostrarAlerta("Error al cambiar pantalla: " + e.getMessage());
        }
    }
}