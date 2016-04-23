/*     */ package com.hiflying.smartlink.v3;
/*     */ 
/*     */ import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.R1;
import com.hiflying.smartlink.SmartLinkedModule;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SnifferSmartLinkerActivity
/*     */   extends Activity implements OnSmartLinkListener
/*     */ {
/*     */   private static final String TAG = "SnifferSmartLinkerActivity";
/*     */   protected EditText mSsidEditText;
/*     */   protected EditText mPasswordEditText;
/*     */   protected Button mStartButton;
/*     */   protected SnifferSmartLinker mSnifferSmartLinker;
/*  37 */   private boolean mIsConncting = false;
/*  38 */   protected Handler mViewHandler = new Handler();
/*     */   protected ProgressDialog mWaitingDialog;
/*     */   private BroadcastReceiver mWifiChangedReceiver;
/*     */   
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  44 */     super.onCreate(savedInstanceState);
/*     */     
/*  46 */     R1.initContext(getApplicationContext());
/*  47 */     this.mSnifferSmartLinker = SnifferSmartLinker.getInstence();
/*     */     
/*  49 */     this.mWaitingDialog = new ProgressDialog(this);
/*  50 */     this.mWaitingDialog.setMessage(getString(R1.string("hiflying_smartlinker_waiting")));
/*  51 */     this.mWaitingDialog.setButton(-2, getString(17039360), new DialogInterface.OnClickListener()
/*     */     {
/*     */ 
/*     */       public void onClick(DialogInterface dialog, int which) {}
/*     */ 
/*  56 */     });
/*  57 */     this.mWaitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
/*     */     {
/*     */ 
/*     */       public void onDismiss(DialogInterface dialog)
/*     */       {
/*  62 */         SnifferSmartLinkerActivity.this.mSnifferSmartLinker.setOnSmartLinkListener(null);
/*  63 */         SnifferSmartLinkerActivity.this.mSnifferSmartLinker.stop();
/*  64 */         SnifferSmartLinkerActivity.this.mIsConncting = false;
/*     */       }
/*     */       
/*  67 */     });
/*  68 */     setContentView(R1.layout("activity_hiflying_sniffer_smart_linker"));
/*  69 */     this.mSsidEditText = ((EditText)findViewById(R1.id("editText_hiflying_smartlinker_ssid")));
/*  70 */     this.mPasswordEditText = ((EditText)findViewById(R1.id("editText_hiflying_smartlinker_password")));
/*  71 */     this.mStartButton = ((Button)findViewById(R1.id("button_hiflying_smartlinker_start")));
/*  72 */     this.mSsidEditText.setText(getSSid());
/*     */     
/*  74 */     this.mStartButton.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */ 
/*     */       public void onClick(View v)
/*     */       {
/*  79 */         if (!SnifferSmartLinkerActivity.this.mIsConncting)
/*     */         {
/*     */           try
/*     */           {
/*  83 */             SnifferSmartLinkerActivity.this.mSnifferSmartLinker.setOnSmartLinkListener(SnifferSmartLinkerActivity.this);
/*     */             
/*  85 */             SnifferSmartLinkerActivity.this.mSnifferSmartLinker.start(SnifferSmartLinkerActivity.this.getApplicationContext(), SnifferSmartLinkerActivity.this.mPasswordEditText.getText().toString().trim(), new String[] {
/*  86 */               SnifferSmartLinkerActivity.this.mSsidEditText.getText().toString().trim() });
/*  87 */             SnifferSmartLinkerActivity.this.mIsConncting = true;
/*  88 */             SnifferSmartLinkerActivity.this.mWaitingDialog.show();
/*     */           }
/*     */           catch (Exception e) {
/*  91 */             e.printStackTrace();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*  96 */     });
/*  97 */     this.mWifiChangedReceiver = new BroadcastReceiver()
/*     */     {
/*     */       public void onReceive(Context context, Intent intent)
/*     */       {
/* 101 */         ConnectivityManager connectivityManager = (ConnectivityManager)SnifferSmartLinkerActivity.this.getSystemService("connectivity");
/* 102 */         NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
/* 103 */         if ((networkInfo != null) && (networkInfo.isConnected())) {
/* 104 */           SnifferSmartLinkerActivity.this.mSsidEditText.setText(SnifferSmartLinkerActivity.this.getSSid());
/* 105 */           SnifferSmartLinkerActivity.this.mPasswordEditText.requestFocus();
/* 106 */           SnifferSmartLinkerActivity.this.mStartButton.setEnabled(true);
/*     */         } else {
/* 108 */           SnifferSmartLinkerActivity.this.mSsidEditText.setText(SnifferSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_no_wifi_connectivity")));
/* 109 */           SnifferSmartLinkerActivity.this.mSsidEditText.requestFocus();
/* 110 */           SnifferSmartLinkerActivity.this.mStartButton.setEnabled(false);
/* 111 */           if (SnifferSmartLinkerActivity.this.mWaitingDialog.isShowing()) {
/* 112 */             SnifferSmartLinkerActivity.this.mWaitingDialog.dismiss();
/*     */           }
/*     */         }
/*     */       }
/* 116 */     };
/* 117 */     registerReceiver(this.mWifiChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
/*     */   }
/*     */   
/*     */ 
/*     */   protected void onDestroy()
/*     */   {
/* 123 */     super.onDestroy();
/* 124 */     this.mSnifferSmartLinker.setOnSmartLinkListener(null);
/*     */     try {
/* 126 */       unregisterReceiver(this.mWifiChangedReceiver);
/*     */     } catch (Exception e) {
/* 128 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onLinked(final SmartLinkedModule module)
/*     */   {
/* 137 */     Log.w("SnifferSmartLinkerActivity", "onLinked");
/* 138 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 143 */         Toast.makeText(SnifferSmartLinkerActivity.this.getApplicationContext(), SnifferSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_new_module_found"), new Object[] { module.getMac(), module.getModuleIP() }), 0).show();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onCompleted()
/*     */   {
/* 152 */     Log.w("SnifferSmartLinkerActivity", "onCompleted");
/* 153 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 159 */         Toast.makeText(SnifferSmartLinkerActivity.this.getApplicationContext(), SnifferSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_completed")), 0).show();
/* 160 */         SnifferSmartLinkerActivity.this.mWaitingDialog.dismiss();
/* 161 */         SnifferSmartLinkerActivity.this.mIsConncting = false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onTimeOut()
/*     */   {
/* 170 */     Log.w("SnifferSmartLinkerActivity", "onTimeOut");
/* 171 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 177 */         Toast.makeText(SnifferSmartLinkerActivity.this.getApplicationContext(), SnifferSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_timeout")), 0).show();
/* 178 */         SnifferSmartLinkerActivity.this.mWaitingDialog.dismiss();
/* 179 */         SnifferSmartLinkerActivity.this.mIsConncting = false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private String getSSid()
/*     */   {
/* 186 */     WifiManager wm = (WifiManager)getSystemService("wifi");
/* 187 */     if (wm != null) {
/* 188 */       WifiInfo wi = wm.getConnectionInfo();
/* 189 */       if (wi != null) {
/* 190 */         String ssid = wi.getSSID();
/* 191 */         if ((ssid.length() > 2) && (ssid.startsWith("\"")) && (ssid.endsWith("\""))) {
/* 192 */           return ssid.substring(1, ssid.length() - 1);
/*     */         }
/* 194 */         return ssid;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 199 */     return "";
/*     */   }
/*     */ }


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\v3\SnifferSmartLinkerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */