/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.view.transactions;

import com.mx.otac.scan.util.Constantes;
import com.mx.otac.scan.util.DataProvider;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Edrd
 */
public class TransactionsContainer {

    private final DataProvider content = new DataProvider();
    private File rootPath;

    public IndexedContainer crearContenedor() {

        rootPath = new File(Constantes.ROOT_PATH);
        IndexedContainer idxCont = new IndexedContainer();

        idxCont.addContainerProperty("No.", Integer.class, "");
        idxCont.addContainerProperty("Nombre", String.class, "");
        idxCont.addContainerProperty("Tamaño", String.class, "");
        idxCont.addContainerProperty("Fecha Creación", String.class, "");

        int contador = 1;

        List<File> files = content.directoryContents(rootPath);

        if (!files.isEmpty()) {
            for (File file : files) {
                long fileSize = file.length();
                String fileSizeDisplay = FileUtils.byteCountToDisplaySize(fileSize);

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty("No.").setValue(contador++);
                item.getItemProperty("Nombre").setValue(file.getName());
                item.getItemProperty("Tamaño").setValue(fileSizeDisplay);
                item.getItemProperty("Fecha Creación").setValue(getAtributos(file));
            }
        }

        return idxCont;
    }

    private String getAtributos(File file) {
        String fechaCreacion = "";
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime date = attr.creationTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            fechaCreacion = df.format(date.toMillis());
        } catch (IOException ex) {
            Logger.getLogger(TransactionsContainer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fechaCreacion;
    }

}
