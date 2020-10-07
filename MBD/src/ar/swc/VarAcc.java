package ar.swc;

import java.util.ArrayList;

import ar.ArElement;
import ar.intf.ArDataElement;

public class VarAcc extends ArElement {
	
	boolean is_read = false;
//	SRPort port = null;
	String relative_port_name = null;
	ArrayList<ArDataElement> data_eles = new ArrayList<ArDataElement>();
	
	public VarAcc(String name, boolean is_read, String relative_port_name) {
		super(name);
		this.is_read = is_read;
//		this.port = pp;
		this.relative_port_name = relative_port_name;
//		this.data_ele = vp;
	}
	
	public void AddArDataElement(ArDataElement vp) {
		this.data_eles.add(vp);
	}
	
	public boolean IsRead() {
		return is_read;
	}
	
	public String GetRelativePortName() {
		return relative_port_name;
	}
	
	public ArrayList<ArDataElement> GetAllArDataElements() {
		return data_eles;
	}
	
	@Override
	public String ToScript() {
		// TODO
		RunEnt re = (RunEnt) GetParent();
		SwCompo swc = (SwCompo) re.GetParent();
		
		return null;
	}

	@Override
	public Object ArClone() {
		VarAcc va = new VarAcc(name, is_read, relative_port_name);
		va.data_eles.addAll(data_eles);
		return va;
	}
	
}
