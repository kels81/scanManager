/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.mx.otac.scan.zbox.WindowPDF;

/**
 *
 * @author Edrd
 */
public class Constantes {
    
    //CARPETA DE ARCHIVOS PC
    public static final String ROOT_PATH = "C:\\Users\\Edrd\\Documents\\GitHub\\fileManager\\Archivos";
    //CARPETA SUBTIPOS
    public static final String RUTA_RAIZ = WindowPDF.class.getResource("templates.json").getPath().replace("templates.json", "subtipos");
    
    //BOX
    public static final String DEVELOPER_TOKEN = "ViFfu9RELJo2mH3jOHGMC7LMUwcQSR2e";
    
    public static final String TIPO_SINIESTRO = "siniestro";
    public static final String ID_FOLDER_SINIESTRO = "28924547437";
}
