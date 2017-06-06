/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

/**
 *
 * @author Edrd
 */
public enum RespuestaOperacion {

    MS_VALIDMTERR001("No se lograron validar los metadatos requeridos.", "OP_ERR"),
    MS_ALMEXT001("Se almaceno con éxito el documento.", "OP_EXT"),
    MS_ALMERR001("No se completó el proceso de almacenamiento.", "OP_ERR"),
    MS_ALMERR002("Existe un documento con el mismo nombre en la ruta designada.", "OP_ERR"),
    MS_VALIDCHKEXTWC001("Validación exitosa, si están los documentos obligatorios.", "OP_EXT"),
    MS_VALIDCHKEXTWI001("No se encuentran todos los documentos opcionales, pero se encontraron todos los documentos obligatorios.", "OP_EXT"),
    MS_VALIDCHKERR001("Falló el proceso de validación, no se encontraron todos los documentos obligatorios.", "OP_ERR"),
    MS_ACTTPDOCINV001("El Tipo Documental que estas tratando de actualizar no coincide con el Tipo Documental original.", "OP_ERR"),
    MS_ACTMTDINV001("Los Metadatos que estas tratando de asignar no son validos para este Tipo Documental.", "OP_ERR"),
    MS_ACTEXT001("Actualización exitosa de Documento.", "OP_EXT"),
    
    MS_CONSEX001("Se encontraron documentos.", "OP_EXT"),
    MS_CONSNOENCT001("No se encontraron documentos con los filtros seleccionados.", "OP_ERR"),
    
    MS_PORTEXT001("Se encontraron documentos.", "OP_EXT"),
    MS_PORTERR001("No hay documentos en la carpeta de salida.", "OP_ERR"),
    
    //EXCEPCIONES
    MS_EXEPCIONERR001("Falta variable en los metadatos: ","OP_ERR"),
    MS_EXEPCIONERR002("Revisar el nombre de area: ","OP_ERR"),
    MS_EXEPCIONERR003("El subtipo no existe en esta area: ","OP_ERR"),
    MS_EXEPCIONERR004("No se encuentra dentro de la carpeta portal de salida.","OP_ERR");
    
    private final String detalleOperacion;
    private final String respuestaOperacion;

    private RespuestaOperacion(String detalleOperacion, String respuestaOperacion) {
        this.detalleOperacion = detalleOperacion;
        this.respuestaOperacion = respuestaOperacion;
    }
    
    public String getDetalleOperacion(){
        return detalleOperacion;
    }
    
    public String getRespuestaOperacion(){
        return respuestaOperacion;
    }

}
