package ar.type;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import util.TypeHelper;

public class ArDataType extends ArElement {
	
//	ArrayList<String> data_names = new ArrayList<String>();
//	ArrayList<Object> data_types = new ArrayList<Object>();
	
	ArrayList<ArBaseDataType> data_types = new ArrayList<ArBaseDataType>();
	
	public ArDataType(String name) {
		super(name);
	}
	
	public void AddBaseDataType(ArBaseDataType bdt) {
		data_types.add(bdt);
	}
	
	@Override
		public String ToScript() {
			Assert.isTrue(data_types.size() == 1);
			String d_type = data_types.get(0).GetName();
			String c_type = TypeHelper.TranslateArxmlTypeToCType(d_type);
			return c_type;
		}
	
//	public void AddDataElement(String name, Object type) {
//		data_names.add(name);
//		data_types.add(type);
//	}
//	
//	public ArrayList<String> GetDataNames() {
//		return data_names;
//	}
//	
//	public ArrayList<Object> GetDataTypes() {
//		return data_types;
//	}
	
}
