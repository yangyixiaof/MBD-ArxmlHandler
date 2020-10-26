package ar.intf.cs;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import util.StringHelper;

public class ArCsOperation extends ArElement {
	
	ArrayList<ArCsArgument> args = new ArrayList<ArCsArgument>();
	
	public ArCsOperation(String name) {
		super(name);
	}
	
	public void AddArgument(ArCsArgument arg) {
		args.add(arg);
	}
	
	public ArrayList<ArCsArgument> GetAllArguments() {
		return args;
	}
	
	@Override
	public String ToScript() {
		Assert.isTrue(false, "Cannot be implemented!");
		return super.ToScript();
	}
	
	public String ToScriptInEnv(boolean is_read, String model_page_path) {
//		System.err.println("CsOperation is_read:" + is_read);
		ArrayList<ArCsArgument> inputs = new ArrayList<ArCsArgument>();
		ArrayList<ArCsArgument> outputs = new ArrayList<ArCsArgument>();
		for (ArCsArgument aca : args) {
			if (aca.GetDirection().equals("in")) {
				inputs.add(aca);
			}
			if (aca.GetDirection().equals("out")) {
				outputs.add(aca);
			}
		}
		
		StringBuilder in_cnt_builder = new StringBuilder("");
		in_cnt_builder.append("[");
		for (ArCsArgument input : inputs) {
			in_cnt_builder.append("[");
			in_cnt_builder.append(input.GetName());
			in_cnt_builder.append(" ");
			in_cnt_builder.append(input.GetDataType().ToScript());
			in_cnt_builder.append(" 0");
			in_cnt_builder.append("]#");
		}
		if (inputs.size() > 0) {
			in_cnt_builder.deleteCharAt(in_cnt_builder.length() - 1);
		}
		in_cnt_builder.append("]");
		
		StringBuilder out_cnt_builder = new StringBuilder("");
		out_cnt_builder.append("[");
		for (ArCsArgument output : outputs) {
			out_cnt_builder.append("[");
			out_cnt_builder.append(output.GetName());
			out_cnt_builder.append(" ");
			out_cnt_builder.append(output.GetDataType().ToScript());
			out_cnt_builder.append(" 0");
			out_cnt_builder.append("]#");
		}
		if (outputs.size() > 0) {
			out_cnt_builder.deleteCharAt(out_cnt_builder.length() - 1);
		}
		out_cnt_builder.append("]");
		
		StringBuilder cnt_builder = new StringBuilder("");

		if (is_read) {
			String call = "FunctionCallClient";
			cnt_builder.append("AddActor(\"" + model_page_path + "\",\"" + call + "\",\"" + GetName() + "\",");
			String r_ins = StringHelper.InsertFunctionCallNameToFirstParameterInList(GetName(), in_cnt_builder.toString());
			cnt_builder.append(r_ins);
			cnt_builder.append(",");
			cnt_builder.append(out_cnt_builder.toString());
			cnt_builder.append(",\"client\");");
		} else {
//			String op_path = cs_op.GetGeneratedPath();
			cnt_builder.append("AddActor(\"" + model_page_path + "\",\"" + "ArgumentIn" + "\",\"" + GetName() + "" + "\",[]," + in_cnt_builder.toString() + ",\"server\");");
			cnt_builder.append("AddActor(\"" + model_page_path + "\",\"" + "ArgumentOut" + "\",\"" + GetName() + "_ret" + "\"," + out_cnt_builder.toString() + ",[],\"server\");");
		}
		return cnt_builder.toString();
	}
	
}
