package com.example.smartlink;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.content.Context;
import android.util.Log;

import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.smartlink.SDK;

public class SnifferSmartLinker implements ISmartLinker{

	private static final String TAG = "SnifferSmartLinker";
	
	private static int MULTICAST_PORT = 36666;
	
	private boolean isConnecting = false;
	
	private String ssid, password;
	
	private static SnifferSmartLinker snifferSmartLinker;
	
	private MulticastSocket multicastSocket;
	
	public SnifferSmartLinker(){
		isConnecting = false;
	}
	
	public static SnifferSmartLinker getInstance(){
		if(snifferSmartLinker == null){
			snifferSmartLinker = new SnifferSmartLinker();
		}
		return snifferSmartLinker;
	}
	
	private byte[] getBytes(String ssid, String password){
		byte[] bytes = new byte[22];
		String content = ssid+"\0"+password+"\0";
		byte[] head = new byte[4];
		head[0] = 0;
		head[1] = 1;
		head[2] = (byte)(4 + content.getBytes().length);
		head[3] = 0;
		byte[] contentByte = content.getBytes();
	
		bytes = new byte[head.length+contentByte.length];
		System.arraycopy(head, 0, bytes, 0, head.length);
		System.arraycopy(contentByte, 0, bytes, head.length, contentByte.length);
		
		byte[] cutbyte = new byte[bytes.length-1];
		cutbyte = cutOutByte(bytes, 1, cutbyte.length);
		
		byte[] out = new byte[2];
		SDK.SlaveCrc8(cutbyte, cutbyte.length, out);
		Log.d(TAG, "C++ Version:" + out[0]);
		bytes[0] = out[0];
		return bytes;
	}
	
	public byte[] cutOutByte(byte[] b, int start, int len){
		if(b.length == 0 || len == 0){
			return null;
		}
		byte[] retb = new byte[len];
		for(int i=0;i<len;i++){
			retb[i] = b[start+i];
		}
		return retb;
	}
	
	@Override
	public void setOnSmartLinkListener(
			OnSmartLinkListener paramOnSmartLinkListener) {
	}

	@Override
	public void start(Context paramContext, String password, String... ssid) throws Exception {
		Log.e(this.TAG, ssid + ":" + password);
		if((ssid != null) && (ssid.length > 0)){
			this.ssid = ssid[0];
		}
		this.password = password;

		byte[] data = getBytes("m", "p");
		//byte[] data = getBytes(this.ssid, this.password);
		isConnecting = true;
		while(isConnecting){
			
			Log.d(TAG, "Connecting..." + data.toString());
			
			if(multicastSocket == null){
				multicastSocket = new MulticastSocket(MULTICAST_PORT);
				Log.d(TAG, "new MulticastSocket()");
			}
			multicastSocket.setLoopbackMode(true);
			InetAddress group;
			DatagramPacket packet;
				for(int i = 0; i < data.length; i++){
					Log.d(TAG, i + ":" + data[i]);
					group = InetAddress.getByName("239."+i+"."+data[i]+".254");
					
					multicastSocket.joinGroup(group);
					packet = new DatagramPacket("".getBytes(), "".getBytes().length, group, MULTICAST_PORT);
					multicastSocket.send(packet);
					multicastSocket.leaveGroup(group);
				}	
		}
		
	}

	@Override
	public void stop() {
	     isConnecting = false;
	     if (this.multicastSocket != null) {
	       this.multicastSocket.close();
	     }
	}

	@Override
	public boolean isSmartLinking() {
	     return isConnecting;
	}

}
