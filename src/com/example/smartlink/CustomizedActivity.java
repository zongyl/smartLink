package com.example.smartlink;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.SmartLinkedModule;

public class CustomizedActivity extends Activity implements OnSmartLinkListener{
	
	private static final String TAG = "CustomizedActivity";

	protected EditText mSsidEditText;
	protected EditText mPasswordEditText;
	protected Button mStartButton, mStopButton;
	protected SnifferSmartLinker mSnifferSmartLinker;
	private boolean mIsConncting = false;
	protected Handler mViewHandler = new Handler();
	protected ProgressDialog mWaitingDialog;
	private BroadcastReceiver mWifiChangedReceiver;
	
	private MulticastLock multicastLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSnifferSmartLinker = SnifferSmartLinker.getInstance();
		
		mWaitingDialog = new ProgressDialog(this);
		mWaitingDialog.setMessage(getString(R.string.hiflying_smartlinker_waiting));
		mWaitingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		mWaitingDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {

				mSnifferSmartLinker.setOnSmartLinkListener(null);
				mSnifferSmartLinker.stop();
				mIsConncting = false;
			}
		});
		
		setContentView(R.layout.activity_customized);
		mSsidEditText = (EditText) findViewById(R.id.editText_hiflying_smartlinker_ssid);
		mPasswordEditText = (EditText) findViewById(R.id.editText_hiflying_smartlinker_password);
		mStartButton = (Button) findViewById(R.id.button_hiflying_smartlinker_start);
		mStopButton = (Button) findViewById(R.id.button_hiflying_smartlinker_stop);
		mSsidEditText.setText(getSSid());
		mPasswordEditText.setText("manniukejiphone");
		
		mStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSnifferSmartLinker.stop();
			}
		});
		
		mStartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mIsConncting){
					new Thread(new Runnable() {
						public void run() {
							try {
								mSnifferSmartLinker.setOnSmartLinkListener(CustomizedActivity.this);
								mSnifferSmartLinker.start(getApplicationContext(), mPasswordEditText.getText().toString().trim(), 
										mSsidEditText.getText().toString().trim());
								mIsConncting = true;
								mWaitingDialog.show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		
		mWifiChangedReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (networkInfo != null && networkInfo.isConnected()) {
					mSsidEditText.setText(getSSid());
					mPasswordEditText.requestFocus();
					mStartButton.setEnabled(true);
				}else {
					mSsidEditText.setText(getString(R.string.hiflying_smartlinker_no_wifi_connectivity));
					mSsidEditText.requestFocus();
					mStartButton.setEnabled(false);
					if (mWaitingDialog.isShowing()) {
						mWaitingDialog.dismiss();
					}
				}
			}
		};
		registerReceiver(mWifiChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		allowMulticast();
		
		/*content = "m\0p\0";
		
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
		
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NetUtil.sendData(bytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();*/
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
	protected void onDestroy() {
		super.onDestroy();
		mSnifferSmartLinker.setOnSmartLinkListener(null);
		try {
			unregisterReceiver(mWifiChangedReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onLinked(final SmartLinkedModule module) {
		// TODO Auto-generated method stub
		
		Log.w(TAG, "onLinked");
		mViewHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), getString(R.string.hiflying_smartlinker_new_module_found, module.getMac(), module.getModuleIP()), 
						Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void onCompleted() {
		
		Log.w(TAG, "onCompleted");
		mViewHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), getString(R.string.hiflying_smartlinker_completed), 
						Toast.LENGTH_SHORT).show();
				mWaitingDialog.dismiss();
				mIsConncting = false;
			}
		});
	}


	@Override
	public void onTimeOut() {
		
		Log.w(TAG, "onTimeOut");
		mViewHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), getString(R.string.hiflying_smartlinker_timeout), 
						Toast.LENGTH_SHORT).show();
				mWaitingDialog.dismiss();
				mIsConncting = false;
			}
		});
	}	

	private String getSSid(){
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wm != null){
			WifiInfo wi = wm.getConnectionInfo();
			if(wi != null){
				String ssid = wi.getSSID();
				if(ssid.length()>2 && ssid.startsWith("\"") && ssid.endsWith("\"")){
					return ssid.substring(1,ssid.length()-1);
				}else{
					return ssid;
				}
			}
		}
		return "";
	}
	
	private void allowMulticast(){
		WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		multicastLock = wifiManager.createMulticastLock("multicast.demo");
		multicastLock.acquire();
	}
	
}
