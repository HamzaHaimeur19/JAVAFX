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

import java.sql.Date;
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

    /*@FXML
    private TextField equipeField;*/

    @FXML
    private TextField dateField;

    @FXML
    private TableColumn<Joueur, Void> deleteButtonColumn;

    @FXML
    private TableColumn<Joueur, Void> updateButtonColumn;

    @FXML
    private ComboBox<String> equipeComboBox;

    @FXML
    private TextField ModifiernomField;
    @FXML
    private TextField ModifierprenomField;
    @FXML
    private TextField ModifiersalaireField;
    @FXML
    private TextField ModifiernumeroField;
    @FXML
    private TextField ModifiermatchField;
    @FXML
    private TextField ModifierbutsField;
    @FXML
    private TextField ModifierposteField;
    @FXML
    private TextField ModifierdateField;
    @FXML
    private ComboBox ModifierequipeComboBox;

    public void initialize() {
        JoueurService joueurService = new JoueurService();
        EquipeService equipeService = new EquipeService();
        List<Equipe> equipeList = equipeService.findAll();
        for (Equipe equipe : equipeList) {
            equipeComboBox.getItems().add(equipe.getNomEquipe());
        }

        for (Equipe equipe : equipeList) {
            ModifierequipeComboBox.getItems().add(equipe.getNomEquipe());
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
                    Joueur joueur= getTableView().getItems().get(getIndex());
                    ModifiernomField.setText(joueur.getNom());
                    ModifierprenomField.setText(joueur.getPrenom());
                    ModifiersalaireField.setText(String.valueOf(joueur.getSalaire()));
                    ModifiernumeroField.setText(String.valueOf(joueur.getNumero()));
                    ModifiermatchField.setText(String.valueOf(joueur.getMatchs()));
                    ModifierbutsField.setText(String.valueOf(joueur.getButs()));
                    ModifierposteField.setText(joueur.getPoste());
                    if (joueur.getEquipe() == null) {
                        ModifierequipeComboBox.getSelectionModel().clearSelection();
                    } else {
                        ModifierequipeComboBox.getSelectionModel().select(joueur.getEquipe().getNomEquipe());
                    }
                    ModifierdateField.setText(String.valueOf(joueur.getDateNaissance()));

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
            showAlert("Erreur de saisie", "Les valeurs numériques doivent être valides.");
            return;
        }

        // avoir equipe par nom selectionné
        String equipeN = equipeComboBox.getSelectionModel().getSelectedItem();

        Equipe equipe = equipeService.findByName(equipeN);

        if (equipe == null) {
            showAlert("Club introuvable", "Club introuvable ! Entrez un club existant.");
            return;
        }

        Joueur joueur = new Joueur(0, nom, prenom, salaire, numero, matchs, buts, poste, equipe, dateNaissance);

        joueurService.save(joueur);

        // Clear the fields
        nomField.clear();
        prenomField.clear();
        salaireField.clear();
        numeroField.clear();
        matchField.clear();
        butsField.clear();
        posteField.clear();
        dateField.clear();
        equipeComboBox.getSelectionModel().clearSelection();

        // Rafraichir
        joueurTable.getItems().add(joueur);
    }

    @FXML
    void modifierJoueur() {
        JoueurService joueurService = new JoueurService();
        EquipeService equipeService = new EquipeService();

        // joueur selectionné
        Joueur joueur = joueurTable.getSelectionModel().getSelectedItem();

        if (joueur != null) {
            String newNom = ModifiernomField.getText();
            String newPrenom = ModifierprenomField.getText();
            double newSalaire;
            int newNumero;
            int newMatchs;
            int newButs;
            String newPoste;
            Date newDateNaissance;

            // Checker pour valeurs nulls
            if (newNom.isEmpty() || newPrenom.isEmpty() || ModifiersalaireField.getText().isEmpty() ||
                    ModifiernumeroField.getText().isEmpty() || ModifiermatchField.getText().isEmpty() ||
                    ModifierbutsField.getText().isEmpty() || ModifierposteField.getText().isEmpty() ||
                    ModifierdateField.getText().isEmpty()) {
                showAlert("Erreur de saisie", "Veuillez remplir tous les champs.");
                return;
            }

            try {
                newSalaire = Double.parseDouble(ModifiersalaireField.getText());
                newNumero = Integer.parseInt(ModifiernumeroField.getText());
                newMatchs = Integer.parseInt(ModifiermatchField.getText());
                newButs = Integer.parseInt(ModifierbutsField.getText());
                newPoste = ModifierposteField.getText();
                newDateNaissance = Date.valueOf(ModifierdateField.getText());
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Les valeurs numériques doivent être valides.");
                return;
            }

            // maj joueur avec nouvelles valeurs
            joueur.setNom(newNom);
            joueur.setPrenom(newPrenom);
            joueur.setSalaire(newSalaire);
            joueur.setNumero(newNumero);
            joueur.setMatchs(newMatchs);
            joueur.setButs(newButs);
            joueur.setPoste(newPoste);
            joueur.setDateNaissance(newDateNaissance);

            // maj eqipe joueur
            String equipeName = ModifierequipeComboBox.getSelectionModel().getSelectedItem().toString();
            Equipe equipe = equipeService.findByName(equipeName);
            joueur.setEquipe(equipe);

            // save joueur
            joueurService.update(joueur);

            // rafraichir
            joueurTable.refresh();

            // Clear the fields
            ModifiernomField.clear();
            ModifierprenomField.clear();
            ModifiersalaireField.clear();
            ModifiernumeroField.clear();
            ModifiermatchField.clear();
            ModifierbutsField.clear();
            ModifierposteField.clear();
            ModifierdateField.clear();
            ModifierequipeComboBox.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
