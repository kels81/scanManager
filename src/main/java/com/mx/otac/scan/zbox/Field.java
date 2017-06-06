package com.mx.otac.scan.zbox;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Field {

	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("keyZbox")
	@Expose
	private String keyZbox;
	@SerializedName("fieldKey")
	@Expose
	private String fieldKey;
	@SerializedName("required")
	@Expose
	private Boolean required;
	
	/**
	* No args constructor for use in serialization
	* 
	*/
	public Field() {
	}

	/**
	* 
	* @param required
	* @param type
	* @param keyZbox
	* @param fieldKey
	*/
	public Field(String type, String keyZbox, String fieldKey, Boolean required) {
	super();
	this.type = type;
	this.keyZbox = keyZbox;
	this.fieldKey = fieldKey;
	this.required = required;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getKeyZbox() {
		return keyZbox;
	}
	
	public void setKeyZbox(String keyZbox) {
		this.keyZbox = keyZbox;
	}
	
	public String getFieldKey() {
		return fieldKey;
	}
	
	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}
	
	public Boolean getRequired() {
		return required;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
	}

}

/*
public class Field implements Serializable, Cloneable {
	private String type = "";
	private String keyZbox = "";
	private String fieldKey = "";
	private boolean required = false;
	public Field (String type, String keyZbox, String fieldKey, boolean required ){
		this.type = type;
		this.keyZbox = keyZbox;
		this.fieldKey = fieldKey;
		this.required= required;
	}
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getKeyZbox() { return keyZbox; }
	public void setKeyZbox(String keyZbox) { this.keyZbox = keyZbox; }
	public String getFieldKey() { return fieldKey; }
	public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
	public boolean getRequired() { return required; }
	public void setRequired(boolean required) { this.required= required; }	
}
*/