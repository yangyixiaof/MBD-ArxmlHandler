package util;

import org.eclipse.core.runtime.Assert;

import ar.ArModel;
import ar.intf.ArInterface;
import ar.swc.Port;
import ar.swc.SwCompoInst;
import ar.swc.VarAcc;

public class ArUtil {
	
	public static ArInterface TraceToTerminal(Port ario) {
		Port curr = ario;
		if (ario.IsInput()) {
			while (curr.GetSource() != null) {
				curr = curr.GetSource();
			}
		} else {
			while (curr.GetTarget() != null) {
				curr = curr.GetTarget();
			}
		}
		VarAcc var_acc = curr.GetVarAcc();
		if (var_acc != null) {
			return var_acc.GetAccInterface();
		}
		return null;
	}
	
	public static Port GetPortFromCompoInstRef(ArModel am, Object p_prov, Object c_prov) {
		String source_c = StringHelper.GetProxyValidPath(c_prov.toString());
		String source = StringHelper.GetProxyValidPath(p_prov.toString());
		Assert.isTrue(am.GetSwCompoInst(source_c).GetType() == am.GetSwCompo(StringHelper.NonLastPartInPath(source)));
		
		SwCompoInst inst = am.GetSwCompoInst(source_c);
		Port p_s = inst.FindPort(StringHelper.LastPartInPath(source));
		
		return p_s;
	}
	
}
