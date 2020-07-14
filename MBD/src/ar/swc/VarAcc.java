package ar.swc;

import ar.ArProperty;
import ar.intf.ArInterface;

public class VarAcc implements ArProperty {
	
	boolean is_read = false;
//	ArIO self_port = null;
	ArInterface acc_intf = null;
	String acc_intf_property = null;
	
	public VarAcc(boolean is_read, ArInterface acc_intf, String acc_intf_property) {// , ArIO self_port
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
	public void GeneratePath(String path) {
		// do nothing
	}

	@Override
	public String GetGeneratedPath() {
		return null;
	}

	@Override
	public String ToScript() {
		return "";
	}

	@Override
	public Object ArClone() {
		return new VarAcc(is_read, acc_intf, acc_intf_property);
	}
	
}
