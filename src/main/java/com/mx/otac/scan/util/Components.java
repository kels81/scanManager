/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Eduardo
 */
public class Components {

    public TextField createTextField(String caption) {
        TextField txt = new TextField(caption);
        txt.setNullRepresentation("");
        txt.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        return txt;
    }

    public DateField createDateField(String caption) {
        DateField date = new DateField(caption);
        date.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        date.setValidationVisible(true);
        date.setInvalidAllowed(false);
        date.setParseErrorMessage("Fecha incorrecta");
        return date;
    }

    public Button createButtonPrimary(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return btn;
    }

    public Button createButtonNormal(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        return btn;
    }

    public ComboBox createComboBox(String caption) {
        ComboBox cmb = new ComboBox(caption);
        cmb.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        cmb.setPageLength(0);
        cmb.setImmediate(true);
        cmb.setTextInputAllowed(false);
        cmb.setNullSelectionAllowed(false);
        cmb.setInputPrompt("Elegir " + caption.toLowerCase());
        cmb.addStyleName(ValoTheme.COMBOBOX_SMALL);
        return cmb;
    }

    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.addStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName("primary");
        return menu;
    }

}
