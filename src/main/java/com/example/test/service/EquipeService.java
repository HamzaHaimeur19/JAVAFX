package com.example.test.service;

import com.example.test.dao.EquipeDao;
import com.example.test.dao.impl.EquipeDaoImp;
import com.example.test.entities.Equipe;
import com.example.test.entities.Joueur;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EquipeService {
    // injection de dependances
    private EquipeDaoImp equipeDao = new EquipeDaoImp();

    public void save(Equipe equipe) {

        equipeDao.insert(equipe);

    }

    public void update(Equipe equipe) {
        equipeDao.update(equipe);
    }

    public void remove(Equipe equipe) {
        equipeDao.deleteById(equipe.getId());
    }

    public List<Equipe> findAll() {
        return equipeDao.findAll();
    }

    public Equipe findById(Equipe equipe) {
        return equipeDao.findById(equipe.getId());
    }

    // Retrieve the equipe based on the selected name
    public Equipe findByName(String equipeName) {
        return equipeDao.findByName(equipeName);
    }

    // méthode pour extraire données à partir du fichier inputData.txt
    public ArrayList<Equipe> exportDataTextEquipe(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        ArrayList<Equipe> list = new ArrayList<Equipe>();
        Equipe e;
        String readLine = br.readLine();

        //lire data du fichier texte
        List<String> existingData = new ArrayList<String>();
        while (readLine != null) {
            existingData.add(readLine);
            readLine = br.readLine();
        }
        br.close();

        // Chercher dans la base de donnees sur les joueurs existants
        List<String> existingTeams = equipeDao.findAll().stream()
                .map(equipe -> equipe.getNomEquipe())
                .collect(Collectors.toList());

        // Boucler sur les lignes du fichier
        for (String equipeLine : existingData) {
            String[] equipe = equipeLine.split("\\|");
            String nom = equipe[0].trim();

            // Checker si joueur existe dans la base de donnèes deja
            if (!existingTeams.contains(nom)) {
                e = new Equipe();
                e.setNomEquipe(nom);
                list.add(e);
            }
        }

        return list;
    }

    public void exportDataToTextFileEquipe(String filePath) throws IOException {
        List<Equipe> equipes = equipeDao.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Equipe equipe: equipes) {
                if (equipe.isDeleted()) {
                    // passer sur les lignes supprimées
                    continue;
                }

                String line = equipe.getNomEquipe();

                if (equipe.isUpdated()) {
                    // Ajouter un indicateur pour les lignes modifiés
                    line += " | (Updated)";
                }

                writer.write(line);
                writer.newLine();
            }
        }
    }



}
