/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.data;

import com.vaadin.ui.Upload;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Eduardo
 */
public class LineBreakCounter implements Upload.Receiver {

    private String fileName;
    private String mtype;
    private int counter;

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        counter = 0;
            fileName = filename;
            mtype = mimeType;
            return new OutputStream() {
                private static final int searchedByte = '\n';

                @Override
                public void write(int b) throws IOException {
                    if (b == searchedByte) {
                        counter++;
                    }
                }
            };
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mtype;
        }

        public int getLineBreakCount() {
            return counter;
        }

}
