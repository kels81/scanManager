/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.component;

import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.data.Property;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Eduardo
 */
public class UploadPicture extends VerticalLayout {

    private final Image profilePic;
    private final OnOffSwitch onOffswitch;
    //private final Switch swtch;
    //private final UploadField upload;
    private final Upload upload;
    private final Label lblAct;
    private final ImageReceiver imageReceiver;

    private final String path;

    public UploadPicture() {
        this.path = VaadinService.getCurrent().getBaseDirectory() + "/VAADIN/themes/UPLOADS/";

        setSizeUndefined();
        setSpacing(true);
        //setMargin(new MarginInfo(false, true, false, false));
        addStyleName("profilePic");
        profilePic = new Image(null, new ThemeResource(
                "img/profile-pic-300px.jpg"));
        addComponent(profilePic);

//        upload = new UploadField();
//        upload.setButtonCaption("Seleccionar");
//        upload.setFieldType(UploadField.FieldType.FILE);
//        upload.setAcceptFilter("image/*");
//        upload.setMaxFileSize(1048576);   //limit to 1MB size in  bytes
//        upload.setFileDeletesAllowed(false);
//        upload.setDisplayUpload(false);
//        upload.setFileFactory((String fileName, String mimeType) -> {
//            File f = new File(path,fileName);
//            return f;
//        });


        imageReceiver = new ImageReceiver();
        upload = new Upload(null, imageReceiver);
        upload.setImmediate(true);
        upload.setButtonCaption("Cambiar");
        // Restricting file types upload
        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[0].setAttribute('accept', 'image/png, image/gif, image/jpeg')");
        upload.addSucceededListener(imageReceiver);
        

        // Prevent too big downloads
        final long UPLOAD_LIMIT = 1048576;      //limit to 1MB size  se escriben en bytes
        upload.addStartedListener((Upload.StartedEvent event) -> {
            
            if (event.getContentLength() > UPLOAD_LIMIT) {
                upload.interruptUpload();
                Notification.show("Too big file",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        // Check the size also during progress 
        upload.addProgressListener((long readBytes, long contentLength) -> {
            if (readBytes > UPLOAD_LIMIT) {
                upload.interruptUpload();
                Notification.show("Too big file",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        // Create uploadFiles directory
        File uploadFiles = new File(path);
        if (!uploadFiles.exists() && !uploadFiles.mkdir()) {
            addComponent(new Label("ERROR: Could not create uploadFile dir"));
        }

        HorizontalLayout hor = new HorizontalLayout();
        hor.setSpacing(true);
        hor.setMargin(new MarginInfo(true, false, false, false));
        onOffswitch = new OnOffSwitch(true);
        onOffswitch.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object checked = event.getProperty().getValue();
                if (checked.equals(true)) {
                    lblAct.setCaption("Activo");
                    lblAct.removeStyleName("red-label");
                    lblAct.addStyleName("green-label");
                } else {
                    lblAct.setCaption("Inactivo");
                    lblAct.removeStyleName("green-label");
                    lblAct.addStyleName("red-label");
                }
            }
        });
        lblAct = new Label();
        lblAct.setCaption("Activo");
        //lblAct.addStyleName("green-label");
        hor.addComponents(onOffswitch, lblAct);

        //swtch = new Switch();
        //swtch.addStyleName("compact");
        //swtch.setImmediate(true);
        addComponents(upload, hor);
    }

    class ImageReceiver implements Upload.Receiver, Upload.SucceededListener {

        public File file;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            // Create upload stream
            FileOutputStream fos = null; // Stream to write to

                try {
                    // Open the file for writing.
                    file = new File(path + filename);
                    fos = new FileOutputStream(file);
                } catch (final java.io.FileNotFoundException e) {
                    new Notification("Could not open file<br/>",
                            e.getMessage(),
                            Notification.Type.ERROR_MESSAGE)
                            .show(Page.getCurrent());
                    return null;
                }

            return fos; // Return the output stream to write to
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            // Show the uploaded file in the image viewer
            profilePic.setSource(new FileResource(file));
            //profilePic.setWidth(120.0f, Sizeable.Unit.PIXELS);
            //profilePic.setHeight(120.0f, Sizeable.Unit.PIXELS);
        }
    }

}
