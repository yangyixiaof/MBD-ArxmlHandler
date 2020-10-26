package util;

import org.eclipse.core.runtime.Assert;

public class StringHelper {
	
	public static String Trim(String origin, String to_trim) {
		return origin.replaceAll(to_trim+"$", "").replaceAll("^"+to_trim, "");
	}
	
	public static String NonLastPartInPath(String ps) {
		return ps.substring(0, ps.lastIndexOf('/'));
	}
	
	public static String LastPartInPath(String ps) {
		return ps.substring(ps.lastIndexOf('/')+1);
	}
	
	public static String GetProxyValidPath(String ts) {
		return ts.substring(ts.indexOf("#/")+1, ts.indexOf("?type="));
	}
	
	public static String TrimPrefix(String all, String prefix) {
		Assert.isTrue(all.startsWith(prefix), "all:" + all + "#prefix:" + prefix);
		return all.substring(prefix.length());
	}
	
	public static int CountCharacterInString(String string, char c) {
        int count = 0;
        //Counts each character except space
        for(int i = 0; i < string.length(); i++) {
            if(string.charAt(i) == c)
                count++;
        }
//        System.out.println("Total number of characters in a string: " + count);
        return count;
	}
	public static String InsertFunctionCallNameToFirstParameterInList(String function_call_name, String param_list) {
		Assert.isTrue(param_list.length() >= 2, "Param length must be >= 2, wrong param:" + param_list);
		param_list = param_list.trim();
		String to_ins = "[\"" + function_call_name + "\" \"string\" \"0\"]";
		String sp = "";
		if (param_list.length() > 2) {
			sp = "#";
		}
		to_ins += sp;
		StringBuilder res = new StringBuilder(param_list);
		StringBuilder r_res = res.insert(1, to_ins);
//		System.err.println("r_res:" + r_res);
		return r_res.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(StringHelper.Trim("a_", "_"));
	}
	
}
