package com.mx.otac.scan.component;

import com.vaadin.data.fieldgroup.PropertyId;
import com.mx.otac.scan.DashboardUtils;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Eduardo
 */
public class PerfilGeneralForm extends FormLayout {
    
     /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */
    
    @PropertyId("firstName")
    private final TextField txtNombre;
    @PropertyId("lastName")
    private final TextField txtApPaterno;
    @PropertyId("lastName2")
    private final TextField txtApMaterno;
    @PropertyId("male")
    private final OptionGroup rdbGenero;
    @PropertyId("email")
    private TextField emailField;
    @PropertyId("location")
    private TextField locationField;
    @PropertyId("phone")
    private TextField phoneField;
    @PropertyId("website")
    private TextField websiteField;
    @PropertyId("bio")
    private TextArea bioField;
    
    //private DateField birthDate;
    private final PopupDateField birthDate;
    private final Label lblSeccion;
    
    DashboardUtils util = new DashboardUtils();

    public PerfilGeneralForm() {
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        lblSeccion = util.createLabelH4("Datos Generales");
        txtNombre = util.createTextField("Nombre(s)");
        txtApPaterno = util.createTextField("Apellido Paterno");
        txtApMaterno = util.createTextField("Apellido Materno");
        //birthDate = new DateField("Fecha Nacimiento");        
        birthDate = util.createDateFieldNac("Fecha Nacimiento");
        rdbGenero = util.createRadioGenero("Género");
        
        addComponents(lblSeccion,
                      txtNombre,
                      txtApPaterno,
                      txtApMaterno,
                      birthDate,
                      rdbGenero);

    }

}
