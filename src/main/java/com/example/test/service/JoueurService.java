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
    public ArrayList<Joueur> exportDataText(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        ArrayList<Joueur> list = new ArrayList<Joueur>();
        Joueur j;
        String readLine = br.readLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //lire data du fichier texte
        List<String> existingData = new ArrayList<String>();
        while (readLine != null) {
            existingData.add(readLine);
            readLine = br.readLine();
        }
        br.close();

        // Chercher dans la base de donnees sur les joueurs existants
        List<String> existingPlayers = joueurDao.findAll().stream()
                .map(joueur -> joueur.getNom() + "|" + joueur.getPrenom())
                .collect(Collectors.toList());

        // Boucler sur les lignes du fichier
        for (String joueurLine : existingData) {
            String[] joueur = joueurLine.split("\\|");
            String nom = joueur[0].trim();
            String prenom = joueur[1].trim();

            // Checker si joueur existe dans la base de donnèes deja
            if (!existingPlayers.contains(nom + "|" + prenom)) {
                j = new Joueur();
                j.setNom(nom);
                j.setPrenom(prenom);
                j.setSalaire(Double.parseDouble(joueur[2].trim()));
                j.setNumero(Integer.parseInt(joueur[3].trim()));
                j.setMatchs(Integer.parseInt(joueur[4].trim()));
                j.setButs(Integer.parseInt(joueur[5].trim()));
                j.setPoste(joueur[6].trim());

                Equipe equipe = equipeDaoImp.findById(Integer.parseInt(joueur[7].trim()));
                j.setEquipe(equipe);

                try {
                    Date dateNaissance = dateFormat.parse(joueur[8].trim());
                    j.setDateNaissance(dateNaissance);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                list.add(j);
            }
        }

        return list;
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
                        joueur.getPoste() + " | " +
                        joueur.getEquipe().getId() + " | " +
                        dateFormat.format(joueur.getDateNaissance());

                if (joueur.isUpdated()) {
                    // Ajouter un indicateur pour les lignes modifiés
                    line += " | (Updated)";
                }

                writer.write(line);
                writer.newLine();
            }
        }
    }

}