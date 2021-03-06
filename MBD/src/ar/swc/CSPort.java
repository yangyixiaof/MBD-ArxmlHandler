package ar.swc;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.cs.ArCsInterfaceWithOperationProperty;

public class CSPort extends ArElement {
	
	boolean is_read = false;
	
//	ArCsOperation cs_op = null;
	ArCsInterfaceWithOperationProperty acsiwop = null;
	
	public CSPort(String name, boolean is_read) {
		super(name);
		this.is_read = is_read;
	}
	
	public void SetArCsInterfaceWithOperationProperty(ArCsInterfaceWithOperationProperty acsiwop) {
		this.acsiwop = acsiwop;
	}
	
	public ArCsInterfaceWithOperationProperty GetArCsInterfaceWithOperationProperty() {
		return acsiwop;
	}
	
	@Override
	public String ToScript() {
//		StringBuffer res = new StringBuffer();
//		Set<ArCsOperation> cs_ops = acsiwop.GetAllArCsOperationWithProperty().keySet();
//		for (ArCsOperation cs_op : cs_ops) {
//			res.append(ToOneOperationScript(cs_op));
//		}
//		return res.toString();
		
		Assert.isTrue(false, "This should not be implemented!");
		return null;
	}
	
//	public String ToOneOperationScript(ArCsOperation cs_op) {
//		ArrayList<ArCsArgument> args = cs_op.GetAllArguments();
//		
//		SwCompo swc = (SwCompo) GetParent();
//		
//		ArrayList<ArCsArgument> inputs = new ArrayList<ArCsArgument>();
//		ArrayList<ArCsArgument> outputs = new ArrayList<ArCsArgument>();
//		for (ArCsArgument aca : args) {
//			if (aca.GetDirection().equals("in")) {
//				inputs.add(aca);
//			}
//			if (aca.GetDirection().equals("out")) {
//				outputs.add(aca);
//			}
//		}
//		
//		StringBuilder in_cnt_builder = new StringBuilder("");
//		in_cnt_builder.append("[");
//		if (is_read) {
//			in_cnt_builder.append("[\"" + cs_op.GetGeneratedPath() + "\",\"string\",\"0\"],");
//		}
//		for (ArCsArgument input : inputs) {
//			in_cnt_builder.append("[");
//			in_cnt_builder.append(input.GetName());
//			in_cnt_builder.append(",");
//			in_cnt_builder.append(input.GetDataType().ToScript());
//			in_cnt_builder.append(",0");
//			in_cnt_builder.append("],");
//		}
//		if (inputs.size() > 0) {
//			in_cnt_builder.deleteCharAt(in_cnt_builder.length() - 1);
//		}
//		in_cnt_builder.append("]");
//		
//		StringBuilder out_cnt_builder = new StringBuilder("");
//		out_cnt_builder.append("[");
//		for (ArCsArgument output : outputs) {
//			out_cnt_builder.append("[");
//			out_cnt_builder.append(output.GetName());
//			out_cnt_builder.append(",");
//			out_cnt_builder.append(output.GetDataType().ToScript());
//			out_cnt_builder.append(",0");
//			out_cnt_builder.append("],");
//		}
//		if (outputs.size() > 0) {
//			out_cnt_builder.deleteCharAt(out_cnt_builder.length() - 1);
//		}
//		out_cnt_builder.append("]");
//		
//		StringBuilder cnt_builder = new StringBuilder("");
//
//		String call = "FunctionCallClient";
//		if (!is_read) {
//			call = "FunctionCall";
//		}
//		cnt_builder.append("AddActor(\"" + swc.GetGeneratedPath() + "\",\"" + call + "\",\"" + cs_op.GetName() + "\",");
//		cnt_builder.append(in_cnt_builder.toString());
//		cnt_builder.append(",");
//		cnt_builder.append(out_cnt_builder.toString());
//		cnt_builder.append(");");
//		if (!is_read) {
//			String op_path = cs_op.GetGeneratedPath();
//			cnt_builder.append("AddModelPage(\"" + op_path + "\",\"ProgramModelPage\",\"csoperation\");");
//			cnt_builder.append("AddFunction(\"" + op_path + "\",\"" + cs_op.GetName() + "_head" + "\"," + in_cnt_builder.toString() + ");");
//			cnt_builder.append("AddReturnValue(\"" + op_path + "\",\"" + cs_op.GetName() + "_tail" + "\"," + out_cnt_builder.toString() + ");");
//		}
//		return cnt_builder.toString();
//	}
	
	@Override
	public Object ArClone() {
//		Assert.isTrue(false, "Not implemented yet!");
		CSPort csp = new CSPort(name, is_read);
		csp.SetArCsInterfaceWithOperationProperty(acsiwop);
//		csp.SetCSOperation(cs_op);
		return csp;
	}
	
}
