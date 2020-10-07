package util;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.ArSRInterfaceWithDataElementProperty;
import ar.swc.SRPort;
import ar.swc.SwCompo;

public class ArUtil {
	
	public static ArSRInterfaceWithDataElementProperty DiscoverPossibleArSRInterfaceWithPartDataElements(SRPort ario) {
		SRPort src = ario;
		SRPort tgt = ario;
		while (src.GetSource() != null) {
			src = src.GetSource();
		}
		while (tgt.GetTarget() != null) {
			tgt = tgt.GetTarget();
		}
		
		ArSRInterfaceWithDataElementProperty src_ad = src.GetArSRInterfaceWithPartDataElements();
		ArSRInterfaceWithDataElementProperty tgt_ad = tgt.GetArSRInterfaceWithPartDataElements();
		Assert.isTrue(!(src_ad != null && tgt_ad != null));
		
		ArSRInterfaceWithDataElementProperty f_ad = null;
		if (src_ad != null) {
			f_ad = src_ad;
		}
		if (tgt_ad != null) {
			f_ad = tgt_ad;
		}
		return f_ad;
	}
	
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
