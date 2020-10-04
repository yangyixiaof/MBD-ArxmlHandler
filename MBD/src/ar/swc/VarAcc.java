package ar.swc;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.ArDataElement;

public class VarAcc extends ArElement {
	
	boolean is_read = false;
//	SRPort port = null;
	String relative_port_name = null;
	ArDataElement data_ele = null;
	
	public VarAcc(String name, boolean is_read, String relative_port_name, ArDataElement vp) {
		super(name);
		this.is_read = is_read;
//		this.port = pp;
		this.relative_port_name = relative_port_name;
		this.data_ele = vp;
	}
	
	public boolean IsRead() {
		return is_read;
	}
	
	public String GetRelativePortName() {
		return relative_port_name;
	}
	
	public ArDataElement GetArDataElement() {
		return data_ele;
	}
	
	@Override
	public String ToScript() {
		Assert.isTrue(false, "Not implemented yet!");
		return null;
	}

	@Override
	public Object ArClone() {
		return new VarAcc(name, is_read, relative_port_name, data_ele);
	}
	
}
