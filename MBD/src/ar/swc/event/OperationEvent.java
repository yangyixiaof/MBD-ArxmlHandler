package ar.swc.event;

import ar.intf.cs.ArCsOperation;
import ar.swc.RunEnt;

public class OperationEvent extends ArEvent {
	
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
	public Object ArClone() {
		OperationEvent oe = new OperationEvent(name);
		oe.SetRunEnt(re);
		oe.SetArCsOperation(aco);
		return oe;
	}
	
	@Override
	public String ToScript() {
		return aco.ToScriptInEnv(true, re.GetGeneratedPath());
	}
	
}
