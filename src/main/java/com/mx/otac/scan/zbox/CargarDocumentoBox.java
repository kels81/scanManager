/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.otac.scan.zbox;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxMetadataFilter;
import com.box.sdk.BoxSearch;
import com.box.sdk.BoxSearchParameters;
import com.box.sdk.Metadata;
import com.box.sdk.MetadataTemplate;
import com.box.sdk.PartialCollection;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.mx.otac.scan.util.Constantes;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Edrd
 */
public class CargarDocumentoBox {

    private static BoxAPIConnection api;

    public CargarDocumentoBox() {
        api = new BoxAPIConnection(Constantes.DEVELOPER_TOKEN);
    }

    public String buscarCarpetasRuta(String idRootFolder, List arrayFolders) {
        String idFolder = "";
        int i = 1;
        idFolder = idRootFolder;
        BoxFolder rootCarpeta = new BoxFolder(api, idFolder);
        for (Object arrayFolder : arrayFolders) {
            /**
             * CUANDO ES LA CARPETA RAIZ (CARPETA PADRE) SE BUSCARA CON UN
             * METODO Y YA DENTRO DE LA CARPETA RAIZ SE RECORRERA LAS CARPETAS
             * HIJAS.
             */
            idFolder = (i == 1 ? existeCarpetaPadre(arrayFolder.toString(), idFolder) : existeCarpetaHija(arrayFolder.toString(), idFolder));

            if (idFolder.equals("")) {
                /**
                 * SI idFolder ESTA VACIO SIGNIFICA QUE NO EXISTE NINGUN FOLDER
                 * QUE COINCIDA CON EL NOMBRE A BUSCADO ANTERIORMENTE, Y PROCEDE
                 * A CREAR EL FOLDER
                 */
                try {
                    BoxFolder.Info folderIdSiniestro = rootCarpeta.createFolder(arrayFolder.toString().trim().replaceAll("\\s+", " "));
                    idFolder = folderIdSiniestro.getID();
                } catch (BoxAPIException ex) {
                    /**
                     * SE MANEJA LA EXCEPCION CUANDO SE INTENTA GUARDAR UN
                     * DOCUMENTO DURANTE LOS PRIMEROS 10 MINUTOS DESPUES DE
                     * HABERLO SUBIDO, YA QUE NO HA SIDO INDEXADO POR PARTE DE
                     * BOX PARA SU BUSQUEDA POR PALABRA
                     */
                    idFolder = ex.getDetails().getConflictId();
                }
            }
            rootCarpeta = new BoxFolder(api, idFolder);
            i++;
        }

        return idFolder;
    }

    public String existeCarpetaHija(String objetoBuscar, String idFolderPadre) {
        //System.out.println("objetoBuscar" + objetoBuscar);
        //System.out.println("idFolderPadre" + idFolderPadre);
        String idFolder = "";
        BoxFolder folder = new BoxFolder(this.api, idFolderPadre);
        for (BoxItem.Info itemInfo : folder) {
            if (itemInfo instanceof BoxFolder.Info) {
                if (itemInfo.getName().equals(objetoBuscar)) {
                    idFolder = itemInfo.getID();
                    //System.out.println("idFolder" + idFolder);
                }
            }
        }
        return idFolder;
    }

    public String existeDocumento(String[] objetoBuscar, String idFolderPadre) {
        //Boolean exist = Boolean.FALSE;
        String idFile = "";
        BoxFolder file = new BoxFolder(this.api, idFolderPadre);
        for (BoxItem.Info itemInfo : file) {
            if (itemInfo.getName().equals(objetoBuscar[0]) || itemInfo.getName().equals(objetoBuscar[1])) {
                idFile = itemInfo.getID();
                break;
            }
        }
        //return exist;
        return idFile;
    }

    public String existeCarpetaPadre(String objetoBuscar, String idFolderPadre) {
        String idFolder = "";
        BoxSearch bs = new BoxSearch(api);
        BoxSearchParameters bsp = new BoxSearchParameters();
        List<String> ancestorFolderIds = new ArrayList<>();
        List<String> contentTypes = new ArrayList<>();
        bsp.clearParameters();
        contentTypes.add("folder");
        bsp.setContentTypes(contentTypes);
        ancestorFolderIds.add(idFolderPadre);
        bsp.setAncestorFolderIds(ancestorFolderIds);
        bsp.setQuery(objetoBuscar);
        PartialCollection<BoxItem.Info> searchResults;
        long offset = 0;
        long limit = 1000;
        long fullSizeOfResult = 0;
        while (offset <= fullSizeOfResult) {
            searchResults = bs.searchRange(offset, limit, bsp);
            fullSizeOfResult = searchResults.fullSize();
            for (BoxItem.Info result : searchResults) {
                BoxFolder.Info folderInfo = result.getParent();
                if (idFolderPadre.equals(folderInfo.getID())) {
                    if (folderInfo.getName().equals(objetoBuscar)) {
                        idFolder = result.getID();
                    }
                }
            }

            offset += limit;
        }

        return idFolder;
    }

    public String subirDocumento(String path, String nombreDocumento, String idFolder, DocumentoVO documentoVO) {
        String idDocumento = null;
        File file = new File(path);
        String nameFile = file.getName();
        String fileExtension = nameFile.substring(nameFile.lastIndexOf('.') + 1);
        BoxFolder rootFolder = new BoxFolder(api, idFolder);
        try (InputStream stream = new FileInputStream(path)) {
            BoxFile.Info info = rootFolder.uploadFile(stream, (nombreDocumento));
            BoxFile uploadedFile = info.getResource();
            idDocumento = uploadedFile.getID();
        } catch (BoxAPIException apiException) {
            /**
             * VALIDACION SI EXISTE UN DOCUMENTO CON EL MISMO NOMBRE, DE SER ASI
             * SE SETEAN LOS SIGUIENTES VALORES PARA LUEGO OCUPARLOS EN EL
             * OBJETO RESPUESTA
             */
            documentoVO.setTipoOpcion(10);
            documentoVO.setIdDocumento(apiException.getDetails().getConflictId());
        } catch (FileNotFoundException ex) {
        } catch (IOException ioe) {
        }
        return idDocumento;
    }

    public Boolean reemplazarComodin(String ruta, DocumentoVO documentoVO) {
        Boolean resultado = Boolean.TRUE;
        String comodinStr = "";
        String separador = "@";
        StringTokenizer token = new StringTokenizer(ruta, "/");
        while (token.hasMoreTokens()) {
            try {
                comodinStr = token.nextToken();
                if (comodinStr.contains(separador)) {
                    ruta = ruta.replace(comodinStr, documentoVO.getMetadato(comodinStr.replace(separador, "")));

                    documentoVO.setPath(ruta);
                }
                System.out.println("ruta = " + ruta);

                //} catch (NullPointerException exception) {
            } catch (Exception exception) {
                //System.out.println("exception = " + exception + " " + comodinStr);
                documentoVO.setTipoOpcion(6);
                documentoVO.setComodin(comodinStr);
                resultado = Boolean.FALSE;
                break;
            }
        }
        return resultado;
    }

    public Boolean crearRespuesta(boolean result, DocumentoVO documentoVO) {
        Respuesta mensaje = new Respuesta();
        String codOperacion = "", detOperacion = "";
        String resOperacion = result ? "OP_EXT" : "OP_ERR";

        switch (documentoVO.getTipoOpcion()) {
            case 1:     // CARGAR
                detOperacion = result ? RespuestaOperacion.MS_ALMEXT001.getDetalleOperacion() : RespuestaOperacion.MS_ALMERR001.getDetalleOperacion();
                codOperacion = result ? RespuestaOperacion.MS_ALMEXT001.toString() : RespuestaOperacion.MS_ALMERR001.toString();
                break;
            case 2:     // ACTUALIZAR
                detOperacion = result ? RespuestaOperacion.MS_ACTEXT001.getDetalleOperacion() : "";
                codOperacion = result ? RespuestaOperacion.MS_ACTEXT001.toString() : "";
                break;
            case 3:     // VALIDAR CHECKLIST
                detOperacion = result ? RespuestaOperacion.MS_VALIDCHKEXTWC001.getDetalleOperacion() : RespuestaOperacion.MS_VALIDCHKERR001.getDetalleOperacion();
                codOperacion = result ? RespuestaOperacion.MS_VALIDCHKEXTWC001.toString() : RespuestaOperacion.MS_VALIDCHKERR001.toString();
                break;
            case 4:     // CONSULTAR
                detOperacion = result ? RespuestaOperacion.MS_CONSEX001.getDetalleOperacion() : RespuestaOperacion.MS_CONSNOENCT001.getDetalleOperacion();
                codOperacion = result ? RespuestaOperacion.MS_CONSEX001.toString() : RespuestaOperacion.MS_CONSNOENCT001.toString();
                break;
            case 5:     // VALIDAR METADATOS
                detOperacion = result ? "" : RespuestaOperacion.MS_VALIDMTERR001.getDetalleOperacion();
                codOperacion = result ? "" : RespuestaOperacion.MS_VALIDMTERR001.toString();
                break;
            case 6:     // EXEPCION COMODIN
                detOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR001.getDetalleOperacion() + documentoVO.getComodin();
                codOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR001.toString();
                break;
            case 7:     // EXEPCION NOMBRE AREA
                detOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR002.getDetalleOperacion() + documentoVO.getAreaResponsable();
                codOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR002.toString();
                break;
            case 8:     // EXEPCION SUBTIPO-AREA
                detOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR003.getDetalleOperacion() + documentoVO.getSubTipoDocumental() + ", " + documentoVO.getAreaResponsable();
                codOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR003.toString();
                break;
            case 9:     // EXEPCION DOCUMENTO NO ESTA EN CARPETA PORTAL SALIDA
                detOperacion = result ? "" : documentoVO.getIdDocumento() + ", " + documentoVO.getNombreDocumento() + ", " + RespuestaOperacion.MS_EXEPCIONERR004.getDetalleOperacion();
                codOperacion = result ? "" : RespuestaOperacion.MS_EXEPCIONERR004.toString();
                break;
            case 10:     // EXEPCION DOCUMENTO MISMO NOMBRE EN CARPETA
                detOperacion = result ? "" : RespuestaOperacion.MS_ALMERR002.getDetalleOperacion();
                codOperacion = result ? "" : RespuestaOperacion.MS_ALMERR002.toString();
                break;
        }

        mensaje.setRespuestaOperacion(resOperacion);
        mensaje.setCodigoOperacion(codOperacion);
        mensaje.setDetalleOperacion(encodeCharSet(detOperacion));
        mensaje.setIdDocumento(documentoVO.getIdDocumento());
        mensaje.setSubtiposChecklist(documentoVO.getSubtiposChecklist());
        mensaje.setElementos(documentoVO.getElementos());

        documentoVO.setRespuesta(mensaje);
        return true;
    }

    public String obtenerContenidoSubtipoDocumental(String idSubtipoDocumental) {
        String contenido = null;
        BoxFile fileOutStream = new BoxFile(api, idSubtipoDocumental);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        fileOutStream.download(output);
        contenido = output.toString();
        return contenido;

    }

    public Boolean verificarMetadatos(DocumentoVO documentoVO) {
        Boolean resultado = Boolean.FALSE;
        /**
         * BUSCAR ARCHIVO JSON DEL SUBTIPO DOCUMENTAL
         */
        try {
            JsonObject jsonObject = null;
            JsonArray fields = null;
            Map<String, String> camposObligatorios = new HashMap<>();
            Map<String, String> allCamposObligatorios = new HashMap<>();
            /**
             * SE CREA UN ARRAY PARA CONOCER EL SUBTIPO DOCUMENTAL Y AGREGAR EL
             * TIPO DOCUMENTAL BASE "SINIESTRO" y "SUBSUBTIPO"
             */
            ArrayList<String> tiposDocumentales = new ArrayList<>();
            tiposDocumentales.add(documentoVO.getSubTipoDocumental());
            tiposDocumentales.add(Constantes.TIPO_SINIESTRO);  //SIEMPRE VA IR EN LOS METADATOS

            String strArea = documentoVO.getMetadato("area");

            ArrayList<JsonObject> jsonTiposDocumentales = new ArrayList<>();

            for (String subtipoDocumental : tiposDocumentales) {
                /**
                 * BUSCAR EL ID DEL SUBTIPO DOCUMENTAL EN LA CARPETA
                 * "CONFIGURACION" EN BOX PARA CONOCER SUS ATRIBUTOS
                 * OBLIGATORIOS Y VERIFICARLOS POSTERIORMENTE SI SE ENCUENTRA SE
                 * OBTIENE SU CONTENIDO
                 */
                if (buscarArchivo(documentoVO, subtipoDocumental)) {
                    if (leerContenidoJson(documentoVO)) {
                        /**
                         * CONOCER Y CONTAR LOS CAMPOS QUE SON REQUERIDOS
                         * "TRUE", OPCIONALES "FALSE"
                         */
                        jsonObject = JsonObject.readFrom(documentoVO.getContenidoJson());
                        fields = (JsonArray) jsonObject.get("fields");
                        jsonTiposDocumentales.add(jsonObject);
                        System.out.format("Metadatos: %s \n", documentoVO.getMetadatos());
                        System.out.format("idDocumento: %s, nombreDOcumento: %s \n", documentoVO.getIdDocumento(), documentoVO.getNombreDocumento());

                        camposObligatorios = verificarCamposRequeridos(fields.iterator(), documentoVO);
                    } else {
                        break;
                    }
                } else {
                    documentoVO.setAreaResponsable(strArea);
                    documentoVO.setTipoOpcion(documentoVO.getTipoOpcion() == 0 ? 8 : documentoVO.getTipoOpcion());
                    break;
                }
                /**
                 * SE CREA UN HASHMAP CON TODOS LOS ELEMENTOS DE TODOS LOS
                 * SUBTIPOS DOCUMENTALES
                 */
                for (Map.Entry<String, String> entry : camposObligatorios.entrySet()) {
                    allCamposObligatorios.put(entry.getKey(), entry.getValue());
                }
            }

            /**
             * SI NO EXISTEN CAMPOS OBLIGATORIOS VACIOS SE CREA EL METADATA
             * TEMPLATE (TIPO DOCUMENTAL)
             */
            if (allCamposObligatorios.isEmpty()) {
                resultado = verificarRuta(documentoVO, jsonTiposDocumentales);
                //documentoVO.setTipoOpcion(documentoVO.getIdError() != 0 ? 2 : 1);
                documentoVO.setTipoOpcion(documentoVO.getTipoOpcion() == 0 ? 1 : documentoVO.getTipoOpcion());
            } else {
                resultado = false;
                documentoVO.setTipoOpcion(5);
                documentoVO.setElementos(allCamposObligatorios);
            }
        } catch (Exception e) {
            //System.out.println("ex verificarMetadatos " + e);
            System.out.println("Problema con contenido json del subtipo documental");
        }

        return resultado;
    }

    private Map<String, String> verificarCamposRequeridos(Iterator<JsonValue> values, DocumentoVO documentoVO) {
        Map<String, String> elementos = new HashMap<>();

        while (values.hasNext()) {
            JsonValue obj = values.next();
            JsonObject jsonObjField = obj.asObject();
            //String type = jsonObjField.get("type").asString();
            String fieldKey = jsonObjField.get("fieldKey").asString();
            boolean required = jsonObjField.get("required").asBoolean();
            String empty = "";
            String value = (documentoVO.getMetadato(fieldKey) == null ? "" : documentoVO.getMetadato(fieldKey).trim());
            /**
             * VALIDAR LOS CAMPOS QUE SON REQUERIDOS
             */
            if (required && value.equals(empty)) {
                elementos.put(fieldKey, String.valueOf(required));
                System.out.format("Atributo : [ %s ], Valor: [ %s ], Requerido [ %s ] \n " + fieldKey, value, required);
            }
        }

        return elementos;
    }

    public Boolean verificarRuta(DocumentoVO documentoVO, ArrayList<JsonObject> jsonTiposDocumentales) {
        Boolean result = Boolean.FALSE;
        /**
         * SI NO EXISTEN CAMPOS OBLIGATORIOS VACIOS SE CREA EL METADATA TEMPLATE
         * (TIPO DOCUMENTAL)
         */
        try {
            BoxFile documento = null;
            String idDocumento = null;
            if (getIdFolder(documentoVO, jsonTiposDocumentales)) {

                //String idCarpetaSubir = getIdFolder(documentoVO, jsonTiposDocumentales);
                /**
                 * VALIDAR SI SE SUBE UN DOCUMENTO NUEVO A LA RUTA ESPECIFICADA
                 * O MOVER UN DOCUMENTO A LA RUTA ESPECIFICADA
                 */
                idDocumento = subirDocumento(documentoVO.getInputStream().toString(), documentoVO.getNombreDocumento(), documentoVO.getIdFolder(), documentoVO);
                documento = new BoxFile(api, idDocumento);
                /**
                 * SE SETEA EL VALOR DE IDDOCUMENTO, PARA OCUPARSE EN EL OBJETO
                 * DE TIPO RESPUESTA
                 */
                //documentoVO.setIdDocumento(documentoVO.getIdError() != 0 ? documentoVO.getIdDocumento() : idDocumento);
                documentoVO.setIdDocumento(documentoVO.getTipoOpcion() != 0 ? documentoVO.getIdDocumento() : idDocumento);

                if (documentoVO.getTipoOpcion() == 0) {
                    /**
                     * RECORRER LOS DIFERENTES TIPOS DOCUMENTALES, OBTENER LOS
                     * METADATOS QUE ENVIAN Y ASIGNARSELOS AL DOCUMENTO
                     */
                    for (JsonObject jsonObject : jsonTiposDocumentales) {
                        JsonArray fields = (JsonArray) jsonObject.get("fields");
                        String templateKey = jsonObject.get("templateKey").asString();
                        result = asignarMetadatos(fields.iterator(), documentoVO, documento, templateKey);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ex verificarRuta: " + e);
        }

        return result;
    }

    private Boolean getIdFolder(DocumentoVO documentoVo, ArrayList<JsonObject> jsonTiposDocumentales) {
        Boolean resultado = Boolean.FALSE;
        try {
            String idFolder = "";
            int i = 1;
            /**
             * SE RECORRE jsonTiposDocumentales, Y DEL PRIMER ELEMENTO DE ESTE
             * ARRAY SE OBTIENE LA "RUTA" EN DONDE SE VA A SUBIR EL ARCHIVO DE
             * TIPODOCUMENTAL
             *
             */
            for (JsonObject jsonObject : jsonTiposDocumentales) {
                if (i == 1) {
                    String idFolderSiniestros = Constantes.ID_FOLDER_SINIESTRO;
                    String ruta = encodeCharSet(jsonObject.get("ruta").asString());
                    //String subtipo = jsonObject.get("subtipo").asString();
                    String subtipo = documentoVo.getSubTipoDocumental();
                    String checklistRelacionado = jsonObject.get("checklistrelacionado").asString();

                    /**
                     * SE CREA UN HASHMAP CON LOS NUEVOS METADATOS A UTILIZAR
                     * PARA EL TIPODCOUMENTAL SINIESTRO, ESTOS VALORES SE
                     * OBTIENEN DEL ARCHIVO DEL SUBTIPO DOCUMENTAL
                     */
                    Map<String, String> metadatosSubTipo = new HashMap<>();
                    metadatosSubTipo.put("subtipo", subtipo);
                    metadatosSubTipo.put("checklistrelacionado", checklistRelacionado);
                    /**
                     * SE RECORRE EL HASHMAP PARA AGREGARLO AL OBJETO DE TIPO
                     * DOCUMENTOVO
                     */
                    for (Map.Entry<String, String> entry : metadatosSubTipo.entrySet()) {
                        documentoVo.setMetadato(entry.getKey(), entry.getValue());
                    }
                    if (reemplazarComodin(ruta, documentoVo)) {

                        //ruta = reemplazarComodin(ruta, documentoVo);
                        /**
                         * SE SETEA EL VALOR DE PATH, PARA OCUPARSE EN EL OBJETO
                         * DE TIPO RESPUESTA
                         */
                        //documentoVo.setPath(ruta);
                        /**
                         * BUSCAR SI SE ENCUENTRA LA CARPETA DEL IDSINIESTRO Y
                         * DEMAS CARPETAS DE LA RUTA
                         */
                        List array = null;
                        array = new ArrayList<>(Arrays.asList(documentoVo.getPath().split("/")));

                        documentoVo.setIdFolder(buscarCarpetasRuta(idFolderSiniestros, array));
                        resultado = Boolean.TRUE;
                    } else {
                        break;
                    }
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println("ex getIdFolder " + e);
        }

        //result = idFolder;
        return resultado;
    }

    private boolean asignarMetadatos(Iterator<JsonValue> values, DocumentoVO documentoVO, BoxFile documento, String templateKey) {
        boolean respuesta;
        MetadataTemplate metadataTipoDocumental = new MetadataTemplate(templateKey, api);
        while (values.hasNext()) {
            JsonValue obj = values.next();
            JsonObject jsonObjField = obj.asObject();
            String fieldKey = jsonObjField.get("keyZbox") == null ? jsonObjField.get("fieldKey").asString() : jsonObjField.get("keyZbox").asString();
            //String value = (documentoVO.getMetadato(fieldKey) == null ? "" : documentoVO.getMetadato(fieldKey).trim());
            String value = (documentoVO.getMetadato(jsonObjField.get("fieldKey").asString()) == null ? ""
                    : documentoVO.getMetadato(jsonObjField.get("fieldKey").asString()).trim());
            metadataTipoDocumental.add(fieldKey, value);
        }
        /**
         * SE TIENE QUE ELIMINAR DEL METADATA LOS CAMPOS CON VALOR VACIO,
         * ESPECIALMENTE LOS DE TIPO DATE YA QUE SI EXISTE ALGUNO DE ESTE TIPO
         * VACIO, NO SE LLEVA A CABO LA ASIGNACION DEL TEMPLATE.
         */
        JsonObject jsonObject = JsonObject.readFrom(metadataTipoDocumental.toString());
        for (String name : jsonObject.names()) {
            JsonValue value = jsonObject.get(name);
            if (value.asString().equals("")) {
                metadataTipoDocumental.remove(name);
            }
        }

        /**
         * SE LE ASIGNA EL METADATA TEMPLATE (TIPO DOCUMENTAL) AL DOCUMENTO
         */
        respuesta = documento.createMetadaTemplate(templateKey, metadataTipoDocumental);
        return respuesta;
    }

    private String encodeCharSet(String palabra) {
        //System.out.println("file.encoding=" + System.getProperty("file.encoding"));   //OTRA OPCION
        Charset defaultCharset = Charset.defaultCharset();
        String stringEncode = defaultCharset.contains(StandardCharsets.UTF_8) ? palabra
                : new String(palabra.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return stringEncode;
    }

    public boolean buscarArchivo(DocumentoVO documentoVO, String subTipoDocumental) {
        /**
         * BUSCAR ARCHIVO JSON DEL SUBTIPO DOCUMENTAL
         */
        Boolean resp = false;
        String str = "", strPath = "";
        StringBuilder buf = new StringBuilder();

        String strArea = documentoVO.getMetadato("area");
        strArea = strArea.replaceAll("\\s", "");

        if (!subTipoDocumental.equals(Constantes.TIPO_SINIESTRO)) {
            String codigoTipoSiniestro = documentoVO.getMetadato("tipoSiniestro") == null ? "" : documentoVO.getMetadato("tipoSiniestro");
            documentoVO.setMetadato("tipoSiniestro", codigoTipoSiniestro);
            strPath = "subtipos/" + strArea + "/" + subTipoDocumental + ".txt";
            InputStream is = Constantes.class.getResourceAsStream(strPath);
            documentoVO.setPath(strPath);
            resp = true;
        } else {
            strPath = "subtipos/" + Constantes.TIPO_SINIESTRO + ".txt";
            InputStream is = Constantes.class.getResourceAsStream(strPath);
            documentoVO.setPath(strPath);
            documentoVO.setMetadato("area", strArea);
            resp = true;
        }

        return resp;

    }

    public boolean leerContenidoJson(DocumentoVO documentoVO) {

        String str = "", strContenidoJSON = "";
        StringBuilder buf = new StringBuilder();
        InputStream is = Constantes.class.getResourceAsStream(documentoVO.getPath());

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
            }
            documentoVO.setContenidoJson(buf.toString());
            return true;
        } catch (Exception e) {
            System.out.println("ex leerContenidoJson " + e);
        } finally {
            try {
                is.close();
            } catch (Throwable ignore) {
            }
        }

        return false;
    }

    public String leerContenidoChecklistJson(String nombreChecklist) {
        System.out.println("entra a consyultar = ");
        String str = "", strContenidoJSON = "";
        StringBuilder buf = new StringBuilder();
        InputStream is = Constantes.class.getResourceAsStream("checklist/" + nombreChecklist + ".txt");
        System.out.println("is = " + is);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
            }
        } catch (Exception e) {
            System.out.println("ex leerContenidoJson " + e);
        } finally {
            try {
                is.close();
            } catch (Throwable ignore) {
            }
        }
        strContenidoJSON = buf.toString();

        return strContenidoJSON;
    }

}
