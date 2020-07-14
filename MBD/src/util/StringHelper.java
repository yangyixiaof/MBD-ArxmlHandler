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
	
	public static void main(String[] args) {
		System.out.println(StringHelper.Trim("a_", "_"));
	}
	
}
