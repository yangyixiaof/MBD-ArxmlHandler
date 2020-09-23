package ar.intf;

import ar.ArElement;
import ar.type.ArDataType;

public class ArDataElement extends ArElement {
	
	ArDataType type = null;
	
	public ArDataElement(String name) {
		super(name);
	}
	
	public void SetDataType(ArDataType type) {
		this.type = type;
	}
	
	public ArDataType GetDataType() {
		return type;
	}
	
}
