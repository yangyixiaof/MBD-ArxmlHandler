package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import ar.ArElement;
import ar.intf.ArDataElement;
import ar.intf.ArSenderReceiverInterface;
import ar.intf.cs.ArClientServerInterface;
import ar.intf.cs.ArCsArgument;
import ar.intf.cs.ArCsOperation;
import ar.swc.CSPort;
import ar.swc.RunEnt;
import ar.swc.SRPort;
import ar.swc.SwCompo;
import ar.swc.SwcBehaviour;
import ar.type.ArBaseDataType;
import ar.type.ArDataType;
import ar.util.ArCloneUtil;
import autosar40.commonstructure.basetypes.SwBaseType;
import autosar40.commonstructure.datadefproperties.SwDataDefProps;
import autosar40.commonstructure.datadefproperties.SwDataDefPropsConditional;
import autosar40.commonstructure.implementationdatatypes.ImplementationDataType;
import autosar40.genericstructure.generaltemplateclasses.identifiable.Referrable;
import autosar40.genericstructure.generaltemplateclasses.primitivetypes.ArgumentDirectionEnum;
import autosar40.swcomponent.communication.ClientComSpec;
import autosar40.swcomponent.communication.PPortComSpec;
import autosar40.swcomponent.communication.RPortComSpec;
import autosar40.swcomponent.communication.ReceiverComSpec;
import autosar40.swcomponent.communication.SenderComSpec;
import autosar40.swcomponent.communication.ServerComSpec;
import autosar40.swcomponent.components.AbstractProvidedPortPrototype;
import autosar40.swcomponent.components.AbstractRequiredPortPrototype;
import autosar40.swcomponent.components.PPortPrototype;
import autosar40.swcomponent.components.PRPortPrototype;
import autosar40.swcomponent.components.PortPrototype;
import autosar40.swcomponent.components.RPortPrototype;
import autosar40.swcomponent.components.SwComponentType;
import autosar40.swcomponent.composition.AssemblySwConnector;
import autosar40.swcomponent.composition.DelegationSwConnector;
import autosar40.swcomponent.composition.SwComponentPrototype;
import autosar40.swcomponent.composition.SwConnector;
import autosar40.swcomponent.composition.instancerefs.PPortInCompositionInstanceRef;
import autosar40.swcomponent.composition.instancerefs.PortInCompositionTypeInstanceRef;
import autosar40.swcomponent.composition.instancerefs.RPortInCompositionInstanceRef;
import autosar40.swcomponent.datatype.dataprototypes.AutosarDataPrototype;
import autosar40.swcomponent.datatype.dataprototypes.VariableDataPrototype;
import autosar40.swcomponent.datatype.datatypes.AutosarDataType;
import autosar40.swcomponent.portinterface.ArgumentDataPrototype;
import autosar40.swcomponent.portinterface.ClientServerInterface;
import autosar40.swcomponent.portinterface.ClientServerOperation;
import autosar40.swcomponent.portinterface.PortInterface;
import autosar40.swcomponent.portinterface.SenderReceiverInterface;
import autosar40.swcomponent.swcinternalbehavior.RunnableEntity;
import autosar40.swcomponent.swcinternalbehavior.SwcInternalBehavior;
import autosar40.swcomponent.swcinternalbehavior.rteevents.InitEvent;
import autosar40.swcomponent.swcinternalbehavior.rteevents.TimingEvent;
import autosar40.util.Autosar40ResourceImpl;

public class InfoManager {
	
	HashMap<EObject, ArElement> eobject_map = new HashMap<EObject, ArElement>();
	ArrayList<ArElement> roots = new ArrayList<ArElement>();
	TreeMap<String, ArElement> path_map = new TreeMap<String, ArElement>();
	
	Map<SwCompo, ArrayList<SwCompo>> swc_depend = new HashMap<SwCompo, ArrayList<SwCompo>>();
	
	/**
	 * first phase, basic construction. 
	 * @param resource
	 */
	public InfoManager(Autosar40ResourceImpl resource) {
		try {
			EList<EObject> acs = resource.getContents();
			for (EObject ac : acs) {
				VisitEObject(ac, "BasicElementBuildPre", null);// "BasicElementBuildPost"
				GenerateAllPathsForAllElements();
				VisitEObject(ac, "BasicLinkBuildPre", null);
				VisitEObject(ac, "SwComponentPrototypeBuildPre", null);
				GenerateAllPathsForAllElements();
				VisitEObject(ac, "SwcConnectorBuildPre", null);
				IdentifyAllTypesForAllSRPorts();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof InvocationTargetException) {
				InvocationTargetException ite = (InvocationTargetException) e;
				Throwable cause = ite.getCause();
				cause.printStackTrace();
			}
		}
	}
	
//	private void EnterRootLevel(Autosar40ResourceImpl resource, String invoke_method) throws Exception {
//		Method md = this.getClass().getMethod(invoke_method, EObject.class);
//		EList<EObject> acs = resource.getContents();
//		for (EObject ac : acs) {
//			Assert.isTrue(!(ac instanceof ARPackage));
//			EList<EObject> ecnts = ac.eContents();
//			for (EObject ecnt : ecnts) {
////				System.out.println("b_ecnt:" + ecnt);
//				if (ecnt instanceof ARPackage) {
//					Object a_root = md.invoke(this, ecnt);
//					if (a_root instanceof ArElement) {
////						Assert.isTrue(a_root != null);
//						roots.add((ArElement) a_root);
////						System.out.println("ecnt:" + ecnt);
//					}
//				}
//			}
//		}
//	}
	
	private void VisitEObject(EObject root, final String invoke_method_pre, final String invoke_method_post) throws Exception {
		boolean ctn = false;
		if (invoke_method_pre != null) {
			Method md = this.getClass().getMethod(invoke_method_pre, EObject.class);
			ctn = (boolean) md.invoke(this, root);
		}
		
		if (ctn) {
			EList<EObject> ecnts = root.eContents();
			for (EObject ecnt : ecnts) {
				VisitEObject(ecnt, invoke_method_pre, invoke_method_post);
			}
		}
		
		if (invoke_method_post != null) {
			Method md = this.getClass().getMethod(invoke_method_post, EObject.class);
			md.invoke(this, root);
		}
	}
	
	public boolean BasicElementBuildPre(EObject root) {
		if (root instanceof Referrable) {
			Referrable rrf = (Referrable) root;
			
			String name = rrf.getShortName();
			
			ArElement ae = null;
			
			if (root instanceof ImplementationDataType) {
				ImplementationDataType idt = (ImplementationDataType) root;
				String s_name = idt.getShortName();
				ae = new ArDataType(s_name);
//				System.out.println("==##sri name:" + ((Referrable) idt.eContainer()).getShortName() + "#idt_name:" + idt.toString() + "#idt_addr:" + idt.hashCode());
			} else if (root instanceof SwBaseType) {
				SwBaseType sbt = (SwBaseType) root;
				String s_name = sbt.getShortName();
				ae = new ArBaseDataType(s_name);
			} else if (root instanceof ClientServerInterface) {
				ClientServerInterface csi = (ClientServerInterface) root;
				ae = new ArClientServerInterface(csi.getShortName());
			} else if (root instanceof ClientServerOperation) {
				ClientServerOperation op = (ClientServerOperation) root;
				ae = new ArCsOperation(op.getShortName());
				ArClientServerInterface acsi = (ArClientServerInterface) eobject_map.get(root.eContainer());
				acsi.AddOperation((ArCsOperation) ae);
			} else if (root instanceof ArgumentDataPrototype) {
				ArgumentDataPrototype arg = (ArgumentDataPrototype) root;
				ArgumentDirectionEnum direct = arg.getDirection();
				String direct_str = null;
				switch (direct.getValue()) {
				case ArgumentDirectionEnum.IN_VALUE:
					direct_str = "in";
					break;
				case ArgumentDirectionEnum.OUT_VALUE:
					direct_str = "out";
					break;
				case ArgumentDirectionEnum.INOUT_VALUE:
					direct_str = "inout";
					break;
				}
				ae = new ArCsArgument(arg.getShortName(), direct_str);
				ArCsOperation aco = (ArCsOperation) eobject_map.get(root.eContainer());
				aco.AddArgument((ArCsArgument) ae);
			} else if (root instanceof VariableDataPrototype) {
				SenderReceiverInterface sri = (SenderReceiverInterface) root.eContainer();
				ArSenderReceiverInterface intf = (ArSenderReceiverInterface) eobject_map.get(sri);
				ae = new ArDataElement(name);
				intf.AddDataElement((ArDataElement) ae);
			} else if (root instanceof SenderReceiverInterface) {
				PortInterface pii = (PortInterface) root;
//				System.out.println("port interface class:" + pii.getClass());
				ae = new ArSenderReceiverInterface(pii.getShortName());
			} else if (root instanceof SwComponentType) {
				Assert.isTrue(root instanceof Referrable);
				ae = new SwCompo(((Referrable) root).getShortName());
			} else if (root instanceof PortPrototype) {
//				System.out.println("port class:" + root.getClass());
				PortPrototype pp = (PortPrototype) root;
				ae = HandlePortPrototype(pp);
				if (ae instanceof SRPort) {
					SwCompo sc = (SwCompo) eobject_map.get(root.eContainer());
					sc.AddSRPort((SRPort) ae);
				} else if (ae instanceof CSPort) {
					SwCompo sc = (SwCompo) eobject_map.get(root.eContainer());
					sc.AddCSPort((CSPort) ae);
				} else {
					Assert.isTrue(false);
				}
			} else if (root instanceof SwcInternalBehavior) {
				ae = new SwcBehaviour(name);
				SwCompo sc = (SwCompo) eobject_map.get(root.eContainer());
				sc.AddSwcBehaviour((SwcBehaviour) ae);
			} else if (root instanceof InitEvent) {
				return false;
			} else if (root instanceof TimingEvent) {
				return false;
			} else if (root instanceof RunnableEntity) {
				RunnableEntity rei = (RunnableEntity) root;
				ae = new RunEnt(rei.gGetShortName());
				SwcInternalBehavior sri = (SwcInternalBehavior) root.eContainer();
				SwcBehaviour swc_b = (SwcBehaviour) eobject_map.get(sri);
				swc_b.AddRunnableEntity((RunEnt) ae);
			} else if (root instanceof SwConnector) {
				return false;
			} else if (root instanceof SwComponentPrototype) {
				return false;
			} else {
				ae = new ArElement(name);
			}
			
			if (ae != null) {
				eobject_map.put(root, ae);
				
				EObject ans = SearchForNearestAncestorWithArElement(root);
				if (ans == null) {
					roots.add(ae);
				} else {
					Assert.isTrue(ans == root.eContainer(), "ans:" + ans + "#root.eContainer():" + root.eContainer() + "#root.eContainer().eContainer():" + root.eContainer().eContainer());
					ArElement ans_ae = eobject_map.get(ans);
					Assert.isTrue(ans_ae != null);
					ans_ae.AddChildElement(ae);
				}
			}
		}
		
		return true;
	}
	
//	public void BasicElementBuildPost(EObject root) {
//		ArElement ae = eobject_map.get(root);
//		if (ae != null) {
//			if (ae instanceof SwComponentType) {
//				((SwCompo) ae).CardChildsAccordingToTypes();
//			}
//		}
//	}
	
//	public String GetFullPath(EObject eo) {
//		Assert.isTrue(info.containsKey(eo));
//		String res = info.get(eo);
////		String curr_name = curr.getShortName();
////		res += "/" + curr_name;
////		ARPackage eoo = curr.getARPackage();
////		System.out.println("short_name:" + eoo.getShortName());
//		return res;
//	}
	
	/**
	 * second phase, handle links except SwComponentPrototype and Connector. 
	 */
	public boolean BasicLinkBuildPre(EObject root) {
		
		if (root instanceof ImplementationDataType) {
			ImplementationDataType idt = (ImplementationDataType) root;
			ArDataType adt = (ArDataType) eobject_map.get(idt);
			SwDataDefProps sddp = idt.getSwDataDefProps();
			EList<SwDataDefPropsConditional> sddpvs = sddp.getSwDataDefPropsVariants();
			for (SwDataDefPropsConditional sddpv : sddpvs) {
				EObject eo = sddpv.getBaseType();
				Assert.isTrue(eo.eIsProxy());
				String abdt_path = StringHelper.GetProxyValidPath(eo.toString());
				ArBaseDataType abdt = (ArBaseDataType) path_map.get(abdt_path);
//				ArBaseDataType abdt = (ArBaseDataType) eobject_map.get(eo);
				adt.AddBaseDataType(abdt);
			}
		} else if (root instanceof SwBaseType) {
			// not handle
		} else if (root instanceof PortInterface) {
			if (root instanceof ClientServerInterface) {
				ClientServerInterface csi = (ClientServerInterface) root;
				EList<ClientServerOperation> ops = csi.getOperations();
				for (ClientServerOperation op : ops) {
					EList<ArgumentDataPrototype> args = op.getArguments();
					for (ArgumentDataPrototype arg : args) {
						AutosarDataType atp = arg.getType();
						Assert.isTrue(atp.eIsProxy());
						String adt_path = StringHelper.GetProxyValidPath(atp.toString());
						ArDataType adt = (ArDataType) path_map.get(adt_path);
//						ArDataType adt = (ArDataType) eobject_map.get(atp);
						
						ArCsArgument aca = (ArCsArgument) eobject_map.get(arg);
						aca.SetDataType(adt);
					}
				}
			} else if (root instanceof SenderReceiverInterface) {
				SenderReceiverInterface sri = (SenderReceiverInterface) root;
				EList<VariableDataPrototype> vdps = sri.getDataElements();
				for (VariableDataPrototype vdpi : vdps) {
//					String ts = type.toString();
//					String data_type_path = StringHelper.GetProxyValidPath(ts);
//					ArElement data_type_ele = path_map.get(data_type_path);
//					intf.SetType((ArDataType) data_type_ele);
//					intf.AddDataElement(new ArDataElement(vdpi.getShortName(), (ArDataType) eobject_map.get(vdpi.getType())));
					ImplementationDataType idt = (ImplementationDataType) vdpi.getType();
//					System.out.println("==##is_proxy:" + idt.eIsProxy() + "#sri name:" + sri.getShortName() + "#idt_name:" + idt.toString() + "#idt_addr:" + idt.hashCode());
					String adt_path = StringHelper.GetProxyValidPath(idt.toString());
					ArDataType adt = (ArDataType) path_map.get(adt_path);
//					ArDataType adt = (ArDataType) eobject_map.get(idt);
					Assert.isTrue(adt != null);
					ArDataElement ade = (ArDataElement) eobject_map.get(vdpi);
					ade.SetDataType(adt);
				}
			} else {
				Assert.isTrue(false, "Unsupportted port interface!");
			}
		} else if (root instanceof SwComponentType) {
			// not handle
		} else if (root instanceof PortPrototype) {
			// not handle
		} else if (root instanceof SwcInternalBehavior) {
			// not handle
		} else if (root instanceof InitEvent) {
			// not handle
		} else if (root instanceof TimingEvent) {
			// not handle
		} else if (root instanceof RunnableEntity) {
			// not handle
		} else if (root instanceof SwConnector) {
			// do nothing.
		} else if (root instanceof SwComponentPrototype) {
			// do nothing.
		} else {
			// do nothing.
		}
		
		return true;
	}
	
	/**
	 * third phase, handle SwComponentPrototype and its links. 
	 */
	public boolean SwComponentPrototypeBuildPre(EObject root) {
		
		if (root instanceof PortInterface) {
			// not handle
		} else if (root instanceof SwComponentType) {
			// not handle
		} else if (root instanceof PortPrototype) {
			// not handle
		} else if (root instanceof SwcInternalBehavior) {
			// not handle
		} else if (root instanceof InitEvent) {
			// not handle
		} else if (root instanceof TimingEvent) {
			// not handle
		} else if (root instanceof RunnableEntity) {
			// not handle
		} else if (root instanceof SwConnector) {
			// do nothing.
		} else if (root instanceof SwComponentPrototype) {
			SwComponentPrototype scpi = (SwComponentPrototype) root;
//			ArElement ar_ele = eobject_map.get(scpi);
//			ArElement par_ele = ar_ele.GetParent();
//			SwCompo par_swc = (SwCompo) par_ele;
			SwCompo par_swc = (SwCompo) eobject_map.get(root.eContainer());
			SwComponentType swc_tp = scpi.getType();
			Assert.isTrue(swc_tp.eIsProxy());
			String swc_name = scpi.getShortName();
			String ref_swc_path = StringHelper.GetProxyValidPath(swc_tp.toString());
			SwCompo ar_ref_swc = (SwCompo) path_map.get(ref_swc_path);
//			SwCompo ar_ref_swc = am.GetSwCompo(ref_swc_path);
			ar_ref_swc.SetReferred(true);
			if (!swc_depend.containsKey(par_swc)) {
				swc_depend.put(par_swc, new ArrayList<SwCompo>());
			}
			swc_depend.get(par_swc).add(ar_ref_swc);
			SwCompo copied_ar_ref_swc = (SwCompo) ArCloneUtil.CopyTreeWithRoot(ar_ref_swc);
			copied_ar_ref_swc.SetName(swc_name);
			par_swc.AddSwComponentProto(copied_ar_ref_swc);
		} else {
			// do nothing.
		}
		
		return true;
	}
	
	/**
	 * forth phase, handle Connector and its links. 
	 */
	public boolean SwcConnectorBuildPre(EObject root) {
		if (root instanceof PortInterface) {
			// not handle
		} else if (root instanceof SwComponentType) {
			// not handle
		} else if (root instanceof PortPrototype) {
			// not handle
		} else if (root instanceof SwcInternalBehavior) {
			// not handle
		} else if (root instanceof InitEvent) {
			// not handle
		} else if (root instanceof TimingEvent) {
			// not handle
		} else if (root instanceof RunnableEntity) {
			// not handle
		} else if (root instanceof SwConnector) {
			if (root instanceof AssemblySwConnector) {
				AssemblySwConnector asci = (AssemblySwConnector) root;
				PPortInCompositionInstanceRef prov = asci.getProvider();
				RPortInCompositionInstanceRef req = asci.getRequester();
				
				AbstractProvidedPortPrototype p_prov = prov.getTargetPPort();
				SwComponentPrototype c_prov = prov.getContextComponent();
				
				AbstractRequiredPortPrototype p_req = req.getTargetRPort();
				SwComponentPrototype c_req = req.getContextComponent();
				
				SRPort p_s = ArUtil.GetSRPortFromCompoInstRef(this, p_prov, c_prov);
				SRPort p_t = ArUtil.GetSRPortFromCompoInstRef(this, p_req, c_req);
				
				EObject ctn_swc = root.eContainer();
				SwCompo sc = (SwCompo) eobject_map.get(ctn_swc);
				
				p_s.SetTarget(p_t, sc);
			} else {
				if (root instanceof DelegationSwConnector) {
					DelegationSwConnector dsci = (DelegationSwConnector) root;
					PortInCompositionTypeInstanceRef inner = dsci.getInnerPort();
					PortPrototype outer = dsci.getOuterPort();
					
					SRPort p_s = null;
					SRPort p_t = null;
					
					if (inner instanceof RPortInCompositionInstanceRef) {
						RPortInCompositionInstanceRef rpref = (RPortInCompositionInstanceRef) inner;
						AbstractRequiredPortPrototype rport = rpref.getTargetRPort();
						SwComponentPrototype cport = rpref.getContextComponent();
						String source = StringHelper.GetProxyValidPath(outer.toString());
						p_s = (SRPort) path_map.get(source);
						p_t = ArUtil.GetSRPortFromCompoInstRef(this, rport, cport);
					} else {
						Assert.isTrue(inner instanceof PPortInCompositionInstanceRef);
						PPortInCompositionInstanceRef ppicref = (PPortInCompositionInstanceRef) inner;
						AbstractProvidedPortPrototype pport = ppicref.getTargetPPort();
						SwComponentPrototype cport = ppicref.getContextComponent();
						p_s = ArUtil.GetSRPortFromCompoInstRef(this, pport, cport);
						String dest = StringHelper.GetProxyValidPath(outer.toString());
						p_t = (SRPort) path_map.get(dest);
					}
					
					EObject ctn_swc = root.eContainer();
					SwCompo sc = (SwCompo) eobject_map.get(ctn_swc);
					
					p_s.SetTarget(p_t, sc);
				} else {
					Assert.isTrue(false);
				}
			}
		} else if (root instanceof SwComponentPrototype) {
			// do nothing.
		} else {
			// do nothing.
		}
		
		return true;
	}
	
	private ArrayList<ArSenderReceiverInterface> GetAllInterfaces() {
		ArrayList<ArSenderReceiverInterface> ais = new ArrayList<ArSenderReceiverInterface>();
		Collection<ArElement> vs = path_map.values();
		for (ArElement v : vs) {
			if (v instanceof ArSenderReceiverInterface) {
				ArSenderReceiverInterface ai = (ArSenderReceiverInterface) v;
				ais.add(ai);
			}
		}
		return ais;
	}
	
	private ArrayList<SwCompo> GetAllSwComposNotProtoWithTopology() {
		ArrayList<SwCompo> ar_swcs = new ArrayList<SwCompo>();
		Collection<ArElement> vs = path_map.values();
		for (ArElement v : vs) {
			if (v instanceof SwCompo) {
				SwCompo sw = (SwCompo) v;
				if (!sw.IsProto()) {
					ar_swcs.add(sw);
				}
			}
		}
		
		ArrayList<SwCompo> ini = new ArrayList<SwCompo>();
		ArrayList<SwCompo> result = new ArrayList<SwCompo>();
		
		ini.addAll(ar_swcs);
		while (ini.size() > 0) {
			int ini_len = ini.size();
			for (int i=0;i<ini_len;i++) {
				SwCompo sc = ini.get(i);
				if (SatisfyDependencies(sc, result)) {
					result.add(sc);
					ini.remove(sc);
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * generate script according to instructions
	 * @return
	 */
	public String ToScript() {
		String result = "";
		ArrayList<ArSenderReceiverInterface> intfs = GetAllInterfaces();
		if (intfs.size() > 0) {
			result += "AddModelPage(\"StructPage\",\"StructModelPage\");";
		}
		for (ArSenderReceiverInterface intf : intfs) {
			result += intf.ToScript();
		}
		ArrayList<SwCompo> rs = GetAllSwComposNotProtoWithTopology();
		for (SwCompo swc : rs) {
			result += swc.ToScript();
		}
		for (SwCompo swc : rs) {
			result += swc.ToRelationScript();
		}
		return result;
	}
	
	/**
	 * utility functions, generate all paths for all elements. 
	 */
	private void GenerateAllPathsForAllElements() {
		for (ArElement root : roots) {
			GeneratePathForElement(root);
		}
	}
	
	private void GeneratePathForElement(ArElement root) {
		if (root.GetGeneratedPath() == null) {
			root.GeneratePath();
			path_map.put(root.GetGeneratedPath(), root);
		}
		ArrayList<ArElement> childs = root.GetAllChildElements();
		for (ArElement child : childs) {
			GeneratePathForElement(child);
		}
	}
	
	private void IdentifyAllTypesForAllSRPorts() {
		for (ArElement root : roots) {
			IdentifyAllTypesForSRPort(root);
		}
	}
	
	private void IdentifyAllTypesForSRPort(ArElement root) {
		if (root instanceof SRPort) {
			SRPort srp = (SRPort) root;
			ArDataElement ade = ArUtil.DiscoverPossibleDataElement(srp);
			if (srp.GetInterfaceDataElement() == null) {
				srp.SetInterfaceDataElement(ade);
			}
		}
		ArrayList<ArElement> childs = root.GetAllChildElements();
		for (ArElement child : childs) {
			IdentifyAllTypesForSRPort(child);
		}
	}

	private EObject SearchForNearestAncestorWithArElement(EObject start) {
		EObject result = null;
		EObject curr = start;
		while (true) {
			EObject par = curr.eContainer();
			if (par == null || eobject_map.containsKey(par)) {
				if (par == null) {
					result = null;
				} else {
					result = par;
				}
				break;
			} else {
				curr = par;
			}
		}
//		Assert.isTrue(result != null && eobject_map.containsKey(result), "result start same:" + (start == result) + "#result:" + result + "#eo_map_res:" + eobject_map.containsKey(result));
		return result;
	}
	
	public ArElement HandlePortPrototype(PortPrototype pp) {
		EList<PPortComSpec> css = null;
		EList<RPortComSpec> rcs = null;
		if (pp instanceof PPortPrototype) {
			PPortPrototype ppp = (PPortPrototype) pp;
			css = ppp.getProvidedComSpecs();
			
		} else if (pp instanceof RPortPrototype) {
			RPortPrototype rpp = (RPortPrototype) pp;
			rcs = rpp.getRequiredComSpecs();
			
		} else if (pp instanceof PRPortPrototype) {
			PRPortPrototype prpp = (PRPortPrototype) pp;
			css = prpp.getProvidedComSpecs();
			rcs = prpp.getRequiredComSpecs();
		}
		
		Boolean is_cs = null;
		Boolean is_r = null;
		if (css != null) {
			for (PPortComSpec ppcs : css) {
				if (ppcs instanceof ServerComSpec) {
					Assert.isTrue(is_cs == null || is_cs);
					is_cs = true;
				} else if (ppcs instanceof SenderComSpec) {
					Assert.isTrue(is_cs == null || !is_cs);
					is_cs = false;
				}
				Assert.isTrue(is_r == null || !is_r);
				is_r = false;
			}
		}
		if (rcs != null) {
			for (RPortComSpec rpcs : rcs) {
				if (rpcs instanceof ClientComSpec) {
					Assert.isTrue(is_cs == null || is_cs);
					is_cs = true;
				} else if (rpcs instanceof ReceiverComSpec) {
					Assert.isTrue(is_cs == null || !is_cs);
					is_cs = false;
				}
				Assert.isTrue(is_r == null || is_r);
				is_r = true;
			}
		}
		Assert.isTrue(is_cs != null);
		Assert.isTrue(is_r != null);
		ArElement ae = null;
		if (is_cs) {
			ae = new CSPort(pp.getShortName(), is_r);
		} else {
			ae = new SRPort(pp.getShortName(), is_r);
		}
		return ae;
	}
	
	public void HandlePortPrototypeLink(PortPrototype pp) {
		EList<PPortComSpec> css_l = null;
		EList<RPortComSpec> rcs_l = null;
		if (pp instanceof PPortPrototype) {
			PPortPrototype ppp = (PPortPrototype) pp;
			css_l = ppp.getProvidedComSpecs();
			
		} else if (pp instanceof RPortPrototype) {
			RPortPrototype rpp = (RPortPrototype) pp;
			rcs_l = rpp.getRequiredComSpecs();
			
		} else if (pp instanceof PRPortPrototype) {
			PRPortPrototype prpp = (PRPortPrototype) pp;
			css_l = prpp.getProvidedComSpecs();
			rcs_l = prpp.getRequiredComSpecs();
		}
		// Solved. Please handle var_proto first and then here, var_proto should be extracted as an element. 
		for (PPortComSpec ppcs : css_l) {
			if (ppcs instanceof ServerComSpec) {
				ServerComSpec scs = (ServerComSpec) ppcs;
				ClientServerOperation cso = scs.getOperation();
				Assert.isTrue(cso.eIsProxy());
				String aco_path = StringHelper.GetProxyValidPath(cso.toString());
				ArCsOperation aco = (ArCsOperation) path_map.get(aco_path);
				Assert.isTrue(aco != null);
//				ArCsOperation aco = (ArCsOperation) eobject_map.get(cso);
				CSPort csp = (CSPort) eobject_map.get(pp);
				csp.SetCSOperation(aco);
			} else if (ppcs instanceof SenderComSpec) {
				SenderComSpec scs = (SenderComSpec) ppcs;
				AutosarDataPrototype de = scs.getDataElement();
				Assert.isTrue(de.eIsProxy());
				String aco_path = StringHelper.GetProxyValidPath(de.toString());
				ArDataElement ade = (ArDataElement) path_map.get(aco_path);
				Assert.isTrue(ade != null);
//				ArDataElement ade = (ArDataElement) eobject_map.get(de);
				SRPort srp = (SRPort) eobject_map.get(pp);
				srp.SetInterfaceDataElement(ade);
			}
		}
		for (RPortComSpec rpcs : rcs_l) {
			if (rpcs instanceof ClientComSpec) {
				ClientComSpec ccs = (ClientComSpec) rpcs;
				ClientServerOperation cso = ccs.getOperation();
				Assert.isTrue(cso.eIsProxy());
				String aco_path = StringHelper.GetProxyValidPath(cso.toString());
				ArCsOperation aco = (ArCsOperation) path_map.get(aco_path);
				Assert.isTrue(aco != null);
//				ArCsOperation aco = (ArCsOperation) eobject_map.get(cso);
				CSPort csp = (CSPort) eobject_map.get(pp);
				csp.SetCSOperation(aco);
			} else if (rpcs instanceof ReceiverComSpec) {
				ReceiverComSpec rcs = (ReceiverComSpec) rpcs;
				AutosarDataPrototype de = rcs.getDataElement();
				Assert.isTrue(de.eIsProxy());
				String aco_path = StringHelper.GetProxyValidPath(de.toString());
				ArDataElement ade = (ArDataElement) path_map.get(aco_path);
				Assert.isTrue(ade != null);
//				ArDataElement ade = (ArDataElement) eobject_map.get(de);
				SRPort srp = (SRPort) eobject_map.get(pp);
				srp.SetInterfaceDataElement(ade);
			}
		}
	}
	
	private boolean SatisfyDependencies(SwCompo sc, ArrayList<SwCompo> result) {
		ArrayList<SwCompo> deps = swc_depend.get(sc);
		if (deps != null) {
			for (SwCompo dep : deps) {
				if (!result.contains(dep)) {
					return false;
				}
			}
		}
		return true;
	}
	
}
