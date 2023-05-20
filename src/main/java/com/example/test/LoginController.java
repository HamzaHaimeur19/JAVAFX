package com.example.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.test.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    public void loginButtonClicked(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            // Establish a connection to the MySQL database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/equipejoueur", "root", "");

            // Create a SQL query to check if the entered credentials exist in the database
            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                messageLabel.setText("Login successful!");

                // Close the login window
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();

                // Open the equipe view
                HelloApplication.navigateToEquipe();
            } else {
                // Message d'erreur si aucun utilisateur n'est trouvé
                messageLabel.setText("Invalid username or password");
            }

            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void deconnexion() {
        // Clear the email and password fields
        emailField.clear();
        passwordField.clear();

        // Reset the message label
        messageLabel.setText("");

        // Navigate back to the login view
        HelloApplication.navigateToLogin();
    }
}
