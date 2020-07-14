package ar;

import ar.swc.Port;

public interface ArIO extends ArProperty {
	
	public Port GetSource();
	
	public Port GetTarget();
	
	public String ToRelationScript();
	
}
