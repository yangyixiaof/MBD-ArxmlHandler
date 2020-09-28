package ar.util;

import java.util.ArrayList;

import ar.ArElement;

public class ArCloneUtil {
	
	public static ArElement CopyTreeWithRoot(ArElement root) {
		ArElement cl_root = (ArElement) root.ArClone();
		ArrayList<ArElement> child_eles = root.GetAllChildElements();
		for (ArElement ele : child_eles) {
			ArElement cl_ele = (ArElement) ele.ArClone();
			cl_root.AddChildElement(cl_ele);
		}
		return cl_root;
	}
	
}
