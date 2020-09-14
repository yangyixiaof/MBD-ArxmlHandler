package ar.swc;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.ArInterface;

public class VarAcc extends ArElement {
	
	boolean is_read = false;
//	ArIO self_port = null;
	ArInterface acc_intf = null;
	String acc_intf_property = null;
	
	public VarAcc(String name, boolean is_read, ArInterface acc_intf, String acc_intf_property) {// , ArIO self_port
		super(name);
		this.is_read = is_read;
//		this.self_port = self_port;
		this.acc_intf = acc_intf;
		this.acc_intf_property = acc_intf_property;
	}
	
	public boolean IsRead() {
		return is_read;
	}
	
//	public ArIO GetSelfPort() {
//		return self_port;
//	}
	
	public ArInterface GetAccInterface() {
		return acc_intf;
	}
	
	public String GetAccInterfaceProperty() {
		return acc_intf_property;
	}
	
	@Override
	public String ToScript() {
		Assert.isTrue(false, "Not implemented yet!");
		return null;
	}

	@Override
	public Object ArClone() {
		return new VarAcc(name, is_read, acc_intf, acc_intf_property);
	}
	
}
