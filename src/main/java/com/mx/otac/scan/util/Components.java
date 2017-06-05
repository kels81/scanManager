/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eduardo
 */
public class Components {

    public CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        cb.setImmediate(true);
        return cb;
    }

    public TextField createTextField(String caption) {
        TextField f = new TextField(caption);
        f.setNullRepresentation("");
        //f.addFocusListener(focusListener);
        //f.addBlurListener(blurListener);
        return f;
    }

    public Button createButtonPrimary(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        //btnFolder.setEnabled(false);
        return btn;
    }
    
    public Button createButtonNormal(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        //btnFolder.setEnabled(false);
        return btn;
    }

    public Button createButtonPath(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        //btnFolder.setEnabled(false);
        return btn;
    }

    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.addStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName("primary");
        return menu;
    }
    
    public List<File> directoryContents(File directory) {
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
    
    
}
