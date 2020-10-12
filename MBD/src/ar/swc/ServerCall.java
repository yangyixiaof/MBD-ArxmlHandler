package ar.swc;

import ar.ArElement;
import ar.intf.cs.ArCsOperation;

public class ServerCall extends ArElement {
	
	ArCsOperation aco = null;
	
	public ServerCall(String name) {
		super(name);
	}
	
	public void SetArCsOperation(ArCsOperation aco) {
		this.aco = aco;
	}
	
	public ArCsOperation GetArCsOperation() {
		return aco;
	}
	
}
