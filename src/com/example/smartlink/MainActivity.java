package com.example.smartlink;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hiflying.smartlink.v3.SnifferSmartLinkerActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView)findViewById(R.id.textView_version)).setText("Version: " + getVersionName()) ;
	}
	
	public void startSnifferSmartLinkerActivity(View view) {
		startActivity(new Intent(this, SnifferSmartLinkerActivity.class));
	}
	
	public void startSnifferSmartLinkerFragment(View view) {
		startActivity(new Intent(this, SnifferSmartLinkerFragmentActivity.class));
	}
	
	public void startCustomizedActivity(View view) {
		startActivity(new Intent(this, CustomizedActivity.class));
	}
	
	private String getVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "unknown";
	}
}
