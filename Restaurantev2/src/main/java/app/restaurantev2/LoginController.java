package app.restaurantev2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox rememberMe;

    private final MainApp.ConexionBaseDatos db = new MainApp.ConexionBaseDatos();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, completa todos los campos.");
            errorLabel.setVisible(true);
            return;
        }

        String rol = db.obtenerRolSiCredencialesValidas(email, password);

        if (rol != null) {
            errorLabel.setVisible(false);

            Stage stageActual = (Stage) emailField.getScene().getWindow();
            stageActual.close();

            try {
                Stage nuevoStage = new Stage();
                Parent root;
                Scene scene;

                if (rol.equalsIgnoreCase("ADMIN")) {
                    root = FXMLLoader.load(getClass().getResource("/app/restaurantev2/Admin.fxml"));
                    scene = new Scene(root, 1600, 900);
                    nuevoStage.setTitle("Admin - Restaurante");
                } else if (rol.equalsIgnoreCase("LIDER_MESERO")) {
                    root = FXMLLoader.load(getClass().getResource("/app/restaurantev2/LiderMeseros.fxml"));
                    scene = new Scene(root, 1600, 900);
                    nuevoStage.setTitle("Líder de Mesero - Restaurante");
                } else if (rol.equalsIgnoreCase("MESERO")) {
                    root = FXMLLoader.load(getClass().getResource("/app/restaurantev2/Mesero.fxml"));
                    scene = new Scene(root, 1600, 900);
                    nuevoStage.setTitle("Mesero - Restaurante");
                } else {
                    errorLabel.setText("Rol desconocido.");
                    errorLabel.setVisible(true);
                    return;
                }

                nuevoStage.setScene(scene);
                nuevoStage.show();

            } catch (Exception e) {
                errorLabel.setText("Error al abrir la ventana: " + e.getMessage());
                errorLabel.setVisible(true);
            }

        } else {
            errorLabel.setText("Correo o contraseña inválidos o usuario inactivo.");
            errorLabel.setVisible(true);
        }
    }
}