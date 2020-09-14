package util;

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
//			 Assert.isTrue(false, "wrong type:" + tp);
//			 break;
			 new Exception("unrecognized type:" + tp + "#note that here must be recovered to Assert").printStackTrace();
			 return "double";
		}
//		return null;
	}
	
}
