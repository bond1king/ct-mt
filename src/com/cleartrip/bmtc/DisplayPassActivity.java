package com.cleartrip.bmtc;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayPassActivity extends Activity {
	
	private ImageView picture;
	private TextView name;
	private TextView validFromDate, validToDate;
	private TextView passType;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_pass);
		picture = (ImageView) findViewById(R.id.profilePicture);
		name = (TextView) findViewById(R.id.NameOnCard);
		validFromDate = (TextView) findViewById(R.id.validFromDate);
		validToDate = (TextView) findViewById(R.id.validToDate);
		validFromDate.setText("1/11/2014");
		validToDate.setText("31/11/2014");
		File file = new File(Environment.getExternalStorageDirectory(),"temporary_holder.jpg");
		decodeFile(file.getAbsolutePath());
		
		if (getIntent() != null && getIntent().getExtras() != null) {
			name.setText(getIntent().getStringExtra("name"));
			//passType.setText(getIntent().getStringExtra("passType"));
		}
		
	}
	
	public void decodeFile(String filePath) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);
		int scale = 1;
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
		picture.setImageBitmap(bitmap);

	}
	

}
