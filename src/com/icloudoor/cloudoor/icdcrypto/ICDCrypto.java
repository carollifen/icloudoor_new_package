package com.icloudoor.cloudoor.icdcrypto;

public class ICDCrypto {
	
	static {
		System.loadLibrary("icdcrypto_jni");
	}
	
	public static byte[] getDeviceId(byte[] inBytes) {
		return decodeDoorDeviceId(inBytes);
	}
	
	public static native byte[] decodeDoorDeviceId(byte[] inBytes);

	public static native byte[] encodeDoorOpenSignal(byte[] inBytes);

	public static native boolean checkIfOpenDoorSuccess(byte[] inBytes);

	
}
