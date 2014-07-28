package com.tmstudio.goon;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninAccFragment extends Fragment {
	
	// params for signin with account
	EditText fillemail;
	EditText fillpassword;
	Button signin;
	TextView createacc;
	TextView forgotpass;
	
	// params for service
	private AQuery aq;
	private String fid;
	private String weight,weight_date;
	private String service_fid, service_uid, service_email, service_password, 
	service_child_name, service_child_age, service_avatar ,service_dad_name, 
	service_mom_name, service_child_sex , service_born_date, service_status;	
	private String diry_id, diry_uid, diry_text, diry_image, diry_weight, diry_created, diry_modified;
	private String CheckStatus;
	private String CheckMessage;
	private String EmailText, PasswordText, PasswordText2;
	private String data_child_sex;
	final Context context = getActivity();
	Dialog createDialog;
	Dialog forgotDialog;
	AlertDialog.Builder popDialog;
	Dialog myDialog;
	private static final String TAG = SigninAccFragment.class.getName();
	
	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String status_login_fb = "1";
	private String status_login_acc = "2";
	private String pref_fid, pref_uid, pref_email, pref_password, pref_child_name, pref_child_age, pref_avatar, pref_dad_name, pref_mom_name, pref_child_sex, pref_born_date, pref_status, pref_status_login, pref_contry;
	
	//check internet
	Boolean isInternetPresent = false;
	NetworkHelper cd;
	
	// param for goonDB 
	GoonDB goonDB;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signin_acc_fragment, container, false);	
		
		goonDB = new GoonDB(getActivity());
		aq = new AQuery(getActivity());
		// Check Internet
		cd = new NetworkHelper(getActivity().getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
		        
		// User Session Manager
        sessionlogin = new UserSessionManager(getActivity());
		boolean isSuccess = false;
		isSuccess = sessionlogin.isUserLoggedIn();

		fillemail = (EditText) view.findViewById(R.id.fillemailforlogin);
		fillpassword = (EditText) view.findViewById(R.id.fillpasswordforlogin);
		signin = (Button) view.findViewById(R.id.loginwithacc);
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
		});
		
		return view;
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
	

	// Login Service
	private class LoginThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener{
		ProgressDialog dialog;
		String fid,user,pass;
		
		LoginThread(String _fid, String _user,String _pass) {
			fid = _fid;
			user = _user;
			pass = _pass;
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
			return GoonService.DefaultService().getUserByLogin(fid,user, pass, getActivity());
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
					 
					// Creating user login session
			        sessionlogin.createUserLoginSession(service_uid, service_fid, service_email, service_password, service_child_name, service_child_age, service_avatar, service_dad_name, service_mom_name, service_child_sex, service_born_date, service_status, status_login_acc, pref_contry);
			        getSession();
			        //goonDB.dropTable();
			        new getDiaryThread(pref_uid).execute();
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
								
							} catch (JSONException e) {
								e.printStackTrace();
							}		
							// Insert Goon DB
			            	long saveStatus = goonDB.insertDiaryNote(diry_created, diry_text, diry_image, 0);
			            	/* if (saveStatus <=  0)
			            	{
			            		Toast.makeText(getActivity(),"Add Diary Error. ",Toast.LENGTH_SHORT).show();   
			            	} else {
			            		Toast.makeText(getActivity(),"Add Diary Successfully. ",Toast.LENGTH_SHORT).show();   
			            	} */						            	
						}	
		             new getWeightThread(pref_uid).execute();
				}
					
			} else {
				Toast.makeText(getActivity(), "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
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
			            	/*if (saveWeight <=  0)
			            	{
			            		Toast.makeText(getActivity(),"Add Weight Error. ",Toast.LENGTH_SHORT).show();   
			            	} else {
			            		Toast.makeText(getActivity(),"Add Weight Successfully. ",Toast.LENGTH_SHORT).show();   
			            	}*/
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
				Toast.makeText(getActivity(), "Sorry !!! Invalid",Toast.LENGTH_LONG).show();
			}
    	}

		@Override
		public void onDismiss(DialogInterface dialog) {
		}
    }
}