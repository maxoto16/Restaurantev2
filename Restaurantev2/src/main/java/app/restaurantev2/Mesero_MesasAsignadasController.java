package app.restaurantev2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class Mesero_MesasAsignadasController {
    @FXML private Button btnHorariolaboral; // Botón Mesas y Meseros
    @FXML private Button btnMesasasignadas; // Plan de día
    @FXML private Button btnTomarorden; // Solicitudes de meseros
    @FXML private Button btnSalir; // Botón para salir

    // Clase utilitaria para cambiar de pantalla
    public static class PantallaController {
        public static void cambiarPantalla(Stage stageActual, String fxml, String titulo, int ancho, int alto) {
            try {
                FXMLLoader loader = new FXMLLoader(LiderMeserosController.class.getResource(fxml));
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
        if (btnHorariolaboral != null) {
            btnHorariolaboral.setOnAction(e -> {
                Stage stageActual = (Stage) btnHorariolaboral.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Mesero.fxml", "Horario Laboral", 1600, 900);
            });
        }
        if (btnMesasasignadas != null) {
            btnMesasasignadas.setOnAction(e -> {
                Stage stageActual = (Stage) btnMesasasignadas.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Mesero_NuevaOrden.fxml", "Mesas Asignadas", 1600, 900);
            });
        }
        if (btnTomarorden!= null) {
            btnTomarorden.setOnAction(e -> {
                Stage stageActual = (Stage) btnTomarorden.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Mesero_Cuentas.fxml", "Tomarorden", 1600, 900);
            });
        }
        if (btnSalir != null) {
            btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());
        }
    }
}