/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.util;

import com.mx.otac.scan.zbox.CargarDocumentoBox;

/**
 *
 * @author Edrd
 */
public class Constantes {
    
    //CARPETA DE ARCHIVOS PC
    public static final String ROOT_PATH = "C:\\Users\\Edrd\\Documents\\GitHub\\scanManager\\Archivos";
    public static final String TARGET_DIR = "C:\\BoxTest";
    
    //public static final String ROOT_PATH = "C:\\Users\\OTAC\\Desktop\\BPM\\img";
    //CARPETA SUBTIPOS
    public static final String RUTA_RAIZ = CargarDocumentoBox.class.getResource("templates.json").getPath().replace("templates.json", "subtipos");
    
    //BOX
    public static final String DEVELOPER_TOKEN = "PycmqRQO4v30qD9oF6ypJ2njszZx25Bs";
    
    public static final String ID_FOLDER_SINIESTRO = "28924547437";
    
}
