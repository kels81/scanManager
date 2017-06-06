/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.mx.otac.scan.zbox.WindowPDF;
import com.vaadin.ui.UI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public void viewPDF(UI ui, File file) {
        new WindowPDF(ui, file);
    }
}
