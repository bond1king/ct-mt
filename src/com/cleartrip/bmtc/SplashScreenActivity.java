package com.cleartrip.bmtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SplashScreenActivity extends Activity implements AnimationListener {

	private TextView cleartrip_logo;
	private Animation fadein;
	private AsyncHttpClient asyncHttpClient;
	private String name, type, authValue, ticketNumber;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * setContentView(R.layout.splashscreen); cleartrip_logo = (TextView)
		 * findViewById(R.id.splash_cleartrip); fadein =
		 * AnimationUtils.loadAnimation(this, R.animator.fadein);
		 * fadein.setAnimationListener(this);
		 * cleartrip_logo.startAnimation(fadein);
		 */
		setContentView(R.layout.activity_splash);
		callDisplayActivity();
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		callDisplayActivity();
	}

	private void callDisplayActivity() {
		
		
		
		String securekey = Secure.getString(SplashScreenActivity.this.getContentResolver(), Secure.ANDROID_ID);
		
		final Runnable splashScreenRunnable = new Runnable() {
			public void run() {
				Intent i;
				i = new Intent(SplashScreenActivity.this, DisplayPassActivity.class);
				i.putExtra("name", name);
				i.putExtra("passType", type.trim());
				i.putExtra("authValue", authValue);
            	i.putExtra("ticketNumber",ticketNumber);
				startActivity(i);
				finish();
			}
		};
		
		asyncHttpClient = AsyncHttpClientUtils.getCommonAsyncHTTPClient();
		
		String url = "http://106.206.220.168/bmtc/login.php?key="+securekey;
		
		asyncHttpClient.get(url, new AsyncHttpResponseHandler(){
            
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                response=response.trim();
                if(response != null && response.length() != 0){
                	String [] resArray = response.split(",");
                	name = resArray[0];
                	type = resArray[1];
                	authValue = resArray[2];
                	ticketNumber = resArray[3];
                	Handler handler = new Handler();
                	handler.postDelayed(splashScreenRunnable,1000);
               } else if (response!=null){
            	   Intent i = new Intent(SplashScreenActivity.this, RegisterActivity.class);
            	   startActivity(i);
            	   finish();
               }
            }
            
            @Override
            public void onFailure(Throwable arg0, String arg1) {
                super.onFailure(arg0, arg1);
                Toast.makeText(getApplicationContext(), "Check your internet connection and try again",Toast.LENGTH_LONG).show();
                Intent i = new Intent(SplashScreenActivity.this, RegisterActivity.class);
         	    startActivity(i);
         	    finish();
            }
        });
		
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

}
