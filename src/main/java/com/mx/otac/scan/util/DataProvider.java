/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.google.gson.Gson;
import com.mx.otac.scan.zbox.SubtipoDocumental;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Edrd
 */
public class DataProvider {
    
     public List<File> directoryContents(File directory) {
        // ARRAY QUE VA A ACONTENER TODOS LOS ARCHIVOS ORDENADOS POR TIPO Y 
        String[] extensionsPermited = new String[] { "pdf", "jpg", "png", "jpeg", "gif" };
        
        List<File> allDocsLst = new ArrayList<>();
        //File[] files = directory.listFiles();
        List<File> fileLst = new ArrayList<>();
        List<File> directoryLst = new ArrayList<>();
        
        List<File> files = (List<File>) FileUtils.listFiles(directory, extensionsPermited, true);
        
        
        for (File file : files) {
            //if (file.isDirectory()) {
                //directoryLst.add(file);
                //directoryContents(file);   //para conocer los archivos de las subcarpetas
            //} else {
            if (file.isFile()) {
                
                fileLst.add(file);
            }
                
            //}
        }
        //allDocsLst.addAll(directoryLst);
        allDocsLst.addAll(fileLst);

        return allDocsLst;
    }
    
    public List<File> directoryFolderContents(File directory) {
        // ARRAY QUE VA A ACONTENER TODOS LOS ARCHIVOS ORDENADOS POR TIPO Y ALFABETICAMENTE
        List<File> allDocsLst = new ArrayList<>();
        File[] files = directory.listFiles();
        List<File> fileLst = new ArrayList<>();
        List<File> directoryLst = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                directoryLst.add(file);
                //directoryContents(file);   //para conocer los archivos de las subcarpetas
            } else {
                fileLst.add(file);
            }
        }
        allDocsLst.addAll(directoryLst);
        allDocsLst.addAll(fileLst);

        return allDocsLst;
    }
    
    public ArrayList<String> getAreaFolders(String rutaAreaFolders, boolean isSubtipo) {
        ArrayList<String> areaFolders = new ArrayList<>();
        File fArea = new File(rutaAreaFolders);
        File[] arrTemp = fArea.listFiles();
        for (int i = 0; i < arrTemp.length; i++) {
            if (arrTemp[i].isDirectory() || isSubtipo) {
                if (!arrTemp[i].getName().contains("OBSOLETO") && !arrTemp[i].getName().contains("Obsoleto") && !arrTemp[i].getName().contains("obsoleto")) {
                    areaFolders.add(arrTemp[i].getName().replace(".txt", ""));
                }//areaFolders.add(arrTemp[i].getName());
            }
        }//areaFolders.add("Crear nuevo");
        return areaFolders;
    }
    
    public SubtipoDocumental getDatos(String jsonStr) {
        Gson gson = new Gson();
        SubtipoDocumental subtipo = null;
        try (Reader reader = new FileReader(jsonStr)) {
            subtipo = gson.fromJson(reader, SubtipoDocumental.class);
        } catch (IOException e) {
        }
        return subtipo;
    }
    
    
}
