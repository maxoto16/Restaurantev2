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

public class MeseroController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnHorariolaboral, btnMesasasignadas, btnTomarorden, btnSalir, btnEditarCuenta;
    @FXML private ImageView imagenlogo;
    @FXML private Label lblTitulo1, lblTitulo;
    @FXML private TableView<MesaAsignada> tablaAsignacionesMesas;
    @FXML private TableColumn<MesaAsignada, String> colNombreMesero;
    @FXML private TableColumn<MesaAsignada, Integer> colNumeroMesa;
    @FXML private TableColumn<MesaAsignada, String> colEstadoMesa;
    @FXML private TableView<TurnoAsignado> tablaTurnosAsignados;
    @FXML private TableColumn<TurnoAsignado, String> colTurno;
    @FXML private TableColumn<TurnoAsignado, String> colHoraInicio;
    @FXML private TableColumn<TurnoAsignado, String> colHoraFin;
    @FXML private TableView<SolicitudModificacion> tablaSolicitudesModificacion;
    @FXML private TableColumn<SolicitudModificacion, Integer> colMesaSolicitud;
    @FXML private TableColumn<SolicitudModificacion, String> colAreaSolicitud;
    @FXML private TableColumn<SolicitudModificacion, String> colEstadoSolicitud;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<MesaAsignada> listaMesas = FXCollections.observableArrayList();
    public ObservableList<TurnoAsignado> listaTurnos = FXCollections.observableArrayList();
    public ObservableList<SolicitudModificacion> listaSolicitudes = FXCollections.observableArrayList();

    public static class MesaAsignada {
        public String mesero;
        public int numeroMesa;
        public String estadoMesa;
        public MesaAsignada(String mesero, int numeroMesa, String estadoMesa) {
            this.mesero = mesero;
            this.numeroMesa = numeroMesa;
            this.estadoMesa = estadoMesa;
        }
        public String getMesero() { return mesero; }
        public int getNumeroMesa() { return numeroMesa; }
        public String getEstadoMesa() { return estadoMesa; }
    }

    public static class TurnoAsignado {
        public String turno;
        public String horaInicio;
        public String horaFin;
        public TurnoAsignado(String turno, String horaInicio, String horaFin) {
            this.turno = turno; this.horaInicio = horaInicio; this.horaFin = horaFin;
        }
        public String getTurno() { return turno; }
        public String getHoraInicio() { return horaInicio; }
        public String getHoraFin() { return horaFin; }
    }

    public static class SolicitudModificacion {
        public int numeroMesa;
        public String areaMesa;
        public String estadoSolicitud;
        public int idCuenta; // ID de la cuenta asociada
        public SolicitudModificacion(int numeroMesa, String areaMesa, String estadoSolicitud, int idCuenta) {
            this.numeroMesa = numeroMesa; this.areaMesa = areaMesa; this.estadoSolicitud = estadoSolicitud; this.idCuenta = idCuenta;
        }
        public int getNumeroMesa() { return numeroMesa; }
        public String getAreaMesa() { return areaMesa; }
        public String getEstadoSolicitud() { return estadoSolicitud; }
        public int getIdCuenta() { return idCuenta; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();

        colNombreMesero.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMesero()));
        colNumeroMesa.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getNumeroMesa()).asObject());
        colEstadoMesa.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstadoMesa()));

        colTurno.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTurno()));
        colHoraInicio.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHoraInicio()));
        colHoraFin.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHoraFin()));

        colMesaSolicitud.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getNumeroMesa()).asObject());
        colAreaSolicitud.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAreaMesa()));
        colEstadoSolicitud.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstadoSolicitud()));

        tablaAsignacionesMesas.setItems(listaMesas);
        tablaTurnosAsignados.setItems(listaTurnos);
        tablaSolicitudesModificacion.setItems(listaSolicitudes);

        cargarMesasAsignadas();
        cargarTurnosAsignados();
        cargarSolicitudesModificacion();

        btnHorariolaboral.setOnAction(e -> reloadScreen("Mesero.fxml"));
        btnMesasasignadas.setOnAction(e -> reloadScreen("Mesero_NuevaOrden.fxml"));
        btnTomarorden.setOnAction(e -> reloadScreen("Mesero_Cuentas.fxml"));
        btnSalir.setOnAction(e -> ((Stage)btnSalir.getScene().getWindow()).close());
        btnEditarCuenta.setOnAction(e -> editarCuenta());
    }

    private void reloadScreen(String fxmlPath) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root, 1600, 900);
            stage.setScene(scene);
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al cambiar pantalla: " + ex.getMessage());
        }
    }

    private void cargarMesasAsignadas() {
        listaMesas.clear();
        int idMesero = LoginController.currentUserId;
        String sql = "SELECT U.NOMBRE AS MESERO, M.NUMERO_MESA, M.ESTADO " +
                "FROM ASIGNACIONES_MESAS AM " +
                "JOIN MESAS M ON AM.ID_MESA = M.ID_MESA " +
                "JOIN USUARIOS U ON AM.ID_USUARIO = U.ID_USUARIO " +
                "WHERE AM.ID_USUARIO = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaMesas.add(new MesaAsignada(
                        rs.getString("MESERO"),
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("ESTADO")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar mesas asignadas: " + e.getMessage());
        }
    }

    private void cargarTurnosAsignados() {
        listaTurnos.clear();
        int idMesero = LoginController.currentUserId;
        String sql = "SELECT T.TURNO, T.HORA_INICIO, T.HORA_FIN " +
                "FROM ASIGNACIONES_MESAS AM " +
                "JOIN TURNOS T ON AM.ID_TURNO = T.ID_TURNO " +
                "WHERE AM.ID_USUARIO = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaTurnos.add(new TurnoAsignado(
                        rs.getString("TURNO"),
                        rs.getString("HORA_INICIO"),
                        rs.getString("HORA_FIN")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar turnos: " + e.getMessage());
        }
    }

    private void cargarSolicitudesModificacion() {
        listaSolicitudes.clear();
        int idMesero = LoginController.currentUserId;
        String sql = "SELECT M.NUMERO_MESA, M.UBICACION AS AREA, SM.ESTADO, C.ID_CUENTA " +
                "FROM SOLICITUDES_MODIFICACION SM " +
                "JOIN CUENTAS C ON SM.ID_CUENTA = C.ID_CUENTA " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "WHERE SM.ID_MESERO = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaSolicitudes.add(new SolicitudModificacion(
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("AREA"),
                        rs.getString("ESTADO"),
                        rs.getInt("ID_CUENTA")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar solicitudes: " + e.getMessage());
        }
    }

    private void editarCuenta() {
        SolicitudModificacion solicitud = tablaSolicitudesModificacion.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarAlerta("Selecciona una solicitud para editar.");
            return;
        }
        if (!"ACEPTADA".equalsIgnoreCase(solicitud.getEstadoSolicitud())) {
            mostrarAlerta("La solicitud debe estar en estado 'ACEPTADA' para editar la cuenta.");
            return;
        }
        ModificacionController.idCuentaEditar = solicitud.getIdCuenta;

        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("Modificacion.fxml"));
            stage.setScene(new Scene(root, 1600, 900));
        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla de edición: " + ex.getMessage());
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