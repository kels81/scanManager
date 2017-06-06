/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Edrd
 */
public class Respuesta {
    
    private String idDocumento;
    private Map<String, String> elementos;
    private String detalleOperacion;
    private String respuestaOperacion;
    private String codigoOperacion;
    private ArrayList<DocumentoVO> subtiposChecklist;

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Map<String, String> getElementos() {
        return elementos;
    }

    public void setElementos(Map<String, String> elementos) {
        this.elementos = elementos;
    }

    public String getDetalleOperacion() {
        return detalleOperacion;
    }

    public void setDetalleOperacion(String detalleOperacion) {
        this.detalleOperacion = detalleOperacion;
    }

    public String getRespuestaOperacion() {
        return respuestaOperacion;
    }

    public void setRespuestaOperacion(String respuestaOperacion) {
        this.respuestaOperacion = respuestaOperacion;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    public void setCodigoOperacion(String codigoOperacion) {
        this.codigoOperacion = codigoOperacion;
    }

    public ArrayList<DocumentoVO> getSubtiposChecklist() {
        return subtiposChecklist;
    }

    public void setSubtiposChecklist(ArrayList<DocumentoVO> subtiposChecklist) {
        this.subtiposChecklist = subtiposChecklist;
    }

    
}

