package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LiderMeserosController {
    @FXML private BorderPane rootPane;
    @FXML private Button btninicio, btnPlandedia, btnHistorial, btnSalir;
    @FXML private Label lblTitulo1;

    // Solicitudes
    @FXML public TableView<Solicitud> tablaSOLICITUDES_MODIFICACION;
    @FXML public TableColumn<Solicitud, Integer> col_ID_CUENTA;
    @FXML public TableColumn<Solicitud, String> col_ID_MESERO;
    @FXML public TableColumn<Solicitud, String> colMOTIVO;
    @FXML public TableColumn<Solicitud, String> colESTADO_SOLICITUD;

    @FXML public Button btnAceptar, btnDenegar, btnDetalles;

    // Mesas
    @FXML public TableView<Mesa> tablaMesas;
    @FXML public TableColumn<Mesa, Integer> colNUMERO_MESA;
    @FXML public TableColumn<Mesa, Integer> colCAPACIDAD;
    @FXML public TableColumn<Mesa, String> colUBICACION;
    @FXML public TableColumn<Mesa, String> colESTADO_MESA;

    // Meseros (solo nombre y estado)
    @FXML public TableView<Mesero> tablaUSUARIOS_ROLMESERO;
    @FXML public TableColumn<Mesero, String> colNombre_mesero;
    @FXML public TableColumn<Mesero, String> colESTADO_USUARIO;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Solicitud> listaSolicitudes;
    public ObservableList<Mesa> listaMesas;
    public ObservableList<Mesero> listaMeseros;

    // Beans
    public static class Solicitud {
        private int idCuenta;
        private String idMesero;
        private String motivo;
        private String estado;

        public Solicitud(int idCuenta, String idMesero, String motivo, String estado) {
            this.idCuenta = idCuenta;
            this.idMesero = idMesero;
            this.motivo = motivo;
            this.estado = estado;
        }
        public int getIdCuenta() { return idCuenta; }
        public String getIdMesero() { return idMesero; }
        public String getMotivo() { return motivo; }
        public String getEstado() { return estado; }
    }

    public static class Mesa {
        private int numeroMesa;
        private int capacidad;
        private String ubicacion;
        private String estado;

        public Mesa(int numeroMesa, int capacidad, String ubicacion, String estado) {
            this.numeroMesa = numeroMesa;
            this.capacidad = capacidad;
            this.ubicacion = ubicacion;
            this.estado = estado;
        }
        public int getNumeroMesa() { return numeroMesa; }
        public int getCapacidad() { return capacidad; }
        public String getUbicacion() { return ubicacion; }
        public String getEstado() { return estado; }
    }

    public static class Mesero {
        private String nombre;
        private String estado;

        public Mesero(String nombre, String estado) {
            this.nombre = nombre;
            this.estado = estado;
        }
        public String getNombre() { return nombre; }
        public String getEstado() { return estado; }
    }

    // Menú de navegación
    public static class PantallaController  {
        @FXML
        public static void cambiarPantalla(Stage stageActual, String fxml, String titulo, int ancho, int alto) {
            try {
                FXMLLoader loader = new FXMLLoader(PantallaController.class.getResource(fxml));
                Parent root = loader.load();
                Scene nuevaEscena = new Scene(root, ancho, alto);
                stageActual.setTitle(titulo);
                stageActual.setScene(nuevaEscena);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();
        listaSolicitudes = FXCollections.observableArrayList();
        listaMesas = FXCollections.observableArrayList();
        listaMeseros = FXCollections.observableArrayList();

        btninicio.setOnAction(e -> {
            Stage stageActual = (Stage) btninicio.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Inicio.fxml", "Inicio - Lider Meseros", 1600, 900);
        });
        btnPlandedia.setOnAction(e -> {
            Stage stageActual = (Stage) btnPlandedia.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/PlanDia.fxml", "Plan del Día", 1600, 900);
        });
        btnHistorial.setOnAction(e -> {
            Stage stageActual = (Stage) btnHistorial.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Historial.fxml", "Historial", 1600, 900);
        });
        btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());

        // Solicitudes
        col_ID_CUENTA.setCellValueFactory(new PropertyValueFactory<>("idCuenta"));
        col_ID_MESERO.setCellValueFactory(new PropertyValueFactory<>("idMesero"));
        colMOTIVO.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colESTADO_SOLICITUD.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaSOLICITUDES_MODIFICACION.setItems(listaSolicitudes);

        btnAceptar.setOnAction(e -> aceptarSolicitud());
        btnDenegar.setOnAction(e -> denegarSolicitud());
        btnDetalles.setOnAction(e -> mostrarDetallesSolicitud());

        // Mesas
        colNUMERO_MESA.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));
        colCAPACIDAD.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colUBICACION.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colESTADO_MESA.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaMesas.setItems(listaMesas);

        // Meseros - SOLO nombre y estado
        colNombre_mesero.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colESTADO_USUARIO.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaUSUARIOS_ROLMESERO.setItems(listaMeseros);

        cargarSolicitudes();
        cargarMesas();
        cargarMeseros();
    }

    public void cargarSolicitudes() {
        listaSolicitudes.clear();
        String sql = "SELECT ID_CUENTA, ID_MESERO, MOTIVO, ESTADO FROM SOLICITUDES_MODIFICACION";
        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Solicitud solicitud = new Solicitud(
                        rs.getInt("ID_CUENTA"),
                        rs.getString("ID_MESERO"),
                        rs.getString("MOTIVO"),
                        rs.getString("ESTADO")
                );
                listaSolicitudes.add(solicitud);
            }
            tablaSOLICITUDES_MODIFICACION.setItems(listaSolicitudes);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar las solicitudes: " + e.getMessage());
        }
    }

    public void cargarMesas() {
        listaMesas.clear();
        String sql = "SELECT NUMERO_MESA, CAPACIDAD, UBICACION, ESTADO FROM MESAS";
        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesa mesa = new Mesa(
                        rs.getInt("NUMERO_MESA"),
                        rs.getInt("CAPACIDAD"),
                        rs.getString("UBICACION"),
                        rs.getString("ESTADO")
                );
                listaMesas.add(mesa);
            }
            tablaMesas.setItems(listaMesas);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar las mesas: " + e.getMessage());
        }
    }

    // SOLO muestra nombre y estado de los meseros
    public void cargarMeseros() {
        listaMeseros.clear();
        String sql = "SELECT NOMBRE, ESTADO FROM USUARIOS WHERE ROL = 'MESERO'";
        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesero mesero = new Mesero(
                        rs.getString("NOMBRE"),
                        rs.getString("ESTADO")
                );
                listaMeseros.add(mesero);
            }
            tablaUSUARIOS_ROLMESERO.setItems(listaMeseros);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar los meseros: " + e.getMessage());
        }
    }

    public void aceptarSolicitud() {
        Solicitud solicitud = tablaSOLICITUDES_MODIFICACION.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarAlerta("Error", "Selecciona una solicitud.");
            return;
        }
        try {
            String sql = "UPDATE SOLICITUDES_MODIFICACION SET ESTADO = 'ACEPTADA' WHERE ID_CUENTA = ? AND ID_MESERO = ?";
            try (Connection conn = db.obtenerConexion();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, solicitud.getIdCuenta());
                pstmt.setString(2, solicitud.getIdMesero());
                pstmt.executeUpdate();
                cargarSolicitudes();
                mostrarAlerta("Éxito", "Solicitud aceptada.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al aceptar la solicitud: " + e.getMessage());
        }
    }

    public void denegarSolicitud() {
        Solicitud solicitud = tablaSOLICITUDES_MODIFICACION.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarAlerta("Error", "Selecciona una solicitud.");
            return;
        }
        try {
            String sql = "UPDATE SOLICITUDES_MODIFICACION SET ESTADO = 'DENEGADA' WHERE ID_CUENTA = ? AND ID_MESERO = ?";
            try (Connection conn = db.obtenerConexion();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, solicitud.getIdCuenta());
                pstmt.setString(2, solicitud.getIdMesero());
                pstmt.executeUpdate();
                cargarSolicitudes();
                mostrarAlerta("Éxito", "Solicitud denegada.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al denegar la solicitud: " + e.getMessage());
        }
    }

    public void mostrarDetallesSolicitud() {
        Solicitud solicitud = tablaSOLICITUDES_MODIFICACION.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarAlerta("Error", "Selecciona una solicitud.");
            return;
        }
        String detalles = "Cuenta: " + solicitud.getIdCuenta() +
                "\nMesero: " + solicitud.getIdMesero() +
                "\nMotivo: " + solicitud.getMotivo() +
                "\nEstado: " + solicitud.getEstado();
        mostrarAlerta("Detalles de Solicitud", detalles);
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}