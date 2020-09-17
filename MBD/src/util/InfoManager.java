package util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import ar.ArElement;
import ar.intf.ArDataElement;
import ar.intf.ArInterface;
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
	
	/**
	 * first phase, basic construction. 
	 * @param resource
	 */
	public InfoManager(Autosar40ResourceImpl resource) {
		try {
			EList<EObject> acs = resource.getContents();
			for (EObject ac : acs) {
				VisitEObject(ac, "BasicElementBuildPre", "BasicElementBuildPost");
				GenerateAllPathsForAllElements();
				VisitEObject(ac, "BasicLinkBuildPre", null);
				VisitEObject(ac, "SwComponentPrototypeBuildPre", null);
				GenerateAllPathsForAllElements();
				VisitEObject(ac, "SwcConnectorBuildPre", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		if (invoke_method_pre != null) {
			Method md = this.getClass().getMethod(invoke_method_pre, EObject.class);
			md.invoke(this, root);
		}
		
		EList<EObject> ecnts = root.eContents();
		for (EObject ecnt : ecnts) {
			VisitEObject(ecnt, invoke_method_pre, invoke_method_post);
		}
		
		if (invoke_method_post != null) {
			Method md = this.getClass().getMethod(invoke_method_post, EObject.class);
			md.invoke(this, root);
		}
	}
	
	protected void BasicElementBuildPre(EObject root) {
		if (root instanceof Referrable) {
			Referrable rrf = (Referrable) root;
			
			String name = rrf.getShortName();
			
			ArElement ae = null;
			
			if (root instanceof ImplementationDataType) {
				ImplementationDataType idt = (ImplementationDataType) root;
				String s_name = idt.getShortName();
				ae = new ArDataType(s_name);
			} else if (root instanceof SwBaseType) {
				SwBaseType sbt = (SwBaseType) root;
				String s_name = sbt.getShortName();
				ae = new ArBaseDataType(s_name);
			} else if (root instanceof PortInterface) {
				if (root instanceof ClientServerInterface) {
					ClientServerInterface csi = (ClientServerInterface) root;
					ae = new ArClientServerInterface(csi.getShortName());
					EList<ClientServerOperation> ops = csi.getOperations();
					for (ClientServerOperation op : ops) {
						ArCsOperation aco = new ArCsOperation(op.getShortName());
						((ArClientServerInterface) ae).AddOperation(aco);
						EList<ArgumentDataPrototype> args = op.getArguments();
						for (ArgumentDataPrototype arg : args) {
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
							ArCsArgument aca = new ArCsArgument(arg.getShortName(), direct_str);
							aco.AddArgument(aca);
						}
					}
				} else if (root instanceof SenderReceiverInterface) {
					PortInterface pii = (PortInterface) root;
					System.out.println("port interface class:" + pii.getClass());
					ae = new ArInterface(pii.getShortName());
				}
			} else if (root instanceof SwComponentType) {
				Assert.isTrue(root instanceof Referrable);
				ae = new SwCompo(((Referrable) root).getShortName());
			} else if (root instanceof PortPrototype) {
				System.out.println("port class:" + root.getClass());
				PortPrototype pp = (PortPrototype) root;
				ae = HandlePortPrototype(pp);
			} else if (root instanceof SwcInternalBehavior) {
				ae = new SwcBehaviour(name);
			} else if (root instanceof InitEvent) {
				// not handle
			} else if (root instanceof TimingEvent) {
				// not handle
			} else if (root instanceof RunnableEntity) {
				RunnableEntity rei = (RunnableEntity) root;
				ae = new RunEnt(rei.gGetShortName());
			} else if (root instanceof SwConnector) {
				// do nothing.
			} else if (root instanceof SwComponentPrototype) {
				// do nothing.
			} else {
				ae = new ArElement(name);
			}
			
			if (ae != null) {
				eobject_map.put(root, ae);
				
				EObject ans = SearchForNearestAncestorWithArElement(root);
				if (ans == root) {
					roots.add(ae);
				} else {
					Assert.isTrue(ans == root.eContainer());
					ArElement ans_ae = eobject_map.get(ans);
					ans_ae.AddChildElement(ae);
				}
			}
		}
	}
	
	protected void BasicElementBuildPost(EObject root) {
		ArElement ae = eobject_map.get(root);
		if (ae != null) {
			if (ae instanceof SwComponentType) {
				((SwCompo) ae).CardChildsAccordingToTypes();
			}
		}
	}
	
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
	protected void BasicLinkBuildPre(EObject root) {
		
		if (root instanceof ImplementationDataType) {
			ImplementationDataType idt = (ImplementationDataType) root;
			ArDataType adt = (ArDataType) eobject_map.get(idt);
			SwDataDefProps sddp = idt.getSwDataDefProps();
			EList<SwDataDefPropsConditional> sddpvs = sddp.getSwDataDefPropsVariants();
			for (SwDataDefPropsConditional sddpv : sddpvs) {
				EObject eo = sddpv.getBaseType();
				ArBaseDataType abdt = (ArBaseDataType) eobject_map.get(eo);
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
						ArDataType adt = (ArDataType) eobject_map.get(atp);
						
						ArCsArgument aca = (ArCsArgument) eobject_map.get(arg);
						aca.SetDataType(adt);
					}
				}
			} else if (root instanceof SenderReceiverInterface) {
				SenderReceiverInterface sri = (SenderReceiverInterface) root;
				ArInterface intf = (ArInterface) eobject_map.get(sri);
				EList<VariableDataPrototype> vdps = sri.getDataElements();
				for (VariableDataPrototype vdpi : vdps) {
//					String ts = type.toString();
//					String data_type_path = StringHelper.GetProxyValidPath(ts);
//					ArElement data_type_ele = path_map.get(data_type_path);
//					intf.SetType((ArDataType) data_type_ele);
					intf.AddDataElement(new ArDataElement(vdpi.getShortName(), (ArDataType) eobject_map.get(vdpi.getType())));
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
	}
	
	/**
	 * third phase, handle SwComponentPrototype and its links. 
	 */
	protected void SwComponentPrototypeBuildPre(EObject root) {
		
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
			ArElement ar_ele = eobject_map.get(scpi);
			ArElement par_ele = ar_ele.GetParent();
			SwCompo par_swc = (SwCompo) par_ele;
			SwComponentType swc_tp = scpi.getType();
			String swc_name = scpi.getShortName();
			String ref_swc_path = StringHelper.GetProxyValidPath(swc_tp.toString());
			SwCompo ar_ref_swc = (SwCompo) path_map.get(ref_swc_path);
//			SwCompo ar_ref_swc = am.GetSwCompo(ref_swc_path);
			ar_ref_swc.SetReferred();
			par_swc.AddDependency(ar_ref_swc);
			SwCompo copied_ar_ref_swc = (SwCompo) ArCloneUtil.CopyTreeWithRoot(ar_ref_swc);
			copied_ar_ref_swc.SetName(swc_name);
			par_swc.AddSwComponentProto(copied_ar_ref_swc);
		} else {
			// do nothing.
		}
	}
	
	/**
	 * forth phase, handle Connector and its links. 
	 */
	protected void SwcConnectorBuildPre(EObject root) {
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
	}
	
	private ArrayList<ArInterface> GetAllInterfaces() {
		ArrayList<ArInterface> ais = new ArrayList<ArInterface>();
		Collection<ArElement> vs = path_map.values();
		for (ArElement v : vs) {
			if (v instanceof ArInterface) {
				ArInterface ai = (ArInterface) v;
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
				if (sc.SatisfyDependencies(result)) {
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
		ArrayList<ArInterface> intfs = GetAllInterfaces();
		for (ArInterface intf : intfs) {
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
	public void GenerateAllPathsForAllElements() {
		for (ArElement root : roots) {
			GeneratePathForElement(root);
		}
	}
	
	private void GeneratePathForElement(ArElement root) {
		if (root.GetGeneratedPath() != null) {
			root.GeneratePath();
			path_map.put(root.GetGeneratedPath(), root);
		}
		ArrayList<ArElement> childs = root.GetAllChildElements();
		for (ArElement child : childs) {
			GeneratePathForElement(child);
		}
	}

	private EObject SearchForNearestAncestorWithArElement(EObject start) {
		EObject curr = start;
		while (true) {
			EObject par = curr.eContainer();
			if (par != null && eobject_map.containsKey(par)) {
				curr = par;
			} else {
				break;
			}
		}
		return curr;
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
				ArCsOperation aco = (ArCsOperation) eobject_map.get(cso);
				CSPort csp = (CSPort) eobject_map.get(pp);
				csp.SetCSOperation(aco);
			} else if (ppcs instanceof SenderComSpec) {
				SenderComSpec scs = (SenderComSpec) ppcs;
				AutosarDataPrototype de = scs.getDataElement();
				ArDataElement ade = (ArDataElement) eobject_map.get(de);
				SRPort srp = (SRPort) eobject_map.get(pp);
				srp.SetInterfaceDataElement(ade);
			}
		}
		for (RPortComSpec rpcs : rcs_l) {
			if (rpcs instanceof ClientComSpec) {
				ClientComSpec ccs = (ClientComSpec) rpcs;
				ClientServerOperation cso = ccs.getOperation();
				ArCsOperation aco = (ArCsOperation) eobject_map.get(cso);
				CSPort csp = (CSPort) eobject_map.get(pp);
				csp.SetCSOperation(aco);
			} else if (rpcs instanceof ReceiverComSpec) {
				ReceiverComSpec rcs = (ReceiverComSpec) rpcs;
				AutosarDataPrototype de = rcs.getDataElement();
				ArDataElement ade = (ArDataElement) eobject_map.get(de);
				SRPort srp = (SRPort) eobject_map.get(pp);
				srp.SetInterfaceDataElement(ade);
			}
		}
	}
	
}
