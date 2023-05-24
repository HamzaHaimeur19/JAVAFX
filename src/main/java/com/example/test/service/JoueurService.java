package com.example.test.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.test.dao.EquipeDao;
import com.example.test.dao.impl.EquipeDaoImp;
import com.example.test.dao.impl.JoueurDaoImp;
import com.example.test.dao.*;
import com.example.test.entities.Equipe;
import com.example.test.entities.Joueur;

public class JoueurService {
    // injection de dependances
    private JoueurDaoImp joueurDao = new JoueurDaoImp();
    private EquipeDaoImp equipeDaoImp = new EquipeDaoImp();

    public void save(Joueur joueur) {

        joueurDao.insert(joueur);

    }

    public void update(Joueur joueur) {
        joueurDao.update(joueur);
    }

    public void remove(Joueur joueur) {
        joueurDao.deleteById(joueur.getId());
    }

    public List<Joueur> findAll() {
        return joueurDao.findAll();
    }

    public Joueur findById(Integer id) {
        return joueurDao.findById(id);
    }

    public List<Joueur> findByEquipe(Equipe equipe) {
        return joueurDao.findByEquipe(equipe);
    }

    // méthode pour extraire données à partir du fichier inputData.txt
    public List<Joueur> exportDataText(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<Joueur> importedPlayers = new ArrayList<>();
        String readLine = br.readLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Read data from the text file
        while (readLine != null) {
            String[] joueurData = readLine.split("\\|");
            String nom = joueurData[0].trim();
            String prenom = joueurData[1].trim();

            // Check if the player exists in the database already
            boolean playerExists = joueurDao.findAll().stream()
                    .anyMatch(joueurObj -> joueurObj.getNom().equalsIgnoreCase(nom) && joueurObj.getPrenom().equalsIgnoreCase(prenom));

            if (!playerExists) {
                Joueur j = new Joueur();
                j.setNom(nom);
                j.setPrenom(prenom);
                j.setSalaire(Double.parseDouble(joueurData[2].trim()));
                j.setNumero(Integer.parseInt(joueurData[3].trim()));
                j.setMatchs(Integer.parseInt(joueurData[4].trim()));
                j.setButs(Integer.parseInt(joueurData[5].trim()));
                j.setPoste(joueurData[6].trim());

                Equipe equipe = equipeDaoImp.findByName(joueurData[7].trim());
                j.setEquipe(equipe);

                try {
                    Date dateNaissance = dateFormat.parse(joueurData[8].trim());
                    j.setDateNaissance(dateNaissance);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                importedPlayers.add(j);
            }

            readLine = br.readLine();
        }

        br.close();
        return importedPlayers;
    }

    public List<Joueur> importDataText(String filePath) throws IOException {
        JoueurService joueurService = new JoueurService();
        List<Joueur> importedPlayers = exportDataText(filePath);
        for (Joueur joueur : importedPlayers) {
            joueurService.save(joueur); // Save the imported player to the database
        }
        return importedPlayers;
    }



    // Méthode pour extraire les données de la base de données et les écrire dans le fichier inputData.txt
    public void exportDataToTextFile(String filePath) throws IOException {
        List<Joueur> joueurs = joueurDao.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Joueur joueur : joueurs) {
                if (joueur.isDeleted()) {
                    // passer sur les lignes supprimées
                    continue;
                }

                String line = joueur.getNom() + " | " +
                        joueur.getPrenom() + " | " +
                        joueur.getSalaire() + " | " +
                        joueur.getNumero() + " | " +
                        joueur.getMatchs() + " | " +
                        joueur.getButs() + " | " +
                        joueur.getPoste() + " | ";

                Equipe equipe = joueur.getEquipe();
                if (equipe != null) {
                    line += equipe.getNomEquipe() + " | ";
                } else {
                    line += "null | ";
                }

                line += dateFormat.format(joueur.getDateNaissance());

                if (joueur.isUpdated()) {
                    // Ajouter un indicateur pour les lignes modifiées
                    line += " | (Updated)";
                }
                writer.write(line);
                writer.newLine();
            }
        }
    }



}