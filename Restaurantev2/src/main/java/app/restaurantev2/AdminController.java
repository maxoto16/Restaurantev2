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

public class AdminController {
    @FXML private BorderPane rootPane;
    @FXML private Button btnMesas, btnEstadisticas, btnCategorias, btnPlatillos, btnUsuarios, btnSalir;
    @FXML private ImageView logoImage, iconMesas, iconEstadisticas, iconCategorias, iconPlatillos, iconUsuarios, iconSalir;
    @FXML private Label adminName;
    @FXML public TextField txtNumeroMesa;
    @FXML public TextField txtCapacidad;
    @FXML public TextField txtArea;
    @FXML public TableView<Mesa> tablaMesas;
    @FXML public TableColumn<Mesa, Integer> colNumero;
    @FXML public TableColumn<Mesa, Integer> colCapacidad;
    @FXML public TableColumn<Mesa, String> colArea;
    @FXML public Button btnAgregarMesa;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public Label lblTitulo;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Mesa> listaMesas;

    public static class Mesa {
        private int idMesa;
        private int numeroMesa;
        private int capacidad;
        private String area;
        private String estado;

        public Mesa(int idMesa, int numeroMesa, int capacidad, String area, String estado) {
            this.idMesa = idMesa;
            this.numeroMesa = numeroMesa;
            this.capacidad = capacidad;
            this.area = area;
            this.estado = estado;
        }
        public int getIdMesa() { return idMesa; }
        public void setIdMesa(int idMesa) { this.idMesa = idMesa; }
        public int getNumeroMesa() { return numeroMesa; }
        public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }
        public int getCapacidad() { return capacidad; }
        public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
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
        listaMesas = FXCollections.observableArrayList();

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));

        tablaMesas.setItems(listaMesas);

        cargarMesas();

        btnAgregarMesa.setOnAction(e -> agregarMesa());
        btnEditar.setOnAction(e -> editarMesa());
        btnEliminar.setOnAction(e -> eliminarMesa());
        btnActualizar.setOnAction(e -> cargarMesas());

        tablaMesas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // SOMBREA Y AUTOCOMPLETA CAMPOS AL SELECCIONAR REGISTRO
        tablaMesas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtNumeroMesa.setText(String.valueOf(newSel.getNumeroMesa()));
                txtCapacidad.setText(String.valueOf(newSel.getCapacidad()));
                txtArea.setText(newSel.getArea());
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

    public void cargarMesas() {
        listaMesas.clear();
        String sql = "SELECT * FROM MESAS ORDER BY NUMERO_MESA";
        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mesa mesa = new Mesa(
                        rs.getInt("ID_MESA"),
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

    public void agregarMesa() {
        try {
            int numeroMesa = Integer.parseInt(txtNumeroMesa.getText().trim());
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            String area = txtArea.getText().trim();

            if (area.isEmpty()) {
                mostrarAlerta("Error", "El campo 'Area' no puede estar vacío.");
                return;
            }

            String sql = "INSERT INTO MESAS (NUMERO_MESA, CAPACIDAD, UBICACION, ESTADO) VALUES (?, ?, ?, 'DISPONIBLE')";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, numeroMesa);
                pstmt.setInt(2, capacidad);
                pstmt.setString(3, area);

                pstmt.executeUpdate();
                limpiarCampos();
                cargarMesas();
                mostrarAlerta("Éxito", "Mesa agregada correctamente");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos número de mesa y capacidad deben ser números");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar la mesa: " + e.getMessage());
        }
    }

    public void editarMesa() {
        Mesa mesaSeleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, seleccione una mesa para editar");
            return;
        }
        try {
            int numeroMesa = Integer.parseInt(txtNumeroMesa.getText().trim());
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            String area = txtArea.getText().trim();

            String sql = "UPDATE MESAS SET NUMERO_MESA = ?, CAPACIDAD = ?, UBICACION = ? WHERE ID_MESA = ?";
            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, numeroMesa);
                pstmt.setInt(2, capacidad);
                pstmt.setString(3, area);
                pstmt.setInt(4, mesaSeleccionada.getIdMesa());

                pstmt.executeUpdate();
                limpiarCampos();
                cargarMesas();
                mostrarAlerta("Éxito", "Mesa actualizada correctamente");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos número de mesa y capacidad deben ser números");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar la mesa: " + e.getMessage());
        }
    }

    public void eliminarMesa() {
        Mesa mesaSeleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, seleccione una mesa para eliminar");
            return;
        }

        // Confirmación antes de eliminar
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmar eliminación");
        confirmDialog.setHeaderText("¿Seguro que deseas eliminar esta mesa?");
        confirmDialog.setContentText("Esta acción no se puede deshacer.");

        ButtonType btnSi = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmDialog.getButtonTypes().setAll(btnSi, btnNo);

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                try {
                    String sql = "DELETE FROM MESAS WHERE ID_MESA = ?";
                    try (Connection conn = db.obtenerConexion();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, mesaSeleccionada.getIdMesa());
                        pstmt.executeUpdate();
                        cargarMesas();
                        mostrarAlerta("Éxito", "Mesa eliminada correctamente");
                    }
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al eliminar la mesa: " + e.getMessage());
                }
            }
            // Si elige No, no hace nada
        });
    }

    public void limpiarCampos() {
        txtNumeroMesa.clear();
        txtCapacidad.clear();
        txtArea.clear();
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}