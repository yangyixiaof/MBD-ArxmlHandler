package ar.swc;

import ar.ArProperty;

public class RunEnt implements ArProperty{
	
	String name = null;
//	SwCompo sc = null;
	
//	ArrayList<VarAcc> read_vas = new ArrayList<VarAcc>();
//	ArrayList<VarAcc> write_vas = new ArrayList<VarAcc>();
	
	public RunEnt(String name) {// , SwCompo sc
		this.name = name;
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
	public void GeneratePath(String path) {
	}

	@Override
	public String GetGeneratedPath() {
		return null;
	}

	@Override
	public String ToScript() {
		return null;
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
