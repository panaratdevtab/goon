package com.tmstudio.goon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class Diary extends Activity {
	
	private String picPath;
	private Intent pictureActionIntent = null;
	protected static final int CAMERA_REQUEST = 0;
	protected static final int GALLERY_PICTURE = 1;
	protected static final int PIC_CROP = 2;
	private Bitmap bitmap;
	private int serverResponseCode = 0;
	private String upLoadServerUri = null;
	private Intent cropIntent;
	
	// params for signin with fb
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static String message = "Sample status posted from android app";
	// for share fb
	ImageView shareFbButton;
	
	// params for progress bar
	LinearLayout progress;
	LinearLayout frame;
	LinearLayout showProgressLinearLayout;
	RelativeLayout PinPosition;
	int maxwidth;

	TextView showProgress;
	TextView wombAge;
	LinearLayout FrameShowProgress;
	LinearLayout FrameFullProgress;

	TextView showDate;
	TextView fullday;

	// params for diary
	LinearLayout todayWeightLayout;
	TextView todayWeightTextView;

	ImageView addPhoto;
	TextView texthere;
	Button done;

	String note, date;

	// for dialog method
	String showWeight;
	String finalWeight;
	float w;
	String noteString;
	GoonDB goonDB;
	HashMap<String, String> map;
	Calendar calen;

	//
	String resMessage;
	String resCode;

	// params for service
	private AQuery aq;
	private String CheckStatus;
	private String CheckMessage;
	String strDate;
	final Context context = this;
	private String diry_uid, diry_image, diry_text, diry_weight, diary_date;

	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String status_login_fb = "1";
	private String status_login_acc = "2";
	private String pref_fid, pref_uid, pref_email, pref_password, pref_child_name, pref_child_age, pref_avatar, pref_dad_name, pref_mom_name, pref_child_sex, pref_born_date, pref_status, pref_status_login, pref_contry;
	
	
	//check internet
	Boolean isInternetPresent = false;
	NetworkHelper cd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.diary);
		
		upLoadServerUri = "http://rid.in.th/service/upload.php";
		goonDB = new GoonDB(this);
		date = getIntent().getStringExtra("date");
		insertTestData();
		
		aq = new AQuery(this);
		// Session class instance
		sessionlogin = new UserSessionManager(getApplicationContext());	
		getSession();
		
		// Check Internet
		cd = new NetworkHelper(Diary.this.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
		
		// date one (today)
		Calendar c = Calendar.getInstance();
		SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
		String dateStr1 = curFormater.format(c.getTime()); 
		Date todayDate = null;
		try {
			todayDate = curFormater.parse(dateStr1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// date two 
		// convert date format
		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy/MM/dd");
	    Date dateBfConvert = null;
		try {
			dateBfConvert = fromFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    SimpleDateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");
	    String dateStr2 = toFormat.format(dateBfConvert);
	    Date clickedDate = null;
	    try {
			clickedDate = toFormat.parse(dateStr2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		// get days between 2 date
		long days = getDateDiffString(todayDate, clickedDate);
		
		// get resolution width and height
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				maxwidth = size.x;
				
				// /// Progress event
				showProgress = (TextView) findViewById(R.id.showProgress);
				fullday = (TextView) findViewById(R.id.fulldays);
				wombAge = (TextView) findViewById(R.id.wombage);

				frame = (LinearLayout) findViewById(R.id.frame);
				progress = (LinearLayout) findViewById(R.id.progress);
				
				showProgressLinearLayout = (LinearLayout) findViewById(R.id.showProgressLayout);
				
				// convert 60 dp to px
				Resources r1 = getResources();
				float layoutPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r1.getDisplayMetrics());
				
				// convert 80 dp to px
				Resources r2 = getResources();
				float layoutWeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r1.getDisplayMetrics());
				
				int width =  (Math.round(layoutPx) * 2) + Math.round(layoutWeightPx);
				
				        int frameWidth = maxwidth - (width);
				        
				        int currentDay = Integer.parseInt(pref_child_age);

						float currentProgressValue = ((currentDay * frameWidth) / 280);
						
						frame.setLayoutParams(new LinearLayout.LayoutParams(Math.round(frameWidth), LinearLayout.LayoutParams.MATCH_PARENT));
						LinearLayout.LayoutParams frameParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
						//frameParams.setMargins(0, 16, 0, 0);
						frame.setLayoutParams(frameParams);
						
						progress.setLayoutParams(new LinearLayout.LayoutParams(Math.round(currentProgressValue),LinearLayout.LayoutParams.MATCH_PARENT));

						showProgress.setText(Math.round(currentDay) + "\n" + getResources().getString(R.string.day));
						fullday.setText("280\n" + getResources().getString(R.string.day));
						wombAge.setText("●  "+ getResources().getString(R.string.womb_age) + " " + Math.round(currentDay) + " " + getResources().getString(R.string.day));

						PinPosition = (RelativeLayout) findViewById(R.id.pinpos);
						

						// convert 12 dp to px
						Resources r3 = getResources();
						float pinPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r3.getDisplayMetrics());
						
						// position of pin (mark Today)
						RelativeLayout.LayoutParams paramsOfToday = (RelativeLayout.LayoutParams) PinPosition.getLayoutParams();
						paramsOfToday.setMargins(Math.round((layoutPx - pinPx) + currentProgressValue), 0, Math.round(pinPx), 0);
						PinPosition.setLayoutParams(paramsOfToday);
						
						////set gap Layout between today and choosed
						LinearLayout progress2 = (LinearLayout) findViewById(R.id.progress2);
						
						/** calculate start date **/
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						cal.add(Calendar.DATE, -currentDay);
						String startDateString = dateFormat.format(cal.getTime());
						
						 Date startdDate = null;
						    try {
								startdDate = toFormat.parse(startDateString);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						/** calculate finished **/ catch (java.text.ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						// calculate days from start date to clicked date
						long daysFromStart = getDateDiffString(startdDate, clickedDate);
						Toast.makeText(getApplicationContext(), daysFromStart+"", Toast.LENGTH_LONG).show();
						float choosedProgressValue = ((daysFromStart * frameWidth) / 280);
						//calculate finish//
						
						if(daysFromStart >= days && daysFromStart != 0){
							progress2.setLayoutParams(new LinearLayout.LayoutParams(Math.round(choosedProgressValue - currentProgressValue),LinearLayout.LayoutParams.MATCH_PARENT));
						}
						showProgress.setText(Math.round(currentDay) + "\n" + getResources().getString(R.string.day));
						fullday.setText("280\n" + getResources().getString(R.string.day));
						wombAge.setText("●  "+ getResources().getString(R.string.womb_age) + " " + Math.round(currentDay) + " " + getResources().getString(R.string.day));

						RelativeLayout PinPosition2 = (RelativeLayout) findViewById(R.id.pinpos_2);
						
						// position of pin (mark day of choosed)
						if(daysFromStart == currentDay){
							ImageView pin2 = (ImageView) findViewById(R.id.pin2);
							pin2.setVisibility(View.GONE);
						}
						else if(daysFromStart < 0){
							ImageView pin2 = (ImageView) findViewById(R.id.pin2);
							pin2.setVisibility(View.GONE);
						}
						else if(daysFromStart <= 280 && daysFromStart != currentDay){
							RelativeLayout.LayoutParams paramsOfChoosed = (RelativeLayout.LayoutParams) PinPosition2.getLayoutParams();
							paramsOfChoosed.setMargins(Math.round(layoutPx - pinPx) + Math.round(choosedProgressValue), 0, 0, 0);
							PinPosition2.setLayoutParams(paramsOfChoosed);
						}
						else {
							RelativeLayout.LayoutParams paramsOfChoosed = (RelativeLayout.LayoutParams) PinPosition2.getLayoutParams();
							paramsOfChoosed.setMargins(Math.round(layoutPx - pinPx) + ((280*frameWidth) / 280), 0, 0, 0);
							PinPosition2.setLayoutParams(paramsOfChoosed);
						}
						
						
				
		// Diary event
		// fill today's weight
		todayWeightLayout = (LinearLayout) findViewById(R.id.todayweightLayout);
		todayWeightTextView = (TextView) findViewById(R.id.todayweight);
		todayWeightTextView.setText(goonDB.getWeightByDate(date));
		todayWeightLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				w = Float.parseFloat(todayWeightTextView.getText().toString());
				Dialog(w, date);
			}
		});

		addPhoto = (ImageView) findViewById(R.id.addphoto);
		texthere = (TextView) findViewById(R.id.texthere);

		texthere.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogForNote();
			}
		});
		
		addPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startDialog();
			}
		});
		setDiaryNote(date);
		new ImageLoadTask().execute();
		
		// set scale of imageview
		ImageView tipsPlaster = (ImageView) findViewById(R.id.tipsplaster);
		tipsPlaster.setScaleType(ScaleType.FIT_XY);


		// Share to fb
		shareFbButton = (ImageView) findViewById(R.id.sharefb);
		shareFbButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(pref_status_login.equals("1")){
					// post wall facebook
					postImage();
				}else if (pref_status_login.equals("2")){
					// intent to facebook dialog
					Intent intent = new Intent(Diary.this, ShareFacebook.class);
					startActivity(intent);
				}
			}
		});
	}

	public void getSession() {
		// get user data from session
		HashMap<String, String> user = sessionlogin.getUserDetails();
		// get name
		pref_uid = user.get(UserSessionManager.KEY_UID);
		pref_fid = user.get(UserSessionManager.KEY_FID);
		pref_email = user.get(UserSessionManager.KEY_EMAIL);
		pref_password = user.get(UserSessionManager.KEY_PASSWORD);
		pref_child_name = user.get(UserSessionManager.KEY_CHILD_NAME);
		pref_child_age = user.get(UserSessionManager.KEY_CHILD_AGE);
		pref_avatar = user.get(UserSessionManager.KEY_AVATAR);
		pref_dad_name = user.get(UserSessionManager.KEY_DAD_NAME);
		pref_mom_name = user.get(UserSessionManager.KEY_MOM_NAME);
		pref_child_sex = user.get(UserSessionManager.KEY_CHILD_SEX);
		pref_born_date = user.get(UserSessionManager.KEY_BORN_DATE);
		pref_status = user.get(UserSessionManager.KEY_STATUS);
		pref_status_login = user.get(UserSessionManager.KEY_STATUS_LOGIN);
		pref_contry = user.get(UserSessionManager.KEY_CONTRY);
	}


	private class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap map = null;
			HashMap<String, String> hashmap = goonDB.getDiaryNoteByDate(date);
			if (hashmap != null) {
				// String url = hashmap.get("image");
				String url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkveWawZTl-yHREE1P4_Vvxn8bTeu1Anyj5T1iofA_TtbEF2vQBw";
				map = downloadImage(url);
			} else {
				Drawable myDrawable = getResources().getDrawable(
						R.drawable.camera);
				map = ((BitmapDrawable) myDrawable).getBitmap();
			}
			return map;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			addPhoto.setImageBitmap(transform(result));
		}
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			try {
				stream = getHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return bitmap;
		}
	}
	private InputStream getHttpConnection(String urlString) throws IOException {
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();
			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stream;
	}

	// take a photo
	public void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");
		myAlertDialog.setMessage("How do you want to set your picture?");

		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								Intent.ACTION_GET_CONTENT, null);
						pictureActionIntent.setType("image/*");
						pictureActionIntent.putExtra("crop", "true");
						pictureActionIntent.putExtra("aspectX", 0);
						pictureActionIntent.putExtra("aspectY", 0);
						pictureActionIntent.putExtra("return-data", true);
						startActivityForResult(pictureActionIntent,
								GALLERY_PICTURE);
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pictureActionIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI
										.toString());
						pictureActionIntent.putExtra("crop", "true");
						pictureActionIntent.putExtra("aspectX", 1);
						pictureActionIntent.putExtra("aspectY", 1);
						pictureActionIntent.putExtra("outputX", 2000);
						pictureActionIntent.putExtra("outputY", 2000);
						startActivityForResult(pictureActionIntent,
								CAMERA_REQUEST);

					}
				});
		myAlertDialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
				if (data != null) {
					Uri selectedUri = data.getData();
					Bitmap bmp = (Bitmap) data.getExtras().get("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					performCrop(selectedUri);
					picPath = getPath(selectedUri);
					bitmap = bmp;
					createDirectoryAndSaveFile(bitmap);
					addPhoto.setImageBitmap(transform(bmp));
					bmp.recycle();
					uploadImage(picPath);
				}
			} else if (resultCode == RESULT_OK
					&& requestCode == GALLERY_PICTURE) {
				if (data != null) {
					Bitmap photo = data.getExtras().getParcelable("data");
					Uri selectedUri = getImageUri(photo);
					picPath = getPath(selectedUri);
					bitmap = photo;
					addPhoto.setImageBitmap(transform(photo));
//					createDirectoryAndSaveFile(bitmap);
					uploadImage(picPath);
				}
			}
		} catch (RuntimeException e) {
			Log.d("Runtime Memory", e.getMessage() + " / "
					+ Runtime.getRuntime().totalMemory());
			System.out.println();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public Uri getImageUri(Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(getContentResolver(), inImage,
				"Title", null);
		return Uri.parse(path);
	}
	
	private void createDirectoryAndSaveFile(Bitmap imageToSave) {
		File direct = new File(Environment.getExternalStorageDirectory()
				+ "/Goo.N");
		if (!direct.exists()) {
			File wallpaperDirectory = new File("/sdcard/Goo.N/");
			wallpaperDirectory.mkdirs();
		}
		String fileName = getFileName();
		File file = new File(new File("/sdcard/Goo.N/"), fileName +".jpg");
		if (file.exists()) {
			file.delete();
		}
		try {
			
			FileOutputStream out = new FileOutputStream(file);
			imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
//			imageToSave.recycle();
			out.flush();
			out.close();
			Toast.makeText(Diary.this, "Complete.", Toast.LENGTH_SHORT)
					.show();
		} catch (FileNotFoundException e) {
			Toast.makeText(Diary.this, "No Complete FileNotFoundException.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(Diary.this, "No Complete IOException.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(Diary.this, "No Complete Exception.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	private String getFileName() {
		String fileName = "";
		int day, month, year, hours, minu,sec;
		Calendar calen = Calendar.getInstance(Locale.getDefault());
		day = calen.get(Calendar.DATE);
		month = calen.get(Calendar.MONTH) + 1;
		year = calen.get(Calendar.YEAR);
		hours = calen.get(Calendar.HOUR);
		minu = calen.get(Calendar.MINUTE);
		sec = calen.get(Calendar.SECOND);
		fileName = "goon" + day + month + year + hours + minu + sec;
		return fileName;
	}
	
	private void uploadImage(String picPath) {
		if (map != null) {
			goonDB.updateDiaryNote(date, noteString, picPath + "", 1);
		} else {
			goonDB.insertDiaryNote(date, noteString, picPath + "", 0);
		}
//		uploadFile(picPath);
	}

	private void performCrop(Uri picUri) {
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri
		cropIntent.setDataAndType(picUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		// retrieve data on return
		cropIntent.putExtra("return-data", true);
		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, PIC_CROP);
	}

	private void uploadImage(Uri selectedUri) {
		if (map != null) {
			goonDB.updateDiaryNote(date, noteString, selectedUri + "", 1);
		} else {
			goonDB.insertDiaryNote(date, noteString, selectedUri + "", 0);
		}
	}

	private void rotate(float degree) {
		final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);

		rotateAnim.setDuration(0);
		rotateAnim.setFillAfter(true);
		addPhoto.startAnimation(rotateAnim);
	}

	public static int getImageOrientation(String imagePath) {
		int rotate = 0;
		try {

			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotate;
	}

	protected Bitmap tempImage(Uri imageUri, int reqWidth, int reqHeight) {
		Bitmap bitmapImage = null;

		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(this.getContentResolver()
					.openInputStream(imageUri), null, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			options.inJustDecodeBounds = false;
			bitmapImage = BitmapFactory.decodeStream(this.getContentResolver()
					.openInputStream(imageUri), null, options);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bitmapImage;
	}

	protected int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public Bitmap transform(Bitmap source) {
		int size = Math.min(source.getWidth(), source.getHeight());
		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;
		Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
		if (result != source) {
			source.recycle();
		}
		return result;
	}

	public void showInitImage() {
		HashMap<String, String> map = goonDB.getDiaryNoteByDate(date);
		if (map != null) {
			// String mImage = map.get("image");
			System.out.println(bitmap);
			addPhoto.setImageBitmap(bitmap);
		}
	}

	public void Dialog(float w, String date) {
		final String gDate = date;
		final Dialog dialog = new Dialog(Diary.this);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.weight_mesurement_dialog);
		dialog.setCanceledOnTouchOutside(true);

		showWeight = Float.toString(w);
		finalWeight = showWeight;

		final TextView weight = (TextView) dialog.findViewById(R.id.weight);
		weight.setText(showWeight); // init weight from profile

		ImageButton plus = (ImageButton) dialog.findViewById(R.id.plus);
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showWeight = String.format("%.1f",
						(Float.parseFloat(showWeight) + 0.1))
						+ "";
				finalWeight = showWeight;
				weight.setText(showWeight);
			}
		});

		ImageButton minus = (ImageButton) dialog.findViewById(R.id.minus);
		minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				showWeight = String.format("%.1f",
						(Float.parseFloat(showWeight) - 0.1))
						+ "";
				finalWeight = showWeight;
				weight.setText(showWeight);
			}
		});
		Button ok = (Button) dialog.findViewById(R.id.weightchaged);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String sWeight = showWeight;
				long rows = goonDB.UpdateWeightMeasure(gDate, sWeight);
				todayWeightTextView.setText(goonDB.getWeightByDate(gDate));
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void DialogForNote() {
		final Dialog dialog = new Dialog(Diary.this);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.diary_text);
		dialog.setCanceledOnTouchOutside(true);
		EditText noteEditText = (EditText) dialog
				.findViewById(R.id.noteedittext);
		if (goonDB.checkDiary(date) == true) {
			HashMap<String, String> map;
			map = goonDB.getDiaryNoteByDate(date);
			String thisNote = map.get("note_text");
			noteEditText.setText(thisNote);
		}
		Button saveButton = (Button) dialog.findViewById(R.id.savenote);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText noteEditText = (EditText) dialog
						.findViewById(R.id.noteedittext);
				noteString = noteEditText.getText().toString();
				goonDB = new GoonDB(getApplicationContext());

				if (goonDB.checkDiary(date) == false) {
					goonDB.insertDiaryNote(date, noteString, "null", 0);
					//new InsertDiaryThread(uid, "image", text, weight, date).execute();
					new InsertDiaryThread(pref_uid, "", noteString, showWeight, date).execute();
				} else {
					goonDB.updateDiaryNote(date, noteString, "null", 1);
					new UpdateDiaryThread(pref_uid, "", noteString, showWeight, date).execute();
				}
				setDiaryNote(date);
				dialog.dismiss();
			}
		});

		Button cancelButton = (Button) dialog.findViewById(R.id.cancelnote);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void setDiaryNote(String date) {
		String diaryNote;
		map = new HashMap<String, String>();
		map = goonDB.getDiaryNoteByDate(date);
		if (map != null) {
			diaryNote = map.get("note_text");
		} else {
			diaryNote = "";
		}
		texthere.setText(diaryNote);
	}

	public void insertTestData() {
		int day, month, year;
		String fDate;

		calen = Calendar.getInstance(Locale.getDefault());
		day = calen.get(Calendar.DATE);
		month = calen.get(Calendar.MONTH) + 1;
		year = calen.get(Calendar.YEAR);
		if (day > 0 && day < 10 && month > 0 && month < 10) {
			fDate = year + "/" + "0" + month + "/" + "0" + day;
		} else if (month > 0 && month < 10) {
			fDate = year + "/" + "0" + month + "/" + day;
		} else {
			fDate = year + "/" + month + "/" + day;
		}
		goonDB.insertWeightMeasure(fDate, "0.0");
	}

	// convert dp to px
	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	// convert px to dp
	public int pxToDp(int px) {
		DisplayMetrics displayMetrics = getApplicationContext().getResources()
				.getDisplayMetrics();
		int dp = Math.round(px
				/ (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	// calculate the number of days between dateOne and dateTwo.  
	public long getDateDiffString(Date dateOne, Date dateTwo)
	{
	    long timeOne = dateOne.getTime();
	    long timeTwo = dateTwo.getTime();
	    long oneDay = 1000 * 60 * 60 * 24;
	    long delta = (timeTwo - timeOne) / oneDay;

	   
		return delta;
	}
	
	

	// insertDiary Service
	private class InsertDiaryThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
		ProgressDialog dialog;
		String uid, date, text, image, weight;
		
		InsertDiaryThread(String _uid, String _date, String _text, String _image, String _weight) {
			uid = _uid;
			date = _date;
			text = _text;
			image = _image;
			weight = _weight;
		}
		
		@Override
		protected void onPreExecute() {		
			if (isInternetPresent) {
				dialog = ProgressDialog.show(Diary.this, "","Loading ...", true);
				dialog.setOnDismissListener(this);
				dialog.setCancelable(true);
			} else {
				Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Diary.this);
	        	this.cancel(true);
			}
    	}
		
		@Override
		protected String doInBackground(Integer... params) {
			return GoonService.DefaultService().insertDiary(uid, date, text, image, weight, Diary.this);
		}
    
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			
			if(result != null && result.length() != 0) {	
				Log.i("result", result.toString());
				//read response json
				JSONObject jsonResponse;	                     
		        try {	  
		        	
		             jsonResponse = new JSONObject(result);                    		           
		             CheckStatus = jsonResponse.getString("status");
		             CheckMessage = jsonResponse.getString("message");  
		           
		         } catch (JSONException e) {         
		             e.printStackTrace();
		         }
		        
		        if (CheckStatus.equals("0")) {
		        	Toast.makeText(Diary.this, "Insert Diary Error !",Toast.LENGTH_LONG).show();
					
				} else if(CheckStatus.equals("1")) {	
					Toast.makeText(Diary.this, "Insert Diary Complete !",Toast.LENGTH_LONG).show();
					// Insert Goon DB
				}
					
			} else {
				Toast.makeText(Diary.this, "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
				this.cancel(true);
			}
    	}

		@Override
		public void onDismiss(DialogInterface dialog) {
			cancel(true);
		}
    }	

	// updateDiary
	private class UpdateDiaryThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
		ProgressDialog dialog;
		String uid, date, text, image, weight;
		
		UpdateDiaryThread(String _uid, String _date, String _text, String _image, String _weight) {
			uid = _uid;
			date = _date;
			text = _text;
			image = _image;
			weight = _weight;
		}
		
		@Override
		protected void onPreExecute() {		
			if (isInternetPresent) {
				dialog = ProgressDialog.show(Diary.this, "","Loading ...", true);
				dialog.setOnDismissListener(this);
				dialog.setCancelable(true);
			} else {
				Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Diary.this);
	        	this.cancel(true);
			}
    	}
		
		@Override
		protected String doInBackground(Integer... params) {
			return GoonService.DefaultService().updateDiary(uid, date, text, image, weight, Diary.this);
		}
    
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			
			if(result != null && result.length() != 0) {
				Log.i("result", result);
				
				//read response json
				JSONObject jsonResponse;	                     
		        try {	  
		        	
		             jsonResponse = new JSONObject(result);                    		           
		             CheckStatus = jsonResponse.getString("status");
		             CheckMessage = jsonResponse.getString("message");  
		           
		         } catch (JSONException e) {         
		             e.printStackTrace();
		         }
		        
		        if (CheckStatus.equals("0")) {
		        	Toast.makeText(Diary.this, "Update Diary Error !",Toast.LENGTH_LONG).show();
					
				} else if(CheckStatus.equals("1")) {	
					Toast.makeText(Diary.this, "Update Diary Complete !",Toast.LENGTH_LONG).show();
					// Insert Goon DB
				}
					
			} else {
				Toast.makeText(Diary.this, "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
			}
    	}

		@Override
		public void onDismiss(DialogInterface dialog) {
			cancel(true);
		}
    }
	
	
	// Method Facebook
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};
 
	public void postImage() {
		if (checkPermissions()) {
			// port url image
			Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
			
			Request uploadRequest = Request.newUploadPhotoRequest(
					Session.getActiveSession(), img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Toast.makeText(Diary.this, "Photo uploaded successfully",Toast.LENGTH_LONG).show();
						}
						
						
					});
			uploadRequest.executeAsync();
			
			/*
			byte[] data = null;

		    Bitmap bi = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		    data = baos.toByteArray();
		    
		    Bundle params = new Bundle();
			params.putString(Facebook.TOKEN, facebook.getAccessToken());
		    params.putString("method", "photos.upload");
		    params.putByteArray("picture", data); // image to post
		    params.putString("caption", "My text on wall with Image "); // text to post
		    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
		    mAsyncRunner.request(null, params, "POST", new SampleUploadListener(),
		            null); */
		} else {
			requestPermissions();
		}
	}
	
	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}
 
	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null)
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PERMISSIONS));
	}
 
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		//buttonsEnabled(Session.getActiveSession().isOpened());
	}
 
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
 
	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
}
