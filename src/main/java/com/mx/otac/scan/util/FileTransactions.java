/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Edrd
 */
public class FileTransactions {

    public Boolean deleteFile(Path sourceDir, File file) {
        Boolean confirm = null;
        try {
            Files.delete(sourceDir);
            confirm = Boolean.TRUE;
        } catch (IOException ex) {
            confirm = Boolean.FALSE;
        }
        return confirm;
    }

    public Boolean renameFile(Path sourceDir, File oldFile, File newFile) {
        Boolean confirm = null;
        try {
            oldFile.renameTo(newFile);
            confirm = Boolean.TRUE;
            //notification.createSuccess("Se renombró el archivo correctamente: " + oldFile.getName());
        } catch (Exception ex) {
            confirm = Boolean.FALSE;
            //notification.createFailure("No se renombró el archivo");
        }
        return confirm;
    }

//    public void viewPDF(UI ui, File file) {
//        new WindowViewer(ui, file);
//    }
    public Boolean checkDir(File file) {
        Boolean confirm = null;

        Path sourceDir = Paths.get(file.getAbsolutePath());
        Path targetDir = Paths.get(Constantes.TARGET_DIR.concat("\\").concat(file.getName()));
        System.out.println("targetDir = " + targetDir);
        //File dir = new File(targetDir.toString());

        //if directory exists?
        File diretorio = new File(Constantes.TARGET_DIR);
        if (!diretorio.exists()) {
            if (diretorio.mkdir()) {
                System.out.println("Directory is created!");
                //confirm = moveFile(sourceDir, targetDir, file);
            }
        } 
        
        confirm = moveFile(sourceDir, targetDir, file);

        return confirm;
    }
    
    public Boolean moveFile(Path sourceDir, Path targetDir, File file) {
        Boolean confirm = null;

                try {
                    Files.move(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Se movio archivo");
                    confirm = Boolean.TRUE;
                } catch (FileAlreadyExistsException ex) {
                    System.out.println("Ya existe un archivo con el mismo nombre en esta carpeta");
                    confirm = Boolean.FALSE;
                } catch (IOException ex) {
                    System.out.println("Problemas al mover el archivo");
                    confirm = Boolean.FALSE;
                }
        
        return confirm;
    }
}
