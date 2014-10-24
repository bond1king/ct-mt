package com.cleartrip.bmtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreenActivity extends Activity implements AnimationListener{
	
	private TextView bmtc_logo, cleartrip_logo;
	private boolean bmtc_animation_ended, cleartrip_animation_ended;
	private Animation bottomUp;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		bmtc_logo = (TextView) findViewById(R.id.splash_bmtc);
		cleartrip_logo= (TextView) findViewById(R.id.splash_cleartrip);
		bottomUp = AnimationUtils.loadAnimation(this,R.anim.splash_translate);
		bottomUp.setAnimationListener(this);
		bmtc_logo.startAnimation(bottomUp);
		bmtc_logo.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(bmtc_animation_ended){
			cleartrip_animation_ended=true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent= new Intent(this, MenuActivity.class);
			startActivity(intent);
			finish();
		}else{
			bmtc_animation_ended=true;
			bmtc_logo.setVisibility(View.INVISIBLE);
			bmtc_logo.clearAnimation();
			cleartrip_logo.startAnimation(animation);
			cleartrip_logo.setVisibility(View.VISIBLE);
			
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}
