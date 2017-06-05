/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Edrd
 */
public class DirectoryContentData {
    
    public List<File> directoryContents(File directory) {
        // ARRAY QUE VA A ACONTENER TODOS LOS ARCHIVOS ORDENADOS POR TIPO Y ALFABETICAMENTE
        List<File> allDocsLst = new ArrayList<>();
        File[] files = directory.listFiles();
        List<File> fileLst = new ArrayList<>();
        List<File> directoryLst = new ArrayList<>();
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
    
}
