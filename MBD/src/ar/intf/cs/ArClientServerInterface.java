package ar.intf.cs;

import java.util.ArrayList;

import ar.ArElement;

public class ArClientServerInterface extends ArElement {
	
	ArrayList<ArCsOperation> acos = new ArrayList<ArCsOperation>();
	
	public ArClientServerInterface(String name) {
		super(name);
	}
	
	public void AddOperation(ArCsOperation op) {
		acos.add(op);
	}
	
	public ArrayList<ArCsOperation> GetAllOperations() {
		return acos;
	}
	
}
