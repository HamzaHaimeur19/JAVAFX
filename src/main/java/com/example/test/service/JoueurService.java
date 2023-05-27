package com.example.test.service;

import java.io.*;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    public List<Joueur> exportDataText(String filePath, Joueur updatedPlayer) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<Joueur> importedPlayers = new ArrayList<>();
        String readLine = br.readLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Read data from the text file
        while (readLine != null) {
            String[] joueurData = readLine.split("\\|");
            if (joueurData.length >= 9) {
                String nom = joueurData[0].trim();
                String prenom = joueurData[1].trim();

                if (updatedPlayer != null && nom.equalsIgnoreCase(updatedPlayer.getNom()) && prenom.equalsIgnoreCase(updatedPlayer.getPrenom())) {
                    // Update the player's data
                    joueurData[2] = String.valueOf(updatedPlayer.getSalaire());
                    joueurData[3] = String.valueOf(updatedPlayer.getNumero());
                    joueurData[4] = String.valueOf(updatedPlayer.getMatchs());
                    joueurData[5] = String.valueOf(updatedPlayer.getButs());
                    joueurData[6] = updatedPlayer.getPoste();
                    joueurData[7] = updatedPlayer.getEquipe().getNomEquipe();
                    joueurData[8] = dateFormat.format(updatedPlayer.getDateNaissance());

                    // Join the updated data back into a line
                    readLine = String.join("|", joueurData);
                }

                // Create a new Joueur object and add it to the importedPlayers list
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
            } else {
                System.out.println("Invalid data format: " + readLine);
            }

            readLine = br.readLine();
        }

        br.close();
        return importedPlayers;
    }

    public void clearDatabase() {
        List<Joueur> joueurs = joueurDao.findAll();

        for (Joueur joueur : joueurs) {
            joueurDao.deleteById(joueur.getId());
        }
    }

    public List<Joueur> importDataText(String filePath) throws IOException {
        clearDatabase();
        JoueurService joueurService = new JoueurService();
        List<Joueur> importedPlayers = exportDataText(filePath, null);

        // Iterate over the imported players
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



    //import data from excel file
    public List<Joueur> importDataExcel(String filePath) throws IOException, ParseException {
        List<Joueur> importedPlayers = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip the header row
                    continue;
                }

                String nom = getCellValueAsString(row.getCell(0));
                String prenom = getCellValueAsString(row.getCell(1));

                // Check if the player exists in the database already
                boolean playerExists = joueurDao.findAll().stream()
                        .anyMatch(joueurObj -> joueurObj.getNom().equalsIgnoreCase(nom) && joueurObj.getPrenom().equalsIgnoreCase(prenom));

                if (!playerExists) {
                    Joueur j = new Joueur();
                    j.setNom(nom);
                    j.setPrenom(prenom);
                    j.setSalaire(parseDoubleValue(row.getCell(2)));
                    j.setNumero(parseIntValue(row.getCell(3)));
                    j.setMatchs(parseIntValue(row.getCell(4)));
                    j.setButs(parseIntValue(row.getCell(5)));
                    j.setPoste(getCellValueAsString(row.getCell(6)));

                    Equipe equipe = equipeDaoImp.findByName(getCellValueAsString(row.getCell(7)));
                    j.setEquipe(equipe);

                    Date dateNaissance = dateFormat.parse(getCellValueAsString(row.getCell(8)));
                    j.setDateNaissance(dateNaissance);

                    importedPlayers.add(j);
                }
            }
        }

        // Save the imported players to the database
        for (Joueur joueur : importedPlayers) {
            save(joueur);
        }

        return importedPlayers;
    }

    private Double parseDoubleValue(Cell cell) {
        String cellValue = getCellValueAsString(cell);
        try {
            return Double.parseDouble(cellValue);
        } catch (NumberFormatException e) {
            return 0.0; // Set a default value or handle it accordingly
        }
    }

    private Integer parseIntValue(Cell cell) {
        String cellValue = getCellValueAsString(cell);
        try {
            return Integer.parseInt(cellValue);
        } catch (NumberFormatException e) {
            return 0; // Set a default value or handle it accordingly
        }
    }



    //export data to excel file
    public void exportDataToExcel(String filePath) throws IOException {
        List<Joueur> joueurs = joueurDao.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Joueurs");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom");
        headerRow.createCell(1).setCellValue("Prénom");
        headerRow.createCell(2).setCellValue("Salaire");
        headerRow.createCell(3).setCellValue("Numéro");
        headerRow.createCell(4).setCellValue("Matchs");
        headerRow.createCell(5).setCellValue("Buts");
        headerRow.createCell(6).setCellValue("Poste");
        headerRow.createCell(7).setCellValue("Equipe");
        headerRow.createCell(8).setCellValue("Date de naissance");

        int rowIndex = 1;
        for (Joueur joueur : joueurs) {
            if (joueur.isDeleted()) {
                // Skip deleted players
                continue;
            }

            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(joueur.getNom());
            row.createCell(1).setCellValue(joueur.getPrenom());
            row.createCell(2).setCellValue(joueur.getSalaire());
            row.createCell(3).setCellValue(joueur.getNumero());
            row.createCell(4).setCellValue(joueur.getMatchs());
            row.createCell(5).setCellValue(joueur.getButs());
            row.createCell(6).setCellValue(joueur.getPoste());

            Equipe equipe = joueur.getEquipe();
            if (equipe != null) {
                row.createCell(7).setCellValue(equipe.getNomEquipe());
            } else {
                row.createCell(7).setCellValue("null");
            }

            Cell dateNaissanceCell = row.createCell(8);
            dateNaissanceCell.setCellValue(dateFormat.format(joueur.getDateNaissance()));

            if (joueur.isUpdated()) {
                // Mark updated players
                dateNaissanceCell.setCellValue(dateNaissanceCell.getStringCellValue() + " (Updated)");
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            workbook.write(fileOutputStream);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}