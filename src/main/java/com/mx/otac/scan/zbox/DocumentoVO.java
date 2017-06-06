/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

import com.box.sdk.Metadata;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Edrd
 */
public class DocumentoVO {

    private String idDocumento;
    private String tipoDocumental;
    private String subTipoDocumental;
    private Map<String, String> metadatos;
    private File inputStream;
    private String nombreDocumento;
    private Boolean cargado;
    private String idChecklist;
    private ArrayList<String> subtiposDocumentales;
    private Respuesta respuesta;
    private String path;
    private List<MetadatosTemplateVO> metadatosTemplate;

    private Map<String, String> elementos;
    private int tipoOpcion;
    private ArrayList<DocumentoVO> subtiposChecklist;
    private int idError;
    private Integer version;
    private List<Metadata> metadataList;
    private String areaResponsable;
    private Boolean obligatorioCheckList;

    private String comodin;
    private String bnd;

    private String idJsonFile;
    private String contenidoJson;
    private String idFolder;
    private ArrayList<DocumentoVO> documentosPortal;

    public Boolean getCargado() {
        return cargado;
    }

    public Respuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }

    public void setCargado(Boolean cargado) {
        this.cargado = cargado;
    }

    /**
     * Regresa el archivo.
     *
     * @return Tipo File.
     */
    public File getInputStream() {
        return this.inputStream;
    }

    /**
     * Asigna el inputStream.
     *
     * @param inputStream el archivo tipo File.
     */
    public void setInputStream(File inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Regresa el nombre del documento.
     *
     * @return regresa un tipo String.
     */
    public String getNombreDocumento() {
        return this.nombreDocumento;
    }

    /**
     * Asigna el nombre del documento.
     *
     * @param nombreDocumento el nombre del documento.
     */
    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    /**
     * Regresa el identificador del documento.
     *
     * @return tipo String.
     */
    public String getIdDocumento() {
        return this.idDocumento;
    }

    /**
     * Asigna el identificador del documento.
     *
     * @param idDocumento el identificador documental.
     */
    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    /**
     * Regresa el tipo documental.
     *
     * @return tipo String.
     */
    public String getTipoDocumental() {
        return this.tipoDocumental;
    }

    /**
     * Asigna el tipo documental.
     *
     * @param tipoDocumental tipo documental.
     */
    public void setTipoDocumental(String tipoDocumental) {
        this.tipoDocumental = tipoDocumental;
    }

    /**
     * Regresa los metadatos.
     *
     * @return mapa con metadatos.
     */
    public Map<String, String> getMetadatos() {
        return this.metadatos;
    }

    /**
     * Asigna metadatos.
     *
     * @param metadatos metadatos a asignar.
     */
    public void setMetadatos(Map<String, String> metadatos) {
        if (metadatos == null) {
            this.metadatos = new HashMap<String, String>();
        }
        this.metadatos = metadatos;
    }

    /**
     * Regresa el metadato.
     *
     * @param key Campo llave.
     * @return Valor tipo String.
     */
    public String getMetadato(String key) {
        return this.metadatos.get(key);
    }

    /**
     * Asigna metadatos.
     *
     * @param key Campo llave.
     * @param value Valor del metadato.
     */
    public void setMetadato(String key, String value) {
        this.metadatos.put(key, value);
    }

    public String getSubTipoDocumental() {
        return subTipoDocumental;
    }

    public void setSubTipoDocumental(String subTipoDocumental) {
        this.subTipoDocumental = subTipoDocumental;
        if (this.subtiposDocumentales == null) {
            this.subtiposDocumentales = new ArrayList<String>();
        }
        this.subtiposDocumentales.add(subTipoDocumental);
    }

    /**
     * Regresa idChecklist.
     *
     * @return the idChecklist.
     */
    public String getIdChecklist() {
        return idChecklist;
    }

    /**
     * Asigna idChecklist.
     *
     * @param idChecklist the idChecklist to set.
     */
    public void setIdChecklist(String idChecklist) {
        this.idChecklist = idChecklist;
    }

    public ArrayList<String> getSubtiposDocumentales() {
        return subtiposDocumentales;
    }

    public void setSubtiposDocumentales(ArrayList<String> subtiposDocumentales) {
        this.subtiposDocumentales = subtiposDocumentales;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<MetadatosTemplateVO> getMetadatosTemplate() {
        return metadatosTemplate;
    }

    public void setMetadatosTemplate(List<MetadatosTemplateVO> metadatosTemplate) {
        this.metadatosTemplate = metadatosTemplate;
    }

    public Map<String, String> getElementos() {
        return elementos;
    }

    public void setElementos(Map<String, String> elementos) {
        this.elementos = elementos;
    }

    public int getTipoOpcion() {
        return tipoOpcion;
    }

    public void setTipoOpcion(int tipoOpcion) {
        this.tipoOpcion = tipoOpcion;
    }

    public ArrayList<DocumentoVO> getSubtiposChecklist() {
        return subtiposChecklist;
    }

    public void setSubtiposChecklist(ArrayList<DocumentoVO> subtiposChecklist) {
        this.subtiposChecklist = subtiposChecklist;
    }

    public int getIdError() {
        return idError;
    }

    public void setIdError(int idError) {
        this.idError = idError;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public String getAreaResponsable() {
        return areaResponsable;
    }

    public void setAreaResponsable(String areaResponsable) {
        this.areaResponsable = areaResponsable;
    }

    public Boolean getObligatorioCheckList() {
        return obligatorioCheckList;
    }

    public void setObligatorioCheckList(Boolean obligatorioCheckList) {
        this.obligatorioCheckList = obligatorioCheckList;
    }

    public String getComodin() {
        return comodin;
    }

    public void setComodin(String comodin) {
        this.comodin = comodin;
    }

    public String getIdJsonFile() {
        return idJsonFile;
    }

    public void setIdJsonFile(String idJsonFile) {
        this.idJsonFile = idJsonFile;
    }

    public String getContenidoJson() {
        return contenidoJson;
    }

    public void setContenidoJson(String contenidoJson) {
        this.contenidoJson = contenidoJson;
    }

    public String getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(String idFolder) {
        this.idFolder = idFolder;
    }

    public String getBnd() {
        return bnd;
    }

    public void setBnd(String bnd) {
        this.bnd = bnd;
    }

    public ArrayList<DocumentoVO> getDocumentosPortal() {
        return documentosPortal;
    }

    public void setDocumentosPortal(ArrayList<DocumentoVO> documentosPortal) {
        this.documentosPortal = documentosPortal;
    }

}

