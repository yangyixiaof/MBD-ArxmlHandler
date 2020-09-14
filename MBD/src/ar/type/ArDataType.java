package ar.type;

import java.util.ArrayList;

import ar.ArElement;

public class ArDataType extends ArElement {
	
	ArrayList<String> data_names = new ArrayList<String>();
	ArrayList<Object> data_types = new ArrayList<Object>();
	
	public ArDataType(String name) {
		super(name);
	}
	
	public void AddDataElement(String name, Object type) {
		data_names.add(name);
		data_types.add(type);
	}
	
	public ArrayList<String> GetDataNames() {
		return data_names;
	}
	
	public ArrayList<Object> GetDataTypes() {
		return data_types;
	}
	
}
