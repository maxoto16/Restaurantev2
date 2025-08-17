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

public class PlatillosController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;

    @FXML private ImageView logoImage, iconMesas, iconEstadisticas, iconCategorias, iconPlatillos, iconUsuarios, iconSalir;
    @FXML private Label adminName;
    @FXML public TableView<Platillo> tablaPlatillos;
    @FXML public TableColumn<Platillo, String> colNombrePlatillo;
    @FXML public TableColumn<Platillo, Double> colPrecioPlatillo;
    @FXML public TableColumn<Platillo, String> colDescripcionPlatillo;
    @FXML public TableColumn<Platillo, String> colCategoriaPlatillo;
    @FXML public TextField txtNombrePlatillo;
    @FXML public TextField txtPrecioPlatillo;
    @FXML public TextField txtDescripcionPlatillo;
    @FXML public ComboBox<String> comboCategoriaPlatillo;
    @FXML public Button btnAnadir;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public Label lblTitulo;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Platillo> listaPlatillos;
    public ObservableList<Categoria> listaCategorias;

    public static class Platillo {
        public int idPlatillo;
        public String nombrePlatillo;
        public double precioPlatillo;
        public String descripcionPlatillo;
        public String nombreCategoriaPlatillo;

        public Platillo(int idPlatillo, String nombrePlatillo, double precioPlatillo, String descripcionPlatillo, String nombreCategoriaPlatillo) {
            this.idPlatillo = idPlatillo;
            this.nombrePlatillo = nombrePlatillo;
            this.precioPlatillo = precioPlatillo;
            this.descripcionPlatillo = descripcionPlatillo;
            this.nombreCategoriaPlatillo = nombreCategoriaPlatillo;
        }
        public int getIdPlatillo() { return idPlatillo; }
        public String getNombrePlatillo() { return nombrePlatillo; }
        public double getPrecioPlatillo() { return precioPlatillo; }
        public String getDescripcionPlatillo() { return descripcionPlatillo; }
        public String getNombreCategoriaPlatillo() { return nombreCategoriaPlatillo; }
    }

    public static class Categoria {
        public int idCategoria;
        public String nombreCategoria;
        public Categoria(int idCategoria, String nombreCategoria) {
            this.idCategoria = idCategoria;
            this.nombreCategoria = nombreCategoria;
        }
        public int getIdCategoria() { return idCategoria; }
        public String getNombreCategoria() { return nombreCategoria; }
        @Override
        public String toString() { return nombreCategoria; }
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
        listaPlatillos = FXCollections.observableArrayList();
        listaCategorias = FXCollections.observableArrayList();

        colNombrePlatillo.setCellValueFactory(new PropertyValueFactory<>("nombrePlatillo"));
        colPrecioPlatillo.setCellValueFactory(new PropertyValueFactory<>("precioPlatillo"));
        colDescripcionPlatillo.setCellValueFactory(new PropertyValueFactory<>("descripcionPlatillo"));
        colCategoriaPlatillo.setCellValueFactory(new PropertyValueFactory<>("nombreCategoriaPlatillo"));

        tablaPlatillos.setItems(listaPlatillos);

        cargarCategorias();
        cargarPlatillos();

        btnAnadir.setOnAction(event -> agregarPlatillos());
        btnEditar.setOnAction(e -> editarPlatillos());
        btnEliminar.setOnAction(e -> eliminarPlatillos());
        btnActualizar.setOnAction(e -> {
            cargarCategorias();
            cargarPlatillos();
        });

        tablaPlatillos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Autocompleta los campos al seleccionar una fila
        tablaPlatillos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNombrePlatillo.setText(newSel.getNombrePlatillo());
                txtPrecioPlatillo.setText(String.valueOf(newSel.getPrecioPlatillo()));
                txtDescripcionPlatillo.setText(newSel.getDescripcionPlatillo());
                comboCategoriaPlatillo.setValue(newSel.getNombreCategoriaPlatillo());
            }
        });
    }

    // Animaciones de iconos
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
        comboCategoriaPlatillo.getItems().clear();

        String sql = "SELECT ID_CATEGORIA, NOMBRE FROM CATEGORIAS ORDER BY NOMBRE";
        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("ID_CATEGORIA"),
                        rs.getString("NOMBRE")
                );
                listaCategorias.add(categoria);
                comboCategoriaPlatillo.getItems().add(categoria.getNombreCategoria());
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar las categorías: " + e.getMessage());
        }
    }

    public void cargarPlatillos() {
        listaPlatillos.clear();

        String sql = "SELECT P.ID_PLATILLO, P.NOMBRE, P.PRECIO, P.DESCRIPCION, C.NOMBRE AS NOMBRE_CATEGORIA " +
                "FROM PLATILLOS P JOIN CATEGORIAS C ON P.ID_CATEGORIA = C.ID_CATEGORIA";

        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Platillo platillo = new Platillo(
                        rs.getInt("ID_PLATILLO"),
                        rs.getString("NOMBRE"),
                        rs.getDouble("PRECIO"),
                        rs.getString("DESCRIPCION"),
                        rs.getString("NOMBRE_CATEGORIA")
                );
                listaPlatillos.add(platillo);
            }
            tablaPlatillos.setItems(listaPlatillos);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar los platillos: " + e.getMessage());
        }
    }

    public void agregarPlatillos() {
        try {
            String nombrePlatillo = txtNombrePlatillo.getText();
            double precioPlatillo = Double.parseDouble(txtPrecioPlatillo.getText());
            String descripcionPlatillo = txtDescripcionPlatillo.getText();
            String nombreCategoria = comboCategoriaPlatillo.getValue();

            if (nombrePlatillo.isEmpty() || descripcionPlatillo.isEmpty() || nombreCategoria == null) {
                mostrarAlerta("Error", "Completa todos los campos.");
                return;
            }

            int idCategoriaPlatillo = -1;
            for (Categoria cat : listaCategorias) {
                if (cat.getNombreCategoria().equals(nombreCategoria)) {
                    idCategoriaPlatillo = cat.getIdCategoria();
                    break;
                }
            }
            if (idCategoriaPlatillo == -1) {
                mostrarAlerta("Error", "Categoría inválida.");
                return;
            }

            String sql = "INSERT INTO PLATILLOS (NOMBRE, PRECIO, DESCRIPCION, ID_CATEGORIA) VALUES (?, ?, ?, ?)";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombrePlatillo);
                pstmt.setDouble(2, precioPlatillo);
                pstmt.setString(3, descripcionPlatillo);
                pstmt.setInt(4, idCategoriaPlatillo);

                pstmt.executeUpdate();
                limpiarCampos();
                cargarPlatillos();
                mostrarAlerta("Éxito", "Platillo agregado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar platillo: " + e.getMessage());
        }
    }

    public void editarPlatillos() {
        Platillo platillo = tablaPlatillos.getSelectionModel().getSelectedItem();
        if (platillo == null) {
            mostrarAlerta("Error", "Selecciona un platillo");
            return;
        }

        try {
            String nombrePlatillo = txtNombrePlatillo.getText();
            double precioPlatillo = Double.parseDouble(txtPrecioPlatillo.getText());
            String descripcionPlatillo = txtDescripcionPlatillo.getText();
            String nombreCategoria = comboCategoriaPlatillo.getValue();

            if (nombrePlatillo.isEmpty() || descripcionPlatillo.isEmpty() || nombreCategoria == null) {
                mostrarAlerta("Error", "Completa todos los campos.");
                return;
            }

            int idCategoriaPlatillo = -1;
            for (Categoria cat : listaCategorias) {
                if (cat.getNombreCategoria().equals(nombreCategoria)) {
                    idCategoriaPlatillo = cat.getIdCategoria();
                    break;
                }
            }
            if (idCategoriaPlatillo == -1) {
                mostrarAlerta("Error", "Categoría inválida.");
                return;
            }

            String sql = "UPDATE PLATILLOS SET NOMBRE = ?, PRECIO = ?, DESCRIPCION = ?, ID_CATEGORIA = ? WHERE ID_PLATILLO = ?";
            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombrePlatillo);
                pstmt.setDouble(2, precioPlatillo);
                pstmt.setString(3, descripcionPlatillo);
                pstmt.setInt(4, idCategoriaPlatillo);
                pstmt.setInt(5, platillo.getIdPlatillo());
                pstmt.executeUpdate();

                limpiarCampos();
                cargarPlatillos();
                mostrarAlerta("Éxito", "Platillo editado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al editar platillo: " + e.getMessage());
        }
    }

    public void eliminarPlatillos() {
        Platillo platillo = tablaPlatillos.getSelectionModel().getSelectedItem();
        if (platillo == null) {
            mostrarAlerta("Error", "Selecciona un platillo");
            return;
        }

        try {
            String sql = "DELETE FROM PLATILLOS WHERE ID_PLATILLO = ?";
            try(Connection conn = db.obtenerConexion();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, platillo.getIdPlatillo());
                pstmt.executeUpdate();
                limpiarCampos();
                cargarPlatillos();
                mostrarAlerta("Éxito", "Platillo eliminado");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar el platillo: " + e.getMessage());
        }
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void limpiarCampos() {
        txtNombrePlatillo.clear();
        txtPrecioPlatillo.clear();
        txtDescripcionPlatillo.clear();
        comboCategoriaPlatillo.getSelectionModel().clearSelection();
    }
}