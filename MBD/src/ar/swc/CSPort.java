package ar.swc;

import ar.ArElement;
import ar.intf.cs.ArCsOperation;

public class CSPort extends ArElement {
	
	boolean is_read = false;
	
	ArCsOperation cs_op = null;
	
	public CSPort(String name, boolean is_read) {
		super(name);
		this.is_read = is_read;
	}
	
	public void SetCSOperation(ArCsOperation cs_op) {
		this.cs_op = cs_op;
	}
	
	public ArCsOperation GetCsOperation() {
		return cs_op;
	}
	
}
