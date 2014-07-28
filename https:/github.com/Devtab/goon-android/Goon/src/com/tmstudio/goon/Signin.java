package com.tmstudio.goon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.androidquery.AQuery;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;
import com.viewpagerindicator.CirclePageIndicator;

@SuppressLint("NewApi")
public class Signin extends FragmentActivity implements ViewFactory {

	// params for signin with fb
	private LoginButton loginBtn;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static String message = "Sample status posted from android app";
	
	// params for swipe layouts
	LinearLayout firstView1, secondView1;
	LayoutParams params;
	LinearLayout next, prev;
	int viewWidth;
	GestureDetector gestureDetector;
	HorizontalScrollView horizontalScrollView;
	ArrayList<LinearLayout> layouts;
	int parentLeft, parentRight;
	int mWidth;
	int currPosition, prevPosition;
	View.OnTouchListener gestureListener;
	private int mActiveFeature = 0;

	private static final int SWIPE_MIN_DISTANCE = 5;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;
	
	// params for signin space
	protected static LinearLayout[] CONTENT = {};
	LinearLayout firstView;
	LinearLayout secondView;
	ViewPagerAdapter mAdapter;
	ViewPager mPager;
	CirclePageIndicator mIndicator;

	// params for signin with account
	EditText fillemail;
	EditText fillpassword;
	Button signin;
	TextView createacc;
	TextView forgotpass;
	
	// params for page control
	ImageView circle1;
	ImageView circle2;

	// params for animate background
	private static final String TAG = Signin.class.getName();
	private final int[] images = { R.drawable.image_bg_1, R.drawable.image_bg_2, R.drawable.image_bg_3, R.drawable.image_bg_4, R.drawable.image_bg_5 };
	private int index = 0;
	private final int interval = 5000;
	private boolean isRunning = true;

	// params for dialog
	String emailForCreate;
	String passwordForCreate;
	String confirmPasswordForCreate;

	// params for service
	private AQuery aq;
	private String fid;
	private String strDate;
	private String categorytip_id,categorytip_title, categorytip_modified;
	private String dirytip_id, dirytip_tip_text, dirytip_num_date,dirytip_modified;
	private String tip_id, tip_cat_id, tip_title, tip_description, tip_modified;
	private String diry_id, diry_uid, diry_text, diry_image, diry_weight, diry_created, diry_modified;
	private String service_fid, service_uid, service_email, service_password, 
	service_child_name, service_child_age, service_avatar ,service_dad_name, 
	service_mom_name, service_child_sex , service_born_date, service_status;	
	private String CheckStatus;
	private String CheckMessage;
	private String EmailText, PasswordText, PasswordText2;
	private String data_child_sex;
	private String ChildnameText, ChildageText, MomnameText, DadnameText, ChildsexText;
	final Context context = this;
	Dialog createDialog;
	Dialog forgotDialog;
	AlertDialog.Builder popDialog;
	Dialog myDialog;
		
	// param for goonDB 
	GoonDB goonDB;
	int dtid, num_date, ctid, tdid, tid;
	
	// param contry
	String contry;
	
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
		setContentView(R.layout.signin);

		goonDB = new GoonDB(Signin.this);
		aq = new AQuery(this);
		
		//Get Country
		contry = Locale.getDefault().getCountry();

		// Check Internet
		cd = new NetworkHelper(Signin.this.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
		        
		// User Session Manager
        sessionlogin = new UserSessionManager(getApplicationContext());
		boolean isSuccess = false;
		isSuccess = sessionlogin.isUserLoggedIn();
		
		// insert data from service
		insertDataFromService();

		if (isSuccess) {	
			
			Intent intent = new Intent(Signin.this,TabhostActivity.class);
			startActivity(intent);
			
		}else{
			insertDataFromService();
			aq.id(R.id.createacc).clicked(this,"CreateAccountClicked");
			aq.id(R.id.forgotpass).clicked(this,"ForgotPasswordClicked");
			startAnimatedBackground();

			mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
			mPager = (ViewPager) findViewById(R.id.myviewpager);
			mPager.setAdapter(mAdapter);
			mIndicator = (CirclePageIndicator) findViewById(R.id.myindicator);
			mIndicator.setViewPager(mPager);
//			View v = LayoutInflater.inflate(R.id.authButton, null);
		
			// We set this on the indicator, NOT the pager
			mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							if(position == 0){
								//Sign in with facebook event
								/*
								loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
									@Override
									public void onUserInfoFetched(GraphUser user) {
										if (user != null) {
											
											 Toast.makeText(Signin.this, "fid :"+user.getId(),Toast.LENGTH_LONG).show();
											
											 Intent i = new Intent(Signin.this, TabhostActivity.class);
								             startActivity(i);
								             
										} else {
											
										}
									}
								}); */
								
								
								
							}
							else{
								// Sign in with account event
								/*
								fillemail = (EditText) findViewById(R.id.fillemailforlogin);
								fillpassword = (EditText) findViewById(R.id.fillpasswordforlogin);
								signin = (Button) findViewById(R.id.loginwithacc);
								signin.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										
										EditText EmailInput = (EditText) aq.id(R.id.fillemailforlogin).getView();
										EditText PasswordInput = (EditText) aq.id(R.id.fillpasswordforlogin).getView();	
										EmailInput.setFocusable(true);
										
										fid = "0";
										EmailText = EmailInput.getText().toString();
										PasswordText = PasswordInput.getText().toString();
										
										
										if (EmailText.matches("") || PasswordText.matches("")) {
											AlertDialog.Builder dialog = new AlertDialog.Builder(aq.getContext());
											dialog.setTitle("Cannot submit");
											dialog.setMessage("Please fill out this form completely.");
											dialog.setPositiveButton("OK", null);
											dialog.show();
											
										} else {
											new LoginThread(fid,EmailText,PasswordText).execute();									
										}	

									}
								});*/
							}
						}

						@Override
						public void onPageScrolled(int position,
								float positionOffset, int positionOffsetPixels) {
						}

						@Override
						public void onPageScrollStateChanged(int state) {
						}
					});
		}
		buttonsEnabled(false);
	}	
	
	private void startAnimatedBackground() {
		Animation aniIn = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in);
		aniIn.setDuration(3000);
		Animation aniOut = AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out);
		aniOut.setDuration(3000);

		final ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		imageSwitcher.setInAnimation(aniIn);
		imageSwitcher.setOutAnimation(aniOut);
		imageSwitcher.setFactory(this);
		imageSwitcher.setImageResource(images[index]);

		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if (isRunning) {
					index++;
					index = index % images.length;
					Log.d("Intro Screen", "Change Image " + index);
					imageSwitcher.setImageResource(images[index]);
					handler.postDelayed(this, interval);
				}
			}
		};
		handler.postDelayed(runnable, interval);

	}

	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return imageView;
	}

	@Override
	public void finish() {
		isRunning = false;
		super.finish();
	}
	
	//method facebook
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				buttonsEnabled(true);
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};
 
	public void buttonsEnabled(boolean isEnabled) {
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
		buttonsEnabled(Session.getActiveSession().isOpened());
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
 
	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}

	public void insertDataFromService(){		
		strDate = "0000-00-00 00:00:00";		
		new getDiaryTipThread(strDate).execute();
		new getCategoryTipThread(strDate).execute();
		new getTipThread(strDate).execute();
	}
	
	public void getSession(){
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
	
		// Create Account
		@SuppressWarnings("static-access")
		public void CreateAccountClicked(View view) {
			
			createDialog = new Dialog(Signin.this);
			createDialog.requestWindowFeature(createDialog.getWindow().FEATURE_NO_TITLE);
			createDialog.setContentView(R.layout.create_acc);
	
			final Button submitbtn = (Button) createDialog.findViewById(R.id.btn_submit);
			submitbtn.setOnClickListener(new OnClickListener() {
	             @Override
	              public void onClick(View v) {
	            	EditText EmailInput = (EditText) createDialog.findViewById(R.id.edt_email);
	         		EditText PasswordInput = (EditText) createDialog.findViewById(R.id.edt_password);
	         		EditText PasswordInput2 = (EditText) createDialog.findViewById(R.id.edt_password2);		
	         		EmailInput.setFocusable(true);
	         		
	         		fid = "0";
	         		EmailText = EmailInput.getText().toString();
	         		PasswordText = PasswordInput.getText().toString();
	         		PasswordText2 = PasswordInput2.getText().toString();
	 
	         		if (EmailText.matches("") || PasswordText.matches("") || PasswordText2.matches("")){
	         			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	    				alertDialogBuilder.setTitle("Cannot submit");
	    				alertDialogBuilder.setMessage("Please fill out this form completely.");
	    				alertDialogBuilder.setPositiveButton("OK",null);
	    				AlertDialog alertDialog = alertDialogBuilder.create();
	    				alertDialog.show();
	         		} else {
	         			new RegisterThread(fid,EmailText,PasswordText).execute();        
	         		}
	              }
	        });
	        createDialog.show();		
		}
	
	
	   // Forgot Password
		@SuppressWarnings("static-access")
		public void ForgotPasswordClicked(View view) {
			forgotDialog = new Dialog(Signin.this);
			forgotDialog.requestWindowFeature(forgotDialog.getWindow().FEATURE_NO_TITLE);
			forgotDialog.setContentView(R.layout.forgot_password);

	        final Button forgotbtn = (Button) forgotDialog.findViewById(R.id.btn_forgot_submit);
	        forgotbtn.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	
	            	EditText EmailInput = (EditText) forgotDialog.findViewById(R.id.edt_forgot_password);
	        		EmailInput.setFocusable(true);
	        		
	        		EmailText = EmailInput.getText().toString();
	        		
	        		if (EmailText.matches("")) {
	        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	    				alertDialogBuilder.setTitle("Cannot submit");
	    				alertDialogBuilder.setMessage("Please fill out this form completely.");
	    				alertDialogBuilder.setPositiveButton("OK", null);
	    				AlertDialog alertDialog = alertDialogBuilder.create();
	    				alertDialog.show();
	 
	        		} else {
	        			new ForgotPasswordThread(EmailText).execute();
	        		}	      	 
	             }
	       });
	        
	        forgotDialog.show();
		}

		// editprofile
		public void EditProfile() {
			getSession();
			myDialog = new Dialog(Signin.this);
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
					
					if (ChildnameText.matches("") || ChildageText.matches("")){
						AlertDialog.Builder dialog = new AlertDialog.Builder(aq.getContext());
						dialog.setTitle("Cannot submit");
						dialog.setMessage("Please fill out this form completely.");
						dialog.setPositiveButton("OK", null);
						dialog.show();
					} else {
						myDialog.cancel();
						new EditProfileThread(pref_uid, pref_fid, pref_email, pref_password, ChildnameText, ChildageText, pref_avatar, DadnameText, MomnameText, ChildsexText, pref_born_date, pref_status).execute();
					}	
					
				}
			});
	        
	        myDialog.show();
		}

			
		// Create Account Service
		private class RegisterThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String fid,email,password;
			
			RegisterThread(String _fid, String _email, String _pass) {
				fid = _fid;
				email = _email;
				password = _pass;
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
					dialog = ProgressDialog.show(Signin.this, "","Loading ...", true);
					dialog.setOnDismissListener(this);
					dialog.setCancelable(true);
				} else {
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().createAccount(fid, email, password, Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				
				if(result != null && result.length() != 0) {
					
					JSONObject jsonResponse = null;	                     
			        try {	  
			             jsonResponse = new JSONObject(result);                     		           
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  

			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }

			        if (CheckStatus.equals("0")) {
			        	Utilities.ShowDialogNotify("Create Account Error !", CheckMessage.toString(), "OK", false, Signin.this);	
			        }else if(CheckStatus.equals("1")) {
			        	 
				        try {	  			        	 
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
				        
			        	// Creating user login session
				        sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status, status_login_acc, pref_contry);
						
				        //Edit Profile
				        EditProfile();
						
			        }
				} else {
					Utilities.ShowDialogNotify("Sorry !!!", "Invalid Username or Password", "Close", false, Signin.this);
					this.cancel(true);
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
				cancel(true);
			}
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
					dialog = ProgressDialog.show(Signin.this, "","Loading ...", true);
					dialog.setOnDismissListener(this);
					dialog.setCancelable(true);
				}else{
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().updateUser(uid, fid, email, password, child_name, child_age, avatar, dad_name, mom_name, child_sex, born_date, status,  Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				
				if(result != null && result.length() != 0) {					
					//read response json
					JSONObject jsonResponse = null;	                     
			        try {	  			        	
			             jsonResponse = new JSONObject(result);                     		           
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  			            
			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }
			        
			        if (CheckStatus.equals("0")) {
			        	Toast.makeText(Signin.this, "Edit Profile Error !",Toast.LENGTH_LONG).show();
						
					} else if(CheckStatus.equals("1")) {
						 try {	  			        	 
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
						 
						Utilities.ShowDialogNotify("Edit Profile", "Edit Profile Complete !", "OK", false, Signin.this);	
						sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status, status_login_acc, pref_contry);	
						getSession();
			        	
						// Starting MainActivity
						Intent i = new Intent(Signin.this,TabhostActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	
						// Add new Flag to start new Activity
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i); 
						finish();
					}
			        
				} else {
					Toast.makeText(Signin.this, "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
					this.cancel(true);
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
				cancel(true);
			}
	    }
		
		
		// ForgotPassword Service
		private class ForgotPasswordThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String email;
			
			ForgotPasswordThread(String _email) {
				email = _email;
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
					dialog = ProgressDialog.show(Signin.this, "","Loading ...", true);
					dialog.setOnDismissListener(this);
					dialog.setCancelable(true);
				}else{
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().forgotPassword(email, Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				
				if(result != null && result.length() != 0) {						
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
			        	Utilities.ShowDialogNotify("Forgot Password Error !", CheckMessage.toString(), "OK", false, Signin.this);	
			        }else if(CheckStatus.equals("1")) {
			        	Utilities.ShowDialogNotify("Forgot Password Complete !", "Please Check Your email.", "OK", false, Signin.this);	
						forgotDialog.dismiss();
			        }
				}
				else {
					Utilities.ShowDialogNotify("Sorry !!!", "Invalid Username or Password", "Close", false, Signin.this);
					this.cancel(true);
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
				cancel(true);
			}
	    }
		
		
		// getDiary Tip
		private class getDiaryTipThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String lastTime;
			
			getDiaryTipThread(String _lastTime) {
				lastTime = _lastTime;
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
				} else {
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().getDiaryTip(lastTime, Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				
				if(result != null && result.length() != 0) {
					Log.i("result", result);
					
					//read response json
					JSONObject jsonResponse = null;	                     
			        try {	  
			             jsonResponse = new JSONObject(result);     
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  			            
			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }
			        
			        if (CheckStatus.equals("0")) {		        	
			        	Toast.makeText(Signin.this, "Diary Tip Error !",Toast.LENGTH_LONG).show();
						
					} else if(CheckStatus.equals("1")) {

						 JSONArray dataArr = jsonResponse.optJSONArray("dataArr");	
			             for (int i = 0; i < dataArr.length(); i++) {
								JSONObject jsonObject;
								try {
									jsonObject = dataArr.getJSONObject(i);		
									dirytip_id = jsonObject.getString("id"); 
									dirytip_tip_text = jsonObject.getString("tip_text");
						            dirytip_num_date = jsonObject.getString("num_date");
						            dirytip_modified = jsonObject.getString("modified");
						            dtid = Integer.valueOf(dirytip_id);
						            num_date = Integer.valueOf(dirytip_num_date);
									
								} catch (JSONException e) {
									e.printStackTrace();
								}	
								
								// Insert Goon DB
				            	long saveStatus = goonDB.insertDiaryTips(dtid, dirytip_tip_text, num_date, dirytip_modified);
				            	/*if (saveStatus <=  0)	
				            	{
				            		Toast.makeText(Signin.this,"Add DiaryTip Error. ",Toast.LENGTH_SHORT).show();   
				            	} else {
				            		Toast.makeText(Signin.this,"Add DiaryTip Successfully. ",Toast.LENGTH_SHORT).show();   
				            	}*/
							}				
					}

				} else {
					Utilities.ShowDialogNotify("Sorry !!!", "Invalid", "Close", false, Signin.this);
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
	    }
	
		
		// getCategory Tip
		private class getCategoryTipThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String lastTime;
			
			getCategoryTipThread(String _lastTime) {
				lastTime = _lastTime;		
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
				}else{
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().getCategoryTip(lastTime, Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				
				if(result != null && result.length() != 0) {					
					//read response json
					JSONObject jsonResponse;	                     
			        try {	  			        	
			             jsonResponse = new JSONObject(result);                     		           
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  

			            if (CheckStatus.equals("0")) {
			            	
			            	Log.i(TAG, "Load Catagory Tip Error !");
			            	
						} else if(CheckStatus.equals("1")) {	
							
							 JSONArray dataArr = jsonResponse.optJSONArray("dataArr");	
				             for (int i = 0; i < dataArr.length(); i++) {
									JSONObject jsonObject;
									try {
										jsonObject = dataArr.getJSONObject(i);		
										categorytip_id = jsonObject.getString("id"); 
							            categorytip_title = jsonObject.getString("title");
							            categorytip_modified = jsonObject.getString("modified");
							            ctid = Integer.valueOf(categorytip_id);
									} catch (JSONException e) {
										e.printStackTrace();
									}	
									
									// Insert Goon DB
					            	long saveStatus = goonDB.insertCategoryTip(ctid, categorytip_title, categorytip_modified);
					            	/*if (saveStatus <=  0)
					            	{
					            		Toast.makeText(Signin.this,"Add CategoryTip Error. ",Toast.LENGTH_SHORT).show();   
					            	} else {
					            		Toast.makeText(Signin.this,"Add CategoryTip Successfully. ",Toast.LENGTH_SHORT).show();   
					            	}	*/						
				             }
						}
	 
			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }

				} else {
					Toast.makeText(Signin.this, "Sorry !!! Invalid Catagory  Tip",Toast.LENGTH_LONG).show();
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
	    }
		
		
		// getTip
		private class getTipThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String lastTime;
			
			getTipThread(String _lastTime) {
				lastTime = _lastTime;		
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
				}else{
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Signin.this);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().getTip(lastTime, Signin.this);
			}
	    
			@Override
			protected void onPostExecute(String result) {
				
				if(result != null && result.length() != 0) {					
					//read response json
					JSONObject jsonResponse;	                     
			        try {	  			        	
			             jsonResponse = new JSONObject(result);                     		           
			             CheckStatus = jsonResponse.getString("status");
			             CheckMessage = jsonResponse.getString("message");  
	
			            if (CheckStatus.equals("0")) {
			            	Log.i(TAG, "Load Tip Complete !");
								
						} else if(CheckStatus.equals("1")) {	
				             
				             JSONArray dataArr = jsonResponse.optJSONArray("dataArr");	
				             for (int i = 0; i < dataArr.length(); i++) {
									JSONObject jsonObject;
									try {
										jsonObject = dataArr.getJSONObject(i);		
										tip_id = jsonObject.getString("id"); 
										tip_cat_id = jsonObject.getString("cat_id");
										tip_title = jsonObject.getString("title");
										tip_description = jsonObject.getString("description");
										tip_modified = jsonObject.getString("modified");
										
										tdid = Integer.valueOf(tip_id);
										tid = Integer.valueOf(tip_cat_id);
									} catch (JSONException e) {
										e.printStackTrace();
									}	
									
									// Insert Goon DB
					            	long saveStatus = goonDB.insertTipDetail(tdid, tid, tip_title, tip_description, tip_modified);
					            	/*if (saveStatus <=  0)
					            	{
					            		Toast.makeText(Signin.this,"Add Tip Error. ",Toast.LENGTH_SHORT).show();   
					            	} else {
					            		Toast.makeText(Signin.this,"Add Tip Successfully. ",Toast.LENGTH_SHORT).show();   
					            	}*/						
				             } 
						}
	 
			         } catch (JSONException e) {         
			             e.printStackTrace();
			         }

				} else {
					Toast.makeText(Signin.this, "Sorry !!! Invalid Catagory  Tip",Toast.LENGTH_LONG).show();
				}
	    	}

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
	    }
}
