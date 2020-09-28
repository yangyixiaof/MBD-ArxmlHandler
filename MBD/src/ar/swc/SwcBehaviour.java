package ar.swc;

import java.util.ArrayList;

import ar.ArElement;

public class SwcBehaviour extends ArElement {
	
	ArrayList<RunEnt> runs = new ArrayList<RunEnt>();
	
	public SwcBehaviour(String name) {
		super(name);
	}
	
	public void AddRunnableEntity(RunEnt rei) {
		rei.SetParent(this);
		runs.add(rei);
	}
	
	@Override
	public Object ArClone() {
		SwcBehaviour swc_b = new SwcBehaviour(name);
		for (RunEnt r : runs) {
			swc_b.AddRunnableEntity((RunEnt) r.ArClone());
		}
		return swc_b;
	}
	
	@Override
	public String ToScript() {
		StringBuilder res = new StringBuilder("");
		for (RunEnt r : runs) {
			res.append(r.ToScript());
		}
		return res.toString();
	}
	
}
