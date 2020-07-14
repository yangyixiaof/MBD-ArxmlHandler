package ar.swc;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArProperty;

public class SwCompoInst implements ArProperty {
	
	String name = null;
	
	SwCompo type = null;
	
	SwCompo parent = null;
	
	String generate_path = null;
	
	ArrayList<Port> ports = new ArrayList<Port>();
	ArrayList<RunEnt> run_ents = new ArrayList<RunEnt>();
	
	public SwCompoInst(String name, SwCompo self, SwCompo parent) {
		this.name = name;
		
		Assert.isTrue(self.swcs.size() == 0);
		
		this.type = self;
		
		this.parent = parent;

		for (Port p : self.ports) {
			ports.add((Port) p.ArClone());
		}
		for (RunEnt re : self.run_ents) {
			run_ents.add((RunEnt) re.ArClone());
		}
	}
	
	public SwCompo GetType() {
		return type;
	}
	
	public String GetName() {
		return name;
	}

	@Override
	public void GeneratePath(String path) {
		generate_path = path + "/" + name;
		for (Port p : ports) {
			p.GeneratePath(generate_path);
		}
	}

	@Override
	public String GetGeneratedPath() {
		return generate_path;
	}

	@Override
	public String ToScript() {
		return "addActor(\"" + parent.GetGeneratedPath() + "\",\"/" + name + "\",\"" + type.GetGeneratedPath() + "\");";
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
		return res;
	}
	
}
