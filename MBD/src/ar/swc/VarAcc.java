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
		StringBuffer res = new StringBuffer();
		RunEnt re = (RunEnt) GetParent();
		SwCompo swc = (SwCompo) re.GetParent().GetParent();
		for (ArDataElement data_ele : data_eles) {
			String SrcActorName = is_read ? swc.GetName() + "_head" : GetName();
			String SrcOutportName = is_read ? relative_port_name : data_ele.GetName();
			String DstActorName = is_read ? GetName() : swc.GetName() + "_tail";
			String DstInportName = is_read ? data_ele.GetName() : relative_port_name;
			res.append("AddRelation(\"" + swc.GetGeneratedPath() + "\",\"" + SrcActorName + "\",\"" + SrcOutportName + "\",\"" + DstActorName + "\",\"" + DstInportName + "\");");
		}
		return res.toString();
	}
	
	public String ToRunnablePartPorts() {
		StringBuffer sb = new StringBuffer();
		for (ArDataElement data_ele : data_eles) {
			sb.append("[\"" + data_ele.GetName() + "\",\"" + data_ele.GetDataType().ToScript() + "\"," + "\"0\"],");
		}
		if (sb.charAt(sb.length()-1) == ',') {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	@Override
	public Object ArClone() {
		VarAcc va = new VarAcc(name, is_read, relative_port_name);
		va.data_eles.addAll(data_eles);
		return va;
	}
	
}
