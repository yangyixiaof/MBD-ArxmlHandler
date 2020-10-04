package ar.swc;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;

public class RunEnt extends ArElement {
	
	ArrayList<VarAcc> read_vas = new ArrayList<VarAcc>();
	ArrayList<VarAcc> write_vas = new ArrayList<VarAcc>();
	
	public RunEnt(String name) {// , SwCompo sc
		super(name);
//		this.sc = sc;
	}
	
	public void AddReadVarAccesses(ArrayList<VarAcc> rd_vas) {
		read_vas.addAll(rd_vas);
	}
	
	public void AddWriteVarAccesses(ArrayList<VarAcc> wt_vas) {
		write_vas.addAll(wt_vas);
	}

	@Override
	public Object ArClone() {
		RunEnt re = new RunEnt(name);
		for (VarAcc r_va : read_vas) {
			VarAcc c_ele = (VarAcc) r_va.ArClone();
			re.read_vas.add(c_ele);
			re.AddChildElement(c_ele);
		}
		for (VarAcc w_va : write_vas) {
			VarAcc c_ele = (VarAcc) w_va.ArClone();
			re.write_vas.add(c_ele);
			re.AddChildElement(c_ele);
		}
		return re;
	}
	
	@Override
	public String ToScript() {
		StringBuilder res = new StringBuilder("");
		String full_path = GetGeneratedPath();
		Assert.isTrue(full_path != null);
		res.append("AddModelPage(\"" + full_path + "\",\"ProgramModelPage\");");
		// TODO handle variable access which needs to handle Function head and tail (return). 
		return res.toString();
	}
	
//	public void HandleVarAccString(String va_str) {
//		ArrayList<Port> pts = sc.GetAllPorts();
//		int match_num = 0;
//		for (Port pt : pts) {
//			String pt_name = pt.GetPortName();
//			if (va_str.contains(pt_name)) {
//				match_num++;
//				String[] ops = va_str.split(pt_name);
//				vas.add(new VarAcc(pt_name, StringHelper.Trim(ops[1], "_"), StringHelper.Trim(ops[0], "_")));
//			}
//		}
//		Assert.isTrue(match_num == 1);
//	}
	
}
