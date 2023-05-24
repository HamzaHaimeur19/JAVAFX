package com.example.test;

import com.example.test.HelloApplication;
import com.example.test.entities.Equipe;
import com.example.test.service.EquipeService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EquipeController {

    @FXML
    private Label welcomeText;

    @FXML
    private TableView<Equipe> equipeTable;

    @FXML
    private TableColumn<Equipe, Integer> columnEquipeId;

    @FXML
    private TableColumn<Equipe, String> columnEquipeNom;

    @FXML
    private TextField nomField;

    EquipeService equipeService = new EquipeService();

    public void initialize() {
        // Set the selection mode to SINGLE
        equipeTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // colonnes des tables
        columnEquipeId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        columnEquipeNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomEquipe()));

        // Enable editing for the columnEquipeNom
        equipeTable.setEditable(true);
        columnEquipeNom.setCellFactory(TextFieldTableCell.forTableColumn());
        columnEquipeNom.setOnEditCommit(event -> {
            Equipe equipe = event.getRowValue();
            String newNom = event.getNewValue().trim();

            if (newNom.isEmpty()) {
                showAlert("Nom d'équipe requis");
                return;
            }

            equipe.setNomEquipe(newNom);
            equipeService.update(equipe);
        });

        // fixer la taille des colonnes
        equipeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // remplir la table avec les données
        List<Equipe> equipeList = equipeService.findAll();
        equipeTable.getItems().addAll(equipeList);

        // Add the "Supprimer" button column
        TableColumn<Equipe, Void> deleteButtonColumn = new TableColumn<>("Supprimer");
        deleteButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Equipe equipe = getTableView().getItems().get(getIndex());
                    equipeTable.getItems().remove(equipe);
                    equipeService.remove(equipe);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        equipeTable.getColumns().add(deleteButtonColumn);
    }

    @FXML
    private void navigateToJoueur() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
        HelloApplication.navigateToJoueur();
    }

    @FXML
    void ajouterEquipe() {
        String nom = nomField.getText().trim();

        if (nom.isEmpty()) {
            showAlert("Nom d'équipe requis");
            return;
        }

        // Check if the equipe already exists
        boolean equipeExists = equipeTable.getItems().stream()
                .anyMatch(equipe -> equipe.getNomEquipe().equalsIgnoreCase(nom));

        if (equipeExists) {
            showAlert("L'équipe existe déjà");
            return;
        }

        Equipe equipe = equipeTable.getSelectionModel().getSelectedItem();

        if (equipe == null) {
            equipe = new Equipe(0, nom);
            equipeService.save(equipe);
            equipeTable.getItems().add(equipe);
        } else {
            equipe.setNomEquipe(nom);
            equipeService.update(equipe);
            equipeTable.getSelectionModel().clearSelection();
        }

        nomField.clear();
    }

    @FXML
    void AnnulerEquipe() {
        nomField.clear();
    }

    @FXML
    private void deconnexion() {
        try {
            // Load the LoginView.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            // Create a new stage and set the login view as the root
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 600));

            // Close the current stage
            Stage currentStage = (Stage) welcomeText.getScene().getWindow();
            currentStage.close();

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.toFront();

        alert.showAndWait();
    }
}