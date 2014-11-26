package com.cleartrip.bmtc;

import java.io.File;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RegisterActivity extends ActionBarActivity implements
		OnClickListener {

	static TextView dateOfBirth, mobileNumberText, errorString, passTypes;
	private EditText mobileNumberValue;
	static ImageView picture;
	private Bitmap bitmap;
	private Button submitButton;
	private String passType;
	private int gender;
	private RelativeLayout setProfilePictureLayout;
	// private RadioButton passtype1, passtype2;
	static int year = 2000, month = 0, day = 1;
	final int REQ_HEIGHT=150;
	final int REQ_WIDTH=150;
	private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";
	private SharedPreferences pref;
	private final String PASS_TYPE = "passType";
	private final String NAME = "name";
	private final String GENDER = "gender";
	private final String ADDRESS = "address";
	private final String MOBILE_PHONE = "mobilePhone";
	private final String DATE_OF_BIRTH = "dateOfBirth";
	private SharedPreferences.Editor editor;
	private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

	// SecurePreferences editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		dateOfBirth = (TextView) findViewById(R.id.dateOfBirthValue);
		picture = (ImageView) findViewById(R.id.setProfilePicture);
		submitButton = (Button) findViewById(R.id.submitApplication);
		mobileNumberValue = (EditText) findViewById(R.id.mobileNumberValue);
		mobileNumberText = (TextView) findViewById(R.id.mobileNumberText);
		errorString = (TextView) findViewById(R.id.errorString);
		// passtype1 = (RadioButton) findViewById(R.id.passType1);
		// passtype2 = (RadioButton) findViewById(R.id.passType2);
		findViewById(R.id.passTypes).setOnClickListener(this);
		registerForContextMenu(findViewById(R.id.passTypes));
		setProfilePictureLayout = (RelativeLayout) findViewById(R.id.profilePictureLayout);
		setProfilePictureLayout.setOnClickListener(this);
		picture.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		String phoneNumber = getPhoneNumber();
		if (phoneNumber != null && phoneNumber.isEmpty() != true) {
			mobileNumberText.setText(phoneNumber);
			mobileNumberText.setVisibility(View.VISIBLE);
			mobileNumberValue.setVisibility(View.INVISIBLE);
		}
		// Drawable s = getResources().getDrawable(R.drawable.text1);
		// getActionBar().setBackgroundDrawable(s);

		pref = getSharedPreferences("bmtc_pass_preferences", 0);
		editor = pref.edit();
		// editor = new
		// SecurePreferences(this,"bmtc_pass_preferences",securekey,true);
	}

	public void registerForContextMenu(View view) {
		view.setOnCreateContextMenuListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.passtype, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.pass_1:
			((TextView) findViewById(R.id.passTypes))
					.setText("Vajra A/C Daily pass");
			passType = "1";
			return true;
		case R.id.pass_2:
			((TextView) findViewById(R.id.passTypes))
					.setText("Non A/C Daily pass");
			passType = "2";
			return true;
		case R.id.pass_3:
			((TextView) findViewById(R.id.passTypes)).setText("Student pass");
			passType = "3";
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	private String getPhoneNumber() {
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		return mPhoneNumber;

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putString(PASS_TYPE, passType);
	    savedInstanceState.putString(NAME, ((TextView) findViewById(R.id.nameValue)).getText().toString());
	    savedInstanceState.putInt(GENDER,((RadioButton) findViewById(R.id.radioButtonFemale)).isChecked() ? 1:0);
	    savedInstanceState.putString(ADDRESS, ((TextView) findViewById(R.id.addressValue)).getText().toString());
	    savedInstanceState.putString(DATE_OF_BIRTH,((TextView) findViewById(R.id.dateOfBirthValue)).getText().toString());
	    savedInstanceState.putString(MOBILE_PHONE, ((TextView) findViewById(R.id.mobileNumberValue)).getText().toString());
	    super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.passTypes:
			openContextMenu(v);
			break;
		case R.id.profilePictureLayout:
		case R.id.setProfilePicture:
			try {
				Intent intent = new Intent(
						Intent.ACTION_PICK,	android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.putExtra("crop", "true");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
				intent.putExtra("outputFormat",	Bitmap.CompressFormat.JPEG.toString());
				startActivityForResult(	Intent.createChooser(intent, "Select Picture"), 1);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
			break;
		case R.id.submitApplication:
			if (validateForm()) {
				editor.putString("name", 		((TextView) findViewById(R.id.nameValue)).getText().toString());
				editor.putString("dateOfBirth",	((TextView) findViewById(R.id.dateOfBirthValue)).getText().toString());
				editor.putString("address",		((TextView) findViewById(R.id.addressValue)).getText().toString());
				editor.putString("emailAddress",((TextView) findViewById(R.id.nameValue)).getText()	.toString());
				editor.putString("mobilePhone",	((TextView) findViewById(R.id.mobileNumberValue)).getText().toString());
				editor.putString("passType",	((TextView) findViewById(R.id.passTypes)).getText().toString());
				editor.putString("gender",		((RadioButton) findViewById(R.id.radioButtonFemale)).isChecked() ? "female" : "male");
				gender = ((RadioButton) findViewById(R.id.radioButtonFemale)).isChecked() ? 1:0;
				editor.commit();
				
				AsyncHttpClient asyncHttpClient = AsyncHttpClientUtils.getCommonAsyncHTTPClient();
				
				StringBuilder sb = new StringBuilder("http://106.206.220.168/bmtc/register.php?");
				sb.append("key=").append(Secure.getString(RegisterActivity.this.getContentResolver(), Secure.ANDROID_ID)).append("&");
				sb.append("name=").append(pref.getString("name", null)).append("&");
				sb.append("add=").append(pref.getString("address", null)).append("&");
				sb.append("gen=").append(gender).append("&");
				sb.append("mb=").append(pref.getString("mobilePhone", null)).append("&");
				sb.append("dob=").append(pref.getString("dateOfBirth", null)).append("&");
				sb.append("pt=").append(passType);
				
				asyncHttpClient.get(sb.toString(),new AsyncHttpResponseHandler(){
		            
		            @Override
		            public void onSuccess(String response) {
		                super.onSuccess(response);
		                response=response.trim();
		                if(response != null && response.length() != 0){
		                	String [] resArray = response.split(",");
		                	Intent i = new Intent(RegisterActivity.this, DisplayPassActivity.class);
		                	i.putExtra("name", resArray[0]);
		                	i.putExtra("passType", resArray[1]);
		                	i.putExtra("authValue", resArray[2]);
		                	i.putExtra("ticketNumber",resArray[3]);
		                	startActivity(i);
		                	finish();
		               } else if (response!=null){
		            	   Toast.makeText(getApplicationContext(), "Cannot connect to the server, Please try again",Toast.LENGTH_LONG).show();
		               }
		            }
		            
		            @Override
		            public void onFailure(Throwable arg0, String arg1) {
		                super.onFailure(arg0, arg1);
		                Toast.makeText(getApplicationContext(), "Check your internet connection and try again",Toast.LENGTH_LONG).show();
		                Intent i = new Intent(RegisterActivity.this, DisplayPassActivity.class);
	                	i.putExtra("name", pref.getString("name", "test"));
	                	i.putExtra("passType", passType);
	                	i.putExtra("authValue", ""+(int)(Math.random()*1000));
	                	i.putExtra("ticketNumber","CTMT"+ (int)(Math.random()*1000));
	                	startActivity(i);
	                	finish();
		                               
		            }
		        });
				
				//Intent intent = new Intent(this, DisplayPassActivity.class);
				//intent.putExtra("name",
					//	((TextView) findViewById(R.id.nameValue)).getText()
					//			.toString());
		//		intent.putExtra("passType",((TextView) findViewById(R.id.passTypes)).getText()
			//			.toString());
				//finish();
			//	startActivity(intent);
			}
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
					String filemanagerstring = selectedImageUri.getPath();
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",	Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}

					if (filePath != null) {
						decodeFile(filePath);
						findViewById(R.id.setProfilePictureText).setVisibility(	View.GONE);
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

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
			
		final int height = o.outHeight;
	    final int width = o.outWidth;
	    
	    BitmapFactory.decodeFile(filePath, o);

		int scale = 1;
		if (height > REQ_HEIGHT || width > REQ_WIDTH) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / scale) > REQ_HEIGHT
	                && (halfWidth / scale) > REQ_WIDTH) {
	            scale *= 2;
	        }
	    }

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		picture.setImageBitmap(bitmap);

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
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
			CharSequence seq = day + "-" + (month + 1) + "-" + year;
			RegisterActivity.year = year;
			RegisterActivity.month = month;
			RegisterActivity.day = day;
			dateOfBirth.setText(seq);
		}
	}

	public boolean validateForm() {
		errorString.setVisibility(View.VISIBLE);
		if (((TextView) findViewById(R.id.passTypes)).getText().toString()
				.isEmpty() == true) {
			errorString.clearComposingText();
			openContextMenu(findViewById(R.id.passTypes));
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		if(picture.getDrawable()==null){
			errorString.setText("Set a passport size photo from your gallery");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}		
		if (((TextView) findViewById(R.id.nameValue)).getText().toString()
				.isEmpty() == true) {
			errorString.setText("Name cannot be empty");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		if (((TextView) findViewById(R.id.addressValue)).getText().toString()
				.isEmpty() == true) {
			errorString.setText("address cannot be empty");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		if (dateOfBirth.getText().toString().isEmpty() == true) {
			errorString.setText("Date of birth cannot be empty");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		if (mobileNumberValue.isShown()
				&& mobileNumberValue.getText().toString().isEmpty() == true) {
			errorString.setText("mobile number cannot be empty");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		if (mobileNumberText.isShown()
				&& mobileNumberText.getText().toString().isEmpty() == true) {
			errorString.setText("mobile number cannot be empty");
			((ScrollView)findViewById(R.id.touchnGo)).fullScroll(ScrollView.FOCUS_UP);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

}
