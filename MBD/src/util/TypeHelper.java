package util;

import org.eclipse.core.runtime.Assert;

public class TypeHelper {
	
	public static String TranslateArxmlTypeToCType(String tp) {
		switch (tp) {
		case "float32":
			return "float";
		case "float64":
			return "double";
		case "uint16":
			return "unsigned short";
		case "uint32":
			return "unsigned int";
		case "uint64":
			return "unsigned long";
		case "sint16":
			return "short";
		case "sint32":
			return "int";
		case "sint64":
			return "long";
		 default:
			 Assert.isTrue(false);
			 break;
		}
		return null;
	}
	
}
