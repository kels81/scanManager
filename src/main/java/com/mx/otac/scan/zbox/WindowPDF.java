/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

import com.google.gson.Gson;
import com.mx.otac.scan.util.Constantes;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author OTAC
 */
public class WindowPDF extends FormLayout{
    private final String rutaRaiz = Constantes.RUTA_RAIZ;
    
    private UI myUI;
    private ArrayList<Object> arrObject;
    private ArrayList<Object> arrComponents;
    private SubtipoDocumental subtipoDocumental;
    private File pdfFile = new File("C:\\prueba.pdf");
    
    private final Panel panel;
    private TabSheet tabsheet;
    private final Window subWindow = new Window();
    
    private ComboBox cbxArea;
    private ComboBox cbxTipoDocumental;
    private ComboBox cbxSubtipoDocumental;
    
    private final TextField txfTemplateKey;
    private final TextField txfChecklistRelacionado;
    private final TextField txfRuta;
    
    private VerticalLayout vltMeta = new VerticalLayout();
    private VerticalLayout vltMetadatos;
    
    private Button btnSaveJson;
    
    public WindowPDF (UI ui, File file) {
        this.myUI = ui;
        pdfFile = file;
        
        subWindow.setCaption(file.getName());
        subWindow.setDraggable(false);
        subWindow.setResizable(false);
        subWindow.setClosable(true);
        subWindow.setModal(true);
        
        Embedded pdf = new Embedded(null, new FileResource(pdfFile));
        pdf.setMimeType("application/pdf");
        if(pdfFile.getName().contains(".pdf")){
            pdf.setType(Embedded.TYPE_BROWSER);
        }else{
            pdf.setType(Embedded.TYPE_IMAGE);
        }
        pdf.setHeight("600px");
        pdf.setWidth("100%");
        
        
        cbxArea = new ComboBox();
        cbxArea.setCaption("�rea");
        cbxArea.setSizeFull();
        cbxArea.setPageLength(0);
        cbxArea.setImmediate(true);
        cbxArea.setTextInputAllowed(false);
        cbxArea.setNullSelectionAllowed(false);
        cbxArea.setInputPrompt("Elegir el �rea");        
        cbxArea.addItems(getAreaFolders(rutaRaiz, false));
        cbxArea.addValueChangeListener(event ->{    
            if ( cbxArea.getValue() != null ){
                cbxTipoDocumental.removeAllItems();
                cbxTipoDocumental.addItems(getAreaFolders(rutaRaiz+"/"+event.getProperty().getValue().toString(), false));
                cbxTipoDocumental.select(null);
                cbxSubtipoDocumental.clear();
                actualizarDatosJson(null);
            }
        });

        cbxTipoDocumental = new ComboBox();
        cbxTipoDocumental.setCaption("Tipo Documental");
        cbxTipoDocumental.setSizeFull();
        cbxTipoDocumental.setPageLength(0);
        cbxTipoDocumental.setImmediate(true);
        cbxTipoDocumental.setTextInputAllowed(false);
        cbxTipoDocumental.setNullSelectionAllowed(false);
        cbxTipoDocumental.setInputPrompt("Elegir el tipo documental");
        cbxTipoDocumental.addValueChangeListener(event ->{
            if ( cbxTipoDocumental.getValue() != null ){
                cbxSubtipoDocumental.removeAllItems();
                cbxSubtipoDocumental.addItems(getAreaFolders(rutaRaiz+"/"+cbxArea.getValue().toString()+"/"+event.getProperty().getValue().toString(),true));
                cbxSubtipoDocumental.select(null);
                actualizarDatosJson(null);
            }
        });

        cbxSubtipoDocumental = new ComboBox();
        cbxSubtipoDocumental.setCaption("Subtipo Documental");
        cbxSubtipoDocumental.setSizeFull();
        cbxSubtipoDocumental.setPageLength(0);
        cbxSubtipoDocumental.setImmediate(true);
        cbxSubtipoDocumental.setTextInputAllowed(false);
        cbxSubtipoDocumental.setNullSelectionAllowed(false);
        cbxSubtipoDocumental.setInputPrompt("Elegir el subtipo documental");
        cbxSubtipoDocumental.addValueChangeListener(event ->{    
            if ( cbxSubtipoDocumental.getValue() != null ){
                actualizarDatosJson(rutaRaiz+"/"+cbxArea.getValue()+"/"+cbxTipoDocumental.getValue()+"/"+event.getProperty().getValue().toString());
                vltMetadatos.removeComponent(vltMeta);
                vltMetadatos.removeComponent(btnSaveJson);
                vltMeta = this.getComponentes();
                vltMetadatos.addComponent(vltMeta);
                vltMetadatos.addComponent(btnSaveJson);
                vltMetadatos.setComponentAlignment(btnSaveJson, Alignment.BOTTOM_CENTER);
            } else {
                actualizarDatosJson(null);
            }
        });

        txfTemplateKey = new TextField();
        txfTemplateKey.setCaption("Template Key");
        txfTemplateKey.setSizeFull();
        //txfTemplateKey.setReadOnly(true);

        txfChecklistRelacionado = new TextField();
        txfChecklistRelacionado.setCaption("Checklist Relacionado ");
        txfChecklistRelacionado.setSizeFull();
        //txfChecklistRelacionado.setReadOnly(true);

        txfRuta = new TextField();
        txfRuta.setCaption("Ruta");
        txfRuta.setSizeFull();
        //txfRuta.setReadOnly(true);
        
        btnSaveJson = new Button();
        btnSaveJson.setWidth("50%");
        btnSaveJson.setEnabled(true);
        btnSaveJson.setDescription("Guardar");
        btnSaveJson.setIcon(FontAwesome.SAVE);
        btnSaveJson.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnSaveJson.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnSaveJson.addClickListener(e -> {
            this.subirMetadatos();
        });
        
        vltMetadatos = new VerticalLayout(cbxArea, cbxTipoDocumental, cbxSubtipoDocumental, txfTemplateKey, txfChecklistRelacionado, txfRuta, vltMeta);
        vltMetadatos.setWidth("100%");
        vltMetadatos.setMargin(true);
        
        panel = new Panel();
        panel.setWidth("100%");
        panel.setHeight("600px");
        panel.setScrollTop(1600);
        panel.setContent(vltMetadatos);
        
        HorizontalLayout hlt = new HorizontalLayout(pdf,panel);
        hlt.setSizeFull();
        hlt.setExpandRatio(pdf, 0.75f);
        hlt.setExpandRatio(panel, 0.25f);
        
        subWindow.setContent(hlt);
        subWindow.setSizeFull();
        subWindow.center();
        myUI.addWindow(subWindow);
    }
    
    public void subirMetadatos(){
        Map<String, String> metadatos = new HashMap<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //FORMATO TIME-STAMP QUE RECIBE BOX PARA LA FECHA
        String date = sdf.format(new Date());
        
        System.out.println(arrComponents.size());        
        boolean faltanObligatorios = false;
        for (Object obj : arrComponents) {
            if( obj.getClass().equals(TextField.class) ){
                if( (!((TextField)obj).getValue().equals("")) || !(((TextField)obj).getValue().isEmpty())){
                    metadatos.put( ((TextField)obj).getCaption(), ((TextField)obj).getValue());
                }else{
                    if ( ((TextField)obj).isRequired()) {
                        faltanObligatorios = true;
                    }
                }
            } else if ( obj.getClass().equals(DateField.class) ) {
                try{
                    if(!((DateField)obj).isEmpty()){
                        //date = ((DateField)obj).getValue().toString();
                        metadatos.put( ((DateField)obj).getCaption(), ((DateField)obj).getValue().toString());
                        
                    }else{
                        if ( ((DateField)obj).isRequired() ) {
                            faltanObligatorios = true;
                        }
                    }
                } catch (NullPointerException nullPointerException) {
                    System.out.println("nullPointerException -> "+nullPointerException);
                    //subtipoFormulario.addField(new FieldFormulario(((DateField)obj).getCaption(), ""));
                }
            }
        }
        if ( faltanObligatorios ) {
            Notification.show("Datos Faltantes", "Completa los campos requeridos.", Notification.Type.WARNING_MESSAGE);
            return;
        }
        
        
        
        
        System.out.println("INICIO");
        //Path pathFile = Paths.get(Constantes.PDF_TEST);
        

        CargarDocumentoBox cargarDoc = new CargarDocumentoBox();

        DocumentoVO documentoVO = new DocumentoVO();
        documentoVO.setInputStream(pdfFile);
        //documentoVO.setPath(pdfFile.getPath());
        
        System.out.println("path -> " + documentoVO.getPath());
        
        documentoVO.setNombreDocumento(pdfFile.getName());

        //SINIESTRO
        metadatos.put("numeroSiniestro", "66");
        metadatos.put("area", cbxArea.getValue().toString());
        metadatos.put("tipodocumental", cbxTipoDocumental.getValue().toString());
        String strExtension = cbxSubtipoDocumental.getValue().toString();
        metadatos.put("extension", strExtension.substring(strExtension.indexOf("."), strExtension.length()-1));
        metadatos.put("operacion", "Tercero");

        documentoVO.setMetadatos(metadatos);
        String strSubtipo = cbxSubtipoDocumental.getValue().toString();
        System.out.println("str -> "+strSubtipo);
        strSubtipo = strSubtipo.substring(0, strSubtipo.indexOf("."));
        System.out.println("str -> "+strSubtipo);
        documentoVO.setSubTipoDocumental(strSubtipo);
        
        for (Map.Entry<String, String> entry : metadatos.entrySet()) {
            System.out.format("Atributo : [ %s ], Valor : [ %s ] \n", entry.getKey(), entry.getValue());
        }
        
        Boolean resultado = cargarDoc.verificarMetadatos(documentoVO);
        
        System.out.println("boolean -> "+resultado);
    }
    
    public VerticalLayout getComponentes() {
        arrComponents = new ArrayList<>();
        VerticalLayout frmlt = new VerticalLayout();
        frmlt.setMargin(true);
        for (Field field : subtipoDocumental.getFields()) {
            if (field.getType().equals("date")) {
                DateField tempDtFld = new DateField();
                //tempDtFld.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                tempDtFld.setSizeFull();
                tempDtFld.setValidationVisible(true);
                tempDtFld.setInvalidAllowed(false);
                tempDtFld.setParseErrorMessage("Fecha incorrecta.");
                if( field.getRequired() ){
                    tempDtFld.setRequired(true);
                }
                tempDtFld.setCaption(field.getFieldKey());
                arrComponents.add( tempDtFld );
            } else {
                TextField tempTxtFld = new TextField();
                tempTxtFld.setSizeFull();
                if( field.getRequired() ){
                    tempTxtFld.setRequired(true);
                }
                tempTxtFld.setCaption(field.getFieldKey());
                arrComponents.add( tempTxtFld );
            }
        }
        for (Object obj : arrComponents) {
            if( obj.getClass().equals(TextField.class) ){
                frmlt.addComponent((TextField)obj);
            }else if( obj.getClass().equals(DateField.class) ){
                frmlt.addComponent((DateField)obj);
            }
        }
        return frmlt;
    }
    
    public void actualizarDatosJson(String rutaArchivoJson){
    	try{
            if ( rutaArchivoJson==null){
                txfTemplateKey.clear();
                txfChecklistRelacionado.clear();
                txfRuta.clear();
                vltMetadatos.removeComponent(btnSaveJson);
                vltMetadatos.removeComponent(vltMeta);
            } else {
                subtipoDocumental = this.getDatos(rutaArchivoJson.toString());
                if(subtipoDocumental.getTemplateKey()!=null){
                    txfTemplateKey.setValue(subtipoDocumental.getTemplateKey().toString());
                }if(subtipoDocumental.getChecklistrelacionado()!=null){
                    txfChecklistRelacionado.setValue(subtipoDocumental.getChecklistrelacionado().toString());
                }if(subtipoDocumental.getRuta()!=null){
                    txfRuta.setValue(subtipoDocumental.getRuta().toString());
                }
            }
        }catch(Exception e){
            System.out.println("actualizarDatosJson() : "+e);
            e.printStackTrace();
    	}
    	
    }
    
    public SubtipoDocumental getDatos(String jsonStr) {
        Gson gson = new Gson();
        SubtipoDocumental subtipo = null;
        try (Reader reader = new FileReader(jsonStr)) {
            subtipo = gson.fromJson(reader, SubtipoDocumental.class);
        } catch (IOException e) {
            System.out.println("getDatosJson -> "+e);
        }		
        return subtipo;
    }
    
    public Object getItem(String caption){
        for (Object object : arrObject) {
            if( object.getClass().equals(TextField.class) ){
                if(((TextField)object).getCaption().equals(caption)){
                    return object;
                }
            } else if ( object.getClass().equals(DateField.class) ) {
                if(((DateField)object).getCaption().equals(caption)){
                    return object;
                }
            }
        }
        return null;
    }
    
    public ArrayList<String> getAreaFolders(String rutaAreaFolders, boolean isSubtipo){
        ArrayList<String> areaFolders = new ArrayList<>();
        File fArea = new File(rutaAreaFolders);
        File[] arrTemp = fArea.listFiles();
        for (int i = 0; i < arrTemp.length; i++) {
            if(arrTemp[i].isDirectory() ||  isSubtipo){
                if ( !arrTemp[i].getName().contains("OBSOLETO") &&  !arrTemp[i].getName().contains("Obsoleto") && !arrTemp[i].getName().contains("obsoleto")) {
                    areaFolders.add(arrTemp[i].getName());
                }//areaFolders.add(arrTemp[i].getName());
            }
        }//areaFolders.add("Crear nuevo");
        return areaFolders;
    }
}