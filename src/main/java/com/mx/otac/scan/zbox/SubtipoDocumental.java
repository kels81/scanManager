package com.mx.otac.scan.zbox;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubtipoDocumental {

	@SerializedName("tipoDocumental")
	@Expose
	private String tipoDocumental;
	@SerializedName("templateKey")
	@Expose
	private String templateKey;
	@SerializedName("subTipoDocumental")
	@Expose
	private String subTipoDocumental;
	@SerializedName("ruta")
	@Expose
	private String ruta;
	@SerializedName("checklistrelacionado")
	@Expose
	private String checklistrelacionado;
	@SerializedName("fields")
	@Expose
	private List<Field> fields = null;
	
	/**
	* No args constructor for use in serialization
	* 
	*/
	public SubtipoDocumental() {
	}

	/**
	* 
	* @param templateKey
	* @param checklistrelacionado
	* @param ruta
	* @param subTipoDocumental
	* @param tipoDocumental
	* @param fields
	*/
	public SubtipoDocumental(String tipoDocumental, String templateKey, String subTipoDocumental, String ruta, String checklistrelacionado, List<Field> fields) {
		super();
		this.tipoDocumental = tipoDocumental;
		this.templateKey = templateKey;
		this.subTipoDocumental = subTipoDocumental;
		this.ruta = ruta;
		this.checklistrelacionado = checklistrelacionado;
		this.fields = fields;
	}
	
	public String getTipoDocumental() {
		return tipoDocumental;
	}
	
	public void setTipoDocumental(String tipoDocumental) {
		this.tipoDocumental = tipoDocumental;
	}
	
	public String getTemplateKey() {
		return templateKey;
	}
	
	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}
	
	public String getSubTipoDocumental() {
		return subTipoDocumental;
	}
	
	public void setSubTipoDocumental(String subTipoDocumental) {
		this.subTipoDocumental = subTipoDocumental;
	}
	
	public String getRuta() {
		return ruta;
	}
	
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	
	public String getChecklistrelacionado() {
		return checklistrelacionado;
	}
	
	public void setChecklistrelacionado(String checklistrelacionado) {
		this.checklistrelacionado = checklistrelacionado;
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public Field getField(int i){
		return this.fields.get(i);
	}
	
	public void setField(Field field) {
		this.fields.add(field);
	}
	
	public void setField(Field field, int i) {
		this.fields.set(i, field);
	}
	
	public void delField(int i) {
		this.fields.remove(i);
	}
	
	public void addField(Field field) {
		this.fields.add(field);
	}
}






/*



import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SubtipoDocumental {
	
	String tipoDocumental;
	String templateKey;
	String subTipoDocumental;
	String ruta;
	String checklistrelacionado;
	ArrayList<Field> fields;
	
	public SubtipoDocumental( JSONObject jsonObj ){
		super();
		this.tipoDocumental = (String) jsonObj.get("tipoDocumental");
		this.templateKey = (String) jsonObj.get("templateKey");
		this.subTipoDocumental = (String) jsonObj.get("subTipoDocumental");
		this.ruta = (String) jsonObj.get("ruta");
		this.checklistrelacionado = (String) jsonObj.get("checklistrelacionado");
		this.fields = new ArrayList<>();
		JSONArray jsonArray = (JSONArray) jsonObj.get("fields");
		setFields( jsonArray );
	}
	
	public void setFields( JSONArray jsonArray ) {
        Iterator<Map> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
        	Map<String, Object> obj = iterator.next();
        	Field field = new Field(obj.get("type").toString(), obj.get("keyZbox").toString(), obj.get("fieldKey").toString(), (boolean) obj.get("required"));
			this.fields.add(field);
        }
	}
	
	public SubtipoDocumental(String tipoDocumental, String templateKey, String subTipoDocumental, String ruta,
			String checklistrelacionado, ArrayList<Field> fields) {
		super();
		this.tipoDocumental = tipoDocumental;
		this.templateKey = templateKey;
		this.subTipoDocumental = subTipoDocumental;
		this.ruta = ruta;
		this.checklistrelacionado = checklistrelacionado;
		this.fields = fields;
	}
	public String getTipoDocumental() {
		return tipoDocumental;
	}
	public void setTipoDocumental(String tipoDocumental) {
		this.tipoDocumental = tipoDocumental;
	}
	public String getTemplateKey() {
		return templateKey;
	}
	public void setTemplateKey(String templateKey) {
		this.templateKey = templateKey;
	}
	public String getSubTipoDocumental() {
		return subTipoDocumental;
	}
	public void setSubTipoDocumental(String subTipoDocumental) {
		this.subTipoDocumental = subTipoDocumental;
	}
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String getChecklistrelacionado() {
		return checklistrelacionado;
	}
	public void setChecklistrelacionado(String checklistrelacionado) {
		this.checklistrelacionado = checklistrelacionado;
	}
	public ArrayList<Field> getFields() {
		return fields;
	}
	
	
}

*/