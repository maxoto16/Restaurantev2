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
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LiderMeserosController {
    @FXML private BorderPane rootPane;
    @FXML private Button btninicio, btnPlandedia, btnSalir;
    @FXML private Label lblTitulo1;

    @FXML public TableView<Solicitud> tablaSOLICITUDES_MODIFICACION;
    @FXML public TableColumn<Solicitud, Integer> col_ID_CUENTA;
    @FXML public TableColumn<Solicitud, String> col_ID_MESERO;
    @FXML public TableColumn<Solicitud, String> colMOTIVO;
    @FXML public TableColumn<Solicitud, String> colESTADO_SOLICITUD;
    @FXML public Button btnAceptar, btnDenegar, btnDetalles;

    @FXML public TableView<Mesa> tablaMesas;
    @FXML public TableColumn<Mesa, Integer> colNUMERO_MESA;
    @FXML public TableColumn<Mesa, Integer> colCAPACIDAD;
    @FXML public TableColumn<Mesa, String> colUBICACION;
    @FXML public TableColumn<Mesa, String> colESTADO_MESA;

    @FXML public TableView<Mesero> tablaUSUARIOS_ROLMESERO;
    @FXML public TableColumn<Mesero, String> colNombre_mesero;
    @FXML public TableColumn<Mesero, String> colESTADO_USUARIO;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Solicitud> listaSolicitudes;
    public ObservableList<Mesa> listaMesas;
    public ObservableList<Mesero> listaMeseros;

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

    // Para mostrar los detalles de la cuenta adicionalmente en la ventana emergente
    public static class PlatilloDetalle {
        public String nombrePlatillo;
        public double precio;
        public int cantidad;
        public int numeroMesa;
        public String area;

        public PlatilloDetalle(String nombrePlatillo, double precio, int cantidad, int numeroMesa, String area) {
            this.nombrePlatillo = nombrePlatillo;
            this.precio = precio;
            this.cantidad = cantidad;
            this.numeroMesa = numeroMesa;
            this.area = area;
        }
        public String getNombrePlatillo() { return nombrePlatillo; }
        public double getPrecio() { return precio; }
        public int getCantidad() { return cantidad; }
        public int getNumeroMesa() { return numeroMesa; }
        public String getArea() { return area; }
    }

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
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/LiderMeseros.fxml", "Inicio - Lider Meseros", 1600, 900);
        });
        btnPlandedia.setOnAction(e -> {
            Stage stageActual = (Stage) btnPlandedia.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/LiderdeMeseros_Plandedia.fxml", "Plan del Día - Lider Meseros", 1600, 900);
        });

        btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());

        col_ID_CUENTA.setCellValueFactory(new PropertyValueFactory<>("idCuenta"));
        col_ID_MESERO.setCellValueFactory(new PropertyValueFactory<>("idMesero"));
        colMOTIVO.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colESTADO_SOLICITUD.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaSOLICITUDES_MODIFICACION.setItems(listaSolicitudes);

        btnAceptar.setOnAction(e -> aceptarSolicitud());
        btnDenegar.setOnAction(e -> denegarSolicitud());
        btnDetalles.setOnAction(e -> mostrarDetallesSolicitud());

        colNUMERO_MESA.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));
        colCAPACIDAD.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colUBICACION.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colESTADO_MESA.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaMesas.setItems(listaMesas);

        colNombre_mesero.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colESTADO_USUARIO.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tablaUSUARIOS_ROLMESERO.setItems(listaMeseros);

        cargarSolicitudes();
        cargarMesas();
        cargarMeseros();
    }

    public void cargarSolicitudes() {
        listaSolicitudes.clear();
        String sql = "SELECT ID_CUENTA, ID_MESERO, MOTIVO, ESTADO FROM SOLICITUDES_MODIFICACION WHERE ESTADO = 'PENDIENTE'";
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

                listaSolicitudes.remove(solicitud);
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

                listaSolicitudes.remove(solicitud);
                mostrarAlerta("Éxito", "Solicitud denegada.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al denegar la solicitud: " + e.getMessage());
        }
    }

    // MODIFICADA: muestra ventana emergente con motivo, estado, cuenta, mesero
    // y además los detalles de la cuenta: platillos, precio, cantidad, mesa, area
    public void mostrarDetallesSolicitud() {
        Solicitud solicitud = tablaSOLICITUDES_MODIFICACION.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarAlerta("Error", "Selecciona una solicitud.");
            return;
        }

        // Consulta los detalles de la cuenta asociada
        ObservableList<PlatilloDetalle> detallesPlatillos = FXCollections.observableArrayList();
        String sqlDetalles =
                "SELECT P.NOMBRE AS NOMBRE_PLATILLO, P.PRECIO, DC.CANTIDAD, M.NUMERO_MESA, M.UBICACION " +
                        "FROM DETALLES_CUENTA DC " +
                        "JOIN PLATILLOS P ON DC.ID_PLATILLO = P.ID_PLATILLO " +
                        "JOIN CUENTAS C ON DC.ID_CUENTA = C.ID_CUENTA " +
                        "JOIN MESAS M ON C.ID_MESA = M.ID_MESA " +
                        "WHERE DC.ID_CUENTA = ?";
        try (Connection conn = db.obtenerConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlDetalles)) {
            pstmt.setInt(1, solicitud.getIdCuenta());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                detallesPlatillos.add(new PlatilloDetalle(
                        rs.getString("NOMBRE_PLATILLO"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("CANTIDAD"),
                        rs.getInt("NUMERO_MESA"),
                        rs.getString("UBICACION")
                ));
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron obtener los detalles de la cuenta: " + e.getMessage());
            return;
        }

        StringBuilder detalles = new StringBuilder();
        detalles.append("Cuenta: ").append(solicitud.getIdCuenta())
                .append("\nMesero: ").append(solicitud.getIdMesero())
                .append("\nMotivo: ").append(solicitud.getMotivo())
                .append("\nEstado: ").append(solicitud.getEstado())
                .append("\n-----\n");

        if (detallesPlatillos.isEmpty()) {
            detalles.append("La cuenta no tiene platillos.\n");
        } else {
            detalles.append("Platillos de la cuenta:\n");
            for (PlatilloDetalle pd : detallesPlatillos) {
                detalles.append("Platillo: ").append(pd.getNombrePlatillo())
                        .append(" | Precio: $").append(pd.getPrecio())
                        .append(" | Cantidad: ").append(pd.getCantidad())
                        .append(" | Mesa: ").append(pd.getNumeroMesa())
                        .append(" | Área: ").append(pd.getArea())
                        .append("\n");
            }
        }
        mostrarAlerta("Detalles de Solicitud", detalles.toString());
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}