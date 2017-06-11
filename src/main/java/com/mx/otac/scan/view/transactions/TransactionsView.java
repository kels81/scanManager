package com.mx.otac.scan.view.transactions;

import java.text.SimpleDateFormat;

import com.google.common.eventbus.Subscribe;
import com.mx.otac.scan.util.Constantes;
import com.mx.otac.scan.util.FileTransactions;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.mx.otac.scan.data.DirectoryContentData;
import com.mx.otac.scan.event.DashboardEvent.BrowserResizeEvent;
import com.mx.otac.scan.event.DashboardEventBus;
import com.mx.otac.scan.util.Components;
import com.mx.otac.scan.util.DataProvider;
import com.mx.otac.scan.util.Notifications;
import com.mx.otac.scan.zbox.CargarDocumentoBox;
import com.mx.otac.scan.zbox.DocumentoVO;
import com.mx.otac.scan.zbox.Field;
import com.mx.otac.scan.zbox.SubtipoDocumental;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings({"serial", "unchecked"})
public final class TransactionsView extends VerticalLayout implements View {

    private Table table;
    private Window window;
    private Button save;
    private Button cancel;

    private File rootPath;

    private static final String[] DEFAULT_COLLAPSIBLE = {"fecha", "tamaño"};

    private final DirectoryContentData content = new DirectoryContentData();
    private final FileTransactions fileTrans = new FileTransactions();
    private final Components component = new Components();
    private final Notifications notification = new Notifications();

    public TransactionsView() {
        setSizeFull();
        addStyleName("transactions");
        DashboardEventBus.register(this);

        addComponent(buildToolbar());

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);

    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        DashboardEventBus.unregister(this);
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Archivos");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        HorizontalLayout tools = new HorizontalLayout(buildFilter());
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(final TextChangeEvent event) {
                Filterable data = (Filterable) table.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                            final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("nombre", item,
                                event.getText());
                    }

                    private boolean filterByProperty(final String prop, final Item item,
                            final String text) {
                        if (item == null || item.getItemProperty(prop) == null
                                || item.getItemProperty(prop).getValue() == null) {
                            return false;
                        }
                        String val = item.getItemProperty(prop).getValue().toString().trim()
                                .toLowerCase();
                        return val.contains(text.toLowerCase().trim());
                    }

                    @Override
                    public boolean appliesToProperty(Object propertyId) {
                        return propertyId.equals("nombre");
                    }
                });
            }
        });

        filter.setInputPrompt("Filtrar");
        filter.setDescription("Filtrar por nombre del archivo");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.setWidth(100.0f, Unit.PERCENTAGE);
        filter.addShortcutListener(new ShortcutListener("Clear",
                KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((Filterable) table.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private Table buildTable() {
        Table table = new Table();

        table.setSizeFull();
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setSelectable(false);
        table.setImmediate(true);
        table.setSortEnabled(false);

        table.setColumnAlignment("tamaño", Align.RIGHT);
        table.setColumnAlignment("fecha", Align.RIGHT);
        table.setColumnAlignment(" ", Align.CENTER);

        table.setColumnHeader("nombre", "Nombre Archivo");
        table.setColumnHeader("fecha", "Fecha Creación");
        table.setColumnHeader("tamaño", "Tamaño");

        //PARA HACER RESPONSIVO LA TABLA
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsible("nombre", false);
        table.setColumnCollapsible(" ", false);

        table.refreshRowCache();
        table.setRowHeaderMode(Table.RowHeaderMode.INDEX);          //PARA ENUMERAR LAS FILAS
        table.setContainerDataSource(crearContenedor());

        return table;
    }

    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                table.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    public IndexedContainer crearContenedor() {

        rootPath = new File(Constantes.ROOT_PATH);
        IndexedContainer idxCont = new IndexedContainer();

        idxCont.addContainerProperty("nombre", Label.class, "");
        idxCont.addContainerProperty("fecha", String.class, "");
        idxCont.addContainerProperty("tamaño", String.class, "");
        idxCont.addContainerProperty(" ", MenuBar.class, "");

        List<File> files = content.directoryContents(rootPath);

        if (!files.isEmpty()) {
            for (File file : files) {
                long fileSize = file.length();
                String fileSizeDisplay = FileUtils.byteCountToDisplaySize(fileSize);

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty("nombre").setValue(new Label(FontAwesome.FILE.getHtml() + " " + file.getName(), ContentMode.HTML));
                item.getItemProperty("fecha").setValue(getAtributos(file));
                item.getItemProperty("tamaño").setValue(fileSizeDisplay);
                item.getItemProperty(" ").setValue(createButtonMenu(file));
            }
        }

        return idxCont;
    }

    private String getAtributos(File file) {
        String fechaCreacion = "";
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime date = attr.creationTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy   hh:mm:ss a");
            fechaCreacion = df.format(date.toMillis());
            /*
            SimpleDateFormat df2 = new SimpleDateFormat("MMM");
            String mes = df2.format(date.toMillis());
            System.out.println("mes = " + mes);

            //Obteniendo mes de la fecha
            String strMes = fechaCreacion.substring(fechaCreacion.indexOf(" ") + 1, fechaCreacion.length() - 4);
            System.out.println("strMes = " + strMes);
            //Primera letra del mes en Mayuscula
            //fechaCreacion = fechaCreacion.replaceFirst(strMes.substring(0, 1), strMes.substring(0, 1).toUpperCase());
             */
        } catch (IOException ex) {
            Logger.getLogger(TransactionsContainer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fechaCreacion;
    }

    public MenuBar createButtonMenu(File file) {
//        Button btn = new Button(FontAwesome.ELLIPSIS_H);
//        btn.addStyleName(ValoTheme.BUTTON_TINY);
//
//        ContextMenu contextMenu = new ContextMenu(this, false);
//        fillMenu(contextMenu, file);
//        contextMenu.setAsContextMenuOf(btn);

        MenuBar btnMenu = component.createMenuBar();
        MenuBar.MenuItem menu = btnMenu.addItem("", FontAwesome.ELLIPSIS_H, null);
        menu.addItem("Editar", FontAwesome.PENCIL, (MenuBar.MenuItem selectedItem) -> {
            //Notification.show(file.getName());
            Window w = createWindowEdit(file);
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        menu.addItem("Eliminar", FontAwesome.TRASH, (MenuBar.MenuItem selectedItem) -> {
            //Notification.show(file.getName());
            Window w = createWindowConfirm(file);
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        menu.addItem("Metadatos", FontAwesome.CODE, (MenuBar.MenuItem selectedItem) -> {
            //fileTrans.viewPDF(UI.getCurrent(), file);
            WindowViewer viewerWindow = new WindowViewer(file);
            Window w = viewerWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });

        return btnMenu;
    }

    private Window createWindowEdit(File file) {
        window = new Window();
        window.addStyleName("createfolder-window");
        Responsive.makeResponsive(this);

        window.setModal(true);
        window.setResizable(false);
        window.setClosable(true);
        window.setHeight(90.0f, Sizeable.Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        window.setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        /*[ NAMEFOLDER ]*/
        VerticalLayout body = new VerticalLayout();
        body.setCaption("Editar");
        body.setSizeFull();
        body.setSpacing(true);
        body.setMargin(true);

        TextField txtEditName = new TextField();
        txtEditName.focus();
        txtEditName.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        txtEditName.setValue(FilenameUtils.getBaseName(file.getName()));    //Para mostrar solamente el nombre del archivo sin la extensión
        txtEditName.setInputPrompt("Nuevo nombre del archivo");
        txtEditName.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);          //EAGER, Para que evento no sea lento
        txtEditName.setTextChangeTimeout(200);
        txtEditName.setImmediate(true);
        txtEditName.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            save.setEnabled(StringUtils.isNotBlank(event.getText()));
        });

        body.addComponent(txtEditName);
        body.setComponentAlignment(txtEditName, Alignment.MIDDLE_CENTER);
        /*[ /NAMEFOLDER ]*/

 /*[ FOOTER ]*/
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        cancel = new Button("Cancelar");
        cancel.addStyleName(ValoTheme.BUTTON_SMALL);
        cancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                window.close();
            }
        });
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        save = component.createButtonPrimary("Guardar");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setEnabled(false);
        save.addClickListener((ClickEvent event) -> {
            Path source = Paths.get(file.getAbsolutePath());
            String newName = txtEditName.getValue();
            String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
            File newFile = new File(source.getParent().toString() + "\\" + newName + "." + extension);

            Boolean confirm = fileTrans.renameFile(source, file, newFile);

            if (confirm) {
                this.uptadeTable();
                notification.createSuccess("Se renombró el archivo correctamente: " + newName + "." + extension);
            } else {
                notification.createFailure("No se renombró el archivo");
            }

            window.close();
        });
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setExpandRatio(cancel, 1);
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        /*[ /FOOTER ]*/

        detailsWrapper.addComponent(body);
        content.addComponent(footer);

        return window;
    }

    private Window createWindowConfirm(File file) {
        window = new Window();
        window.addStyleName("confirm-window");
        Responsive.makeResponsive(this);

        window.setModal(true);
        window.setResizable(false);
        window.setClosable(true);
        window.setHeight(90.0f, Sizeable.Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        window.setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);

        /*[ BODY TEXT ]*/
        VerticalLayout body = new VerticalLayout();
        body.setCaption("Confirmar");
        body.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        body.setMargin(true);

        Label messageLbl = new Label("¿Está seguro de que desea eliminar este archivo?");

        body.addComponent(messageLbl);
        body.setComponentAlignment(messageLbl, Alignment.MIDDLE_LEFT);
        /*[ /BODY TEXT ]*/

 /*[ FOOTER ]*/
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        cancel = new Button("Cancelar");
        cancel.addStyleName(ValoTheme.BUTTON_SMALL);
        cancel.addClickListener((ClickEvent event) -> {
            window.close();
        });
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        save = component.createButtonPrimary("Aceptar");
        save.focus();
        save.addClickListener((ClickEvent event) -> {
            Path source = Paths.get(file.getAbsolutePath());

            Boolean confirm = fileTrans.deleteFile(source, file);

            if (confirm) {
                this.uptadeTable();
                notification.createSuccess("Se eliminó el archivo correctamente: " + file.getName());
            } else {
                notification.createFailure("No se elimino el archivo");
            }

            window.close();
        });
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setExpandRatio(cancel, 1);
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        /*[ /FOOTER ]*/

        detailsWrapper.addComponent(body);
        content.addComponent(footer);

        return window;
    }

    public void uptadeTable() {
        removeComponent(table);

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);
    }
    
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
                uptadeTable();
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


}
