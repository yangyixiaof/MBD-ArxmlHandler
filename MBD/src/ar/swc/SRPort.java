package ar.swc;

import org.eclipse.core.runtime.Assert;

import ar.ArElement;
import ar.intf.ArSRInterfaceWithDataElementProperty;
import util.StringHelper;

public class SRPort extends ArElement {
	
	boolean is_input = false;
	
//	ArrayList<ArDataElement> a_des = new ArrayList<ArDataElement>();
	
//	ArSenderReceiverInterface asri = null;
	ArSRInterfaceWithDataElementProperty asriwpde = null;
	
//	VarAcc v_acc = null;
	
	SwCompo relation_base = null;
	
//	SwCompo swc = null;
	
	SRPort source = null;
	
	SRPort target = null;
	
	public SRPort(String name, boolean is_input) {// , SwCompo swc
		super(name);
//		this.swc = swc;
		this.is_input = is_input;
	}
	
	public void SetArSRInterfaceWithDataElementProperty(ArSRInterfaceWithDataElementProperty asriwpde) {
		this.asriwpde = asriwpde;
	}
	
	public ArSRInterfaceWithDataElementProperty GetArSRInterfaceWithPartDataElements() {
		return asriwpde;
	}
	
//	public void AddAllInterfaceDataElements(ArrayList<ArDataElement> ades) {
//		this.a_des.addAll(ades);
//	}
//	
//	public ArrayList<ArDataElement> GetAllInterfaceDataElements() {
//		return a_des;
//	}
	
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

//	public void SetVarAcc(VarAcc v_acc) {
//		Assert.isTrue(this.v_acc == null && v_acc != null);
//		this.v_acc = v_acc;
//	}
	
//	public VarAcc GetVarAcc() {
//		return v_acc;
//	}
	
	public boolean IsInput() {
		return is_input;
	}

//	@Override
//	public String ToScript() {
//		boolean is_input_port = IsInput();
//		String func = "addOutport";
//		if (is_input_port) {
//			func = "addInport";
//		}
//		String ctype = "Unknown";
//		ArSenderReceiverInterface io = ArUtil.TraceToTerminal(this);
//		if (io != null) {
//			Assert.isTrue(false, "The io type not handled!");
////			ctype = io.GetCType();
//		}
//		return (func + "(\"" + StringHelper.NonLastPartInPath(gen_path) + "\",\"/" + name + "\",\"" + ctype + "\");");
//	}
	
	public String ToParameterDeclaration() {
		String srport_type = "Unknown";
		if (GetArSRInterfaceWithPartDataElements() != null) {
			srport_type = GetArSRInterfaceWithPartDataElements().ToTypeString();
		}
		return ("[" + "\"" + GetName() + "\"" + " " + "\"" + srport_type + "\" " + "\"0\"" + "]");
	}
	
	public String ToRelationScript() {
		if (target != null) {
//			String res = "addRelation(\"" + relation_base.GetGeneratedPath() + "\",\"" + StringHelper.TrimPrefix(GetGeneratedPath(), relation_base.GetGeneratedPath()) + "\",\"" + StringHelper.TrimPrefix(target.GetGeneratedPath(), relation_base.GetGeneratedPath()) + "\");";
			String src_actor = null;
			String src_port = null;
			String tgt_actor = null;
			String tgt_port = null;
			String relative_src = StringHelper.TrimPrefix(GetGeneratedPath(), relation_base.GetGeneratedPath()+"/");
			if (GetParent() == relation_base) {
				src_actor = relation_base.GetName() + "";
				src_port = relative_src;
			} else {
				Assert.isTrue(StringHelper.CountCharacterInString(relative_src, '/') == 1, "relative_src:" + relative_src);
				if (StringHelper.CountCharacterInString(relative_src, '/') == 1) {
					// do nothing.
				}
				src_actor = StringHelper.NonLastPartInPath(relative_src);
				src_port = StringHelper.LastPartInPath(relative_src);
			}
			
			String relative_tgt = StringHelper.TrimPrefix(target.GetGeneratedPath(), relation_base.GetGeneratedPath()+"/");
			if (target.GetParent() == relation_base) {
				tgt_actor = relation_base.GetName() + "_ret";
				tgt_port = relative_tgt;
			} else {
				Assert.isTrue(StringHelper.CountCharacterInString(relative_tgt, '/') == 1, "relative_src:" + relative_tgt);
				if (StringHelper.CountCharacterInString(relative_tgt, '/') == 1) {
					// do nothing.
				}
				tgt_actor = StringHelper.NonLastPartInPath(relative_tgt);
				tgt_port = StringHelper.LastPartInPath(relative_tgt);
			}
			
			String res = "AddRelation(\"" + relation_base.GetGeneratedPath() + "\",\"" + src_actor + "\",\"" + src_port + "\",\"" + tgt_actor + "\",\"" + tgt_port + "\");";
			return res;
		}
		return "";
	}

	@Override
	public Object ArClone() {
		Assert.isTrue(relation_base == null && source == null && target == null);
//		Assert.isTrue(a_de != null);
		
		SRPort p = new SRPort(name, is_input);
//		p.SetArSenderReceiverInterface(asri);
		p.SetArSRInterfaceWithDataElementProperty(asriwpde);
//		p.AddAllInterfaceDataElements(a_des);
//		if (v_acc != null) {
//			p.SetVarAcc((VarAcc) v_acc.ArClone());
//		}
		return p;
	}
	
}
