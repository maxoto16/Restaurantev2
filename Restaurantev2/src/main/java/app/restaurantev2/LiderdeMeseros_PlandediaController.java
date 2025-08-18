package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class LiderdeMeseros_PlandediaController {

    @FXML private BorderPane rootPane;
    @FXML private ComboBox<Integer> MESASDISPONIBLES;
    @FXML private ComboBox<String> NOMBREMESEROSDISPONIBLES;

    @FXML private Button btnAgregarAdignacionMesa;
    @FXML private Button btnEDITAR;
    @FXML private Button btnELIMINAR;

    @FXML private TableView<AsignacionMesa> tablaAsignacionMesasSoloConfechadeHoy;
    @FXML private TableColumn<AsignacionMesa, Integer> colMesa;
    @FXML private TableColumn<AsignacionMesa, String> colMesero;

    @FXML private TableView<AsignacionMesaHistorial> tablaAsignacionMesasCompleta;
    @FXML private TableColumn<AsignacionMesaHistorial, Integer> colMesa2;
    @FXML private TableColumn<AsignacionMesaHistorial, String> colMesero2;
    @FXML private TableColumn<AsignacionMesaHistorial, String> colFecha;

    @FXML private Button btninicio;
    @FXML private Button btnPlandedia;
    @FXML private Button btnSalir;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Integer> listaMesasDisponibles = FXCollections.observableArrayList();
    public ObservableList<String> listaMeserosDisponibles = FXCollections.observableArrayList();
    public ObservableList<AsignacionMesa> listaAsignacionesHoy = FXCollections.observableArrayList();
    public ObservableList<AsignacionMesaHistorial> listaHistorial = FXCollections.observableArrayList();

    // Modelos
    public static class AsignacionMesa {
        private int mesa;
        private String mesero;
        public AsignacionMesa(int mesa, String mesero) {
            this.mesa = mesa;
            this.mesero = mesero;
        }
        public int getMesa() { return mesa; }
        public String getMesero() { return mesero; }
    }
    public static class AsignacionMesaHistorial {
        private int mesa;
        private String mesero;
        private String fecha;
        public AsignacionMesaHistorial(int mesa, String mesero, String fecha) {
            this.mesa = mesa;
            this.mesero = mesero;
            this.fecha = fecha;
        }
        public int getMesa() { return mesa; }
        public String getMesero() { return mesero; }
        public String getFecha() { return fecha; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();
        MESASDISPONIBLES.setItems(listaMesasDisponibles);
        NOMBREMESEROSDISPONIBLES.setItems(listaMeserosDisponibles);

        colMesa.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesa()).asObject());
        colMesero.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMesero()));
        tablaAsignacionMesasSoloConfechadeHoy.setItems(listaAsignacionesHoy);

        colMesa2.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesa()).asObject());
        colMesero2.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMesero()));
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha()));
        tablaAsignacionMesasCompleta.setItems(listaHistorial);

        cargarMesasDisponibles();
        cargarMeserosDisponibles();
        cargarAsignacionesDeHoy();
        cargarHistorialAsignaciones();

        btnAgregarAdignacionMesa.setOnAction(e -> agregarAsignacionMesa());
        btnELIMINAR.setOnAction(e -> eliminarAsignacionMesa());
        btnEDITAR.setOnAction(e -> mostrarAlerta("Pendiente", "Funcionalidad de edición pendiente."));

        btninicio.setOnAction(e -> cambiarPantalla("/app/restaurantev2/LiderMeseros.fxml", "Inicio - Lider Meseros"));
        btnPlandedia.setOnAction(e -> cambiarPantalla("/app/restaurantev2/LiderdeMeseros_Plandedia.fxml", "Plan del Día - Lider Meseros"));
        btnSalir.setOnAction(e -> salir());
    }

    private void cargarMesasDisponibles() {
        listaMesasDisponibles.clear();
        String sql = "SELECT NUMERO_MESA FROM MESAS";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listaMesasDisponibles.add(rs.getInt("NUMERO_MESA"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar mesas disponibles", e.getMessage());
        }
    }

    private void cargarMeserosDisponibles() {
        listaMeserosDisponibles.clear();
        String sql = "SELECT NOMBRE FROM USUARIOS WHERE ROL = 'MESERO'";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listaMeserosDisponibles.add(rs.getString("NOMBRE"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar meseros disponibles", e.getMessage());
        }
    }

    private void cargarAsignacionesDeHoy() {
        listaAsignacionesHoy.clear();
        String hoy = LocalDate.now().toString();
        String sql = "SELECT M.NUMERO_MESA, U.NOMBRE " +
                "FROM ASIGNACIONES_MESAS AM " +
                "JOIN MESAS M ON AM.ID_MESA = M.ID_MESA " +
                "JOIN USUARIOS U ON AM.ID_USUARIO = U.ID_USUARIO " +
                "WHERE AM.FECHA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoy);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaAsignacionesHoy.add(new AsignacionMesa(
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("NOMBRE")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar asignaciones de hoy", e.getMessage());
        }
    }

    private void cargarHistorialAsignaciones() {
        listaHistorial.clear();
        String sql = "SELECT M.NUMERO_MESA, U.NOMBRE, AM.FECHA " +
                "FROM ASIGNACIONES_MESAS AM " +
                "JOIN MESAS M ON AM.ID_MESA = M.ID_MESA " +
                "JOIN USUARIOS U ON AM.ID_USUARIO = U.ID_USUARIO " +
                "ORDER BY AM.FECHA DESC";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                listaHistorial.add(new AsignacionMesaHistorial(
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("NOMBRE"),
                        rs.getString("FECHA")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar historial de asignaciones", e.getMessage());
        }
    }

    private void agregarAsignacionMesa() {
        Integer numeroMesa = MESASDISPONIBLES.getValue();
        String nombreMesero = NOMBREMESEROSDISPONIBLES.getValue();
        if (numeroMesa == null || nombreMesero == null) {
            mostrarAlerta("Faltan datos", "Selecciona mesa y mesero.");
            return;
        }
        int idMesa = obtenerIdMesaPorNumero(numeroMesa);
        int idMesero = obtenerIdMeseroPorNombre(nombreMesero);
        String fecha = LocalDate.now().toString();

        String sqlCheck = "SELECT 1 FROM ASIGNACIONES_MESAS WHERE ID_MESA = ? AND ID_USUARIO = ? AND FECHA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
            pstmtCheck.setInt(1, idMesa);
            pstmtCheck.setInt(2, idMesero);
            pstmtCheck.setString(3, fecha);
            ResultSet rsCheck = pstmtCheck.executeQuery();
            if (rsCheck.next()) {
                mostrarAlerta("Duplicado", "Ya existe una asignación para esa mesa y mesero hoy.");
                return;
            }
        } catch (Exception e) {
            mostrarAlerta("Error al verificar duplicados", e.getMessage());
            return;
        }

        String sqlInsert = "INSERT INTO ASIGNACIONES_MESAS (ID_MESA, ID_USUARIO, FECHA) VALUES (?, ?, ?)";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setInt(1, idMesa);
            pstmt.setInt(2, idMesero);
            pstmt.setString(3, fecha);
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Asignación agregada correctamente.");
            cargarAsignacionesDeHoy();
            cargarHistorialAsignaciones();
        } catch (Exception e) {
            mostrarAlerta("Error al agregar asignación", e.getMessage());
        }
    }

    private void eliminarAsignacionMesa() {
        AsignacionMesa seleccion = tablaAsignacionMesasSoloConfechadeHoy.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Eliminar", "Selecciona una asignación de la tabla de hoy para eliminar.");
            return;
        }
        int idMesa = obtenerIdMesaPorNumero(seleccion.getMesa());
        int idMesero = obtenerIdMeseroPorNombre(seleccion.getMesero());
        String fecha = LocalDate.now().toString();

        String sql = "DELETE FROM ASIGNACIONES_MESAS WHERE ID_MESA = ? AND ID_USUARIO = ? AND FECHA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesa);
            pstmt.setInt(2, idMesero);
            pstmt.setString(3, fecha);
            pstmt.executeUpdate();
            mostrarAlerta("Eliminado", "Asignación eliminada correctamente.");
            cargarAsignacionesDeHoy();
            cargarHistorialAsignaciones();
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar asignación", e.getMessage());
        }
    }

    private int obtenerIdMesaPorNumero(int numeroMesa) {
        String sql = "SELECT ID_MESA FROM MESAS WHERE NUMERO_MESA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("ID_MESA");
        } catch (Exception e) { }
        return -1;
    }

    private int obtenerIdMeseroPorNombre(String nombreMesero) {
        String sql = "SELECT ID_USUARIO FROM USUARIOS WHERE NOMBRE = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreMesero);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("ID_USUARIO");
        } catch (Exception e) { }
        return -1;
    }

    private void cambiarPantalla(String fxml, String titulo) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxml));
            javafx.scene.Parent root = loader.load();
            rootPane.getScene().setRoot(root);
            ((Stage)rootPane.getScene().getWindow()).setTitle(titulo);
        } catch (Exception e) {
            mostrarAlerta("Error al cambiar pantalla", e.getMessage());
        }
    }

    private void salir() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}