package ar.intf;

import ar.ArElement;
import ar.swc.VarAcc;
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
	
	@Override
	public String ToScript() {
		return type.ToScript();
	}
	
	@Override
	public String GetName() {
		return super.GetName();
	}
	
	public String GetPortName(VarAcc va) {
		return va.GetRelativePortName() + "_" + super.GetName();
	}
	
}
