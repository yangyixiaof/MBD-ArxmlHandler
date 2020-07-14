package ar.intf;

import org.eclipse.core.runtime.Assert;

import ar.ArProperty;

public class ArInterface implements ArProperty {
	
	String name = null;
	String ctype = null;
	String full_path = null;
	
	String gen_path = null;
	
	int read_times = 0;
	int write_times = 0;
	
	public ArInterface(String name, String ctype, String full_path) {
		this.name = name;
		this.ctype = ctype;
		this.full_path = full_path;
	}
	
	public String GetName() {
		return name;
	}
	
	public String GetCType() {
		return ctype;
	}
	
	public String GetFullPath() {
		return full_path;
	}
	
	public void RecordOneRead() {
		read_times++;
	}
	
	public void RecordOneWrite() {
		write_times++;
	}
	
//	public void Validate() {
//		Assert.isTrue((read_times == 0 && write_times > 0) || (read_times > 0 && write_times == 0), "intf_name:" + name + "#read_times:" + read_times + "#write_times:" + write_times);
//	}
	
	public boolean IsInput() {
		return read_times > 0;
	}
	
	public String ToScript() {
//		boolean is_input_port = IsInput();
//		String func = "addOutport";
//		if (is_input_port) {
//			func = "addInport";
//		}
//		return (func + "(\"" + gen_path + "\",\"" + ctype + "\");");
		Assert.isTrue(false);
		return null;
	}
	
	public void GeneratePath(String path) {
		gen_path = path + "/" + name;
	}

	@Override
	public String GetGeneratedPath() {
		return gen_path;
	}

	@Override
	public Object ArClone() {
		Assert.isTrue(false);
		return null;
	}
	
}
