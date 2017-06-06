/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Edrd
 */
public class MetadatosTemplateVO{
    private String plantilla;
    private String contenidoPlantilla;
    private Map<String, String> properties;
    
    public MetadatosTemplateVO(){
        properties = new ConcurrentHashMap<String, String>();
    }
    
    public String getMetadato(String key) {
        return this.properties.get(key);
    }

    public void setMetadato(String key, String value) {
        key = key.replace("/", "");
        this.properties.put(key, value);
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }
    public Set<String> getKeys(){
        return this.properties.keySet();
    }
    public Boolean removeMetadato(String key){
        return this.properties.remove(key)==null?Boolean.FALSE:Boolean.TRUE;
    }

    public String getContenidoPlantilla() {
        return contenidoPlantilla;
    }

    public void setContenidoPlantilla(String contenidoPlantilla) {
        this.contenidoPlantilla = contenidoPlantilla;
    }
}
