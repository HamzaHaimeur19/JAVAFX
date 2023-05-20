package com.example.test.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadAndWriteExcelFileJoueur {
	
	
	
	public List<Object[]> readData(String filePath) throws IOException {
	    // Ouvrir le fichier Excel
	    FileInputStream file = new FileInputStream(new File(filePath));
	    XSSFWorkbook workbook = new XSSFWorkbook(file);

	    // Recuperer premier Sheet
	    XSSFSheet sheet = workbook.getSheetAt(0);

	    // Lire les donnèes
	    List<Object[]> dataList = new ArrayList<>();
	    for (Row row : sheet) {
	        Object[] values = new Object[row.getLastCellNum()];
	        int cellIndex = 0;
	        for (Cell cell : row) {
	            switch (cell.getCellType()) {
	                case STRING:
	                    values[cellIndex] = cell.getStringCellValue();
	                    break;
	                case NUMERIC:
	                    values[cellIndex] = cell.getNumericCellValue();
	                    break;
	                case BOOLEAN:
	                    values[cellIndex] = cell.getBooleanCellValue();
	                    break;
	                default:
	                    values[cellIndex] = "";
	                    break;
	            }
	            cellIndex++;
	        }
	        dataList.add(values);
	    }
	    workbook.close();
	    return dataList;
	}
	
	public void writeData(XSSFWorkbook workbook, String filePath, List<Object[]>list) throws IOException {
	    // Créer sheet
	    XSSFSheet sheet = workbook.createSheet("Liste des joueurs");

	    //ecrire données dans le fichier
	    int rowCount = 0;
	    for (Object[] rowData : list) {
	        XSSFRow row = sheet.createRow(rowCount++);
	        int columnCount = 0;
	        for (Object field : rowData) {
	            XSSFCell cell = row.createCell(columnCount++);
	            if (field instanceof String) {
	                cell.setCellValue((String) field);
	            } else if (field instanceof Double) {
	                cell.setCellValue((Double) field);
	            } else if (field instanceof Integer) {
	                cell.setCellValue((Integer) field);
	            } else if (field instanceof Boolean) {
	                cell.setCellValue((Boolean) field);
	            }
	            
	        }
	    }
	  
	    // Ecrire le fichier
	    try(FileOutputStream fileOut = new FileOutputStream(filePath)){
	    workbook.write(fileOut);
	    fileOut.close();
	    System.out.println("Les donnèes du fichier inputDataExcel ont été copiées avec succés!");
	    
	    
	    // Afficher les données
	    for (Object[] values : list) {
	        for (Object value : values) {
	            System.out.print(value + "\t");
	        }
	        System.out.println();
	    }
	}   catch (IOException e) {
        System.out.println(e.getStackTrace());
    }
	}

	

}
