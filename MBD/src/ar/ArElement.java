package ar;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

public class ArElement implements ArCloneable, ArProperty {
	
	protected String path = null;
	protected String name = null;
	
	protected ArElement parent = null;
	protected ArrayList<ArElement> eles = new ArrayList<ArElement>();
	
	public ArElement(String name) {
		this.name = name;
	}
	
	public void SetName(String name) {
		this.name = name;
	}
	
	public String GetName() {
		return name;
	}
	
	public void SetParent(ArElement parent) {
		this.parent = parent;
	}
	
	public void AddChildElement(ArElement ele) {
		eles.add(ele);
		ele.SetParent(this);
	}
	
	public ArElement GetParent() {
		return parent;
	}
	
	public ArrayList<ArElement> GetAllChildElements() {
		return eles;
	}

	@Override
	public void GeneratePath() {
		if (path == null) {
			path = name;
			String par = "";
			if (parent != null) {
				par = parent.GetGeneratedPath();
			}
			path = par + "/" + path;
		}
	}

	@Override
	public String GetGeneratedPath() {
		Assert.isTrue(path != null);
		return path;
	}

	@Override
	public String ToScript() {
		Assert.isTrue(false, "Raw ArElement cannot be into script.");
		return null;
	}

	@Override
	public Object ArClone() {
		ArElement ae = new ArElement(name);
		return ae;
	}
	
}
