package app.restaurantev2;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CategoriasController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;
    @FXML private ImageView logoImage, iconMesas, iconEstadisticas, iconCategorias, iconPlatillos, iconUsuarios, iconSalir;
    @FXML private Label adminName;
    @FXML public TextField txtnombrecategoria;
    @FXML public TextField txtDescripcionCategoria;
    @FXML public TableView<Categoria> tablaCategorias;
    @FXML public TableColumn<Categoria, String> colCategoria;
    @FXML public TableColumn<Categoria, String> colCategoriaDescripcion;
    @FXML public Button btnAnadircategoria;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public Label lblTitulo;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Categoria> listaCategorias;

    public static class Categoria {
        public int idCategoria;
        public String nombreCategoria;
        public String descripcionCategoria;

        public Categoria(int idCategoria, String nombreCategoria, String descripcionCategoria) {
            this.idCategoria = idCategoria;
            this.nombreCategoria = nombreCategoria;
            this.descripcionCategoria = descripcionCategoria;
        }
        public int getIdCategoria() { return idCategoria; }
        public String getNombreCategoria() { return nombreCategoria; }
        public String getDescripcionCategoria() { return descripcionCategoria; }
        public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
        public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
        public void setDescripcionCategoria(String descripcionCategoria) { this.descripcionCategoria = descripcionCategoria; }
    }

    // === PantallaController para navegar entre pantallas ===
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
        animateIconOnHover(btnMesas, iconMesas);
        animateIconOnHover(btnEstadisticas, iconEstadisticas);
        animateIconOnHover(btnCategorias, iconCategorias);
        animateIconOnHover(btnPlatillos, iconPlatillos);
        animateIconOnHover(btnUsuarios, iconUsuarios);
        animateIconOnHover(btnSalir, iconSalir);

        // Navegación entre pantallas
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
        listaCategorias = FXCollections.observableArrayList();

        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colCategoriaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionCategoria"));

        tablaCategorias.setItems(listaCategorias);

        cargarCategorias();

        btnAnadircategoria.setOnAction(e -> agregarCategoria());
        btnEditar.setOnAction(e -> editarCategoria());
        btnEliminar.setOnAction(e -> eliminarCategoria());
        btnActualizar.setOnAction(e -> cargarCategorias());

        tablaCategorias.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Autocompleta los campos al seleccionar una fila
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtnombrecategoria.setText(newSel.getNombreCategoria());
                txtDescripcionCategoria.setText(newSel.getDescripcionCategoria());
            }
        });
    }

    // ANIMACIÓN DE ICONOS PNG EN BOTONES
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
            mostrarAlerta("Error", "Error al cargar las categorias: " + e.getMessage());
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
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar la categoria: " + e.getMessage());
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

        // Ventana de confirmación antes de eliminar la categoría
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar eliminación");
        confirmDialog.setHeaderText("¿Seguro que deseas eliminar esta categoría?");
        confirmDialog.setContentText("Esta acción no se puede deshacer.");

        ButtonType btnSi = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmDialog.getButtonTypes().setAll(btnSi, btnNo);

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
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
                    mostrarAlerta("Error", "Error al eliminar la categoria: " + e.getMessage());
                }
            }
            // Si elige No, no hace nada
        });
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