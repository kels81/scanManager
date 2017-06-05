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
 * @author Edrd
 */
public enum Human implements FontIcon {
    USER_36(0XF137),
    USER_4(0XF139);
    
    private final int codepoint;
    public static final String FONT_FAMILY = "Human";

    Human(int codepoint) {
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
        return GenericFontIcon.getHtml(Human.FONT_FAMILY, codepoint);
    }

    @Override
    public String getMIMEType() {
        throw new UnsupportedOperationException(
                FontIcon.class.getSimpleName()
                + " should not be used where a MIME type is needed.");
    }

}
