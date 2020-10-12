package ar.swc;

import ar.ArElement;
import ar.intf.cs.ArCsOperation;

public class OperationEvent extends ArElement {
	
	RunEnt re = null;
	ArCsOperation aco = null;
	
	public OperationEvent(String name) {
		super(name);
	}
	
	public void SetRunEnt(RunEnt re) {
		this.re = re;
	}
	
	public RunEnt GetRunEnt() {
		return re;
	}
	
	public void SetArCsOperation(ArCsOperation aco) {
		this.aco = aco;
	}
	
	public ArCsOperation GetArCsOperation() {
		return aco;
	}
	
	@Override
	public String ToScript() {
		return aco.ToScriptInEnv(true, re.GetGeneratedPath());
	}
	
}
