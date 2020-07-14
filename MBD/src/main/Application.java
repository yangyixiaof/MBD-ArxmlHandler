package main;

import java.util.ArrayList;
import java.util.Map;

import org.artop.aal.common.resource.impl.AutosarResourceFactoryImpl;
import org.artop.aal.common.resource.impl.ExtendedAutosarResourceAdapter;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import ar.ArModel;
import ar.intf.ArInterface;
import ar.swc.Port;
import ar.swc.SwCompo;
import ar.swc.SwCompoInst;
import ar.swc.VarAcc;
import autosar40.autosartoplevelstructure.impl.AUTOSARImpl;
import autosar40.genericstructure.generaltemplateclasses.arpackage.ARPackage;
import autosar40.genericstructure.generaltemplateclasses.arpackage.impl.ARPackageImpl;
import autosar40.genericstructure.generaltemplateclasses.documentation.annotation.Annotation;
import autosar40.genericstructure.generaltemplateclasses.identifiable.Referrable;
import autosar40.swcomponent.components.AbstractProvidedPortPrototype;
import autosar40.swcomponent.components.AbstractRequiredPortPrototype;
import autosar40.swcomponent.components.PortPrototype;
import autosar40.swcomponent.components.SwComponentType;
import autosar40.swcomponent.components.impl.RPortPrototypeImpl;
import autosar40.swcomponent.composition.SwComponentPrototype;
import autosar40.swcomponent.composition.impl.AssemblySwConnectorImpl;
import autosar40.swcomponent.composition.impl.DelegationSwConnectorImpl;
import autosar40.swcomponent.composition.impl.SwComponentPrototypeImpl;
import autosar40.swcomponent.composition.instancerefs.PPortInCompositionInstanceRef;
import autosar40.swcomponent.composition.instancerefs.PortInCompositionTypeInstanceRef;
import autosar40.swcomponent.composition.instancerefs.RPortInCompositionInstanceRef;
import autosar40.swcomponent.composition.instancerefs.impl.PPortInCompositionInstanceRefImpl;
import autosar40.swcomponent.composition.instancerefs.impl.RPortInCompositionInstanceRefImpl;
import autosar40.swcomponent.datatype.dataprototypes.DataPrototype;
import autosar40.swcomponent.datatype.dataprototypes.impl.VariableDataPrototypeImpl;
import autosar40.swcomponent.datatype.datatypes.AutosarDataType;
import autosar40.swcomponent.portinterface.impl.PortInterfaceImpl;
import autosar40.swcomponent.swcinternalbehavior.dataelements.VariableAccess;
import autosar40.swcomponent.swcinternalbehavior.dataelements.impl.AutosarVariableRefImpl;
import autosar40.swcomponent.swcinternalbehavior.dataelements.instancerefsusage.impl.VariableInAtomicSWCTypeInstanceRefImpl;
import autosar40.swcomponent.swcinternalbehavior.impl.RunnableEntityImpl;
import autosar40.swcomponent.swcinternalbehavior.impl.SwcInternalBehaviorImpl;
import autosar40.swcomponent.swcinternalbehavior.rteevents.impl.InitEventImpl;
import autosar40.swcomponent.swcinternalbehavior.rteevents.impl.TimingEventImpl;
import autosar40.util.Autosar40ResourceFactoryImpl;
import autosar40.util.Autosar40ResourceImpl;
import util.ArUtil;
import util.InfoManager;
import util.StringHelper;
import util.TypeHelper;

@SuppressWarnings("restriction")
public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext arg0) throws Exception {
		System.out.println("MBD Eclipse Starts!");
		
		URI uri = HandleArgs(arg0);
		if (uri != null) {
			ArModel am = new ArModel();
			AutosarResourceFactoryImpl fact = new Autosar40ResourceFactoryImpl();
			Autosar40ResourceImpl resource = (Autosar40ResourceImpl) fact.createResource(uri);
			resource.load(null);
			EcoreUtil.resolveAll(resource);
//			TestAndPrint(resource);
			InfoManager im = new InfoManager(resource);
			ExtractAllInterfaces(resource, im, am);
			ExtractAllSwcs(resource, im, am);
			am.Process();
			String s = am.ToScript();
			s = s.replace(";", "\n");
			System.out.println("Final Result:" + s);
		}
		return IApplication.EXIT_OK;
	}
	
	public void Visit(Object root) {
		int children_size = 0;
		int anno_children_size = 0;
		int e_content_size = 0;
		if (root instanceof AUTOSARImpl) {
			AUTOSARImpl ai = (AUTOSARImpl) root;
			EList<ARPackage> packs = ai.getArPackages();
			children_size = packs.size();
			for (ARPackage pack : packs) {
				Visit(pack);
			}
		}
		if (root instanceof ARPackageImpl) {
			ARPackageImpl api = (ARPackageImpl) root;
			EList<ARPackage> packs = api.getArPackages();
			children_size = packs.size();
			for (ARPackage pack : packs) {
				Visit(pack);
			}
			EList<Annotation> annos = api.getAnnotations();
			anno_children_size = annos.size();
			EList<EObject> e_contents = api.eContents();
			e_content_size = e_contents.size();
			for (EObject ec : e_contents) {
				Visit(ec);
			}
		}
		System.out.println("EObject Class:" + root + "#children_size:" + children_size + "#anno_children_size:" + anno_children_size + "#e_content_size:" + e_content_size);
	}
	
	protected void ExtractAllInterfaces(Autosar40ResourceImpl resource, InfoManager im, ArModel am) {
		ArrayList<EObject> ints = new ArrayList<EObject>();
		TreeIterator<EObject> acs = resource.getAllContents();
		while (acs.hasNext()) {
			EObject ac = acs.next();
			String name = ac.getClass().getName();
//			System.out.println("name:" + name);
			boolean is_int = name.endsWith("InterfaceImpl");
			if (is_int) {
				ints.add(ac);
			}
		}
//		System.out.println("int size:" + ints.size());
//		autosar40.swcomponent.portinterface.impl.SenderReceiverInterfaceImpl sri = null;
//		sri.getShortName();
		for (EObject intf : ints) {
//			System.out.println("intf:" + intf);
			if (intf instanceof PortInterfaceImpl) {
				PortInterfaceImpl pii = (PortInterfaceImpl) intf;
				EList<EObject> ecnts = pii.eContents();
				String c_type = null;
				for (EObject ecnt : ecnts) {
//					System.out.println("ecnt:" + ecnt);
					if (ecnt instanceof VariableDataPrototypeImpl) {
						VariableDataPrototypeImpl vdpi = (VariableDataPrototypeImpl) ecnt;
						AutosarDataType type = vdpi.getType();
						String ts = type.toString();
						String data_type = StringHelper.GetProxyValidPath(ts);
						String r_type = StringHelper.LastPartInPath(data_type);
						c_type = TypeHelper.TranslateArxmlTypeToCType(r_type);
//						System.out.println("ar type:" + data_type);
					}
				}
				am.AddInterface(im.GetFullPath(pii), new ArInterface(pii.getShortName(), c_type, im.GetFullPath(pii)));
//				System.out.println("pii full path:" + im.GetFullPath(pii));
			}
		}
	}
	
	protected void ExtractAllSwcs(Autosar40ResourceImpl resource, InfoManager im, ArModel am) {
		ArrayList<EObject> swcs = new ArrayList<EObject>();
		TreeIterator<EObject> acs = resource.getAllContents();
		while (acs.hasNext()) {
			EObject ac = acs.next();
			String name = ac.getClass().getName();
//			System.out.println("name:" + name);
			boolean is_swc = name.endsWith("SwComponentTypeImpl");
			if (is_swc) {
				swcs.add(ac);
			}
		}
//		System.out.println("swc size:" + swcs.size());
		for (EObject swc : swcs) {
//			System.out.println("swc:" + swc);
			if (swc.getClass().getName().endsWith("SwComponentTypeImpl")) {
				Assert.isTrue(swc instanceof Referrable);
				SwCompo sc = new SwCompo(((Referrable) swc).getShortName());
				String swc_path = im.GetFullPath(swc);
				am.AddSwCompo(swc_path, sc);
				EList<EObject> ecnts = swc.eContents();
				for (EObject eo : ecnts) {
					if (eo.getClass().getName().endsWith("PortPrototypeImpl")) {
						boolean is_input = eo instanceof RPortPrototypeImpl;
						Referrable pp = (Referrable) eo;
						String s_name = pp.getShortName();
						Port p = new Port(s_name, is_input);// sc, 
						sc.AddPort(p);
//						am.AddPort(im.GetFullPath(pp), p);
					}
				}
			}
		}
		
		for (EObject swc : swcs) {
//			System.out.println("swc:" + swc);
			if (swc.getClass().getName().endsWith("SwComponentTypeImpl")) {
				String swc_path = im.GetFullPath(swc);
				SwCompo sc = am.GetSwCompo(swc_path);
				EList<EObject> ecnts = swc.eContents();
				for (EObject eo : ecnts) {
					if (eo.getClass().getName().endsWith("SwComponentPrototypeImpl")) {
						SwComponentPrototypeImpl scpi = (SwComponentPrototypeImpl) eo;
						SwComponentType swc_tp = scpi.getType();
						String swc_name = scpi.getShortName();
						String ref_swc_path = StringHelper.GetProxyValidPath(swc_tp.toString());
						SwCompo ar_ref_swc = am.GetSwCompo(ref_swc_path);
						ar_ref_swc.SetReferred();
						sc.AddDependency(ar_ref_swc);
						SwCompoInst swci = new SwCompoInst(swc_name, ar_ref_swc, sc);
						sc.AddSwCompoInst(swci);
						am.AddSwCompoInst(im.GetFullPath(scpi), swci);
//						System.out.println("swc_tp:" + swc_tp);
					}
				}
			}
		}

		for (EObject swc : swcs) {
//			System.out.println("swc:" + swc);
			if (swc.getClass().getName().endsWith("SwComponentTypeImpl")) {
				Assert.isTrue(swc instanceof Referrable);
//				SwCompo sc = new SwCompo(((Referrable) swc).getShortName());
//				String swc_path = im.GetFullPath(swc);
//				am.AddSwCompo(swc_path, sc);
//				SwCompo sc = am.GetSwCompo(swc_path);
				EList<EObject> ecnts = swc.eContents();
				for (EObject eo : ecnts) {
					if (eo.getClass().getName().endsWith("SwcInternalBehaviorImpl")) {
						SwcInternalBehaviorImpl si = (SwcInternalBehaviorImpl) eo;
						EList<EObject> e_cnts = si.eContents();
						for (EObject ec : e_cnts) {
		//					System.out.println("ec child:" + ec);
							// TimingEventImpl
							// InitEventImpl
							// RunnableEntityImpl
							if (ec instanceof InitEventImpl) {
								// not handle
							}
							if (ec instanceof TimingEventImpl) {
								// not handle
							}
							if (ec instanceof RunnableEntityImpl) {
								RunnableEntityImpl rei = (RunnableEntityImpl) ec;
//								RunEnt e = new RunEnt(rei.gGetShortName());
								
								EList<VariableAccess> reads = rei.getDataReadAccess();
								ArrayList<VarAcc> r_vas = HandleVariableAccess(reads, im, am, true);
								for (VarAcc read_va : r_vas) {
									ArInterface ario = read_va.GetAccInterface();
									ario.RecordOneRead();
//									ArIO f_root = ArUtil.TraceToTerminal(ario);
//									if (f_root instanceof ArInterface) {
//										((ArInterface) f_root).RecordOneRead();
//									}
								}
//								e.AddReadVarAccesses(r_vas);
								
								EList<VariableAccess> writes = rei.getDataWriteAccess();
								ArrayList<VarAcc> w_vas = HandleVariableAccess(writes, im, am, false);
								for (VarAcc write_va : w_vas) {
									ArInterface ario = write_va.GetAccInterface();
									ario.RecordOneWrite();
//									ArIO f_root = ArUtil.TraceToTerminal(ario);
//									if (f_root instanceof ArInterface) {
//										((ArInterface) f_root).RecordOneWrite();
//									}
								}
//								e.AddWriteVarAccesses(w_vas);
								
		//						for (VariableAccess va : writes) {
		//							System.out.println("write-va:" + va);
		//						}
								
		//						EList<EObject> recs = rei.eContents();
		//						for (EObject rec : recs) {
		//							if (rec instanceof AdminDataImpl) {
		//								// not handle
		//							}
		//							if (rec instanceof VariableAccessImpl) {
		////								EList<EObject> varecs = rec.eContents();
		////								for (EObject varec : varecs) {
		////									System.out.println("varec:" + varec);
		////								}
		//							}
		////							System.out.println("rec child:" + rec);
		//						}
							}
						}
		//				EList<GRunnableEntity> runs = si.gGetRunnables();
		//				for (GRunnableEntity re : runs) {
		//					RunEnt e = new RunEnt(re.gGetShortName(), sc);
		//					EList<EObject> cnts = re.eContents();
		//					for (EObject cnt : cnts) {
		//						if (cnt instanceof VariableAccessImpl) {
		//							VariableAccessImpl vai = (VariableAccessImpl) cnt;
		//							String v_name = vai.getShortName();
		//							e.HandleVarAccString(v_name);
		//						}
		//						System.out.println("GRunnableEntity child cnt:" + cnt);
		//					}
		//					sc.AddRunEnt(e);
		//				}
					}
				}
			}
		}
		
		for (EObject swc : swcs) {
//			System.out.println("swc:" + swc);
			if (swc.getClass().getName().endsWith("SwComponentTypeImpl")) {
				String swc_path = im.GetFullPath(swc);
				SwCompo sc = am.GetSwCompo(swc_path);
				EList<EObject> ecnts = swc.eContents();
				for (EObject eo : ecnts) {
					if (eo.getClass().getName().endsWith("SwConnectorImpl")) {
//						System.out.println("eo:" + eo);
						if (eo instanceof AssemblySwConnectorImpl) {
							AssemblySwConnectorImpl asci = (AssemblySwConnectorImpl) eo;
							PPortInCompositionInstanceRef prov = asci.getProvider();
							RPortInCompositionInstanceRef req = asci.getRequester();
							
							AbstractProvidedPortPrototype p_prov = prov.getTargetPPort();
							SwComponentPrototype c_prov = prov.getContextComponent();
							
							AbstractRequiredPortPrototype p_req = req.getTargetRPort();
							SwComponentPrototype c_req = req.getContextComponent();
							
//							String dest_c = StringHelper.GetProxyValidPath(c_req.toString());
//							String dest = StringHelper.GetProxyValidPath(p_req.toString());
//							Assert.isTrue(am.GetSwCompoInst(dest_c).GetType() == am.GetSwCompo(StringHelper.NonLastPartInPath(dest)));
//							String d = dest_c + "/" + StringHelper.LastPartInPath(dest);
							
//							Port p_s = am.GetPort(s);
//							Port p_t  = am.GetPort(d);
							
							Port p_s = ArUtil.GetPortFromCompoInstRef(am, p_prov, c_prov);
							Port p_t = ArUtil.GetPortFromCompoInstRef(am, p_req, c_req);
							
							p_s.SetTarget(p_t, sc);
						} else {
							if (eo instanceof DelegationSwConnectorImpl) {
								DelegationSwConnectorImpl dsci = (DelegationSwConnectorImpl) eo;
								PortInCompositionTypeInstanceRef inner = dsci.getInnerPort();
								PortPrototype outer = dsci.getOuterPort();
								
//								String source = null;
//								String dest = null;
								Port p_s = null;
								Port p_t = null;
								if (inner instanceof RPortInCompositionInstanceRefImpl) {
									RPortInCompositionInstanceRefImpl rpref = (RPortInCompositionInstanceRefImpl) inner;
									AbstractRequiredPortPrototype rport = rpref.getTargetRPort();
									SwComponentPrototype cport = rpref.getContextComponent();
//									System.out.println("inner:" + rport);
									String source = StringHelper.GetProxyValidPath(outer.toString());
									p_s = am.GetPort(source);
									p_t =  ArUtil.GetPortFromCompoInstRef(am, rport, cport);
//									dest = StringHelper.GetProxyValidPath(cport.toString()) + "/" + StringHelper.LastPartInPath(StringHelper.GetProxyValidPath(rport.toString()));
								} else {
									Assert.isTrue(inner instanceof PPortInCompositionInstanceRefImpl);
									PPortInCompositionInstanceRefImpl ppicref = (PPortInCompositionInstanceRefImpl) inner;
									AbstractProvidedPortPrototype pport = ppicref.getTargetPPort();
									SwComponentPrototype cport = ppicref.getContextComponent();
									p_s =  ArUtil.GetPortFromCompoInstRef(am, pport, cport);
//									System.out.println("inner:" + pport);
//									source = StringHelper.GetProxyValidPath(cport.toString()) + "/" + StringHelper.LastPartInPath(StringHelper.GetProxyValidPath(pport.toString()));
//									StringHelper.GetProxyValidPath(pport.toString());
									String dest = StringHelper.GetProxyValidPath(outer.toString());
									p_t = am.GetPort(dest);
								}
//								EList<EObject> icnts = inner.eContents();
//								for (EObject icnt : icnts) {
//									System.out.println("icnt:" + icnt);
//								}
								
//								System.out.println("outer:" + outer);
//								Port p_s = am.GetPort(source);
//								Port p_t = am.GetPort(dest);
								p_s.SetTarget(p_t, sc);
							} else {
								Assert.isTrue(false);
							}
						}
					}
				}
			}
		}
	}
	
	protected ArrayList<VarAcc> HandleVariableAccess(EList<VariableAccess> rvas, InfoManager im, ArModel am, boolean is_read) {
		ArrayList<VarAcc> res = new ArrayList<VarAcc>();
		for (VariableAccess va : rvas) {
//			System.out.println("read-va:" + va);
			EList<EObject> vas = va.eContents();
			for (EObject r_va : vas) {
//				System.out.println("rva:" + r_va);
				if (r_va instanceof AutosarVariableRefImpl) {
					AutosarVariableRefImpl avri = (AutosarVariableRefImpl) r_va;
					EList<EObject> acnts = avri.eContents();
					for (EObject acnt : acnts) {
//						System.out.println("acnt:" + acnt);
						if (acnt instanceof VariableInAtomicSWCTypeInstanceRefImpl) {
							VariableInAtomicSWCTypeInstanceRefImpl viatiri = (VariableInAtomicSWCTypeInstanceRefImpl) acnt;
							DataPrototype target_data = viatiri.getTargetDataPrototype();
							PortPrototype port_proto = viatiri.getPortPrototype();
							
							String tds = target_data.toString();
							String pps = port_proto.toString();
							String accessed_var_prop = StringHelper.GetProxyValidPath(tds);
							String port_full_name = StringHelper.GetProxyValidPath(pps);
//							Assert.isTrue(accessed_var_prop.startsWith(port_full_name), "accessed_var_prop:" + accessed_var_prop + "#port_full_name:" + port_full_name);
							String port_prop = StringHelper.LastPartInPath(accessed_var_prop);
							String port_var = StringHelper.NonLastPartInPath(accessed_var_prop);
							Port pp = am.GetPort(port_full_name);
							VarAcc v_acc = new VarAcc(is_read, am.GetInterface(port_var), port_prop);// pp, 
							pp.SetVarAcc(v_acc);
							res.add(v_acc);
							
//							System.out.println("target_data:" + target_data + "#is_proxy:" + target_data.eIsProxy() + "#short_name:" + target_data.getShortName() + "#uuid:" + target_data.getUuid() + "#category:" + target_data.getCategory());
//							System.out.println("port_proto:" + port_proto + "#is_proxy:" + port_proto.eIsProxy() + "#short_name:" + port_proto.getShortName() + "#uuid:" + port_proto.getUuid());
							
//							EList<EObject> vias = viatiri.eContents();
//							for (EObject via : vias) {
//								System.out.println("via:" + via);
//							}
						}
					}
//						 VariableInAtomicSWCTypeInstanceRef av = avri.getAutosarVariable();
//						 ArVariableInImplementationDataInstanceRef aviid = avri.getAutosarVariableInImplDatatype();
//						 System.out.println("av:" + av);
//						 System.out.println("aviid:" + aviid);
				}
			}
		}
		return res;
	}
	
	protected void TestAndPrint(Autosar40ResourceImpl resource) {
//		System.out.println("public id:" + resource.getPublicId());
//		Map<EObject, String> mp = resource.getEObjectToIDMap();
//		Set<EObject> mp_keys = mp.keySet();
//		Iterator<EObject> mp_itr = mp_keys.iterator();
//		System.out.println("==== eo begin ====");
//		while (mp_itr.hasNext()) {
//			EObject eo = mp_itr.next();
//			System.out.println("EObject:" + eo);
//		}
//		System.out.println("==== eo end ====");
		System.out.println("KeyString:" + resource.toKeyString());
//		System.out.println("Encoding:" + resource.getEncoding());
		System.out.println("isLoaded:" + resource.isLoaded());
		System.out.println("resource:" + resource);
		TreeIterator<EObject> acs = resource.getAllContents();
		System.out.println("==== ac begin ====");
		while (acs.hasNext()) {
			EObject ac = acs.next();
			System.out.println("ac:" + ac);
		}
		System.out.println("==== ac end ====");
		EList<Adapter> eas = resource.eAdapters();
		System.out.println("eas_size:" + eas.size());
		System.out.println("==== ea begin ====");
		for (Adapter ea : eas) {
			ExtendedAutosarResourceAdapter easa = (ExtendedAutosarResourceAdapter) ea;
			System.out.println("easa:" + easa);
		}
		System.out.println("==== ea begin ====");
		EList<EObject> cnts = resource.getContents();
		System.out.println("cnts_size:" + cnts.size());
		System.out.println("==== cnt begin ====");
		for (EObject cnt : cnts) {
			System.out.println("cnt:" + cnt);
			Visit(cnt);
		}
		System.out.println("==== cnt end ====");
	}

	@Override
	public void stop() {
		System.out.println("MBD Eclipse Stops!");
	}
	
	private URI HandleArgs(IApplicationContext arg0) {
		Map<?, ?> args = arg0.getArguments();
//		Set<?> akeys = args.keySet();
//		Iterator<?> aitr = akeys.iterator();
//		while (aitr.hasNext()) {
//			Object a = aitr.next();
//			Object a_val = args.get(a);
//			System.out.println("a:" + a + "#a_val:" + a_val);
//		}
        String[] realArgs = (String[]) args.get("application.args");
        for (String ra : realArgs) {
        	System.out.println("ra:" + ra);
        }
        if ((realArgs.length == 0) || (!realArgs[0].equals("--file"))) {
        	return null;
        }
        String fn = realArgs[1];
        URI uri = URI.createFileURI(fn);
        System.out.println("uri:" + uri);
        return uri;
	}
	
}
