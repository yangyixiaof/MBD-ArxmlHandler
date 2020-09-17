package ar.intf.cs;

import ar.ArElement;
import ar.type.ArDataType;

public class ArCsArgument extends ArElement {
	
	String direct = null;
	ArDataType adt = null;
	
	public ArCsArgument(String name, String direct) {
		super(name);
		this.direct = direct;
	}
	
	public String GetDirection() {
		return direct;
	}
	
	public void SetDataType(ArDataType adt) {
		this.adt = adt;
	}
	
	public ArDataType GetDataType() {
		return adt;
	}
	
}
