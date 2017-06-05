/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Edrd
 */
public class FileTransactions {

    public void deleteFile(Path sourceDir, File file) {
        try {
            Files.delete(sourceDir);
            
            //notification.createSuccess("Se eliminó el archivo correctamente: " + file.getName());
        } catch (IOException ex) {
            //notification.createFailure("No se elimino el archivo");
        }
    }

    public void renameFile(Path sourceDir, File oldFile, File newFile) {
        try {
            oldFile.renameTo(newFile);
            
            //notification.createSuccess("Se renombró el archivo correctamente: " + oldFile.getName());
        } catch (Exception ex) {
            //notification.createFailure("No se renombró el archivo");
        }
    }

    
}
