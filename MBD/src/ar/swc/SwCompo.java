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
	ArrayList<RunEnt> runs = new ArrayList<RunEnt>();
	
	ArrayList<SwCompo> dependencies = new ArrayList<SwCompo>();
	
	public SwCompo(String name) {
		super(name);
	}
	
	public void SetProto() {
		this.is_proto = true;
	}
	
	public boolean IsProto() {
		return is_proto;
	}
	
	public void SetReferred() {
		this.is_referred = true;
	}
	
	public boolean IsReferred() {
		return is_referred;
	}
	
//	public void AddPort(SRPort p) {
//		ports.add(p);
//	}
//	
//	public void AddRunEnt(RunEnt t) {
//		run_ents.add(t);
//	}
	
//	public void AddSwCompoInst(SwCompoInst swc) {
//		swcs.add(swc);
//	}
//	
//	public ArrayList<SwCompoInst> GetAllSwCompoInsts() {
//		return swcs;
//	}
	
	public void AddDependency(SwCompo depend) {
		dependencies.add(depend);
	}
	
	public boolean SatisfyDependencies(ArrayList<SwCompo> result) {
		for (SwCompo dep : dependencies) {
			if (!result.contains(dep)) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<SRPort> GetAllPorts() {
		return ports;
	}
	
//	public ArrayList<RunEnt> GetRunEnts() {
//		return run_ents;
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
			in_cnt.append("[" + "\"" + in.GetName() + "\"" + "," + "\"" + in.GetInterfaceDataElement().ToScript() + "\"," + "\"0\"" + "],");
		}
		if (inputs.size() > 0) {
			in_cnt.deleteCharAt(in_cnt.length()-1);
		}
		in_cnt.append("]");
		
		out_cnt.append("[");
		for (SRPort out : outputs) {
			out_cnt.append("[" + "\"" + out.GetName() + "\"" + "," + "\"" + out.GetInterfaceDataElement().ToScript() + "\"," + "\"0\"" + "],");
		}
		if (outputs.size() > 0) {
			out_cnt.deleteCharAt(out_cnt.length()-1);
		}
		out_cnt.append("]");
		
		StringBuilder res = new StringBuilder("");
		
		res.append("AddModelPage(\"" + GetGeneratedPath() + "\",\"ProgramModelPage\");");
		res.append("AddFunction(\"" + GetGeneratedPath() + "\",\"" + GetName() + "\"," + in_cnt.toString() + ");");
		res.append("AddReturnValue(\"" + GetGeneratedPath() + "\",\"" + GetName() + "\"," + out_cnt.toString() + ");");
		
		for (CSPort p : cs_ports) {
			res.append(p.ToScript());
		}
		
		for (RunEnt re : runs) {
			res.append(re.ToScript());
		}
		
		for (SwCompo swc : swcs) {
			res.append(swc.ToScript());
		}
		
		return res.toString();
	}

	@Override
	public Object ArClone() {
		Assert.isTrue(false, "Not implemented yet!");
		return null;
	}
	
	public SRPort FindPort(String name) {
		for (SRPort p : ports) {
			if (p.GetName().equals(name)) {
				return p;
			}
		}
		Assert.isTrue(false);
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
		copied_ar_ref_swc.SetProto();
		swcs.add(copied_ar_ref_swc);
		this.AddChildElement(copied_ar_ref_swc);
	}

	public void CardChildsAccordingToTypes() {
		for (ArElement ele : eles) {
			if (ele instanceof CSPort) {
				cs_ports.add((CSPort) ele);
			}
			if (ele instanceof SRPort) {
				ports.add((SRPort) ele);
			}
			if (ele instanceof RunEnt) {
				runs.add((RunEnt) ele);
			}
			if (ele instanceof SwCompo) {
				swcs.add((SwCompo) ele);
			}
		}
	}
	
}
