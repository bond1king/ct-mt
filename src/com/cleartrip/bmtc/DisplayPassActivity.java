package com.cleartrip.bmtc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class DisplayPassActivity extends Activity {

	private static final String TEMP_PHOTO_FILE = "barcode_holder.jpg";
	private ImageView picture, barcode;
	private TextView name;
	private TextView validityString;
	private TextView passType, authValue, ticketNumber;
	final String charset = "UTF-8";
	private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// String securekey = "";
		// SecurePreferences pref = new SecurePreferences(this,
		// "bmtc_pass_preferences", securekey, true);
		setContentView(R.layout.activity_display_pass);
		picture = (ImageView) findViewById(R.id.profilePicture);
		barcode = (ImageView) findViewById(R.id.barcode);
		name = (TextView) findViewById(R.id.NameOnCard);
		passType = (TextView) findViewById(R.id.passDescription);
		authValue = (TextView) findViewById(R.id.authValue);
		ticketNumber = (TextView) findViewById(R.id.ticketNumber);
		validityString = (TextView) findViewById(R.id.validString);
		validityString.setText("Valid till: Nov 31, 2014");

		getActionBar().hide();
		
		//getWindow().setFlags(LayoutParams.FLAG_SECURE,                LayoutParams.FLAG_SECURE);

		SharedPreferences pref = getSharedPreferences("bmtc_pass_preferences",
				Context.MODE_PRIVATE);

	
		File file = new File(Environment.getExternalStorageDirectory(),"temporary_holder.jpg");
		decodeFile(file.getAbsolutePath(), picture);

		if (getIntent() != null && getIntent().getExtras() != null) {
			name.setText(getIntent().getStringExtra("name"));
			String type = getIntent().getStringExtra("passType");
			if (type != null) {
				switch (type) {
				case "1":
					// passTypePicture.setImageResource(R.drawable.vajraservices);
					passType.setText("Vajra A/c Bus Pass");
					break;
				case "2":
					passType.setText("BMTC Non A/C Bus Pass");
					break;
				case "3":
					passType.setText("Student Pass");
				default:
					passType.setText(type);
					break;
				}
			}
			authValue.setText(getIntent().getStringExtra("authValue"));
			ticketNumber.setText(getIntent().getStringExtra("ticketNumber"));
			Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			
			try {
				createQRCode(ticketNumber.getText().toString(), getTempFile().getPath() , charset, hintMap, 300, 300);
			} catch (Exception e) {
			}
			
			
		} else if (pref != null) {
			name.setText(pref.getString("name", ""));
			if (pref.getString("passType", "") != null) {
				switch (pref.getString("passType", "")) {
				case "1":
					// passTypePicture.setImageResource(R.drawable.vajraservices);
					passType.setText("Vajra A/c Bus Pass");
					break;
				case "2":
					passType.setText("BMTC Non A/C Bus Pass");
					break;
				default:
					passType.setText(pref.getString("passType", ""));
					break;
				}
			}

		} else 	if (pref.getString("name", null) == null) {
			Intent i = new Intent(this, RegisterActivity.class);
			startActivity(i);
			finish();
		}


	}

	public void decodeFile(String filePath, ImageView picture) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		
		int scale = 1;
		final int height = o.outHeight;
	    final int width = o.outWidth;
	    
	    BitmapFactory.decodeFile(filePath, o);
		
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		if (height > 150 || width > 150) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / scale) > 150
	                && (halfWidth / scale) > 150) {
	            scale *= 2;
	        }
	    }

		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
		picture.setImageBitmap(bitmap);

	}
	
	private File getTempFile() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),	TEMP_PHOTO_FILE);
			try {
				file.createNewFile();
			} catch (IOException e) {
			}

			return file;
		} else {

			return null;
		}
	}
	
	public void createQRCode(String qrCodeData, String filePath,	String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
			throws WriterException, IOException {
		
		BitMatrix result = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
	//	MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
		int width = result.getWidth();
		int height =result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
		int offset = y * width;
		for (int x = 0; x < width; x++) {

		if(result.get(x, y)== true)
		{
		pixels[offset + x] = BLACK ;
		}
		else
		pixels[offset + x] = WHITE ;

		}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		barcode.setImageBitmap(bitmap);
	}

}
