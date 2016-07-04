package org.jfw.util.auth;


public final class AuthUtil {
	
	private static final int[] AUTH_BIT = new int[]{1,2,4,8,16,32,64,128};
	
	private static final int[] AUTH_BIT_XOR = new int[8];
	
	private AuthUtil(){}
	
	public static boolean hasAuthority(byte[] auths,int auth){
		if(auth <1) return false;
		int bIndex = auth >> 3;
		if(bIndex>= auths.length) return false;
		byte b = auths[bIndex];		
		int  ind = auth % 8;
		return 0 != ( (b & 0xff) & AUTH_BIT[ind]);		
	}
	
	public static byte[] grant(byte[] auths,int auth){
		if(auth<1) return auths;
		int bIndex = auth >> 3;
		if(bIndex>=auths.length){
			byte[] nAuths = new byte[bIndex+1];
			System.arraycopy(auths,0, nAuths, 0,auths.length);
			for(int i = auths.length; i < nAuths.length; ++i){
				nAuths[i] = 0;
			}
			auths = nAuths;
		}
		int ind= auth % 8;
		int b = auths[bIndex] & 0xff;
		auths[bIndex] =(byte)( b | AUTH_BIT[ind]);
		return auths;
	}
	
	public static byte[] revoke(byte[] auths,int auth){
		if(auth<1) return auths;
		int bIndex = auth >> 3;
		if(bIndex>=auths.length) return auths;
		int ind= auth % 8;
		int b = auths[bIndex] & 0xff;
		auths[bIndex] =(byte)( b & AUTH_BIT_XOR[ind]) ;
		return auths;
	}
	public static byte[] reBuild(byte[] auths){
		int last = auths.length - 1;  //10  - 1  =   9
		if((last ==0) || (auths[last]!=0)) return auths;
		--last;                       //8
		for(;last>0 ;--last){
			if(auths[last]!=0) break;			
		}
		++last;
		byte[] nAuths = new byte[last];
		System.arraycopy(auths,0, nAuths, 0, last);
		return nAuths;		
	}
	
	
	
	public static void main(String main[]){		
      byte[] b = new byte[]{0};
      
     for(int i = 1; i < 100 ; ++i){
    	 b = grant(b,i);
     }
     
     b = revoke(b, 10);
     
     b = revoke(b, 15);
     b = revoke(b, 20);
     b = revoke(b, 30);
     b = revoke(b, 45);
     b = revoke(b, 65);
     b = revoke(b, 90);
     b = revoke(b, 99);
      
      for(int i = 1; i < 100; ++i){
    	  System.out.println(""+i + ":"+hasAuthority(b, i));
    	  
    	  
      }
	}

	
	static {
		for( int i = 0 ; i < 8 ; ++i){
			AUTH_BIT_XOR[i] = AUTH_BIT[i]  ^ (-1);
		}
	}
}
