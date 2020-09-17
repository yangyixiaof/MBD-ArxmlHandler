package ar.intf.cs;

import java.util.ArrayList;

import ar.ArElement;

public class ArCsOperation extends ArElement {
	
	ArrayList<ArCsArgument> args = new ArrayList<ArCsArgument>();
	
	public ArCsOperation(String name) {
		super(name);
	}
	
	public void AddArgument(ArCsArgument arg) {
		args.add(arg);
		this.eles.add(arg);
	}
	
	public ArrayList<ArCsArgument> GetAllArguments() {
		return args;
	}
	
}
