package util;

import java.util.Collection;

public class PrintUtil {
	
	public static String PrintSet(Collection<?> colls) {
		String res = "";
		for (Object c : colls) {
			res += c.toString() + "#";
		}
//		System.out.println("==== Print Set Begin ====");
//		System.out.println(res);
//		System.out.println("==== Print Set End ====");
		return res;
	}
	
}
