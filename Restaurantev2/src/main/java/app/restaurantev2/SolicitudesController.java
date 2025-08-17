package app.restaurantev2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class SolicitudesController {
    @FXML private Button btnMesas; // Botón Mesas y Meseros
    @FXML private Button btnPlandedia; // Plan de día
    @FXML private Button btnSolicitudes; // Solicitudes de meseros
    @FXML private Button btnSalir; // Botón para salir

    public static class PantallaController {
        public static void cambiarPantalla(Stage stageActual, String fxml, String titulo, int ancho, int alto) {
            try {
                FXMLLoader loader = new FXMLLoader(SolicitudesController.class.getResource(fxml));
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
        if (btnMesas != null) {
            btnMesas.setOnAction(e -> {
                Stage stageActual = (Stage) btnMesas.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/LiderMeseros.fxml", "Mesas y Meseros", 1280, 800);
            });
        }
        if (btnPlandedia != null) {
            btnPlandedia.setOnAction(e -> {
                Stage stageActual = (Stage) btnPlandedia.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/LiderdeMeseros_Plandedia.fxml", "Plan de Día - Líder de Meseros", 1280, 800);
            });
        }
        if (btnSolicitudes != null) {
            btnSolicitudes.setOnAction(e -> {
                Stage stageActual = (Stage) btnSolicitudes.getScene().getWindow();
                PantallaController.cambiarPantalla(stageActual, "/app/restaurantev2/Solicitudes.fxml", "Solicitudes de Meseros", 1280, 800);
            });
        }
        if (btnSalir != null) {
            btnSalir.setOnAction(e -> btnSalir.getScene().getWindow().hide());
        }
    }
}
