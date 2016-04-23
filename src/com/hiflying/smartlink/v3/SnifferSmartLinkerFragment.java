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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SnifferSmartLinkerFragment
/*     */   extends Fragment implements OnSmartLinkListener
/*     */ {
/*     */   private static final String TAG = "SnifferSmartLinkerFragment";
/*     */   protected EditText mSsidEditText;
/*     */   protected EditText mPasswordEditText;
/*     */   protected Button mStartButton;
/*     */   protected SnifferSmartLinker mSnifferSmartLinker;
/*  40 */   private boolean mIsConncting = false;
/*  41 */   protected Handler mViewHandler = new Handler();
/*     */   
/*     */   protected ProgressDialog mWaitingDialog;
/*     */   private BroadcastReceiver mWifiChangedReceiver;
/*     */   private Context mAppContext;
/*     */   
/*     */   public void onAttach(Activity activity)
/*     */   {
/*  49 */     super.onAttach(activity);
/*     */     
/*  51 */     this.mAppContext = activity.getApplicationContext();
/*  52 */     R1.initContext(this.mAppContext);
/*     */     
/*  54 */     this.mSnifferSmartLinker = SnifferSmartLinker.getInstence();
/*  55 */     this.mSnifferSmartLinker.setOnSmartLinkListener(this);
/*     */     
/*  57 */     this.mWaitingDialog = new ProgressDialog(activity);
/*  58 */     this.mWaitingDialog.setMessage(getString(R1.string("hiflying_smartlinker_waiting")));
/*  59 */     this.mWaitingDialog.setButton(-2, getString(17039360), new DialogInterface.OnClickListener()
/*     */     {
/*     */ 
/*     */       public void onClick(DialogInterface dialog, int which) {}
/*     */ 
/*  64 */     });
/*  65 */     this.mWaitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
/*     */     {
/*     */ 
/*     */       public void onDismiss(DialogInterface dialog)
/*     */       {
/*  70 */         SnifferSmartLinkerFragment.this.mSnifferSmartLinker.setOnSmartLinkListener(null);
/*  71 */         SnifferSmartLinkerFragment.this.mSnifferSmartLinker.stop();
/*  72 */         SnifferSmartLinkerFragment.this.mIsConncting = false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDetach()
/*     */   {
/*  80 */     super.onDetach();
/*  81 */     this.mSnifferSmartLinker.setOnSmartLinkListener(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  88 */     return inflater.inflate(R1.layout("activity_hiflying_sniffer_smart_linker"), container, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  94 */     super.onViewCreated(view, savedInstanceState);
/*  95 */     this.mSsidEditText = ((EditText)view.findViewById(R1.id("editText_hiflying_smartlinker_ssid")));
/*  96 */     this.mPasswordEditText = ((EditText)view.findViewById(R1.id("editText_hiflying_smartlinker_password")));
/*  97 */     this.mStartButton = ((Button)view.findViewById(R1.id("button_hiflying_smartlinker_start")));
/*  98 */     this.mSsidEditText.setText(getSSid());
/*     */     
/* 100 */     this.mStartButton.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */ 
/*     */       public void onClick(View v)
/*     */       {
/* 105 */         if (!SnifferSmartLinkerFragment.this.mIsConncting)
/*     */         {
/*     */           try
/*     */           {
/* 109 */             SnifferSmartLinkerFragment.this.mSnifferSmartLinker.setOnSmartLinkListener(SnifferSmartLinkerFragment.this);
/*     */             
/* 111 */             SnifferSmartLinkerFragment.this.mSnifferSmartLinker.start(SnifferSmartLinkerFragment.this.mAppContext, SnifferSmartLinkerFragment.this.mPasswordEditText.getText().toString().trim(), new String[] {
/* 112 */               SnifferSmartLinkerFragment.this.mSsidEditText.getText().toString().trim() });
/* 113 */             SnifferSmartLinkerFragment.this.mIsConncting = true;
/* 114 */             SnifferSmartLinkerFragment.this.mWaitingDialog.show();
/*     */           }
/*     */           catch (Exception e) {
/* 117 */             e.printStackTrace();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 122 */     });
/* 123 */     this.mWifiChangedReceiver = new BroadcastReceiver()
/*     */     {
/*     */       public void onReceive(Context context, Intent intent)
/*     */       {
/* 127 */         ConnectivityManager connectivityManager = (ConnectivityManager)SnifferSmartLinkerFragment.this.mAppContext.getSystemService("connectivity");
/* 128 */         NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
/* 129 */         if ((networkInfo != null) && (networkInfo.isConnected())) {
/* 130 */           SnifferSmartLinkerFragment.this.mSsidEditText.setText(SnifferSmartLinkerFragment.this.getSSid());
/* 131 */           SnifferSmartLinkerFragment.this.mPasswordEditText.requestFocus();
/* 132 */           SnifferSmartLinkerFragment.this.mStartButton.setEnabled(true);
/*     */         } else {
/* 134 */           SnifferSmartLinkerFragment.this.mSsidEditText.setText(SnifferSmartLinkerFragment.this.getString(R1.string("hiflying_smartlinker_no_wifi_connectivity")));
/* 135 */           SnifferSmartLinkerFragment.this.mSsidEditText.requestFocus();
/* 136 */           SnifferSmartLinkerFragment.this.mStartButton.setEnabled(false);
/* 137 */           if (SnifferSmartLinkerFragment.this.mWaitingDialog.isShowing()) {
/* 138 */             SnifferSmartLinkerFragment.this.mWaitingDialog.dismiss();
/*     */           }
/*     */         }
/*     */       }
/* 142 */     };
/* 143 */     this.mAppContext.registerReceiver(this.mWifiChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDestroyView()
/*     */   {
/* 149 */     super.onDestroyView();
/*     */     try {
/* 151 */       this.mAppContext.unregisterReceiver(this.mWifiChangedReceiver);
/*     */     } catch (Exception e) {
/* 153 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onLinked(final SmartLinkedModule module)
/*     */   {
/* 162 */     Log.w("SnifferSmartLinkerFragment", "onLinked");
/* 163 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 168 */         Toast.makeText(SnifferSmartLinkerFragment.this.mAppContext, SnifferSmartLinkerFragment.this.getString(R1.string("hiflying_smartlinker_new_module_found"), new Object[] { module.getMac(), module.getModuleIP() }), 0).show();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void onCompleted()
/*     */   {
/* 176 */     Log.w("SnifferSmartLinkerFragment", "onCompleted");
/* 177 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 183 */         Toast.makeText(SnifferSmartLinkerFragment.this.mAppContext, SnifferSmartLinkerFragment.this.getString(R1.string("hiflying_smartlinker_completed")), 0).show();
/* 184 */         SnifferSmartLinkerFragment.this.mWaitingDialog.dismiss();
/* 185 */         SnifferSmartLinkerFragment.this.mIsConncting = false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void onTimeOut()
/*     */   {
/* 193 */     Log.w("SnifferSmartLinkerFragment", "onTimeOut");
/* 194 */     this.mViewHandler.post(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 200 */         Toast.makeText(SnifferSmartLinkerFragment.this.mAppContext, SnifferSmartLinkerFragment.this.getString(R1.string("hiflying_smartlinker_timeout")), 0).show();
/* 201 */         SnifferSmartLinkerFragment.this.mWaitingDialog.dismiss();
/* 202 */         SnifferSmartLinkerFragment.this.mIsConncting = false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private String getSSid()
/*     */   {
/* 209 */     WifiManager wm = (WifiManager)this.mAppContext.getSystemService("wifi");
/* 210 */     if (wm != null) {
/* 211 */       WifiInfo wi = wm.getConnectionInfo();
/* 212 */       if (wi != null) {
/* 213 */         String ssid = wi.getSSID();
/* 214 */         if ((ssid.length() > 2) && (ssid.startsWith("\"")) && (ssid.endsWith("\""))) {
/* 215 */           return ssid.substring(1, ssid.length() - 1);
/*     */         }
/* 217 */         return ssid;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 222 */     return "";
/*     */   }
/*     */ }


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\v3\SnifferSmartLinkerFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */