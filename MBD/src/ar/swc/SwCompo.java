package ar.swc;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArProperty;

public class SwCompo implements ArProperty {
	
	String name = null;
	
	String gen_path = null;
	
	boolean is_referred = false;
	
	ArrayList<Port> ports = new ArrayList<Port>();
	ArrayList<SwCompoInst> swcs = new ArrayList<SwCompoInst>();
	ArrayList<RunEnt> run_ents = new ArrayList<RunEnt>();
	
	ArrayList<SwCompo> dependencies = new ArrayList<SwCompo>();
	
	public SwCompo(String name) {
		this.name = name;
	}
	
	public String GetName() {
		return name;
	}
	
	public void SetReferred() {
		this.is_referred = true;
	}
	
	public boolean IsReferred() {
		return is_referred;
	}
	
	public void AddPort(Port p) {
		ports.add(p);
	}
	
	public void AddRunEnt(RunEnt t) {
		run_ents.add(t);
	}
	
	public void AddSwCompoInst(SwCompoInst swc) {
		swcs.add(swc);
	}
	
	public ArrayList<SwCompoInst> GetAllSwCompoInsts() {
		return swcs;
	}
	
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
	
	public ArrayList<Port> GetAllPorts() {
		return ports;
	}
	
	public ArrayList<RunEnt> GetRunEnts() {
		return run_ents;
	}
	
	public void GeneratePath(String path) {
		this.gen_path = path + "/" + name;
		for (SwCompoInst swc : swcs) {
			swc.GeneratePath(GetGeneratedPath());
		}
		for (Port p : ports) {
			p.GeneratePath(GetGeneratedPath());
		}
	}
	
	@Override
	public String GetGeneratedPath() {
		return gen_path;
	}
	
	public String ToScript() {
		String res = "";
		
		res += "addFunction(\"" + gen_path +  "\");";
		
		for (Port p : ports) {
			res += p.ToScript();
		}
		
		for (SwCompoInst swc : swcs) {
			res += swc.ToScript();
		}
		
		return res;
	}

	@Override
	public Object ArClone() {
		Assert.isTrue(false);
		return null;
	}
	
	public Port FindPort(String name) {
		for (Port p : ports) {
			if (p.GetName().equals(name)) {
				return p;
			}
		}
		Assert.isTrue(false);
		return null;
	}

	public String ToRelationScript() {
		String res = "";
		for (Port p : ports) {
			res += p.ToRelationScript();
		}
		for (SwCompoInst swc : swcs) {
			res += swc.ToRelationScript();
		}
		return res;
	}
	
}
