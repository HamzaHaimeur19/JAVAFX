package com.example.test;

import com.example.test.entities.Equipe;
import com.example.test.entities.Joueur;
import com.example.test.service.EquipeService;
import com.example.test.service.JoueurService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

public class JoueurController {
    @FXML
    private Label welcomeText;

    @FXML
    private TableView<Joueur> joueurTable;

    @FXML
    private TableColumn<Joueur, Integer> columnJoueurId;

    @FXML
    private TableColumn<Joueur, String> columnJoueurNom;

    @FXML
    private TableColumn<Joueur, String> columnJoueurPrenom;

    @FXML
    private TableColumn<Joueur, Double> columnJoueurSalaire;

    @FXML
    private TableColumn<Joueur, Integer> columnJoueurNumero;

    @FXML
    private TableColumn<Joueur, Integer> columnJoueurMatch;

    @FXML
    private TableColumn<Joueur, Integer> columnJoueurButs;

    @FXML
    private TableColumn<Joueur, String> columnJoueurPoste;

    @FXML
    private TableColumn<Joueur, Equipe> columnJoueurClub;

    @FXML
    private TableColumn<Joueur, Date> columnJoueurDN;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField salaireField;

    @FXML
    private TextField numeroField;

    @FXML
    private TextField matchField;

    @FXML
    private TextField butsField;

    @FXML
    private TextField posteField;


    @FXML
    private TextField dateField;

    @FXML
    private TableColumn<Joueur, Void> deleteButtonColumn;

    @FXML
    private TableColumn<Joueur, Void> updateButtonColumn;

    @FXML
    private ComboBox<String> equipeComboBox;
    @FXML
    private ComboBox ModifierequipeComboBox;

    public void initialize() {
        JoueurService joueurService = new JoueurService();
        EquipeService equipeService = new EquipeService();
        List<Equipe> equipeList = equipeService.findAll();
        for (Equipe equipe : equipeList) {
            equipeComboBox.getItems().add(equipe.getNomEquipe());
        }

        // colonnes des tables
        columnJoueurId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        columnJoueurNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        columnJoueurPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        columnJoueurSalaire.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSalaire()).asObject());
        columnJoueurNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNumero()).asObject());
        columnJoueurMatch.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMatchs()).asObject());
        columnJoueurButs.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getButs()).asObject());
        columnJoueurPoste.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPoste()));
        columnJoueurClub.setCellValueFactory(cellData -> {
            Equipe equipe = cellData.getValue().getEquipe();
            String equipeName = equipe != null ? equipe.getNomEquipe() : "Aucune equipe";
            return new SimpleObjectProperty(equipeName);
        });
        columnJoueurDN.setCellValueFactory(cellData -> {
            Date dateNaissance = new Date(cellData.getValue().getDateNaissance().getTime());
            return new SimpleObjectProperty<>(dateNaissance);
        });

        // fixer la taille des colonnes
        joueurTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // remplir la table avec les données
        List<Joueur> joueurList = joueurService.findAll();
        joueurTable.getItems().addAll(joueurList);

        deleteButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Joueur joueur = getTableView().getItems().get(getIndex());
                    joueurTable.getItems().remove(joueur);
                    joueurService.remove(joueur);
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

        updateButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Selectionner");

            {
                updateButton.setOnAction(event -> {
                    Joueur joueur = getTableView().getItems().get(getIndex());
                    nomField.setText(joueur.getNom());
                    prenomField.setText(joueur.getPrenom());
                    salaireField.setText(String.valueOf(joueur.getSalaire()));
                    numeroField.setText(String.valueOf(joueur.getNumero()));
                    matchField.setText(String.valueOf(joueur.getMatchs()));
                    butsField.setText(String.valueOf(joueur.getButs()));
                    posteField.setText(joueur.getPoste());
                    if (joueur.getEquipe() == null) {
                        equipeComboBox.getSelectionModel().clearSelection();
                    } else {
                        equipeComboBox.getSelectionModel().select(joueur.getEquipe().getNomEquipe());
                    }
                    dateField.setText(String.valueOf(joueur.getDateNaissance()));

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                }
            }
        });
    }


    @FXML
    private void EquipeHandler() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
        HelloApplication.navigateToEquipe();
    }

    @FXML
    void addJoueur() {
        JoueurService joueurService = new JoueurService();
        EquipeService equipeService = new EquipeService();

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        double salaire;
        int numero;
        int matchs;
        int buts;
        String poste;
        Date dateNaissance;

        // Check for null or empty values
        if (nom.isEmpty() || prenom.isEmpty() || salaireField.getText().isEmpty() ||
                numeroField.getText().isEmpty() || matchField.getText().isEmpty() ||
                butsField.getText().isEmpty() || posteField.getText().isEmpty() ||
                dateField.getText().isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            salaire = Double.parseDouble(salaireField.getText());
            numero = Integer.parseInt(numeroField.getText());
            matchs = Integer.parseInt(matchField.getText());
            buts = Integer.parseInt(butsField.getText());
            poste = posteField.getText();
            dateNaissance = Date.valueOf(dateField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Les valeurs numériques / date doivent être valides.");
            return;
        }

        String equipeN = equipeComboBox.getSelectionModel().getSelectedItem();

        Equipe equipe = equipeService.findByName(equipeN);

        if (equipe == null) {
            showAlert("Club introuvable", "Club introuvable ! Entrez un club existant.");
            return;
        }

        Joueur joueur = joueurTable.getSelectionModel().getSelectedItem();

        if (joueur == null) {
            // No selected item, create a new one
            joueur = new Joueur(0, nom, prenom, salaire, numero, matchs, buts, poste, equipe, dateNaissance);
            joueurService.save(joueur);
            joueurTable.getItems().add(joueur);
        } else {
            // Selected item exists, modify it
            String newNom = nomField.getText();
            String newPrenom = prenomField.getText();
            double newSalaire;
            int newNumero;
            int newMatchs;
            int newButs;
            String newPoste;
            Date newDateNaissance;

            // Check for null or empty values
            if (newNom.isEmpty() || newPrenom.isEmpty() || salaireField.getText().isEmpty() ||
                    numeroField.getText().isEmpty() || matchField.getText().isEmpty() ||
                    butsField.getText().isEmpty() || posteField.getText().isEmpty() ||
                    dateField.getText().isEmpty()) {
                showAlert("Erreur de saisie", "Veuillez remplir tous les champs.");
                return;
            }

            try {
                newSalaire = Double.parseDouble(salaireField.getText());
                newNumero = Integer.parseInt(numeroField.getText());
                newMatchs = Integer.parseInt(matchField.getText());
                newButs = Integer.parseInt(butsField.getText());
                newPoste = posteField.getText();
                newDateNaissance = Date.valueOf(dateField.getText());
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Les valeurs numériques doivent être valides.");
                return;
            }

            joueur.setNom(newNom);
            joueur.setPrenom(newPrenom);
            joueur.setSalaire(newSalaire);
            joueur.setNumero(newNumero);
            joueur.setMatchs(newMatchs);
            joueur.setButs(newButs);
            joueur.setPoste(newPoste);
            joueur.setDateNaissance(newDateNaissance);

            String equipeName = equipeComboBox.getSelectionModel().getSelectedItem().toString();
            Equipe newEquipe = equipeService.findByName(equipeName);
            joueur.setEquipe(newEquipe);

            joueurService.update(joueur);
            joueurTable.getSelectionModel().clearSelection();
            joueurTable.refresh();

            nomField.clear();
            prenomField.clear();
            salaireField.clear();
            numeroField.clear();
            matchField.clear();
            butsField.clear();
            posteField.clear();
            dateField.clear();
        }

        nomField.clear();
        prenomField.clear();
        salaireField.clear();
        numeroField.clear();
        matchField.clear();
        butsField.clear();
        posteField.clear();
        dateField.clear();
    }

    @FXML
    void annulerJoueur() {
        nomField.clear();
        prenomField.clear();
        salaireField.clear();
        numeroField.clear();
        matchField.clear();
        butsField.clear();
        posteField.clear();
        dateField.clear();

    }


    @FXML
    void importer() throws IOException {
        JoueurService joueurService = new JoueurService();
        List<Joueur> importedPlayers = joueurService.importDataText("./resources/inputData.txt"); // Pass null as updated player initially

        // Clear the existing items in joueurTable
        joueurTable.getItems().clear();

        for (Joueur joueur : importedPlayers) {
            boolean playerExists = joueurService.findAll().stream()
                    .anyMatch(joueurObj -> joueurObj.getNom().equalsIgnoreCase(joueur.getNom())
                            && joueurObj.getPrenom().equalsIgnoreCase(joueur.getPrenom()));

            if (playerExists) {
                // Player already exists, update the player in the database
                joueurService.update(joueur);
            } else {
                // Player doesn't exist, save the player as a new entry in the database
                joueurService.save(joueur);
            }
        }

        // Retrieve the updated players from the database
        importedPlayers = joueurService.findAll();

        // Add the imported players to joueurTable
        joueurTable.getItems().addAll(importedPlayers);

        // Refresh the table view
        joueurTable.refresh();

        showSuccessAlert("Données importées avec succès depuis le fichier texte !");
    }


    @FXML
    void exporter() throws IOException {
        JoueurService joueurService = new JoueurService();
        joueurService.exportDataToTextFile("./resources/inputData.txt");
        showSuccessAlert("Donness exportés avec succés vers fichier texte!");

    }

    @FXML
    void importerExcel() throws IOException, ParseException {
        JoueurService joueurService = new JoueurService();
        joueurService.importDataExcel("./resources/inputDataExcel.xlsx");
        showSuccessAlert("Donness importés avec succés de fichier excel!");

    }

    @FXML
    void exporterExcel() throws IOException {
        JoueurService joueurService = new JoueurService();
        joueurService.exportDataToExcel("./resources/inputDataExcel.xlsx");
        showSuccessAlert("Donness exportés avec succés vers fichier excel!");

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}