/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.component;

import com.mx.otac.scan.util.DataProvider;
import com.mx.otac.scan.util.Components;
import com.mx.otac.scan.util.Constantes;
import com.mx.otac.scan.util.FileTransactions;
import com.mx.otac.scan.util.Notifications;
import com.mx.otac.scan.view.transactions.TransactionsView;
import com.mx.otac.scan.zbox.CargarDocumentoBox;
import com.mx.otac.scan.zbox.DocumentoVO;
import com.mx.otac.scan.zbox.Field;
import com.mx.otac.scan.zbox.SubtipoDocumental;
import com.vaadin.server.FileResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author OTAC
 */
public class WindowViewer extends Window {

    private final String rutaRaiz = Constantes.RUTA_RAIZ;

    private ArrayList<Object> arrComponents;
    private SubtipoDocumental subtipoDocumental;
    private final File file;

    private final Panel panel;
    private ComboBox cbxArea;
    private ComboBox cbxTipoDocumental;
    private ComboBox cbxSubtipoDocumental;
    private TextField txtSiniestro;

    private VerticalLayout vltForm;
    private VerticalLayout vltCombos;
    private VerticalLayout vltMetadatos;

    private final HorizontalLayout hltContent;
    private final Components component = new Components();
    private final DataProvider data = new DataProvider();
    private final Notifications notification = new Notifications();
    private final FileTransactions fileTrans = new FileTransactions();

    private Button btnGuardar;

    public WindowViewer(File file) {
        this.file = file;

        setCaption(this.file.getName());
        setDraggable(false);
        setResizable(false);
        setClosable(true);
        setModal(true);
        setSizeFull();

        panel = new Panel("Metadatos");
        panel.setSizeFull();

        hltContent = new HorizontalLayout();
        hltContent.setSizeFull();
        hltContent.setMargin(true);
        hltContent.setSpacing(true);

        Component viewer = buildViewer();
        panel.setContent(buildForm());  //SE COLOCAL EL LAYOUT DENTRO DE UN PANEL PARA QUE TENGA SU PROPIO SCROLLBAR

        hltContent.addComponents(viewer, panel);
        hltContent.setExpandRatio(viewer, 0.75f);
        hltContent.setExpandRatio(panel, 0.25f);

        setContent(hltContent);
        center();
    }

    private Embedded buildViewer() {
        Embedded viewer = new Embedded(null, new FileResource(file));
        viewer.setMimeType("application/pdf");

        if (file.getName().contains(".pdf")) {
            viewer.setType(Embedded.TYPE_BROWSER);
        } else {
            viewer.setType(Embedded.TYPE_IMAGE);
        }
        viewer.setHeight("600px");
        viewer.setWidth("100%");

        return viewer;
    }

    private VerticalLayout buildForm() {
        vltForm = new VerticalLayout();
        vltForm.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        vltForm.setMargin(true);
        vltForm.setSpacing(true);

        Component combos = buildFormCombos();
        vltForm.addComponents(combos);
        vltForm.setComponentAlignment(combos, Alignment.TOP_CENTER);

        return vltForm;
    }

    private VerticalLayout buildFormCombos() {
        vltCombos = new VerticalLayout();
        vltCombos.setSpacing(true);

        cbxArea = component.createComboBox("Área");
        cbxArea.addItems(data.getAreaFolders(rutaRaiz, false));
        cbxArea.addValueChangeListener(event -> {
            if (cbxArea.getValue() != null) {
                cbxTipoDocumental.removeAllItems();
                cbxTipoDocumental.addItems(data.getAreaFolders(rutaRaiz + "/" + event.getProperty().getValue().toString(), false));
                cbxTipoDocumental.select(null);
                cbxSubtipoDocumental.clear();
                cleanMetadatos();
            }
        });

        cbxTipoDocumental = component.createComboBox("Tipo Documental");
        cbxTipoDocumental.addValueChangeListener(event -> {
            if (cbxTipoDocumental.getValue() != null) {
                cbxSubtipoDocumental.removeAllItems();
                cbxSubtipoDocumental.addItems(data.getAreaFolders(rutaRaiz + "/" + cbxArea.getValue().toString() + "/" + event.getProperty().getValue().toString(), true));
                cbxSubtipoDocumental.select(null);
                cleanMetadatos();
            }
        });

        cbxSubtipoDocumental = component.createComboBox("Subtipo Documental");
        cbxSubtipoDocumental.addValueChangeListener(event -> {
            if (cbxSubtipoDocumental.getValue() != null) {
                cleanMetadatos();
                vltForm.addComponent(buildFormMetadatos(rutaRaiz + "/" + cbxArea.getValue() + "/" + cbxTipoDocumental.getValue() + "/" + event.getProperty().getValue().toString().concat(".txt")));
            } else {
                cleanMetadatos();
            }
        });
        vltCombos.addComponents(cbxArea, cbxTipoDocumental, cbxSubtipoDocumental);

        return vltCombos;
    }

    private VerticalLayout buildFormMetadatos(String rutaArchivoJson) {
        vltMetadatos = new VerticalLayout();
        vltMetadatos.setSpacing(true);

        arrComponents = new ArrayList<>();
        subtipoDocumental = data.getDatos(rutaArchivoJson);

        for (Field field : subtipoDocumental.getFields()) {
            if (field.getType().equals("date")) {
                DateField tempDateFld = component.createDateField(field.getFieldKey());
                if (field.getRequired()) {
                    tempDateFld.setRequired(true);
                }
                arrComponents.add(tempDateFld);
            } else {
                TextField tempTxtFld = component.createTextField(field.getFieldKey());
                if (field.getRequired()) {
                    tempTxtFld.setRequired(true);
                }
                arrComponents.add(tempTxtFld);
            }
        }
        //AGREGAR COMPONENTES AL FORMULARIO
        for (Object obj : arrComponents) {
            if (obj.getClass().equals(TextField.class)) {
                vltMetadatos.addComponent((TextField) obj);
            } else if (obj.getClass().equals(DateField.class)) {
                vltMetadatos.addComponent((DateField) obj);
            }
        }
        
        txtSiniestro = component.createTextField("Número Siniestro");
        vltMetadatos.addComponent(txtSiniestro);
                
        vltMetadatos.addComponent(buildFooter());

        return vltMetadatos;
    }

    private HorizontalLayout buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnGuardar = component.createButtonPrimary("Guardar");
        btnGuardar.addClickListener(e -> {
            Boolean confirm = this.subirMetadatos(file);

            if (confirm) {
                Boolean mover = fileTrans.checkDir(file);
                System.out.println("mover = " + mover);
                new TransactionsView().uptadeTable();
               notification.createSuccess("Se guardo correctamente");
            } else {
                notification.createFailure("Error al guardar");
            }
            close();
        });

        footer.addComponent(btnGuardar);
        footer.setComponentAlignment(btnGuardar, Alignment.BOTTOM_RIGHT);

        return footer;
    }

    public void cleanMetadatos() {
        try {
            vltForm.removeComponent(vltMetadatos);
        } catch (Exception e) {
            // System.out.println("actualizarDatosJson() : " + e);
        }
    }

    public Boolean subirMetadatos(File file) {
        
        Map<String, String> metadatos = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //FORMATO TIME-STAMP QUE RECIBE BOX PARA LA FECHA

        System.out.println(arrComponents.size());
        boolean faltanObligatorios = false;
        for (Object obj : arrComponents) {
            if (obj.getClass().equals(TextField.class)) {
                if ((!((TextField) obj).getValue().equals("")) || !(((TextField) obj).getValue().isEmpty())) {
                    metadatos.put(((TextField) obj).getCaption(), ((TextField) obj).getValue());
                } else if (((TextField) obj).isRequired()) {
                    faltanObligatorios = true;
                }
            } else if (obj.getClass().equals(DateField.class)) {
                try {
                    if (!((DateField) obj).isEmpty()) {
                        metadatos.put(((DateField) obj).getCaption(), sdf.format(((DateField) obj).getValue()));
                    } else if (((DateField) obj).isRequired()) {
                        faltanObligatorios = true;
                    }
                } catch (NullPointerException nullPointerException) {
                    System.out.println("nullPointerException -> " + nullPointerException);
                    //subtipoFormulario.addField(new FieldFormulario(((DateField)obj).getCaption(), ""));
                }
            }
        }
        if (faltanObligatorios) {
            Notification.show("Datos Faltantes", "Completa los campos requeridos.", Notification.Type.WARNING_MESSAGE);
        }

        System.out.println("----- INICIO -----");

        CargarDocumentoBox cargarDoc = new CargarDocumentoBox();
        String strSubtipo = cbxSubtipoDocumental.getValue().toString();

        DocumentoVO documentoVO = new DocumentoVO();
        documentoVO.setInputStream(file);

        metadatos.put("numeroSiniestro", txtSiniestro.getValue());
        metadatos.put("area", cbxArea.getValue().toString());
        metadatos.put("tipodocumental", cbxTipoDocumental.getValue().toString());
        metadatos.put("operacion", "Tercero");

        documentoVO.setMetadatos(metadatos);
        //strSubtipo = strSubtipo.substring(0, strSubtipo.indexOf("."));
        documentoVO.setSubTipoDocumental(strSubtipo);
        documentoVO.setNombreDocumento(strSubtipo + "_" + file.getName());

        for (Map.Entry<String, String> entry : metadatos.entrySet()) {
            System.out.format("Atributo : [ %s ], Valor : [ %s ] \n", entry.getKey(), entry.getValue());
        }

        //Boolean resultado = true;
        Boolean resultado = cargarDoc.verificarMetadatos(documentoVO);

        System.out.println("----- FIN -----");
        System.out.println("boolean resultado -> " + resultado);
        return resultado;
    }

}
