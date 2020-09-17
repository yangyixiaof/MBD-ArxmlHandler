package ar.type;

import java.util.ArrayList;

import ar.ArElement;

public class ArDataType extends ArElement {
	
//	ArrayList<String> data_names = new ArrayList<String>();
//	ArrayList<Object> data_types = new ArrayList<Object>();
	
	ArrayList<ArBaseDataType> data_types = new ArrayList<ArBaseDataType>();
	
	public ArDataType(String name) {
		super(name);
	}
	
	public void AddBaseDataType(ArBaseDataType bdt) {
		data_types.add(bdt);
	}
	
//	public void AddDataElement(String name, Object type) {
//		data_names.add(name);
//		data_types.add(type);
//	}
//	
//	public ArrayList<String> GetDataNames() {
//		return data_names;
//	}
//	
//	public ArrayList<Object> GetDataTypes() {
//		return data_types;
//	}
	
}
