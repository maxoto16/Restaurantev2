package app.restaurantev2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UsuariosController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;
    @FXML private ImageView logoImage, iconMesas, iconEstadisticas, iconCategorias, iconPlatillos, iconUsuarios, iconSalir;
    @FXML private Label adminName;
    @FXML public TextField txtCorreo;
    @FXML public TextField txtContraseña;
    @FXML public TextField txtTipoCuenta;
    @FXML public TextField txtNombre;
    @FXML public TextField txtEstado;
    @FXML public TextField txtSueldo;
    @FXML public Button btnAgregarUsuario;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public TableView<Usuario> tablaUsuarios;
    @FXML public TableColumn<Usuario, String> colCorreo;
    @FXML public TableColumn<Usuario, String> colContraseña;
    @FXML public TableColumn<Usuario, String> colTipoCuenta;
    @FXML public TableColumn<Usuario, String> colNombre;
    @FXML public TableColumn<Usuario, String> colEstado;
    @FXML public TableColumn<Usuario, Double> colSueldo;
    @FXML public TableColumn<Usuario, String> colFechaRegistro;
    @FXML public Label lblTitulo;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Usuario> listaUsuarios;

    public static class Usuario {
        public int idUsuario;
        public String nombre;
        public String correo;
        public String contrasena;
        public String rol;
        public String estado;
        public double sueldo;
        public String fechaRegistro;

        public Usuario(int idUsuario, String nombre, String correo, String contrasena, String rol, String estado, double sueldo, String fechaRegistro) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.correo = correo;
            this.contrasena = contrasena;
            this.rol = rol;
            this.estado = estado;
            this.sueldo = sueldo;
            this.fechaRegistro = fechaRegistro;
        }
        public int getIdUsuario() { return idUsuario; }
        public String getNombre() { return nombre; }
        public String getCorreo() { return correo; }
        public String getContrasena() { return contrasena; }
        public String getRol() { return rol; }
        public String getEstado() { return estado; }
        public double getSueldo() { return sueldo; }
        public String getFechaRegistro() { return fechaRegistro; }
    }

    public static class PantallaController {
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
        animateIconOnHover(btnMesas, iconMesas);
        animateIconOnHover(btnEstadisticas, iconEstadisticas);
        animateIconOnHover(btnCategorias, iconCategorias);
        animateIconOnHover(btnPlatillos, iconPlatillos);
        animateIconOnHover(btnUsuarios, iconUsuarios);
        animateIconOnHover(btnSalir, iconSalir);

        btnMesas.setOnAction(e -> {
            Stage stageActual = (Stage) btnMesas.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Admin.fxml", "Admin - Restaurante", 1600, 900);
        });
        btnEstadisticas.setOnAction(e -> {
            Stage stageActual = (Stage) btnEstadisticas.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Estadisticas.fxml", "Estadísticas - Restaurante", 1600, 900);
        });
        btnCategorias.setOnAction(e -> {
            Stage stageActual = (Stage) btnCategorias.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Categorias.fxml", "Categorias - Restaurante", 1600, 900);
        });
        btnPlatillos.setOnAction(e -> {
            Stage stageActual = (Stage) btnPlatillos.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Platillos.fxml", "Platillos - Restaurante", 1600, 900);
        });
        btnUsuarios.setOnAction(e -> {
            Stage stageActual = (Stage) btnUsuarios.getScene().getWindow();
            PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Usuarios.fxml", "Usuarios - Restaurante", 1600, 900);
        });
        btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());

        db = new MainApp.ConexionBaseDatos();
        listaUsuarios = FXCollections.observableArrayList();

        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colContraseña.setCellValueFactory(new PropertyValueFactory<>("contrasena"));
        colTipoCuenta.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colSueldo.setCellValueFactory(new PropertyValueFactory<>("sueldo"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        tablaUsuarios.setItems(listaUsuarios);

        cargarUsuarios();

        btnAgregarUsuario.setOnAction(e -> agregarUsuario());
        btnEditar.setOnAction(e -> editarUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());
        btnActualizar.setOnAction(e -> cargarUsuarios());

        tablaUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Autocompleta los campos al seleccionar una fila
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombre.setText(newSel.getNombre());
                txtCorreo.setText(newSel.getCorreo());
                txtContraseña.setText(newSel.getContrasena());
                txtTipoCuenta.setText(newSel.getRol());
                txtEstado.setText(newSel.getEstado());
                txtSueldo.setText(String.valueOf(newSel.getSueldo()));
            }
        });
    }

    // Animación de iconos PNG en los botones del menú lateral
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

    public void cargarUsuarios() {
        listaUsuarios.clear();

        String sql = "SELECT * FROM USUARIOS";

        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("ID_USUARIO"),
                        rs.getString("NOMBRE"),
                        rs.getString("CORREO"),
                        rs.getString("CONTRASENA"),
                        rs.getString("ROL"),
                        rs.getString("ESTADO"),
                        rs.getDouble("SUELDO"),
                        rs.getString("FECHA_REGISTRO")
                );
                listaUsuarios.add(usuario);
            }
            tablaUsuarios.setItems(listaUsuarios);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar los usuarios: " + e.getMessage());
        }
    }

    public void agregarUsuario() {
        try {
            String correo = txtCorreo.getText();
            String contrasena = txtContraseña.getText();
            String rol = txtTipoCuenta.getText();
            String nombre = txtNombre.getText();
            String estado = txtEstado.getText();
            String sueldoStr = txtSueldo.getText();

            if (correo.isEmpty() || contrasena.isEmpty() || rol.isEmpty() || nombre.isEmpty() || estado.isEmpty() || sueldoStr.isEmpty()) {
                mostrarAlerta("Error", "Coloca toda la información");
                return;
            }

            double sueldo;
            try {
                sueldo = Double.parseDouble(sueldoStr);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "El sueldo debe ser un número");
                return;
            }

            String sql = "INSERT INTO USUARIOS (NOMBRE, CORREO, CONTRASENA, ROL, ESTADO, SUELDO) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, correo);
                pstmt.setString(3, contrasena);
                pstmt.setString(4, rol);
                pstmt.setString(5, estado);
                pstmt.setDouble(6, sueldo);
                pstmt.executeUpdate();

                limpiarCampos();
                cargarUsuarios();
                mostrarAlerta("Éxito", "Usuario agregado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar el usuario: " + e.getMessage());
        }
    }

    public void editarUsuario() {
        Usuario usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) {
            mostrarAlerta("Error", "Seleccione un usuario");
            return;
        }

        try {
            String correo = txtCorreo.getText();
            String contrasena = txtContraseña.getText();
            String rol = txtTipoCuenta.getText();
            String nombre = txtNombre.getText();
            String estado = txtEstado.getText();
            String sueldoStr = txtSueldo.getText();

            if (correo.isEmpty() || contrasena.isEmpty() || rol.isEmpty() || nombre.isEmpty() || estado.isEmpty() || sueldoStr.isEmpty()) {
                mostrarAlerta("Error", "Coloca toda la información para editar");
                return;
            }

            double sueldo;
            try {
                sueldo = Double.parseDouble(sueldoStr);
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "El sueldo debe ser un número");
                return;
            }

            String sql = "UPDATE USUARIOS SET NOMBRE = ?, CORREO = ?, CONTRASENA = ?, ROL = ?, ESTADO = ?, SUELDO = ? WHERE ID_USUARIO = ?";

            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombre);
                pstmt.setString(2, correo);
                pstmt.setString(3, contrasena);
                pstmt.setString(4, rol);
                pstmt.setString(5, estado);
                pstmt.setDouble(6, sueldo);
                pstmt.setInt(7, usuario.getIdUsuario());

                pstmt.executeUpdate();
                limpiarCampos();
                cargarUsuarios();
                mostrarAlerta("Éxito", "Usuario editado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar el usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario() {
        Usuario usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) {
            mostrarAlerta("Error", "Seleccione un usuario");
            return;
        }

        try {
            String sql = "DELETE FROM USUARIOS WHERE ID_USUARIO = ?";
            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuario.getIdUsuario());
                pstmt.executeUpdate();
                limpiarCampos();
                cargarUsuarios();
                mostrarAlerta("Éxito", "Usuario eliminado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar el usuario: " + e.getMessage());
        }
    }

    public void limpiarCampos() {
        txtCorreo.clear();
        txtContraseña.clear();
        txtTipoCuenta.clear();
        txtNombre.clear();
        txtEstado.clear();
        txtSueldo.clear();
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}