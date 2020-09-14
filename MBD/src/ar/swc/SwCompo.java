package ar.swc;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;

public class SwCompo extends ArElement {
	
	boolean is_proto = false;
	
	boolean is_referred = false;
	
	ArrayList<SRPort> ports = new ArrayList<SRPort>();
	ArrayList<SwCompo> swcs = new ArrayList<SwCompo>();
	
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
		String res = "";
		
		res += "addFunction(\"" + path +  "\", \"" + (swcs.size() == 0 ? "APPLICATION" : "COMPOSITION") + "\");";
		
		for (SRPort p : ports) {
			res += p.ToScript();
		}
		
		for (SwCompo swc : swcs) {
			res += swc.ToScript();
		}
		
		return res;
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
			if (ele instanceof SRPort) {
				ports.add((SRPort) ele);
			}
			if (ele instanceof SwCompo) {
				swcs.add((SwCompo) ele);
			}
		}
	}
	
}
