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

public class MeseroNuevaOrdenController {

    @FXML private BorderPane rootPane;
    @FXML private ComboBox<Integer> comboMesasAsignadas;
    @FXML private Button btnCrearCuenta;
    @FXML private Button btnEnviarACocina;
    @FXML private TableView<Platillo> tablaPlatillos;
    @FXML private TableColumn<Platillo, String> colNombrePlatillo;
    @FXML private TableColumn<Platillo, Double> colPrecioPlatillo;
    @FXML private TableColumn<Platillo, String> colCategoriaPlatillo;

    @FXML private TableView<DetalleCuenta> TablaDetallesDeCuenta;
    @FXML private TableColumn<DetalleCuenta, String> colPlatilloAgregadoAcuenta;
    @FXML private TableColumn<DetalleCuenta, Double> colPrecioPlatilloEncuenta;
    @FXML private TableColumn<DetalleCuenta, Integer> colNumeroMesaCuenta;

    @FXML private Button btnAñadirPlatilloACuentaDetalles;
    @FXML private Button btnEliminarPlatilloDetalleCuenta;

    @FXML private Button btnHorariolaboral;
    @FXML private Button btnMesasasignadas;
    @FXML private Button btnTomarorden;
    @FXML private Button btnSalir;

    @FXML private Label lblTitulo, lblTitulo1, lblTitulo2, lblTitulo21;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Integer> listaMesas = FXCollections.observableArrayList();
    public ObservableList<Platillo> listaPlatillos = FXCollections.observableArrayList();
    public ObservableList<DetalleCuenta> listaDetallesCuenta = FXCollections.observableArrayList();

    private int idMesero;
    private int idCuentaActual = -1;

    // Modelo Platillo
    public static class Platillo {
        public String nombre;
        public double precio;
        public String categoria;
        public Platillo(String nombre, double precio, String categoria) {
            this.nombre = nombre;
            this.precio = precio;
            this.categoria = categoria;
        }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public String getCategoria() { return categoria; }
    }

    // Modelo DetalleCuenta
    public static class DetalleCuenta {
        public String platillo;
        public double precio;
        public int mesaCuenta;
        public int idDetalleCuenta;
        public DetalleCuenta(String platillo, double precio, int mesaCuenta, int idDetalleCuenta) {
            this.platillo = platillo;
            this.precio = precio;
            this.mesaCuenta = mesaCuenta;
            this.idDetalleCuenta = idDetalleCuenta;
        }
        public String getPlatillo() { return platillo; }
        public double getPrecio() { return precio; }
        public int getMesaCuenta() { return mesaCuenta; }
        public int getIdDetalleCuenta() { return idDetalleCuenta; }
    }

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();
        idMesero = LoginController.currentUserId;

        // Set cell value factories
        colNombrePlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNombre()));
        colPrecioPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        colCategoriaPlatillo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCategoria()));

        colPlatilloAgregadoAcuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlatillo()));
        colPrecioPlatilloEncuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
        colNumeroMesaCuenta.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMesaCuenta()).asObject());

        tablaPlatillos.setItems(listaPlatillos);
        TablaDetallesDeCuenta.setItems(listaDetallesCuenta);
        comboMesasAsignadas.setItems(listaMesas);

        cargarMesasAsignadas();
        cargarPlatillos();

        btnCrearCuenta.setOnAction(e -> crearCuentaNueva());
        btnAñadirPlatilloACuentaDetalles.setOnAction(e -> agregarPlatilloACuenta());
        btnEliminarPlatilloDetalleCuenta.setOnAction(e -> eliminarPlatilloDetalleCuenta());
        btnEnviarACocina.setOnAction(e -> enviarACocina());

        comboMesasAsignadas.setOnAction(e -> cargarDetallesCuenta());

        // Navegación menú lateral (nombres correctos)
        btnHorariolaboral.setOnAction(e -> cambiarPantalla("Mesero.fxml"));
        btnMesasasignadas.setOnAction(e -> cambiarPantalla("Mesero_NuevaOrden.fxml"));
        btnTomarorden.setOnAction(e -> cambiarPantalla("Mesero_Cuentas.fxml"));
        btnSalir.setOnAction(e -> salir());
    }

    private void cargarMesasAsignadas() {
        listaMesas.clear();
        String sql = "SELECT M.NUMERO_MESA FROM ASIGNACIONES_MESAS AM " +
                "JOIN MESAS M ON AM.ID_MESA = M.ID_MESA " +
                "WHERE AM.ID_USUARIO = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaMesas.add(rs.getInt("NUMERO_MESA"));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar mesas: " + e.getMessage());
        }
    }

    private void cargarPlatillos() {
        listaPlatillos.clear();
        String sql = "SELECT P.NOMBRE, P.PRECIO, C.NOMBRE AS CATEGORIA " +
                "FROM PLATILLOS P JOIN CATEGORIAS C ON P.ID_CATEGORIA = C.ID_CATEGORIA";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaPlatillos.add(new Platillo(
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getString("CATEGORIA")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar platillos: " + e.getMessage());
        }
    }

    private void crearCuentaNueva() {
        Integer numeroMesa = comboMesasAsignadas.getValue();
        if (numeroMesa == null) {
            mostrarAlerta("Error", "Selecciona una mesa asignada.");
            return;
        }
        int idMesa = obtenerIdMesaPorNumero(numeroMesa);

        String sql = "INSERT INTO CUENTAS (ID_MESA, ID_MESERO, ESTADO) VALUES (?, ?, 'ABIERTA')";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[] {"ID_CUENTA"})) {
            pstmt.setInt(1, idMesa);
            pstmt.setInt(2, idMesero);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                idCuentaActual = rs.getInt(1);
            } else {
                idCuentaActual = obtenerIdCuentaActiva(idMesa, idMesero);
            }
            mostrarAlerta("Éxito", "Cuenta creada correctamente.");
            cargarDetallesCuenta();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear la cuenta: " + e.getMessage());
        }
    }

    private int obtenerIdMesaPorNumero(int numeroMesa) {
        String sql = "SELECT ID_MESA FROM MESAS WHERE NUMERO_MESA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroMesa);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("ID_MESA");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar id mesa: " + e.getMessage());
        }
        return -1;
    }

    private int obtenerIdCuentaActiva(int idMesa, int idMesero) {
        String sql = "SELECT ID_CUENTA FROM CUENTAS WHERE ID_MESA = ? AND ID_MESERO = ? AND ESTADO = 'ABIERTA' ORDER BY FECHA_APERTURA DESC";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesa);
            pstmt.setInt(2, idMesero);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("ID_CUENTA");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar id cuenta: " + e.getMessage());
        }
        return -1;
    }

    private void cargarDetallesCuenta() {
        listaDetallesCuenta.clear();
        Integer numeroMesa = comboMesasAsignadas.getValue();
        if (numeroMesa == null) return;
        int idMesa = obtenerIdMesaPorNumero(numeroMesa);
        int idCuenta = obtenerIdCuentaActiva(idMesa, idMesero);
        idCuentaActual = idCuenta;
        if (idCuenta == -1) return;
        String sql = "SELECT DC.ID_DETALLE, P.NOMBRE, P.PRECIO, M.NUMERO_MESA " +
                "FROM DETALLES_CUENTA DC " +
                "JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO " +
                "JOIN CUENTAS C ON DC.ID_CUENTA = C.ID_CUENTA " +
                "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                "WHERE DC.ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuenta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listaDetallesCuenta.add(new DetalleCuenta(
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("NUMERO_MESA"),
                        rs.getInt("ID_DETALLE")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar detalles de cuenta: " + e.getMessage());
        }
    }

    private void agregarPlatilloACuenta() {
        Platillo platillo = tablaPlatillos.getSelectionModel().getSelectedItem();
        Integer numeroMesa = comboMesasAsignadas.getValue();
        if (platillo == null || numeroMesa == null || idCuentaActual == -1) {
            mostrarAlerta("Error", "Selecciona una mesa y un platillo.");
            return;
        }
        int idPlatillo = obtenerIdPlatilloPorNombre(platillo.getNombre());
        String sql = "INSERT INTO DETALLES_CUENTA (ID_CUENTA, ID_PLATILLO, CANTIDAD, NOTAS) VALUES (?, ?, 1, '')";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCuentaActual);
            pstmt.setInt(2, idPlatillo);
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Platillo agregado a la cuenta.");
            cargarDetallesCuenta();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar platillo: " + e.getMessage());
        }
    }

    private int obtenerIdPlatilloPorNombre(String nombrePlatillo) {
        String sql = "SELECT ID_PLATILLO FROM PLATILLOS WHERE NOMBRE = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombrePlatillo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("ID_PLATILLO");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al buscar id platillo: " + e.getMessage());
        }
        return -1;
    }

    private void eliminarPlatilloDetalleCuenta() {
        DetalleCuenta detalle = TablaDetallesDeCuenta.getSelectionModel().getSelectedItem();
        if (detalle == null) {
            mostrarAlerta("Error", "Selecciona el platillo de la cuenta a eliminar.");
            return;
        }
        String sql = "DELETE FROM DETALLES_CUENTA WHERE ID_DETALLE = ?";
        try (Connection conn = db.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detalle.getIdDetalleCuenta());
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Platillo eliminado de la cuenta.");
            cargarDetallesCuenta();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar platillo: " + e.getMessage());
        }
    }

    private void enviarACocina() {
        mostrarAlerta("Éxito", "La orden se envió a cocina correctamente.");
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
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxml));
            javafx.scene.Parent root = loader.load();
            rootPane.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarAlerta("Error al cambiar pantalla: " + e.getMessage());
        }
    }

    private void salir() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}