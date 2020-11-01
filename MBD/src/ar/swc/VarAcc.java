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
		RunEnt re = (RunEnt) GetParent();
		SwCompo swc = (SwCompo) re.GetParent().GetParent();
		StringBuffer res = new StringBuffer();
		// Generate code for DeStruct/NewStruct. 
		
		StringBuffer swc_port_cnt = new StringBuffer();
		StringBuffer runnable_port_cnt = new StringBuffer();
		
		SRPort port = swc.SearchForSRPortBasedOnPortName(relative_port_name);
		swc_port_cnt.append("[");
		swc_port_cnt.append(port.ToParameterDeclaration());
		swc_port_cnt.append("]");
		
		runnable_port_cnt.append("[");
		runnable_port_cnt.append(ToRunnablePartPorts());
		runnable_port_cnt.append("]");

		String dns = null;
		StringBuffer in_buffer = null;
		StringBuffer out_buffer = null;
		if (is_read) {
			dns = "DeStruct";
			in_buffer = swc_port_cnt;
			out_buffer = runnable_port_cnt;
		} else {
			dns = "NewStruct";
			in_buffer = runnable_port_cnt;
			out_buffer = swc_port_cnt;
		}
		
		res.append("AddActor(\"" + swc.GetGeneratedPath() + "\",\"" + dns + "\",\"" + GetName() + "\",");
		res.append(in_buffer.toString());
		res.append(",");
		res.append(out_buffer.toString());
		res.append(",\"runnable\");");
		// Generate code for relation. 
		for (ArDataElement data_ele : data_eles) {
			/**
			 * relation from swc to this actor
			 */
			{
				String SrcActorName = is_read ? swc.GetName() + "" : GetName();
				String SrcOutportName = is_read ? relative_port_name : data_ele.GetPortName(this);
				String DstActorName = is_read ? GetName() : swc.GetName() + "_ret";
				String DstInportName = is_read ? data_ele.GetPortName(this) : relative_port_name;
				res.append("AddRelation(\"" + swc.GetGeneratedPath() + "\",\"" + SrcActorName + "\",\"" + SrcOutportName + "\",\"" + DstActorName + "\",\"" + DstInportName + "\");");
			}
			/**
			 * relation from this actor to runnable
			 */
			{
				String SrcActorName = is_read ? GetName() : re.GetName() + "";
				String SrcOutportName = is_read ? data_ele.GetPortName(this) : data_ele.GetPortName(this);
				String DstActorName = is_read ? re.GetName() + "" : GetName();
				String DstInportName = is_read ? data_ele.GetPortName(this) : data_ele.GetPortName(this);
				res.append("AddRelation(\"" + swc.GetGeneratedPath() + "\",\"" + SrcActorName + "\",\"" + SrcOutportName + "\",\"" + DstActorName + "\",\"" + DstInportName + "\");");
			}
		}
		return res.toString();
	}
	
	public String ToRunnablePartPorts() {
		StringBuffer sb = new StringBuffer();
		for (ArDataElement data_ele : data_eles) {
			sb.append("[\"" + data_ele.GetPortName(this) + "\" \"" + data_ele.GetDataType().ToScript() + "\" " + "\"0\"]#");
		}
		if (data_eles.size() > 0) {
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
