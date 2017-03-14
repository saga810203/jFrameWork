package org.jfw.util.auth;

import java.util.Arrays;
import java.util.Random;

import org.jfw.util.codec.Base64;

public final class AuthUtil {

	private static final int[] AUTH_BIT = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192,
			16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432,
			67108864, 134217728, 268435456, 536870912, 1073741824, -2147483648 };

	private static final int[] AUTH_BIT_XOR = new int[] { -2, -3, -5, -9, -17, -33, -65, -129, -257, -513, -1025, -2049,
			-4097, -8193, -16385, -32769, -65537, -131073, -262145, -524289, -1048577, -2097153, -4194305, -8388609,
			-16777217, -33554433, -67108865, -134217729, -268435457, -536870913, -1073741825, 2147483647 };

	private AuthUtil() {
	}

	public static boolean hasAuthority(int[] auths, int auth) {
		if (auth < 1)
			return false;
		int bIndex = auth >> 5;
		if (bIndex >= auths.length)
			return false;
		int ind = auth % 32;
		return 0 != (auths[bIndex] & AUTH_BIT[ind]);
	}
	public static int[] compress(int[] auths){
		if(auths==null || auths.length ==0  ) return new int[]{0};		
		 Arrays.sort(auths);
		int max = auths[auths.length-1];
		int bIndex = max>>5;
		int[] ret = new int[bIndex+1];
		Arrays.fill(ret,0);
		for(int auth:auths){
			int ind = auth % 32;
			ret[bIndex] = ret[bIndex] | AUTH_BIT[ind];
		}
		return ret;
	}
	
	public static int[] unCompress(int[] auths){
		if(auths==null || auths.length ==0  ) return new int[]{0};		
		int[] ret = new int[16];
		int ina = 0;
		int max = auths.length*32;
		for(int i = 1 ; i <= max;){
			int bIndex = i >> 5;
			int ind = i % 32;
			if( 0 != (auths[bIndex] & AUTH_BIT[ind])){
				if(ina>= ret.length){
					int[] nret = new int[ret.length*2];
					System.arraycopy(ret,0,nret, 0,ret.length);
					ret = nret;
				}
				ret[ina]= i;
				++ina;
			}
		}
		if(ret.length> ina) {
			int[] nret = new int[ina];
			System.arraycopy(ret,0,nret, 0,ina);
			return nret;
		}
		return ret;
	}
	public static int[] grant(int[] auths, int auth) {
		if (auth < 1)
			return auths;
		int bIndex = auth >> 5;
		if (bIndex >= auths.length) {
			int[] nAuths = new int[bIndex + 1];
			System.arraycopy(auths, 0, nAuths, 0, auths.length);
			for (int i = auths.length; i < nAuths.length; ++i) {
				nAuths[i] = 0;
			}
			auths = nAuths;
		}
		int ind = auth % 32;
		auths[bIndex] = auths[bIndex] | AUTH_BIT[ind];
		return auths;
	}

	public static int[] revoke(int[] auths, int auth) {
		if (auth < 1)
			return auths;
		int bIndex = auth >> 5;
		if (bIndex >= auths.length)
			return auths;
		int ind = auth % 32;
		auths[bIndex] = auths[bIndex] & AUTH_BIT_XOR[ind];
		return auths;
	}

	public static int[] merge(int[][] auths){
		if(auths ==null) return new int[]{0};
		if(auths.length==0) return auths[0];
		int maxLen = auths[0].length;
		for(int i = 1; i < auths.length ; ++i){
			int eLen = auths[i].length;
			if(eLen>maxLen) maxLen = eLen;
		}
		int[] result = new int[maxLen];
		Arrays.fill(result, 0);
		for(int i = 0 ; i <  auths.length; ++i){
			int[] auth = auths[i];
			int eLen = auth.length;
			for(int j = 0 ; j < eLen; ++j){
				result[j] = result[j] | auth[j];
			}
		}
		return result;
	}
	
	public static int[] reBuild(int[] auths) {
		int last = auths.length - 1; // 10 - 1 = 9
		if ((last == 0) || (auths[last] != 0))
			return auths;
		--last; // 8
		for (; last > 0; --last) {
			if (auths[last] != 0)
				break;
		}
		++last;
		int[] nAuths = new int[last];
		System.arraycopy(auths, 0, nAuths, 0, last);
		return nAuths;
	}

	public static int[] conver(byte[] auths) {
		if (auths == null || auths.length == 0)
			return new int[0];
		byte[] nAuths = auths;
		int bLen = auths.length;
		int lDiv = bLen % 4;
		if (lDiv != 0) {
			bLen += (4 - lDiv);
			nAuths = new byte[bLen];
			nAuths[bLen - 1] = 0;
			nAuths[bLen - 2] = 0;
			nAuths[bLen - 3] = 0;
			System.arraycopy(auths, 0, nAuths, 0, auths.length);
		}
		int iLen = bLen / 4;
		int[] result = new int[iLen];
		int bIndex = 0;

		for (int i = 0; i < iLen; ++i) {
			result[i] = ((nAuths[bIndex++] & 0xff) << 24) | ((nAuths[bIndex++] & 0xff) << 16)
					| ((nAuths[bIndex++] & 0xff) << 8) | (nAuths[bIndex++] & 0xff);
		}
		return result;
	}

	public static byte[] conver(int[] auths) {
		if (auths == null || auths.length == 0)
			return new byte[0];
		byte[] result = new byte[auths.length * 4];
		int bIndex = 0;
		for (int i = 0; i < auths.length; ++i) {
			int auth = auths[i];
			result[bIndex++] = (byte) ((auth >> 24) & 0xFF);
			result[bIndex++] = (byte) ((auth >> 16) & 0xFF);
			result[bIndex++] = (byte) ((auth >> 8) & 0xFF);
			result[bIndex++] = (byte) (auth & 0xFF);
		}
		return result;
	}

	
	public static String serialAuth(int[] auths){
		return Base64.encodeBase64String(conver(auths));	
	}
	public static int[] deSerialAuth(String authStr){
		if(authStr==null || authStr.length()==0) return new int[]{0};
		return conver(Base64.decodeBase64(authStr));
	}
	public static String grant(String authStr,int auth){
		return serialAuth(grant(deSerialAuth(authStr),auth));
	}
	public static String revoke(String authStr,int auth){
		return serialAuth(reBuild(revoke(deSerialAuth(authStr),auth)));		
	}
	
	public static void main(String main[]) {
		System.out.println(System.currentTimeMillis());
		int[] iss = new int[100];
		for (int i = 0; i < iss.length; ++i)
		{
			iss[i] = (new Random()).nextInt();
		}
		byte[] bs = conver(iss);
		int[] ins = conver(bs);
		for (int ii = 0; ii < ins.length; ++ii) {
			System.out.println("" + iss[ii] + ":" + ins[ii]);
		}
		System.out.println(System.currentTimeMillis());
	}

}
