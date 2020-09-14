package ar.swc;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.ArInterface;
import util.ArUtil;
import util.StringHelper;

public class SRPort extends ArElement {
	
	String name = null;
	
	boolean is_input = false;
	
	VarAcc v_acc = null;
	
	SwCompo relation_base = null;
	
//	SwCompo swc = null;
	
	String gen_path = null;
	
	SRPort source = null;
	
	SRPort target = null;
	
	public SRPort(String name, boolean is_input) {// , SwCompo swc
		super(name);
//		this.swc = swc;
		this.is_input = is_input;
	}
	
	public String GetName() {
		return name;
	}
	
	private void SetSource(SRPort source) {
		this.source = source;
	}
	
	public SRPort GetSource() {
		return source;
	}
	
	public void SetTarget(SRPort target, SwCompo relation_base) {
		this.target = target;
		this.relation_base = relation_base;
		target.SetSource(this);
	}
	
	public SRPort GetTarget() {
		return target;
	}

	public void SetVarAcc(VarAcc v_acc) {
		Assert.isTrue(this.v_acc == null && v_acc != null);
		this.v_acc = v_acc;
	}
	
	public VarAcc GetVarAcc() {
		return v_acc;
	}
	
	public boolean IsInput() {
		return is_input;
	}

	@Override
	public String ToScript() {
		boolean is_input_port = IsInput();
		String func = "addOutport";
		if (is_input_port) {
			func = "addInport";
		}
		String ctype = "Unknown";
		ArInterface io = ArUtil.TraceToTerminal(this);
		if (io != null) {
			Assert.isTrue(false, "The io type not handled!");
//			ctype = io.GetCType();
		}
		return (func + "(\"" + StringHelper.NonLastPartInPath(gen_path) + "\",\"/" + name + "\",\"" + ctype + "\");");
	}
	
	public String ToRelationScript() {
		if (target != null) {
			String res = "addRelation(\"" + relation_base.GetGeneratedPath() + "\",\"" + StringHelper.TrimPrefix(GetGeneratedPath(), relation_base.GetGeneratedPath()) + "\",\"" + StringHelper.TrimPrefix(target.GetGeneratedPath(), relation_base.GetGeneratedPath()) + "\");";
			return res;
		}
		return "";
	}

	@Override
	public Object ArClone() {
		Assert.isTrue(relation_base == null);
		SRPort p = new SRPort(name, is_input);
		if (v_acc != null) {
			p.SetVarAcc((VarAcc) v_acc.ArClone());
		}
		return p;
	}
	
}