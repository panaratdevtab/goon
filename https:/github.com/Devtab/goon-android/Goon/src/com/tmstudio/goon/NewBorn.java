package com.tmstudio.goon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.FacebookError;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;

@SuppressLint("NewApi")
public class NewBorn extends Activity implements AnimationListener {

	// params for titlebar
	boolean customTitleSupported;

	// params for signin with fb
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static String message = "Sample status posted from android app";
	
	// take a photo
	private Intent pictureActionIntent = null;
	protected static final int CAMERA_REQUEST = 0;
	protected static final int GALLERY_PICTURE = 1;
	protected static final int PIC_CROP = 2;
	private Bitmap bitmap;
	private GoonDB goonDB;
	private String date, imagepath;

	// params for progress bar
	LinearLayout progress;
	LinearLayout frame;
	RelativeLayout bluePinPosition;
	RelativeLayout redPinPosition;

	TextView showProgress;
	LinearLayout FrameShowProgress;
	LinearLayout FrameFullProgress;

	ImageView bluepin;
	ImageView redpin;

	// params for countdown
	CounterClass timer;
	TextView day;
	TextView hour;
	TextView minute;
	TextView second;
	ImageView addPhoto;
	ImageView showPhoto;
	ImageView saveButton;
	ImageView shareButton;

	long currHour;
	long currMinute;
	long currSeconds;
	long currDay;

	private Animation animation1;
	private Animation animation2;
	private boolean isBackOfCardShowing = true;

	// params for congrats
	Button congrats;

	// params for Animation
	Animation animBlink;
	Animation animSlideUp;

	// params for service
	private AQuery aq;
	private String service_fid, service_uid, service_email, service_password, 
	service_child_name, service_child_age, service_avatar ,service_dad_name, 
	service_mom_name, service_child_sex , service_born_date, service_status;
	private String CheckStatus;
	private String CheckMessage;
	private String data_child_sex;
	private String ChildnameText, ChildageText, MomnameText, DadnameText, ChildsexText;
	
	AlertDialog.Builder popDialog;
	Dialog myDialog;
	
	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String status_login_fb = "1";
	private String status_login_acc = "2";
	private String pref_fid, pref_uid, pref_email, pref_password, pref_child_name, pref_child_age, pref_avatar, pref_dad_name, pref_mom_name, pref_child_sex, pref_born_date, pref_status, pref_status_login, pref_contry;	
	
	// BackPress
	private Toast toast;
	private long backPressed = 0;
	
	//check internet
	Boolean isInternetPresent = false;
	NetworkHelper cd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.new_born);
		
		goonDB = new GoonDB(this);
		aq = new AQuery(this);
		aq.id(R.id.editprofile).clicked(this,"EditProfileClicked");
		popDialog = new AlertDialog.Builder(this);
		
		// Session class instance
		sessionlogin = new UserSessionManager(getApplicationContext());	
		getSession();
		
		// Check Internet
		cd = new NetworkHelper(NewBorn.this.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
		
     // countdown timer event
     		Calendar now = Calendar.getInstance();
     		currHour = now.get(Calendar.HOUR_OF_DAY);
     		currMinute = now.get(Calendar.MINUTE);
     		currSeconds = now.get(Calendar.SECOND);
     		currDay = 20;
     		
     		timer = new CounterClass(24192000000L,1000); 
     		timer.start();
     		
     		// get resolution width and height
     		Display display = getWindowManager().getDefaultDisplay();
     		Point size = new Point();
     		display.getSize(size);
     		int maxwidth = size.x;

     		// /// Progress event
     				showProgress = (TextView) findViewById(R.id.showProgress2);
     				TextView fullday = (TextView) findViewById(R.id.fulldays2);
     				TextView wombAge = (TextView) findViewById(R.id.wombage2);

     				frame = (LinearLayout) findViewById(R.id.frame2);
     				progress = (LinearLayout) findViewById(R.id.progressbar);
     				
     				// convert 60 dp to px
     				Resources r1 = getResources();
     				float layoutPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r1.getDisplayMetrics());
     				
     						int width =  Math.round(layoutPx) * 2;
     				
     				        int frameWidth = maxwidth - width;
     				        
     				        
     				        
     				        int currentDay = Integer.parseInt(pref_child_age);

     						float currentProgressValue = (currentDay * frameWidth) / 280;
     						
     						frame.setLayoutParams(new LinearLayout.LayoutParams(Math.round(frameWidth), LinearLayout.LayoutParams.MATCH_PARENT));
     						LinearLayout.LayoutParams frameParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
     						//frameParams.setMargins(0, 16, 0, 0);
     						frame.setLayoutParams(frameParams);
     						
     						progress.setLayoutParams(new LinearLayout.LayoutParams(Math.round(currentProgressValue),LinearLayout.LayoutParams.MATCH_PARENT));

     						showProgress.setText(Math.round(currentDay) + "\n" + getResources().getString(R.string.day));
     						fullday.setText("280\n" + getResources().getString(R.string.day));
     						wombAge.setText("●  "+ getResources().getString(R.string.womb_age) + " " + Math.round(currentDay) + " " + getResources().getString(R.string.day));

     						RelativeLayout PinPosition = (RelativeLayout) findViewById(R.id.pinpos2);
     						
     						// position of red pin (mark Today)
     						RelativeLayout.LayoutParams paramsOfToday = (RelativeLayout.LayoutParams) PinPosition.getLayoutParams();
     						
     						// convert 12 dp to px
     						Resources r2 = getResources();
     						float pinPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r2.getDisplayMetrics());
     						
     						paramsOfToday.setMargins(Math.round((layoutPx - pinPx) + currentProgressValue), 0, Math.round(pinPx), 0);
     						PinPosition.setLayoutParams(paramsOfToday);
     		
     		
     		
     		// Blink Animation for baby progress image
     		// load the animation
     				animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
     						R.anim.blink);
     				
     				// set animation listener
     				animBlink.setAnimationListener(this);
     				// start the animation
     				ImageView babyProgress = (ImageView) findViewById(R.id.babyprogress);
     				babyProgress.startAnimation(animBlink);
     					

     		// click congratulations button
     		congrats = (Button) findViewById(R.id.congrats);
     		congrats.setOnClickListener(new OnClickListener() {
     			
     			@Override
     			public void onClick(View v) {
     				CongratsDialog();
     			}
     		});

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
	
	// countdown timer
	@SuppressLint("DefaultLocale")
	public class CounterClass extends CountDownTimer implements AnimationListener {

		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public String timeCalculate(long ttime) {
			
			
			long countDay, countHour, countMin, countSec;
			String daysT = "", restT = "";

			countDay = (Math.round(ttime) / 86400);
			countHour = (Math.round(ttime) / 3600) - (countDay * 24);
			countMin = (Math.round(ttime) / 60) - (countDay * 1440)
					- (countHour * 60);
			countSec = Math.round(ttime) % 60;

			if (countDay == 1)
				daysT = String.format("%d day ", countDay);
			if (countDay > 1)
				daysT = String.format("%d days ", countDay);

			restT = String.format("%02d:%02d:%02d", countHour, countMin,
					countSec);

			return daysT + restT;
		}

		@Override
		public void onFinish() {
			start();

		}
		
		// calculate time and animation
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			long millis = millisUntilFinished;
			
			// load the animation
    		animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
    				R.anim.slide_up);
    		// set animation listener
    		animSlideUp.setAnimationListener(this);
    		
    		long _currDay;
    		long _currHour;
    		long _currMin;
    		long _currSec;
    		
    		String dayString;
    		String hourString;
    		String minString;
    		String secString;

			day = (TextView) findViewById(R.id.day);
			hour = (TextView) findViewById(R.id.hour);
			minute = (TextView) findViewById(R.id.minute);
			second = (TextView) findViewById(R.id.second);
    		
    	
    			_currDay = TimeUnit.MILLISECONDS.toDays(millis) - currDay;
    			dayString = String.format("%02d", _currDay);
    			day.setText(dayString);
    			
    			_currHour = TimeUnit.MILLISECONDS.toHours(millis - ((currSeconds*1000) - (currMinute*60000)) - (currHour*3600000)) - TimeUnit.DAYS
						.toHours(TimeUnit.MILLISECONDS.toDays(millis - ((currSeconds*1000) - (currMinute*60000)) - (currHour*3600000)));
    			hourString = String.format("%02d", _currHour);
    			hour.setText(hourString);

				_currMin = TimeUnit.MILLISECONDS.toMinutes((millis - (currSeconds*1000)) - (currMinute*60000)) - TimeUnit.HOURS
						.toMinutes(TimeUnit.MILLISECONDS.toHours((millis - (currSeconds*1000)) - currMinute*60000));
				minString = String.format("%02d", _currMin);
				minute.setText(minString);

				_currSec = TimeUnit.MILLISECONDS.toSeconds(millis - (currSeconds*1000)) - TimeUnit.MINUTES
						.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis - (currSeconds*1000)));
				secString = String.format("%02d", _currSec);
				second.setText(secString);
			
				// set boxtime flip
				if(currHour == 0 && currMinute == 0 && currSeconds == 0){
					// start the animation
					ImageView boxtimeDay = (ImageView) findViewById(R.id.boxtimeday);
					boxtimeDay.setVisibility(View.VISIBLE);
					boxtimeDay.startAnimation(animSlideUp);
				}
				else if(currMinute == 0 && currSeconds == 0){
					// start the animation
					ImageView boxtimeHour = (ImageView) findViewById(R.id.boxtimehour);
					boxtimeHour.setVisibility(View.VISIBLE);
					boxtimeHour.startAnimation(animSlideUp);
				}
				else if(currSeconds == 0){
					// start the animation
					ImageView boxtimeMin = (ImageView) findViewById(R.id.boxtimemin);
					boxtimeMin.setVisibility(View.VISIBLE);
					boxtimeMin.startAnimation(animSlideUp);
				}
				else {
					// start the animation
					ImageView boxtimeSec = (ImageView) findViewById(R.id.boxtimesec);
					boxtimeSec.setVisibility(View.VISIBLE);
					boxtimeSec.startAnimation(animSlideUp);
				}
		}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
		// check for blink animation
		if (animation == animBlink) {
		}
		
	}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub// check for blink animation
			if (animation == animBlink) {
			}
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		// check for blink animation
		if (animation == animSlideUp) {
		}
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == animSlideUp) {
		}
	}
	
	// Dialog for congratulation snap picture
		@SuppressWarnings("static-access")
		public void CongratsDialog() {
			final Dialog congratsDialog = new Dialog(NewBorn.this);
			congratsDialog
					.requestWindowFeature(congratsDialog.getWindow().FEATURE_NO_TITLE);
			congratsDialog.setContentView(R.layout.congrats_dialog);
			congratsDialog.show();
			addPhoto = (ImageView) congratsDialog.findViewById(R.id.congratsframe);
			showPhoto = (ImageView) congratsDialog
					.findViewById(R.id.takeaphotoforcongrats);
			saveButton = (ImageView) congratsDialog.findViewById(R.id.saveButton);
			addPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startDialog();
				}
			});
			saveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					createDirectoryAndSaveFile(bitmap);
				}
			});
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
//				imageToSave.recycle();
				out.flush();
				out.close();
				Toast.makeText(NewBorn.this, "Complete.", Toast.LENGTH_SHORT)
						.show();

			} catch (FileNotFoundException e) {
				Toast.makeText(NewBorn.this, "No Complete FileNotFoundException.",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(NewBorn.this, "No Complete IOException.",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(NewBorn.this, "No Complete Exception.",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
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

		private void addPhoto() {
			showPhoto.setImageBitmap(transform(bitmap));
		}

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
							pictureActionIntent.putExtra("aspectX", 0);
							pictureActionIntent.putExtra("aspectY", 0);
							pictureActionIntent.putExtra("outputX", 1000);
							pictureActionIntent.putExtra("outputY", 1000);
							startActivityForResult(pictureActionIntent,
									CAMERA_REQUEST);

						}
					});
			myAlertDialog.show();
		}

		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			try {
				if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
					if (data != null) {
						Uri selectedUri = data.getData();
						imagepath = getPath(selectedUri);
						bitmap = (Bitmap) data.getExtras().get("data");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						performCrop(selectedUri);
						// addPhoto.setImageBitmap(transform(bmp));
						bitmap = drawableToBitmap(addImage(bitmap));
						showPhoto.setImageBitmap(bitmap);
						
//						bitmap.recycle();
						// uploadImage("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcR2AEQs9fH80uDPmJkF83KaWThzxMmH8FezV0DXvqyLs6Agw-gD");
					}
				} else if (resultCode == RESULT_OK
						&& requestCode == GALLERY_PICTURE) {
					if (data != null) {
						bitmap = data.getExtras().getParcelable("data");
						Uri selectedUri = getImageUri(bitmap);
						imagepath = getPath(selectedUri);
						// BitmapFactory.Options options = new
						// BitmapFactory.Options();
						// options.inTempStorage = new byte[16 * 1024];
						// float i;
						// Bitmap bmp = tempImage(selectedUri, 1000, 1000);
						// addPhoto.setImageBitmap(transform(bmp));

						bitmap = drawableToBitmap(addImage(bitmap));
						showPhoto.setImageBitmap(bitmap);
//						bitmap.recycle();
						// addPhoto.setImageBitmap(transform(photo));
						// rotate(getImageOrientation(selectedUri+""));
						// bmp.recycle();
						// uploadImage("http://image.alienware.com/images/galleries/gallery-shot_laptops_14_05.jpg");
					}
				}
			} catch (RuntimeException e) {

				// Log.d("Runtime Memory", e.getMessage() + " / "+
				// Runtime.getRuntime().totalMemory());
				e.getStackTrace();
				System.out.println();
			} catch (Exception e) {
				e.getStackTrace();
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

		public Uri getImageUri(Bitmap inImage) {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			String path = Images.Media.insertImage(getContentResolver(), inImage,
					"Title", null);
			return Uri.parse(path);
		}
		
		public static Bitmap drawableToBitmap (Drawable drawable) {
		    if (drawable instanceof BitmapDrawable) {
		        return ((BitmapDrawable)drawable).getBitmap();
		    }

		    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(bitmap); 
		    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		    drawable.draw(canvas);

		    return bitmap;
		}

		private LayerDrawable addImage(Bitmap image) {
			Resources r = getResources();
			Drawable[] layout = new Drawable[2];
			layout[1] = r.getDrawable(R.drawable.goon_ipad_new_born_frame_con01);
			Drawable draw = (Drawable) new BitmapDrawable(getResources(),
					transform(image));
			layout[0] = draw;
			LayerDrawable layerDraw = new LayerDrawable(layout);
			return layerDraw;
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
			if (source != null) {
				int size = Math.min(source.getWidth(), source.getHeight());
				int x = (source.getWidth() - size) / 2;
				int y = (source.getHeight() - size) / 2;
				Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
				if (result != source) {
//					source.recycle();
				}
				return result;
			} else {
				Drawable myDrawable = getResources().getDrawable(
						R.drawable.goon_ipad_new_born_frame_con02);
				Bitmap result = ((BitmapDrawable) myDrawable).getBitmap();
				return result;
			}
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
		
		
	    // editprofile
		public void EditProfileClicked(View view) {
			
			myDialog = new Dialog(NewBorn.this);
			myDialog.requestWindowFeature(myDialog.getWindow().FEATURE_NO_TITLE);
	        myDialog.setContentView(R.layout.edit_info);

	        final TextView finishbtn = (TextView) myDialog.findViewById(R.id.info_cancel);
	        final Button sendbtn = (Button) myDialog.findViewById(R.id.info_submit);
	        final Button clearbtn = (Button) myDialog.findViewById(R.id.info_clear);
			final EditText ChildnameInput = (EditText) myDialog.findViewById(R.id.fillchildname);
			final EditText ChildageInput = (EditText) myDialog.findViewById(R.id.fillchildage);
			final EditText DadnameInput = (EditText) myDialog.findViewById(R.id.filldadname);
			final EditText MomnameInput = (EditText) myDialog.findViewById(R.id.fillmomname);		
			final ImageButton img_man = (ImageButton) myDialog.findViewById(R.id.img_man);
			final ImageButton img_women = (ImageButton) myDialog.findViewById(R.id.img_woman);
			clearbtn.setVisibility(View.GONE);

			ChildnameInput.setText(pref_child_name);
			ChildageInput.setText(pref_child_age);
			DadnameInput.setText(pref_dad_name);
			MomnameInput.setText(pref_mom_name);	
			
			if(pref_child_sex != null){
				if(pref_child_sex.equals("0")){
					img_women.setImageResource(R.drawable.goon_ipad_new_born_icon_woman_over);
				}else{
					img_man.setImageResource(R.drawable.goon_ipad_new_born_icon_man_over);
				}
			}
			img_man.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					img_man.setImageResource(R.drawable.goon_ipad_new_born_icon_man_over);
					img_women.setImageResource(R.drawable.goon_ipad_new_born_icon_woman);
	            	ChildsexText = "1";					
				}
			});
			
			img_women.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	img_women.setImageResource(R.drawable.goon_ipad_new_born_icon_woman_over);
	            	img_man.setImageResource(R.drawable.goon_ipad_new_born_icon_man);
	            	ChildsexText = "0";
	            }
			});

	        finishbtn.setOnClickListener(new OnClickListener() {
	             @Override
	              public void onClick(View v) {
	                    myDialog.cancel();
	              }
	        });
	        
	        sendbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ChildnameText = ChildnameInput.getText().toString();
					ChildageText = ChildageInput.getText().toString();
					DadnameText = DadnameInput.getText().toString();
					MomnameText = MomnameInput.getText().toString();
					int age = Integer.parseInt(ChildageText);
					
					if (ChildnameText.matches("") || ChildageText.matches("")){
						Toast.makeText(NewBorn.this, "Please fill out this form completely.",Toast.LENGTH_LONG).show();
					} else {
						myDialog.cancel();
						new EditProfileThread(pref_uid, pref_fid, pref_email, pref_password, ChildnameText, ChildageText, pref_avatar, DadnameText, MomnameText, ChildsexText, pref_born_date, pref_status).execute();
					}	
					
				}
			});
	        
	        myDialog.show();		
		}
		
		// EditProfile Service
		private class EditProfileThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String uid, fid, email, password, child_name, child_age, avatar, dad_name, mom_name, child_sex, born_date, status;
			
			EditProfileThread(String _uid, String _fid, String _email, String _password, String _child_name, String _child_age, String _avatar, String _dad_name, String _mom_name, String _child_sex, String _born_date, String _status) {
				uid = _uid;
				fid = _fid;
				email = _email;
				password = _password;
				child_name = _child_name;
				child_age = _child_age;
				avatar = _avatar;
				dad_name = _dad_name;
				mom_name = _mom_name;
				child_sex = _child_sex;
				born_date = _born_date;
				status = _status;
		
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
					dialog = ProgressDialog.show(NewBorn.this, "","Loading ...", true);
					dialog.setOnDismissListener(this);
					dialog.setCancelable(true);
				}else{
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, NewBorn.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().updateUser(uid, fid, email, password, child_name, child_age, avatar, dad_name, mom_name, child_sex, born_date, status, NewBorn.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				
				if(result != null && result.length() != 0) {	
					Log.i("result editprofile", result);
					//read response json
					JSONObject jsonResponse;	                     
			        try {	  			        	
			             jsonResponse = new JSONObject(result);                     		           
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  
			             
			             JSONObject dataArr = jsonResponse.optJSONObject("dataArr");	
	            		 service_uid = dataArr.getString("uid"); 
	            		 service_fid = dataArr.getString("fid");
	            		 service_email = dataArr.getString("email");
	            		 service_password = dataArr.getString("password");
	            		 service_child_name = dataArr.getString("child_name");
	            		 service_child_age = dataArr.getString("child_age");
	            		 service_avatar = dataArr.getString("avatar");
	            		 service_dad_name = dataArr.getString("dad_name");
	            		 service_mom_name = dataArr.getString("mom_name");
	            		 service_child_sex = dataArr.getString("child_sex");
	            		 service_born_date = dataArr.getString("born_date");
	            		 service_status = dataArr.getString("status"); 
	 
			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }
			        
			        if (CheckStatus.equals("0")) {
			        	Toast.makeText(NewBorn.this, "Edit Profile Error !",Toast.LENGTH_LONG).show();
						
					} else if(CheckStatus.equals("1")) {
						Utilities.ShowDialogNotify("Edit Profile", "Edit Profile Complete !", "OK", false, NewBorn.this);	
						sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status, status_login_acc, pref_contry);	
						getSession();			
					}
			        
				} else {
					Toast.makeText(NewBorn.this, "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
					this.cancel(true);
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
								Toast.makeText(NewBorn.this, "Photo uploaded successfully",Toast.LENGTH_LONG).show();
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

	 
		class SampleUploadListener implements AsyncFacebookRunner.RequestListener {

		    @Override
		    public void onComplete(String response, Object state) {
		    }

		    @Override
		    public void onIOException(IOException e, Object state) {
		    }

		    @Override
		    public void onFileNotFoundException(FileNotFoundException e,
		            Object state) {
		    }

		    @Override
		    public void onMalformedURLException(MalformedURLException e,
		            Object state) {
		    }

		    @Override
		    public void onFacebookError(FacebookError e, Object state) {
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
		
		@Override
		public void onBackPressed() {
			if (backPressed + 2000 > System.currentTimeMillis()) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
		        intent.addCategory(Intent.CATEGORY_HOME);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
			} else {
				Toast.makeText(getBaseContext(), "Press click again to exit!", Toast.LENGTH_SHORT).show();
			}
			backPressed = System.currentTimeMillis();
		}
}
