package com.cleartrip.bmtc;

import java.io.File;
import java.io.IOException;
import java.util.zip.Inflater;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class RegisterActivity extends ActionBarActivity implements OnClickListener {

	static TextView dateOfBirth,mobileNumberText;
	private EditText mobileNumberValue;
	static ImageView picture;
	private Bitmap bitmap;
	private Button submitButton;
	static int year = 2000, month=0, day=1;
	private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		dateOfBirth = (TextView) findViewById(R.id.dateOfBirthValue);
		picture = (ImageView) findViewById(R.id.setProfilePicture);
		submitButton = (Button) findViewById(R.id.submitApplication);
		mobileNumberValue = (EditText)findViewById(R.id.mobileNumberValue);
		picture.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		if(getPhoneNumber()!=null && getPhoneNumber().isEmpty()!=true){
			mobileNumberText.setText(getPhoneNumber());
			mobileNumberText.setVisibility(View.VISIBLE);
			mobileNumberValue.setVisibility(View.INVISIBLE);
		}
		
	}
	
	private String getPhoneNumber(){
		TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		return mPhoneNumber;
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setProfilePicture:
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.putExtra("crop", "true");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
				intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 1);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), (CharSequence) e,
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
			break;
		case R.id.submitApplication:
			Intent intent = new Intent(this, DisplayPassActivity.class);
			intent.putExtra("name", ((TextView)findViewById(R.id.nameValue)).getText().toString());
			intent.putExtra("passType", "1");
			startActivity(intent);
			Toast.makeText(this,"Application Submitted",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				String filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",
								Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}

					if (filePath != null) {
						System.out.println(filePath);
						decodeFile(filePath);
					} else {
						bitmap = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			break;
		}
	}
	
	private Uri getTempUri() {
	    return Uri.fromFile(getTempFile());
	}

	private File getTempFile() {

	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

	        File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
	        try {
	            file.createNewFile();
	        } catch (IOException e) {}

	        return file;
	    } else {

	        return null;
	    }
	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		int scale = 1;
		
		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		picture.setImageBitmap(bitmap);

	}
	

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			CharSequence seq = day + " - " + (month+1) + " - " + year;
			RegisterActivity.year = year;
			RegisterActivity.month = month;
			RegisterActivity.day = day;
			dateOfBirth.setText(seq);
		}
	}


}
