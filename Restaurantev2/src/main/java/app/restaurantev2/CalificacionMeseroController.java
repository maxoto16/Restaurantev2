package app.restaurantev2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CalificacionMeseroController {

    @FXML private ToggleGroup estrellasGroup;
    @FXML private RadioButton estrella1;
    @FXML private RadioButton estrella2;
    @FXML private RadioButton estrella3;
    @FXML private RadioButton estrella4;
    @FXML private RadioButton estrella5;
    @FXML private TextArea txtComentario;
    @FXML private Button btnEnviar;
    @FXML private Button btnCancelar;

    private int idMesero;
    private int idCuenta;
    public MainApp.ConexionBaseDatos db;

    public void inicializarDatos(int idMesero, int idCuenta) {
        this.idMesero = idMesero;
        this.idCuenta = idCuenta;
        db = new MainApp.ConexionBaseDatos();
    }

    @FXML
    public void initialize() {
        btnEnviar.setOnAction(e -> guardarCalificacion());
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    private void guardarCalificacion() {
        int estrellas = getEstrellasSeleccionadas();
        String comentario = txtComentario.getText();

        if (estrellas == 0) {
            mostrarAlerta("Selecciona una calificación de 1 a 5 estrellas.");
            return;
        }

        String sql = "INSERT INTO CALIFICACIONES (ID_MESERO, ID_CUENTA, ESTRELLAS, COMENTARIO) VALUES (?, ?, ?, ?)";
        try (java.sql.Connection conn = db.obtenerConexion();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMesero);
            pstmt.setInt(2, idCuenta);
            pstmt.setInt(3, estrellas);
            pstmt.setString(4, comentario);
            pstmt.executeUpdate();
            mostrarAlerta("¡Gracias por tu calificación!");
            cerrarVentana();
        } catch (Exception ex) {
            mostrarAlerta("Error al guardar calificación: " + ex.getMessage());
        }
    }

    private int getEstrellasSeleccionadas() {
        if (estrella5.isSelected()) return 5;
        if (estrella4.isSelected()) return 4;
        if (estrella3.isSelected()) return 3;
        if (estrella2.isSelected()) return 2;
        if (estrella1.isSelected()) return 1;
        return 0;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calificación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}