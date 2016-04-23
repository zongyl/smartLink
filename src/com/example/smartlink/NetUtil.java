package com.example.smartlink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.util.Log;

public class NetUtil {

	private static final String TAG = "NetUtil";
	
	private static final int MULTICAST_PORT = 36666;
	
	//private static final String GROUP_IP = "239.0.0.254";
	
	private static MulticastSocket multicastSocket;
	
	static void sendData(byte[] data) throws IOException{
		Log.d(TAG, "sendData data.len:"+data.length);
		while(true){
			
			if(multicastSocket == null){
				multicastSocket = new MulticastSocket(MULTICAST_PORT);
				Log.d(TAG, "new MulticastSocket()");
			}
			
			//MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
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
}
