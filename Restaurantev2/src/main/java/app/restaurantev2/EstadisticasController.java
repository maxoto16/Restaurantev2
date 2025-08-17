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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EstadisticasController {
    // Menu -----------------------------------------
    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;
    // F - Menu -----------------------------------------

    public class PantallaController {

        @FXML
        public static void cambiarPantalla(Stage stageActual, String fxml, String titulo, int ancho, int alto) {
            try {
                FXMLLoader loader = new FXMLLoader(EstadisticasController.PantallaController.class.getResource(fxml));
                Parent root = loader.load();
                Scene nuevaEscena = new Scene(root, ancho, alto);
                stageActual.setTitle(titulo);
                stageActual.setScene(nuevaEscena);
                // NO necesitas hacer stageActual.show() aquí si ya está visible
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML public TextField txtnombrecategoria;
    @FXML public TextField txtDescripcionCategoria;
    @FXML public Button btnAnadircategoria;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public TableView<Categoria> tablaCategorias;
    @FXML public TableColumn<Categoria, String> colCategoria;
    @FXML public TableColumn<Categoria, String> colCategoriaDescripcion;
    @FXML public Label lblTitulo;

    // Base de Datos ------------------------------------
    public MainApp.ConexionBaseDatos db;
    public ObservableList<Categoria> listaCategorias;
    // F - Base de Datos ------------------------------------

    // BEAN ---------------------------------------------
    public static class Categoria {
        public int idCategoria;
        public String nombreCategoria;
        public String descripcionCategoria;

        public Categoria(int idCategoria, String nombreCategoria, String descripcionCategoria) {
            this.idCategoria = idCategoria;
            this.nombreCategoria = nombreCategoria;
            this.descripcionCategoria = descripcionCategoria;
        }

        public int getIdCategoria() {
            return idCategoria;
        }

        public void setIdCategoria(int idCategoria) {
            this.idCategoria = idCategoria;
        }

        public String getNombreCategoria() {
            return nombreCategoria;
        }

        public void setNombreCategoria(String nombreCategoria) {
            this.nombreCategoria = nombreCategoria;
        }

        public String getDescripcionCategoria() {
            return descripcionCategoria;
        }

        public void setDescripcionCategoria(String descripcionCategoria) {
            this.descripcionCategoria = descripcionCategoria;
        }
    }
    // F - BEAN ---------------------------------------------

    @FXML
    public void initialize() {
        btnMesas.setOnAction(e -> {
            Stage stageActual = (Stage) btnMesas.getScene().getWindow();
            AdminController.PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Admin.fxml", "Admin - Restaurante", 1600, 900);
        });
        btnEstadisticas.setOnAction(e -> {
            Stage stageActual = (Stage) btnEstadisticas.getScene().getWindow();
            AdminController.PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Estadisticas.fxml", "Estadísticas - Restaurante", 1600, 900);
        });
        btnCategorias.setOnAction(e -> {
            Stage stageActual = (Stage) btnCategorias.getScene().getWindow();
            AdminController.PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Categorias.fxml", "Categorias - Restaurante", 1600, 900);
        });
        btnPlatillos.setOnAction(e -> {
            Stage stageActual = (Stage) btnPlatillos.getScene().getWindow();
            AdminController.PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Platillos.fxml", "Platillos - Restaurante", 1600, 900);
        });
        btnUsuarios.setOnAction(e -> {
            Stage stageActual = (Stage) btnUsuarios.getScene().getWindow();
            AdminController.PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Usuarios.fxml", "Usuarios - Restaurante", 1600, 900);
        });
        btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());


        db = new MainApp.ConexionBaseDatos();
        listaCategorias = FXCollections.observableArrayList();

        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colCategoriaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionCategoria"));

        tablaCategorias.setItems(listaCategorias);

        cargarCategorias();

        btnAnadircategoria.setOnAction(e -> agregarCategoria());
        btnEditar.setOnAction(e -> editarCategoria());
        btnEliminar.setOnAction(e -> eliminarCategoria());
        btnActualizar.setOnAction(e -> editarCategoria());

        tablaCategorias.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void cargarCategorias() {
        listaCategorias.clear();

        String sql = "SELECT * FROM CATEGORIAS";

        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("ID_CATEGORIA"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION")
                );
                listaCategorias.add(categoria);
            }
            tablaCategorias.setItems(listaCategorias);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar las mesas: " + e.getMessage());
        }
    }

    public void agregarCategoria() {
        try {
            String nombreCategoria = txtnombrecategoria.getText();
            String descripcionCategoria = txtDescripcionCategoria.getText();

            if (nombreCategoria.isEmpty() || descripcionCategoria.isEmpty()) {
                mostrarAlerta("Error", "Coloca toda la información");
                return;
            }

            String sql = "INSERT INTO CATEGORIAS (NOMBRE, DESCRIPCION) VALUES (?, ?)";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombreCategoria);
                pstmt.setString(2, descripcionCategoria);
                pstmt.executeUpdate();
                limpiarCampos();
                cargarCategorias();
                mostrarAlerta("Éxito", "Categoria agregada");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos número de mesa y capacidad deben ser números");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar la mesa: " + e.getMessage());
        }
    }

    public void editarCategoria() {
        Categoria categoria = tablaCategorias.getSelectionModel().getSelectedItem();
        if (categoria == null) {
            mostrarAlerta("Error", "Seleccione una categoria");
            return;
        }

        try {
            String nombreCategoria = txtnombrecategoria.getText();
            String descripcionCategoria = txtDescripcionCategoria.getText();

            String sql = "UPDATE CATEGORIAS SET NOMBRE = ?, DESCRIPCION = ? WHERE ID_CATEGORIA = ?";

            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreCategoria);
                pstmt.setString(2, descripcionCategoria);
                pstmt.setInt(3, categoria.getIdCategoria());

                pstmt.executeUpdate();
                limpiarCampos();
                cargarCategorias();
                mostrarAlerta("Éxito", "Categoria editada");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar la Categoría: " + e.getMessage());
        }
    }

    public void eliminarCategoria() {
        Categoria categoria = tablaCategorias.getSelectionModel().getSelectedItem();
        if (categoria == null) {
            mostrarAlerta("Error", "Seleccione una categoria");
            return;
        }

        try {
            String sql = "DELETE FROM CATEGORIAS WHERE ID_CATEGORIA = ?";
            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, categoria.getIdCategoria());
                pstmt.executeUpdate();
                limpiarCampos();
                cargarCategorias();
                mostrarAlerta("Éxito", "Categoria eliminada");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar la mesa: " + e.getMessage());
        }
    }

    public void limpiarCampos() {
        txtnombrecategoria.clear();
        txtDescripcionCategoria.clear();
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }


}
