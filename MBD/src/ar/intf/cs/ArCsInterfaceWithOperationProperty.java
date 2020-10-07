package ar.intf.cs;

import java.util.HashMap;
import java.util.Map;

public class ArCsInterfaceWithOperationProperty {
	
	ArClientServerInterface acsi = null;
	Map<ArCsOperation, ArCsOperationProperty> csops = new HashMap<ArCsOperation, ArCsOperationProperty>();
	
	public ArCsInterfaceWithOperationProperty() {
	}
	
	public void SetArClientServerInterface(ArClientServerInterface acsi) {
		this.acsi = acsi;
	}
	
	public ArClientServerInterface GetArClientServerInterface() {
		return acsi;
	}
	
	public void AddArCsOperation(ArCsOperation aco, ArCsOperationProperty acop) {
		csops.put(aco, acop);
	}
	
	public Map<ArCsOperation, ArCsOperationProperty> GetAllArCsOperationWithProperty() {
		return csops;
	}
	
}
