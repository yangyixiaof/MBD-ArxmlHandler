package ar.swc;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;

public class SwCompo extends ArElement {
	
	boolean is_proto = false;
	
	boolean is_referred = false;
	
	ArrayList<CSPort> cs_ports = new ArrayList<CSPort>();
	ArrayList<SRPort> ports = new ArrayList<SRPort>();
	ArrayList<SwCompo> swcs = new ArrayList<SwCompo>();
	ArrayList<SwcBehaviour> swc_bs = new ArrayList<SwcBehaviour>();
	
	public SwCompo(String name) {
		super(name);
	}
	
	public void SetProto(boolean is_proto) {
		this.is_proto = is_proto;
	}
	
	public boolean IsProto() {
		return is_proto;
	}
	
	public void SetReferred(boolean is_referred) {
		this.is_referred = is_referred;
	}
	
	public boolean IsReferred() {
		return is_referred;
	}
	
	public void AddCSPort(CSPort p) {
		cs_ports.add(p);
	}
	
	public void AddSRPort(SRPort p) {
		ports.add(p);
	}
	
	public void AddSwCompo(SwCompo swc) {
		Assert.isTrue(swc != null);
		Assert.isTrue(swc != this);
		swcs.add(swc);
	}
	
	public void AddSwcBehaviour(SwcBehaviour swc_b) {
		swc_bs.add(swc_b);
	}
	
	public SRPort SearchForSRPortBasedOnPortName(String relative_port_name) {
		for (SRPort port : ports) {
			if (port.GetName().equals(relative_port_name)) {
				return port;
			}
		}
		Assert.isTrue(false, "null sr port!");
		return null;
	}
	
//	public ArrayList<SRPort> GetAllPorts() {
//		return ports;
//	}
	
	public String ToScript() {
		ArrayList<SRPort> inputs = new ArrayList<SRPort>();
		ArrayList<SRPort> outputs = new ArrayList<SRPort>();
		
		for (SRPort p : ports) {
			if (p.IsInput()) {
				inputs.add(p);
			} else {
				outputs.add(p);
			}
		}
		
		StringBuilder in_cnt = new StringBuilder("");
		StringBuilder out_cnt = new StringBuilder("");
		
		in_cnt.append("[");
		for (SRPort in : inputs) {
			in_cnt.append(in.ToParameterDeclaration());
			in_cnt.append(",");
		}
		if (inputs.size() > 0) {
			in_cnt.deleteCharAt(in_cnt.length()-1);
		}
		in_cnt.append("]");
		
		out_cnt.append("[");
		for (SRPort out : outputs) {
			out_cnt.append(out.ToParameterDeclaration());
			out_cnt.append(",");
		}
		if (outputs.size() > 0) {
			out_cnt.deleteCharAt(out_cnt.length()-1);
		}
		out_cnt.append("]");
		
		StringBuilder res = new StringBuilder("");
		String full_path = GetGeneratedPath();
		Assert.isTrue(full_path != null);
		String comment = swcs.size() > 0 ? "comp_swc" : "swc";
		res.append("AddModelPage(\"" + full_path + "\",\"ProgramModelPage\",\"" + comment + "\");");
		res.append("AddFunction(\"" + full_path + "\",\"" + GetName() + "\"," + in_cnt.toString() + ");");
		res.append("AddReturnValue(\"" + full_path + "\",\"" + GetName() + "\"," + out_cnt.toString() + ");");
		
//		for (CSPort p : cs_ports) {
//			res.append(p.ToScript());
//		}
		
		for (SwcBehaviour swcb : swc_bs) {
			res.append(swcb.ToScript());
		}
		
		for (SwCompo swc : swcs) {
			res.append(swc.ToScript());
		}
		
		return res.toString();
	}

	@Override
	public Object ArClone() {
		SwCompo sc = new SwCompo(name);
		sc.SetProto(is_proto);
		sc.SetReferred(is_referred);
		for (CSPort p : cs_ports) {
			CSPort c_ele = (CSPort) p.ArClone();
			sc.AddCSPort(c_ele);
			sc.AddChildElement(c_ele);
		}
		for (SRPort p : ports) {
			SRPort c_ele = (SRPort) p.ArClone();
			sc.AddSRPort(c_ele);
			sc.AddChildElement(c_ele);
		}
		for (SwCompo o_sc : swcs) {
			SwCompo c_ele = (SwCompo) o_sc.ArClone();
			sc.AddSwCompo(c_ele);
			sc.AddChildElement(c_ele);
		}
		for (SwcBehaviour o_swc_b : swc_bs) {
			SwcBehaviour c_ele = (SwcBehaviour) o_swc_b.ArClone();
			sc.AddSwcBehaviour(c_ele);
			sc.AddChildElement(c_ele);
		}
		return sc;
	}
	
	public SRPort FindPort(String name) {
		Assert.isTrue(name != null);
		for (SRPort p : ports) {
			if (p.GetName().equals(name)) {
				return p;
			}
		}
		Assert.isTrue(false, "unfound port:" + name + "#swc is proto:" + is_proto);
		return null;
	}

	public String ToRelationScript() {
		String res = "";
		for (SRPort p : ports) {
			res += p.ToRelationScript();
		}
		for (SwCompo swc : swcs) {
			res += swc.ToRelationScript();
		}
		return res;
	}
	
	public void AddSwComponentProto(SwCompo copied_ar_ref_swc) {
		Assert.isTrue(copied_ar_ref_swc != null);
		Assert.isTrue(copied_ar_ref_swc != this);
		copied_ar_ref_swc.SetProto(true);
		swcs.add(copied_ar_ref_swc);
		this.AddChildElement(copied_ar_ref_swc);
	}

//	public void CardChildsAccordingToTypes() {
//		for (ArElement ele : eles) {
//			if (ele instanceof CSPort) {
//				cs_ports.add((CSPort) ele);
//			}
//			if (ele instanceof SRPort) {
//				ports.add((SRPort) ele);
//			}
//			if (ele instanceof SwcBehaviour) {
//				swc_bs.add((SwcBehaviour) ele);
//			}
//			if (ele instanceof SwCompo) {
//				swcs.add((SwCompo) ele);
//			}
//		}
//	}
	
}
