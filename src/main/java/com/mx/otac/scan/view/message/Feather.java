/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.view.message;

import com.vaadin.server.FontIcon;
import com.vaadin.server.GenericFontIcon;

/**
 *
 * @author Eduardo
 */
public enum Feather implements FontIcon {

    HEAD(0XE074);
    
    private final int codepoint;
    public static final String FONT_FAMILY = "Feather";

    Feather(int codepoint) {
        this.codepoint = codepoint;
    }

    @Override
    public String getFontFamily() {
        return FONT_FAMILY;
    }

    @Override
    public int getCodepoint() {
        return codepoint;
    }

    @Override
    public String getHtml() {
        return GenericFontIcon.getHtml(Feather.FONT_FAMILY, codepoint);
    }

    @Override
    public String getMIMEType() {
        throw new UnsupportedOperationException(
                FontIcon.class.getSimpleName()
                + " should not be used where a MIME type is needed.");
    }
}
