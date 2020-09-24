package util;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.swc.SRPort;
import ar.swc.SwCompo;

public class ArUtil {
	
//	public static ArSenderReceiverInterface TraceToTerminal(SRPort ario) {
//		SRPort curr = ario;
//		if (ario.IsInput()) {
//			while (curr.GetSource() != null) {
//				curr = curr.GetSource();
//			}
//		} else {
//			while (curr.GetTarget() != null) {
//				curr = curr.GetTarget();
//			}
//		}
//		VarAcc var_acc = curr.GetVarAcc();
//		if (var_acc != null) {
//			return var_acc.GetAccInterface();
//		}
//		return null;
//	}
	
	public static SRPort GetSRPortFromCompoInstRef(InfoManager im, Object p_prov, Object c_prov) {
		String source_c = StringHelper.GetProxyValidPath(c_prov.toString());
		String source = StringHelper.GetProxyValidPath(p_prov.toString());
		
//		Assert.isTrue(im.GetSwCompoInst(source_c).GetType() == im.GetSwCompo(StringHelper.NonLastPartInPath(source)));
		
		ArElement ae = im.path_map.get(source_c);
		Assert.isTrue(ae instanceof SwCompo);
		SwCompo inst = (SwCompo) ae;
//		im.GetSwCompoInst(source_c);
		SRPort p_s = inst.FindPort(StringHelper.LastPartInPath(source));
		
		return p_s;
	}
	
}
