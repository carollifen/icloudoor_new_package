package com.icloudoor.cloudoor;

public class IcdCrypto {

	public native void getEncodeSignal(byte[] inBytes);
	public native int decodeOpenDoorResult(byte[] inBytes);

	static {
		System.loadLibrary("icdcrypto-JNI_20150714");
	}
}