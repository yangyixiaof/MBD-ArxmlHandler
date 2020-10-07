package ar.intf;

import java.util.HashMap;
import java.util.Map;

public class ArSRInterfaceWithDataElementProperty {
	
	ArSenderReceiverInterface asri = null;
	Map<ArDataElement, ArDataElementProperty> required_data_eles = new HashMap<ArDataElement, ArDataElementProperty>();
	Map<ArDataElement, ArDataElementProperty> provided_data_eles = new HashMap<ArDataElement, ArDataElementProperty>();
	
	public ArSRInterfaceWithDataElementProperty() {
	}
	
	public void SetArSenderReceiverInterface(ArSenderReceiverInterface asri) {
		this.asri = asri;
	}
	
	public ArSenderReceiverInterface GetArSenderReceiverInterface() {
		return asri;
	}
	
	public void AddRequiredArDataElement(ArDataElement ade, ArDataElementProperty adep) {
		required_data_eles.put(ade, adep);
	}
	
	public Map<ArDataElement, ArDataElementProperty> GetAllRequiredArDataElementsWithProperty() {
		return required_data_eles;
	}

	public void AddProvidedArDataElement(ArDataElement ade, ArDataElementProperty adep) {
		provided_data_eles.put(ade, adep);
	}
	
	public Map<ArDataElement, ArDataElementProperty> GetAllProvidedArDataElements() {
		return provided_data_eles;
	}
	
	public String ToTypeString() {
		return asri.GetName();
	}
	
//	@Override
//	public String ToScript() {
////		return asri.ToScript();
//		Assert.isTrue(false, "Cannot be implemented!");
//		return null;
//	}
	
}
