package ar.swc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;

public class RunEnt extends ArElement {
	
	Map<String, VarAcc> read_vas = new HashMap<String, VarAcc>();
	Map<String, VarAcc> write_vas = new HashMap<String, VarAcc>();
	
	public RunEnt(String name) {// , SwCompo sc
		super(name);
//		this.sc = sc;
	}
	
	public void PutAllReadVarAccesses(Map<String, VarAcc> rd_vas) {
//		AddVarAccesses(rd_vas, read_vas);
		read_vas.putAll(rd_vas);
	}
	
	public void PutAllWriteVarAccesses(Map<String, VarAcc> wt_vas) {
//		AddVarAccesses(wt_vas, write_vas);
		write_vas.putAll(wt_vas);
	}
	
//	private void AddVarAccesses(ArrayList<VarAcc> t_vas, Map<String, VarAcc> map_vas) {
//		for (VarAcc t_va : t_vas) {
//			map_vas.put(t_va.GetRelativePortName(), t_va);
//		}
//	}

	@Override
	public Object ArClone() {
		RunEnt re = new RunEnt(name);
		Set<String> rv_keys = read_vas.keySet();
		for (String rv_k : rv_keys) {
			VarAcc r_va = read_vas.get(rv_k);
			VarAcc c_ele = (VarAcc) r_va.ArClone();
			re.read_vas.put(rv_k, c_ele);
			re.AddChildElement(c_ele);
		}
		
		Set<String> wv_keys = write_vas.keySet();
		for (String wv_k : wv_keys) {
			VarAcc w_va = write_vas.get(wv_k);
			VarAcc c_ele = (VarAcc) w_va.ArClone();
			re.write_vas.put(wv_k, c_ele);
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

		Collection<VarAcc> rvs = read_vas.values();
		Collection<VarAcc> wvs = write_vas.values();
		
		String ins = ToPorts(rvs);
		String outs = ToPorts(wvs);
		
		res.append("AddActor(\"" + full_path + "\",\"" + "FunctionCall" +  "\",\"" + GetName() + "\"," + ins + "," + outs + ",\"runnable\");");
		
		for (VarAcc rv : rvs) {
			res.append(rv.ToScript());
		}
		for (VarAcc wv : wvs) {
			res.append(wv.ToScript());
		}
		return res.toString();
	}
	
	private String ToPorts(Collection<VarAcc> vs) {
		StringBuffer in_pp = new StringBuffer();
		in_pp.append("[");
		for (VarAcc rv : vs) {
			String rpps = rv.ToRunnablePartPorts();
			if (!rpps.isEmpty()) {
				in_pp.append(rpps + ",");
			}
		}
		if (in_pp.charAt(in_pp.length()-1) == ',') {
			in_pp.deleteCharAt(in_pp.length()-1);
		}
		in_pp.append("]");
		return in_pp.toString();
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
