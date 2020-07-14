package ar;

import java.util.ArrayList;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import ar.intf.ArInterface;
import ar.swc.Port;
import ar.swc.SwCompo;
import ar.swc.SwCompoInst;
import util.StringHelper;

public class ArModel implements ArProperty {
	
	TreeMap<String, ArInterface> ar_intfs = new TreeMap<String, ArInterface>();
	TreeMap<String, SwCompo> ar_swcs = new TreeMap<String, SwCompo>();
	TreeMap<String, SwCompoInst> ar_swcis = new TreeMap<String, SwCompoInst>();
	
//	TreeMap<String, Port> ar_ports = new TreeMap<String, Port>();

	String gen_path = null;
	
	public ArModel() {
	}

	public void AddInterface(String key, ArInterface ar_intfs) {
		this.ar_intfs.put(key, ar_intfs);
	}
	
	public ArInterface GetInterface(String intf_path) {
		Assert.isTrue(ar_intfs.containsKey(intf_path));
		return ar_intfs.get(intf_path);
	}
	
	public void AddSwCompo(String key, SwCompo swc) {
		ar_swcs.put(key, swc);	
	}
	
	public SwCompo GetSwCompo(String key) {
		Assert.isTrue(ar_swcs.containsKey(key));
		return ar_swcs.get(key);
	}
	
	public void AddSwCompoInst(String key, SwCompoInst swci) {
		ar_swcis.put(key, swci);	
	}
	
	public SwCompoInst GetSwCompoInst(String key) {
		Assert.isTrue(ar_swcis.containsKey(key));
		return ar_swcis.get(key);
	}
	
//	public void AddPort(String key, Port port) {
//		ar_ports.put(key, port);
//	}
	
	public Port GetPort(String key) {
//		Assert.isTrue(ar_ports.containsKey(key), "Wrong key:" + key + "#Key set:" + PrintUtil.PrintSet(ar_ports.keySet()));
		String swc_name = StringHelper.NonLastPartInPath(key);
		String port_name = StringHelper.LastPartInPath(key);
		SwCompo swc = ar_swcs.get(swc_name);
		SwCompoInst swci = ar_swcis.get(swc_name);
		Port p = null;
		if (swc != null) {
			Assert.isTrue(swci == null, "swc_name:" + swc_name);
			p = swc.FindPort(port_name);
		} else {
			Assert.isTrue(swci != null, "swc_name:" + swc_name);
			p = swci.FindPort(port_name);
		}
		return p;
	}

	private ArrayList<SwCompo> Topology() {
		ArrayList<SwCompo> ini = new ArrayList<SwCompo>();
		ArrayList<SwCompo> result = new ArrayList<SwCompo>();
		
		ini.addAll(ar_swcs.values());
		while (ini.size() > 0) {
			
			int ini_len = ini.size();
			for (int i=0;i<ini_len;i++) {
				SwCompo sc = ini.get(i);
				if (sc.SatisfyDependencies(result)) {
					result.add(sc);
					ini.remove(sc);
					break;
				}
			}
			
		}
		
		
		return result;
	}
	
	public void Process() {
//		Collection<ArInterface> intfs = ar_intfs.values();
//		for (ArInterface intf : intfs) {
//			intf.GeneratePath("");
//		}
//		Collection<SwCompo> swcs = ar_swcs.values();
		ArrayList<SwCompo> rs = Topology();
		for (SwCompo swc : rs) {
//			if (!swc.IsReferred()) {
			swc.GeneratePath("");
//			}
		}
//		Collection<Port> aps = ar_ports.values();
//		for (Port ap : aps) {
//			ap.GeneratePath("");
//		}
	}
	
	public String ToScript() {
		String result = "";
//		Collection<ArInterface> intfs = ar_intfs.values();
//		for (ArInterface intf : intfs) {
//			result += intf.ToScript();
//		}
//		Collection<SwCompo> swcs = ar_swcs.values();
		ArrayList<SwCompo> rs = Topology();
		for (SwCompo swc : rs) {
//			if (!swc.IsReferred()) {
			result += swc.ToScript();
//			}
		}
		for (SwCompo swc : rs) {
			result += swc.ToRelationScript();
		}
//		Collection<Port> aps = ar_ports.values();
//		for (Port ap : aps) {
//			result += ap.ToRelationScript();
//		}
		return result;
	}

	@Override
	public void GeneratePath(String path) {
		gen_path = path + "/";
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
