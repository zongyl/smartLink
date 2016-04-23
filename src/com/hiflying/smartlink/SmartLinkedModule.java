/*    */ package com.hiflying.smartlink;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class SmartLinkedModule implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 833195854008521358L;
/*    */   private String mac;
/*    */   private String ip;
/*    */   private String mid;
/*    */   
/*    */   public String getMac()
/*    */   {
/* 14 */     return this.mac;
/*    */   }
/*    */   
/*    */   public void setMac(String mac) {
/* 18 */     this.mac = mac;
/*    */   }
/*    */   
/*    */   public String getModuleIP() {
/* 22 */     return this.ip;
/*    */   }
/*    */   
/*    */   public void setModuleIP(String moduleIP) {
/* 26 */     this.ip = moduleIP;
/*    */   }
/*    */   
/*    */   public void setMid(String string)
/*    */   {
/* 31 */     this.mid = string;
/*    */   }
/*    */   
/*    */   public String getMid() {
/* 35 */     return this.mid;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 40 */     return 
/* 41 */       "SmartLinkedModule [mac=" + this.mac + ", ip=" + this.ip + ", mid=" + this.mid + "]";
/*    */   }
/*    */ }


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\SmartLinkedModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */