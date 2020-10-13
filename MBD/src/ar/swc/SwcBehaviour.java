package ar.swc;

import java.util.ArrayList;

import ar.ArElement;
import ar.swc.event.ArEvent;

public class SwcBehaviour extends ArElement {
	
	ArrayList<RunEnt> runs = new ArrayList<RunEnt>();
	ArrayList<ArEvent> events = new ArrayList<ArEvent>();
	
	public SwcBehaviour(String name) {
		super(name);
	}
	
	public void AddRunnableEntity(RunEnt rei) {
		runs.add(rei);
	}
	
	public void AddArEvent(ArEvent evt) {
		events.add(evt);
	}
	
	@Override
	public Object ArClone() {
		SwcBehaviour swc_b = new SwcBehaviour(name);
		for (RunEnt r : runs) {
			RunEnt c_ele = (RunEnt) r.ArClone();
			swc_b.AddRunnableEntity(c_ele);
			swc_b.AddChildElement(c_ele);
		}
		for (ArEvent evt : events) {
			ArEvent c_ele = (ArEvent) evt.ArClone();
			swc_b.AddArEvent(c_ele);
			swc_b.AddChildElement(c_ele);
		}
		return swc_b;
	}
	
	@Override
	public String ToScript() {
		StringBuilder res = new StringBuilder("");
		for (RunEnt r : runs) {
//			System.err.println("RunEnt:" + r.GetGeneratedPath());
			res.append(r.ToScript());
		}
		for (ArEvent e : events) {
//			System.err.println("RunEnt:" + r.GetGeneratedPath());
			res.append(e.ToScript());
		}
		return res.toString();
	}
	
}
