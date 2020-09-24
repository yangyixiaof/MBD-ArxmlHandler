package ar.swc;

import ar.ArElement;

public class RunEnt extends ArElement {
	
//	ArrayList<VarAcc> read_vas = new ArrayList<VarAcc>();
//	ArrayList<VarAcc> write_vas = new ArrayList<VarAcc>();
	
	public RunEnt(String name) {// , SwCompo sc
		super(name);
//		this.sc = sc;
	}
	
//	public void AddReadVarAccesses(ArrayList<VarAcc> rd_vas) {
//		read_vas.addAll(rd_vas);
//	}
//	
//	public void AddWriteVarAccesses(ArrayList<VarAcc> wt_vas) {
//		write_vas.addAll(wt_vas);
//	}

	@Override
	public Object ArClone() {
		return new RunEnt(name);
	}
	
	@Override
	public String ToScript() {
		StringBuilder res = new StringBuilder("");
		res.append("AddModelPage(\"" + GetGeneratedPath() + "\",\"ProgramModelPage\");");
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
