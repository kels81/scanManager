package com.mx.otac.scan.view.transactions;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.vaadin.maddon.FilterableListContainer;

import com.google.common.eventbus.Subscribe;
import com.mx.otac.scan.util.Constantes;
import com.mx.otac.scan.util.FileTransactions;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.mx.otac.scan.data.DirectoryContentData;
import com.mx.otac.scan.domain.Transaction;
import com.mx.otac.scan.event.DashboardEvent.BrowserResizeEvent;
import com.mx.otac.scan.event.DashboardEventBus;
import com.mx.otac.scan.util.Components;
import com.mx.otac.scan.util.Notifications;
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.Menu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings({"serial", "unchecked"})
public final class TransactionsView extends VerticalLayout implements View {

    private final Table table;
    private Window window;
    private Button save;
    private Button cancel;
    private Button createReport;

    private File rootPath;

    private static final String[] DEFAULT_COLLAPSIBLE = {"country", "city", "theater", "room", "title", "seats"};

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
        VerticalLayout header = new VerticalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Latest Transactions");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        createReport = buildCreateReport();
        HorizontalLayout tools = new HorizontalLayout(buildFilter(), createReport);
        //HorizontalLayout tools = new HorizontalLayout(buildFilter());
        tools.setSpacing(true);
        tools.setWidth(100.0f, Unit.PERCENTAGE);
        tools.addStyleName("toolbar");
        //tools.setComponentAlignment(buildFilter(), Alignment.MIDDLE_LEFT);
        //tools.setComponentAlignment(createReport, Alignment.MIDDLE_RIGHT);
        header.addComponent(tools);

        return header;
    }

    private Button buildCreateReport() {
        final Button createReport = new Button("Cambiar Nombre");
        //createReport.setDescription("Create a new report from the selected transactions");
        createReport.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                //createNewReportFromSelection();
            }
        });
        createReport.setEnabled(false);
        return createReport;
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

                        return filterByProperty("country", item,
                                event.getText())
                                || filterByProperty("city", item,
                                        event.getText())
                                || filterByProperty("title", item,
                                        event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("country")
                                || propertyId.equals("city")
                                || propertyId.equals("title")) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        filter.setInputPrompt("Filter");
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

        table.setColumnAlignment("Tama�o", Align.RIGHT);
        table.setColumnAlignment("Fecha Creaci�n", Align.RIGHT);
        table.setColumnAlignment(" ", Align.CENTER);

        table.setColumnCollapsingAllowed(false);
        table.setSortEnabled(false);

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

    private boolean filterByProperty(final String prop, final Item item,
            final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false;
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase();
        if (val.contains(text.toLowerCase().trim())) {
            return true;
        }
        return false;
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    private class TempTransactionsContainer extends
            FilterableListContainer<Transaction> {

        public TempTransactionsContainer(
                final Collection<Transaction> collection) {
            super(collection);
        }

        // This is only temporarily overridden until issues with
        // BeanComparator get resolved.
        @Override
        public void sort(final Object[] propertyId, final boolean[] ascending) {
            final boolean sortAscending = ascending[0];
            final Object sortContainerPropertyId = propertyId[0];
            Collections.sort(getBackingList(), new Comparator<Transaction>() {
                @Override
                public int compare(final Transaction o1, final Transaction o2) {
                    int result = 0;
                    if ("time".equals(sortContainerPropertyId)) {
                        result = o1.getTime().compareTo(o2.getTime());
                    } else if ("country".equals(sortContainerPropertyId)) {
                        result = o1.getCountry().compareTo(o2.getCountry());
                    } else if ("city".equals(sortContainerPropertyId)) {
                        result = o1.getCity().compareTo(o2.getCity());
                    } else if ("theater".equals(sortContainerPropertyId)) {
                        result = o1.getTheater().compareTo(o2.getTheater());
                    } else if ("room".equals(sortContainerPropertyId)) {
                        result = o1.getRoom().compareTo(o2.getRoom());
                    } else if ("title".equals(sortContainerPropertyId)) {
                        result = o1.getTitle().compareTo(o2.getTitle());
                    } else if ("seats".equals(sortContainerPropertyId)) {
                        result = new Integer(o1.getSeats()).compareTo(o2
                                .getSeats());
                    } else if ("price".equals(sortContainerPropertyId)) {
                        result = new Double(o1.getPrice()).compareTo(o2
                                .getPrice());
                    }

                    if (!sortAscending) {
                        result *= -1;
                    }
                    return result;
                }
            });
        }

    }

    public IndexedContainer crearContenedor() {

        rootPath = new File(Constantes.ROOT_PATH);
        IndexedContainer idxCont = new IndexedContainer();

        //idxCont.addContainerProperty("No.", Integer.class, "");
        idxCont.addContainerProperty("Nombre", Label.class, "");
        idxCont.addContainerProperty("Fecha Creaci�n", String.class, "");
        idxCont.addContainerProperty("Tama�o", String.class, "");
        idxCont.addContainerProperty(" ", MenuBar.class, "");

        int contador = 1;

        List<File> files = content.directoryContents(rootPath);

        if (!files.isEmpty()) {
            for (File file : files) {
                long fileSize = file.length();
                String fileSizeDisplay = FileUtils.byteCountToDisplaySize(fileSize);

                Item item = idxCont.getItem(idxCont.addItem());
                //item.getItemProperty("No.").setValue(contador++);
                item.getItemProperty("Nombre").setValue(new Label(FontAwesome.FILE.getHtml() + " " + file.getName(), ContentMode.HTML));
                item.getItemProperty("Fecha Creaci�n").setValue(getAtributos(file));
                item.getItemProperty("Tama�o").setValue(fileSizeDisplay);
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

        MenuBar btnMenu = new MenuBar();
        btnMenu.addStyleName(ValoTheme.MENUBAR_SMALL);
        btnMenu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuBar.MenuItem menu = btnMenu.addItem("", FontAwesome.ELLIPSIS_H, null);
        menu.addItem("Editar", FontAwesome.PENCIL, (MenuBar.MenuItem selectedItem) -> {
            Notification.show(file.getName());
            Window w = createWindowEdit(file);
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        menu.addItem("Eliminar", FontAwesome.TRASH, (MenuBar.MenuItem selectedItem) -> {
            Notification.show(file.getName());
            Window w = createWindowConfirm(file);
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        menu.addItem("Metadatos", FontAwesome.CODE, (MenuBar.MenuItem selectedItem) -> {
            Notification.show(file.getName());
//            Window w = createWindow();
//            UI.getCurrent().addWindow(w);
//            w.focus();
        });

        return btnMenu;
    }

    private void fillMenu(ContextMenu menu, File file) {
        //EDITAR
        MenuItem editar = menu.addItem("Editar", e -> {
            Notification.show(file.getName());
//            Window w = createWindowEdit(file);
//            UI.getCurrent().addWindow(w);
//            w.focus();
        });
        editar.setIcon(FontAwesome.PENCIL);
        //BORRAR
        MenuItem borrar = menu.addItem("Eliminar", new Menu.Command() {
            @Override
            public void menuSelected(MenuItem e) {
                Notification.show(file.getName());
//                Window w = createWindowConfirm(file);
//                UI.getCurrent().addWindow(w);
//                w.focus();
            }
        });
        borrar.setIcon(FontAwesome.TRASH);

        // SEPARADOR
        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).addSeparator();
        }
        //MOVER-COPIAR
        MenuItem moverCopiar = menu.addItem("Mover o Copiar", e -> {
            Notification.show(file.getName());
        });
        moverCopiar.setIcon(FontAwesome.COPY);

    }

    private Window createWindowEdit(File file) {
        window = new Window();
        window.addStyleName("createfolder-window");
        Responsive.makeResponsive(this);

        window.setModal(true);
        window.setResizable(false);
        window.setClosable(true);
        window.setHeight(90.0f, Sizeable.Unit.PERCENTAGE);
        //window.setWidth(300.0f, Unit.PIXELS);

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
        body.setCaption("Renombrar");
        body.setSizeFull();
        body.setSpacing(true);
        body.setMargin(true);

        TextField txtEditName = new TextField();
        txtEditName.focus();
        txtEditName.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        txtEditName.setValue(FilenameUtils.getBaseName(file.getName()));    //Para mostrar solamente el nombre del archivo sin la extensi�n
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

            fileTrans.renameFile(source, file, newFile);

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

        Label messageLbl = new Label("�Est� seguro de que desea eliminar este archivo?");

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

            fileTrans.deleteFile(source, file);

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

}
