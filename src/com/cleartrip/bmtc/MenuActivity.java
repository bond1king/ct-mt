package com.cleartrip.bmtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuActivity extends Activity implements OnClickListener{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		findViewById(R.id.displayPass).setOnClickListener(this);
		findViewById(R.id.buyPass).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buyPass:
			Intent i1 = new Intent(this, RegisterActivity.class);
			startActivity(i1);
			break;
		case R.id.displayPass:
			Intent i2= new Intent(this, DisplayPassActivity.class);
			startActivity(i2);
			break;
		}
		
	}

}
