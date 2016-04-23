package com.smartlink;

public class SDK {

	static{
		System.loadLibrary("SlaveCrc8");
	}
	public static native int SlaveCrc8(byte[] b, int len, byte[] out);
	
}
