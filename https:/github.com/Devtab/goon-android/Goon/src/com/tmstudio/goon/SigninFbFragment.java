package com.tmstudio.goon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SigninFbFragment extends Fragment {
	
	// params for signin with fb
	private LoginButton loginBtn;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static String message = "Sample status posted from android app";
	private static final String TAG = SigninFbFragment.class.getName();
	private String get_email, get_id;
	
	// params for service
	private AQuery aq;
	private String weight,weight_date;
	private String diry_id, diry_uid, diry_text, diry_image, diry_weight, diry_created, diry_modified;
	private String service_fid, service_uid, service_email, service_password, 
	service_child_name, service_child_age, service_avatar ,service_dad_name, 
	service_mom_name, service_child_sex , service_born_date, service_status;
	private String CheckStatus;
	private String CheckMessage;
	private String data_child_sex;
	private String ChildnameText, ChildageText, MomnameText, DadnameText, ChildsexText;
	AlertDialog.Builder popDialog;
	Dialog myDialog;
	
	
	// param for goonDB 
	GoonDB goonDB;
	
	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String status_login_fb = "1";
	private String status_login_acc = "2";
	private String pref_fid, pref_uid, pref_email, pref_password, pref_child_name, pref_child_age, pref_avatar, pref_dad_name, pref_mom_name, pref_child_sex, pref_born_date, pref_status, pref_status_login, pref_contry;
	
	//check internet
	Boolean isInternetPresent = false;
	NetworkHelper cd;
	
	String strFirstName;
	String strLocation;
	String strEmail;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// define uiHelper for facebook
		uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
		uiHelper.onCreate(savedInstanceState);		
		View view = inflater.inflate(R.layout.signin_fb_fragment, container, false);
		
		goonDB = new GoonDB(getActivity());
	    aq = new AQuery(getActivity());
		// Check Internet
		cd = new NetworkHelper(getActivity());
		isInternetPresent = cd.isConnectingToInternet();
				        
		// User Session Manager
        sessionlogin = new UserSessionManager(getActivity());
        boolean isSuccess = false;
		isSuccess = sessionlogin.isUserLoggedIn();
		
		loginBtn = (LoginButton) view.findViewById(R.id.authButton);	
		loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
            	if (user != null) {
					 //Toast.makeText(getActivity(), "fid :"+user.getId(),Toast.LENGTH_LONG).show();
					 //Intent i = new Intent(getActivity(), TabhostActivity.class);
		             //startActivity(i);
            		
					 get_id = user.getId();				 
					 get_email = (String) user.getProperty("email");
					 new RegisterThread(get_id, get_email, "").execute();     
				} else {
					
				}          	
            }
        });	

		return view;
	}
	
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
	
	
	// editprofile
	public void EditProfile() {
		getSession();
		myDialog = new Dialog(getActivity());
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
				/*
				if(ChildnameText.matches("") || ChildageText.matches("") || (age >= 1) && (age <= 279)){
					Toast.makeText(getActivity(), "Please enter childname.",Toast.LENGTH_LONG).show();	
					Toast.makeText(getActivity(), "Please enter childage.",Toast.LENGTH_LONG).show();	
					Toast.makeText(getActivity(), "กรุณาป้อนยายุครร4N 1 - 279",Toast.LENGTH_LONG).show();
				}else{
					myDialog.cancel();				
				    new EditProfileThread(pref_uid, pref_fid, pref_email, pref_password, ChildnameText, ChildageText, pref_avatar, DadnameText, MomnameText, ChildsexText, pref_born_date, pref_status).execute();
				}*/
				
				if (ChildnameText.matches("") || ChildageText.matches("")){
					Toast.makeText(getActivity(), "Please fill out this form completely.",Toast.LENGTH_LONG).show();
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
				dialog = ProgressDialog.show(getActivity(), "","Loading ...", true);
				dialog.setOnDismissListener(this);
				dialog.setCancelable(true);
			} else {
				Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, getActivity());
	        	this.cancel(true);
			}
    	}
		
		@Override
		protected String doInBackground(Integer... params) {
			return GoonService.DefaultService().createAccount(fid, email, password, getActivity());
		}
    
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			
			if(result != null && result.length() != 0) {
				Log.i("result", result); //Log
				
				JSONObject jsonResponse = null;	                     
		        try {	  
		             jsonResponse = new JSONObject(result);                     		           
		             CheckStatus = jsonResponse.getString("status");
		             CheckMessage = jsonResponse.getString("message");  

		         } catch (JSONException e) {         
		             e.printStackTrace();
		         }

		        if (CheckStatus.equals("0")) {
		        	Utilities.ShowDialogNotify("Create Account Error !", CheckMessage.toString(), "OK", false, getActivity());	
		        	//Intent intent = new Intent(getActivity(),TabhostActivity.class);
					//startActivity(intent);
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
		        	sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status, status_login_fb, pref_contry);
					
			        //Edit Profile
			        EditProfile();
		        	
		        	
		        
		        }
			} else {
				Utilities.ShowDialogNotify("Sorry !!!", "Invalid Username or Password", "Close", false, getActivity());
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
						dialog = ProgressDialog.show(getActivity(), "","Loading ...", true);
						dialog.setOnDismissListener(this);
						dialog.setCancelable(true);
					}else{
						Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, getActivity());
			        	this.cancel(true);
					}
		    	}
				
				@Override
				protected String doInBackground(Integer... params) {
					return GoonService.DefaultService().updateUser(uid, fid, email, password, child_name, child_age, avatar, dad_name, mom_name, child_sex, born_date, status, getActivity());
				}
		    
				@Override
				protected void onPostExecute(String result) {
					dialog.dismiss();
					
					if(result != null && result.length() != 0) {	
						Log.i("result editprofile", result);
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
				        	Toast.makeText(getActivity(), "Edit Profile Error !",Toast.LENGTH_LONG).show();
							
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
							
							Utilities.ShowDialogNotify("Edit Profile", "Edit Profile Complete !", "OK", false, getActivity());	
							sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status,status_login_fb, pref_contry);	
							getSession();
							//goonDB.dropTable();							
				        	new getDiaryThread(uid).execute();	
						}
				        
					} else {
						Log.i(TAG, "Sorry !!! Invalid");	
						this.cancel(true);
					}
		    	}

				@Override
				public void onDismiss(DialogInterface dialog) {
					cancel(true);
				}
		    }

			// getDiary 
			private class getDiaryThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
				String uid;
				
				getDiaryThread(String _uid) {
					uid = _uid;
				}
				
				@Override
				protected void onPreExecute() {		
					if (isInternetPresent) {
					} else {
						Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, getActivity());
			        	this.cancel(true);
					}
		    	}
				
				@Override
				protected String doInBackground(Integer... params) {
					return GoonService.DefaultService().getDiary(uid, getActivity());
				}
		    
				@Override
				protected void onPostExecute(String result) {
					
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
				        	Log.i(TAG, "Insert Diary Error !");
							
						} else if(CheckStatus.equals("1")) {	
							 JSONArray dataArr = jsonResponse.optJSONArray("dataArr");	
				             for (int i = 0; i < dataArr.length(); i++) {
									JSONObject jsonObject;
									try {
										jsonObject = dataArr.getJSONObject(i);
										diry_id = jsonObject.getString("id");
										diry_uid = jsonObject.getString("uid");							
										diry_text = jsonObject.getString("text");
										diry_image = jsonObject.getString("image");
										diry_weight = jsonObject.getString("description");
										diry_created = jsonObject.getString("created");
										diry_modified = jsonObject.getString("modified");
										
										// Insert Goon DB
									} catch (JSONException e) {
										e.printStackTrace();
									}		
									
									// Insert Goon DB
					            	long saveStatus = goonDB.insertDiaryNote(diry_created, diry_text, diry_image, 0);
					            	if (saveStatus <=  0)
					            	{
					            		Log.i(TAG, "Add Data Error.");	
					            	} else {
					            		Log.i(TAG, "Add Data Successfully.");	  
					            	}
					            	
								}
				             new getWeightThread(uid).execute();
				            	
						}
							
					} else {
						Log.i(TAG, "Sorry !!! Invalid");	
					}
		    	}

				@Override
				public void onDismiss(DialogInterface dialog) {
				}
		    }
			
			
			// getWeight
			private class getWeightThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
				String uid;
				
				getWeightThread(String _uid) {
					uid = _uid;
				}
				
				@Override
				protected void onPreExecute() {		
					if (isInternetPresent) {
					} else {
						Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, getActivity());
			        	this.cancel(true);
					}
		    	}
				
				@Override
				protected String doInBackground(Integer... params) {
					return GoonService.DefaultService().getDiary(uid, getActivity());
				}
		    
				@Override
				protected void onPostExecute(String result) {
					
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
				        	Log.i(TAG, "Insert Weight Error !");
							
						} else if(CheckStatus.equals("1")) {	
							 JSONArray dataArr = jsonResponse.optJSONArray("dataArr");	
				             for (int i = 0; i < dataArr.length(); i++) {
									JSONObject jsonObject;
									try {
										jsonObject = dataArr.getJSONObject(i);
										weight = jsonObject.getString("weight");
										weight_date = jsonObject.getString("created");				
										
										// Insert Goon DB
									} catch (JSONException e) {
										e.printStackTrace();
									}											
					            	
					            	// Insert Goon DB Weight
					            	long saveWeight = goonDB.insertWeightMeasure(weight_date, weight);
					            	if (saveWeight <=  0)
					            	{
					            		Log.i(TAG, "Add Weight Error.");	 
					            	} else {
					            		Log.i(TAG, "Add Weight Successfully.");	  
					            	}
								}
				             
								// Starting MainActivity
								Intent i = new Intent(getActivity(),TabhostActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
								// Add new Flag to start new Activity
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(i); 
								getActivity().finish();
								
						}
							
					} else {
						Log.i(TAG, "Sorry !!! Invalid");	
					}
		    	}

				@Override
				public void onDismiss(DialogInterface dialog) {
				}
		    }

}
