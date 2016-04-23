 package com.hiflying.smartlink.v3;
 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SnifferSmartLinker
/*     */   implements ISmartLinker
/*     */ {
/*  21 */   private String TAG = "HFdebug";
/*     */   
/*     */   private String ssid;
/*     */   private String pswd;
/*     */   private String broadCastIP;
/*     */   private String gateWay;
/*  27 */   private static SnifferSmartLinker me = null;
/*  28 */   private Set<String> successMacSet = new HashSet();
/*     */   
/*  30 */   private int HEADER_COUNT = 200;
/*  31 */   private int HEADER_PACKAGE_DELAY_TIME = 10;
/*  32 */   private int HEADER_CAPACITY = 76;
/*     */   private OnSmartLinkListener callback;
/*  34 */   private int CONTENT_COUNT = 5;
/*  35 */   private int CONTENT_PACKAGE_DELAY_TIME = 50;
/*  36 */   private int CONTENT_CHECKSUM_BEFORE_DELAY_TIME = 100;
/*  37 */   private int CONTENT_GROUP_DELAY_TIME = 500;
/*  38 */   private final String RET_KEY = "smart_config";
/*     */   
/*  40 */   private int port = 49999;
/*  41 */   private byte[] receiveByte = new byte['Ȁ'];
/*     */   
/*     */   public static final int DEVICE_COUNT_ONE = 1;
/*     */   
/*     */   public static final int DEVICE_COUNT_MULTIPLE = -1;
/*  46 */   private boolean isConnecting = false;
/*     */   
/*     */   private InetAddress inetAddressbroadcast;
/*     */   
/*     */   private DatagramSocket socket;
/*     */   
/*     */   private DatagramPacket packetToSendbroadcast;
/*     */   private DatagramPacket packetToSendgateway;
/*     */   private DatagramPacket dataPacket;
/*  55 */   private boolean isfinding = false;
/*     */   

            private MulticastSocket msocket;

/*     */   private SnifferSmartLinker() {
/*  58 */     this.isConnecting = false;
/*  59 */     this.isfinding = false;
/*     */   }
/*     */   
/*     */   private static class SnifferSmartLinkerInner {
/*  63 */     private static final SnifferSmartLinker SNIFFER_SMART_LINKER = new SnifferSmartLinker();
/*     */   }
/*     */   
/*  66 */   public static SnifferSmartLinker getInstence() { return SnifferSmartLinkerInner.SNIFFER_SMART_LINKER; }
/*     */   
/*     */ 
/*  69 */   private Runnable findThread = new Runnable()
/*     */   {
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*  75 */         Thread.sleep(10000L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {}
/*  78 */       for (int i = 0; (i < 20) && (SnifferSmartLinker.this.isConnecting); i++) {
/*  79 */         SnifferSmartLinker.this.sendFindCmd();
/*  80 */         if (SnifferSmartLinker.this.isConnecting)
/*  81 */           try { Thread.sleep(1000L);
/*     */           } catch (InterruptedException localInterruptedException1) {}
/*     */       }
/*  84 */       if ((SnifferSmartLinker.this.isConnecting) && (SnifferSmartLinker.this.callback != null)) {
/*  85 */         if (SnifferSmartLinker.this.successMacSet.size() <= 0) {
/*  86 */           SnifferSmartLinker.this.callback.onTimeOut();
/*  87 */         } else if (SnifferSmartLinker.this.successMacSet.size() > 0) {
/*  88 */           SnifferSmartLinker.this.callback.onCompleted();
/*     */         }
/*     */       }
/*  91 */       Log.e(SnifferSmartLinker.this.TAG, "stop find");
/*  92 */       SnifferSmartLinker.this.isfinding = false;
/*  93 */       SnifferSmartLinker.this.stop();
/*     */     }
/*     */   };

/*     */   private String getBroadcastAddress(Context ctx)
/*     */   {
/* 109 */     WifiManager cm = (WifiManager)ctx
/* 110 */       .getSystemService("wifi");
/* 111 */     DhcpInfo myDhcpInfo = cm.getDhcpInfo();
/* 112 */     if (myDhcpInfo == null) {
/* 113 */       return "255.255.255.255";
/*     */     }
/*     */     
/*     */ 
/* 117 */     int broadcast = myDhcpInfo.ipAddress & myDhcpInfo.netmask | 
/* 118 */       myDhcpInfo.netmask ^ 0xFFFFFFFF;
/* 119 */     byte[] quads = new byte[4];
/* 120 */     for (int k = 0; k < 4; k++)
/* 121 */       quads[k] = ((byte)(broadcast >> k * 8 & 0xFF));
/*     */     try {
/* 123 */       return InetAddress.getByAddress(quads).getHostAddress();
/*     */     } catch (Exception e) {}
/* 125 */     return "255.255.255.255";
/*     */   }
/*     */   
/*     */   private void connect()
/*     */   {
/* 130 */     Log.e(this.TAG, "connect");
/* 131 */     int count = 1;
/* 132 */     byte[] header = getBytes(this.HEADER_CAPACITY);
/* 133 */     while ((count <= this.HEADER_COUNT) && (this.isConnecting)) {
/* 134 */       send(header);
/*     */       try {
/* 136 */         Thread.sleep(this.HEADER_PACKAGE_DELAY_TIME);
/*     */       } catch (InterruptedException e) {
/* 138 */         e.printStackTrace();
/*     */       }
/* 140 */       count++;
/*     */     }
/* 142 */     String pwd = this.pswd;
/* 143 */     int[] content = new int[pwd.length() + 2];
/*     */     
/* 145 */     content[0] = 89;
/* 146 */     int j = 1;
/* 147 */     for (int i = 0; i < pwd.length(); i++) {
/* 148 */       content[j] = (pwd.charAt(i) + 'L');
/* 149 */       j++;
/*     */     }
/* 151 */     content[(content.length - 1)] = 86;
/*     */     
/* 153 */     count = 1;
/* 154 */     while ((count <= this.CONTENT_COUNT) && (this.isConnecting)) {
/* 155 */       for (int i = 0; i < content.length; i++)
/*     */       {
/* 157 */         int _count = 1;
/* 158 */         if ((i == 0) || (i == content.length - 1)) {
/* 159 */           _count = 3;
/*     */         }
/* 161 */         int t = 1;
/* 162 */         while ((t <= _count) && (this.isConnecting)) {
/* 163 */           send(getBytes(content[i]));
/* 164 */           if (i != content.length) {
/*     */             try {
/* 166 */               Thread.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
/*     */             } catch (InterruptedException e) {
/* 168 */               e.printStackTrace();
/*     */             }
/*     */           }
/* 171 */           t++;
/*     */         }
/*     */         
/*     */ 
/* 175 */         if (i != content.length) {
/*     */           try {
/* 177 */             Thread.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
/*     */           } catch (InterruptedException e) {
/* 179 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       try {
/* 184 */         Thread.sleep(this.CONTENT_CHECKSUM_BEFORE_DELAY_TIME);
/*     */       } catch (InterruptedException e) {
/* 186 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 191 */       int checkLength = pwd.length() + 256 + 76;
/*     */ 
/* 194 */       int t = 1;
/* 195 */       while ((t <= 3) && (this.isConnecting)) {
/* 196 */         send(getBytes(checkLength));
/* 197 */         if (t < 3) {
/*     */           try {
/* 199 */             Thread.sleep(this.CONTENT_PACKAGE_DELAY_TIME);
/*     */           } catch (InterruptedException e) {
/* 201 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 204 */         t++;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 209 */         Thread.sleep(this.CONTENT_GROUP_DELAY_TIME);
/*     */       } catch (InterruptedException e) {
/* 211 */         e.printStackTrace();
/*     */       }
/* 213 */       count++;
/*     */     }
/* 215 */     Log.e(this.TAG, "connect END");
/*     */   }
/*     */   
/*     */   private byte[] getBytes(int capacity) {
/* 219 */     byte[] data = new byte[capacity];
/* 220 */     for (int i = 0; i < capacity; i++) {
/* 221 */       data[i] = 5;
/*     */     }
/* 223 */     return data;
/*     */   }
/*     */   
/*     */   private void sendFindCmd()
/*     */   {
/* 234 */     System.out.println("smartlinkfind");
/* 235 */     this.packetToSendbroadcast = new DatagramPacket(
/* 236 */       "smartlinkfind".getBytes(), 
/* 237 */       "smartlinkfind".getBytes().length, this.inetAddressbroadcast, 
/* 238 */       48899);

             printPacket(packetToSendbroadcast);
             printSocket(socket);
/*     */     try {
/* 240 */       this.socket.send(this.packetToSendbroadcast);
/*     */     }
/*     */     catch (IOException e) {
/* 243 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   
/*     */   private void send(byte[] data) {
/* 263 */     this.packetToSendbroadcast = new DatagramPacket(data, data.length, 
/* 264 */       this.inetAddressbroadcast, this.port);
/*     */     try {
/* 266 */       this.socket.send(this.packetToSendbroadcast);
/*     */     } catch (IOException e) {
/* 268 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   private void receive() {
/* 273 */     Log.e(this.TAG, "start RECV");
/* 274 */     this.dataPacket = new DatagramPacket(this.receiveByte, this.receiveByte.length);
/* 275 */     new Thread() {
/*     */       public void run() {
/* 277 */         while (SnifferSmartLinker.this.isConnecting) {
/*     */           try {
/* 279 */             SnifferSmartLinker.this.socket.receive(SnifferSmartLinker.this.dataPacket);
/* 280 */             int len = SnifferSmartLinker.this.dataPacket.getLength();
/* 281 */             if (len > 0) {
/* 282 */               String receiveStr = new String(SnifferSmartLinker.this.receiveByte, 0, 
/* 283 */                 len, "UTF-8");
/*     */               
/*     */ 
/* 286 */               if (receiveStr.contains("smart_config")) {
/* 287 */                 Log.e("RECV", "smart_config");
/* 288 */                 SmartLinkedModule mi = new SmartLinkedModule();
/* 289 */                 mi.setMac(receiveStr.replace("smart_config", "")
/* 290 */                   .trim());
/* 291 */                 String ip = SnifferSmartLinker.this.dataPacket.getAddress().getHostAddress();
/* 292 */                 if ((ip.equalsIgnoreCase("0.0.0.0")) || (ip.contains(":"))) {
/* 293 */                   return;
/*     */                 }
/* 295 */                 mi.setModuleIP(ip);
/* 296 */                 if (!SnifferSmartLinker.this.successMacSet.contains(mi.getMac())) {
/* 297 */                   SnifferSmartLinker.this.successMacSet.add(mi.getMac());
/* 298 */                   if (SnifferSmartLinker.this.callback != null) {
/* 299 */                     SnifferSmartLinker.this.callback.onLinked(mi);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           } catch (IOException e) {
/* 305 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 308 */         Log.e(SnifferSmartLinker.this.TAG, "end RECV");
/* 309 */         SnifferSmartLinker.this.stop();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   public void setOnSmartLinkListener(OnSmartLinkListener listener)
/*     */   {
/* 316 */     this.callback = listener;
/*     */   }
/*     */   
/*     */   public void start(Context context, String password, String... ssid)
/*     */     throws Exception
/*     */   {
/* 322 */     Log.e(this.TAG, ssid + ":" + password);
/* 323 */     if ((ssid != null) && (ssid.length > 0)) {
/* 324 */       this.ssid = ssid[0];
/*     */     } else {
/* 326 */       this.ssid = null;
/*     */     }
/* 328 */     this.pswd = password;
/* 329 */     this.broadCastIP = getBroadcastAddress(context);
     this.socket = new DatagramSocket(this.port);
    this.socket.setBroadcast(true);
     this.inetAddressbroadcast = InetAddress.getByName(this.broadCastIP);
     
    Log.e(this.TAG, "start");
     this.isConnecting = true;
     receive();
     this.successMacSet.clear();
     new Thread(new Runnable()
    {
       public void run()
      {
         while (SnifferSmartLinker.this.isConnecting) {
           SnifferSmartLinker.this.connect();
         }
         Log.e(SnifferSmartLinker.this.TAG, "StopConnet");
         SnifferSmartLinker.this.stop();
       }
     }).start();
     if (!this.isfinding) {
      this.isfinding = true;
       new Thread(this.findThread).start();
     }
   }
   
   public void stop()
   {
     this.isConnecting = false;
     if (this.socket != null) {
       this.socket.close();
     }
   }
 
   public boolean isSmartLinking()
   {
     return this.isConnecting;
   }
   
   public void muc() throws IOException{
	   MulticastSocket msocket = new MulticastSocket(port);
	   msocket.joinGroup(InetAddress.getByName("239.12.6.254"));
	   msocket.setTimeToLive(4);
	   String content = "";
	   DatagramPacket packet = new DatagramPacket(
			   content.getBytes(), content.getBytes().length, InetAddress.getByName("10.12.6.24"), port);
	   msocket.send(packet);
   }
   
   public String byte2String(byte[] b){
	   return new String(b);
   }
   
   public void printSocket(DatagramSocket socket){
	   print("socket:"+socket.getInetAddress().toString());
   }
   
   public void printPacket(DatagramPacket packet){
	   print("length:"+packet.getLength());
	   print("port:"+packet.getPort());
	   print("offset:"+packet.getOffset());
	   print("address:"+packet.getAddress().toString());
	   print("socketaddress:"+packet.getSocketAddress().toString());
	   print("data:"+byte2String(packet.getData()));
   }

   public void print(String text){
	   System.out.println(text);
   }
   
 }


