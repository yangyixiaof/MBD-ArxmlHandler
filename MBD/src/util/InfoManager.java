package util;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage;
import autosar40.genericstructure.generaltemplateclasses.identifiable.Referrable;
import autosar40.util.Autosar40ResourceImpl;

public class InfoManager {
	
	HashMap<EObject, String> info = new HashMap<EObject, String>();
	
	public InfoManager(Autosar40ResourceImpl resource) {
		EList<EObject> acs = resource.getContents();
		for (EObject ac : acs) {
			Build(ac);
		}
	}
	
	protected void Build(EObject root) {
		EList<EObject> ecnts = root.eContents();
		for (EObject ecnt : ecnts) {
//			System.out.println("b_ecnt:" + ecnt);
			if (ecnt instanceof ARPackage) {
				RBuild(ecnt, "");
//				System.out.println("ecnt:" + ecnt);
			}
		}
	}
	
	protected void RBuild(EObject root, String parent_path) {
		Assert.isTrue(root instanceof Referrable, "not referrable object:" + root);
		
		Referrable rrf = (Referrable) root;
		String path = parent_path + "/" + rrf.getShortName();
		info.put(root, path);
//		System.out.println("path:" + path);
		
		EList<EObject> ecnts = root.eContents();
		for (EObject ecnt : ecnts) {
			if (ecnt instanceof Referrable) {
//				Assert.isTrue(root instanceof Referrable, "error eobject:" + root.toString());
//				Referrable rf = (Referrable) ecnt;
//				System.out.println("shortname:" + rf.getShortName() + "#ecnt:" + ecnt);
				RBuild(ecnt, path);
			}
		}
	}
	
	public String GetFullPath(EObject eo) {
		Assert.isTrue(info.containsKey(eo));
		String res = info.get(eo);
//		String curr_name = curr.getShortName();
//		res += "/" + curr_name;
//		ARPackage eoo = curr.getARPackage();
//		System.out.println("short_name:" + eoo.getShortName());
		return res;
	}
	
}
