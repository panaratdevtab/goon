package com.tmstudio.goon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.tmstudio.goon.database.GoonDB;

public class ShareFacebook extends Activity{
	
	private static String APP_ID = "537140153074644";
	
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;
	
	Button btnFbLogin;
	Button btnFbGetProfile;
	Button btnPostToWall;
	Button btnShowAccessTokens;
	
	String dateString;
	
	GoonDB db = new GoonDB(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			dateString = getIntent().getStringExtra("date");
		    postToWall(dateString);
	}
	
	
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {
		 
		  mPrefs = getPreferences(MODE_PRIVATE);
		  String access_token = mPrefs.getString("access_token", null);
		  long expires = mPrefs.getLong("access_expires", 0);
		 
		  if (access_token != null) {
		   facebook.setAccessToken(access_token);
		    
		   btnFbLogin.setVisibility(View.INVISIBLE);
		    
		   // Making get profile button visible
		   btnFbGetProfile.setVisibility(View.VISIBLE);
		 
		   // Making post to wall visible
		   btnPostToWall.setVisibility(View.VISIBLE);
		 
		   // Making show access tokens button visible
		   btnShowAccessTokens.setVisibility(View.VISIBLE);
		 
		   Log.d("FB Sessions", "" + facebook.isSessionValid());
		  }
		 
		  if (expires != 0) {
		   facebook.setAccessExpires(expires);
		  }
		 
		  if (!facebook.isSessionValid()) {
		   facebook.authorize(this,
		     new String[] { "email", "publish_stream" },
		     new DialogListener() {
		 
		      @Override
		      public void onCancel() {
		       // Function to handle cancel event
		      }
		 
		      @Override
		      public void onComplete(Bundle values) {
		       // Function to handle complete event
		       // Edit Preferences and update facebook acess_token
		       SharedPreferences.Editor editor = mPrefs.edit();
		       editor.putString("access_token",
		         facebook.getAccessToken());
		       editor.putLong("access_expires",
		         facebook.getAccessExpires());
		       editor.commit();
		 
		       // Making Login button invisible
		       btnFbLogin.setVisibility(View.INVISIBLE);
		 
		       // Making logout Button visible
		       btnFbGetProfile.setVisibility(View.VISIBLE);
		 
		       // Making post to wall visible
		       btnPostToWall.setVisibility(View.VISIBLE);
		 
		       // Making show access tokens button visible
		       btnShowAccessTokens.setVisibility(View.VISIBLE);
		      }
		 
		      @Override
		      public void onError(DialogError error) {
		       // Function to handle error
		 
		      }
		 
		      @Override
		      public void onFacebookError(FacebookError fberror) {
		       // Function to handle Facebook errors
		 
		      }
		 
		     });
		  }
		 }
	
	@Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  facebook.authorizeCallback(requestCode, resultCode, data);
	 }
	
	/**
	  * Get Profile information by making request to Facebook Graph API
	  * */
	 @SuppressWarnings("deprecation")
	 public void getProfileInformation() {
	  mAsyncRunner.request("me", new RequestListener() {
	   @Override
	   public void onComplete(String response, Object state) {
	    Log.d("Profile", response);
	    String json = response;
	    try {
	     // Facebook Profile JSON data
	     JSONObject profile = new JSONObject(json);
	      
	     // getting name of the user
	     final String name = profile.getString("name");
	      
	     // getting email of the user
	     final String email = profile.getString("email");
	      
	     runOnUiThread(new Runnable() {
	 
	      @Override
	      public void run() {
	       Toast.makeText(getApplicationContext(), "Name: " + name + "\nEmail: " + email, Toast.LENGTH_LONG).show();
	      }
	 
	     });
	 
	      
	    } catch (JSONException e) {
	     e.printStackTrace();
	    }
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
	  });
	 }
	 
	 /**
	  * Function to post to facebook wall
	  * */
	 @SuppressWarnings("deprecation")
	 public void postToWall(String date) {
		 
		 Bundle parameters = new Bundle();
		 String link = "http://www.google.co.th";
		 parameters.putString("name", "Sharing form Goo.N application");
		 parameters.putString("caption", date);
		 parameters.putString("link", link);
		 parameters.putString("description", db.getDiaryNoteByDate_noteText(date));
		 parameters.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
		 
		 
	     // post on user's wall.
		 facebook.dialog(this, "feed", parameters, new DialogListener() {
			
			@Override
			public void onFacebookError(FacebookError e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(DialogError e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete(Bundle values) {
				// TODO Auto-generated method stub
				Utilities.ShowDialogNotify("Share Facebook", "Share Facebook Complete !", "OK", false, ShareFacebook.this);
				finish();
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		});
	 }
//	  facebook.dialog(this, "feed", new DialogListener() {
//	 
//	   @Override
//	   public void onFacebookError(FacebookError e) {
//	   }
//	 
//	   @Override
//	   public void onError(DialogError e) {
//	   }
//	 
//	   @Override
//	   public void onComplete(Bundle values) {
//	   }
//	 
//	   @Override
//	   public void onCancel() {
//	   }
//	  });
//	 
//	 }
	 
	 /**
	  * Function to show Access Tokens
	  * */
	 public void showAccessTokens() {
	  String access_token = facebook.getAccessToken();
	 
	  Toast.makeText(getApplicationContext(),
	    "Access Token: " + access_token, Toast.LENGTH_LONG).show();
	 }
	  
	 /**
	  * Function to Logout user from Facebook
	  * */
	 @SuppressWarnings("deprecation")
	 public void logoutFromFacebook() {
	  mAsyncRunner.logout(this, new RequestListener() {
	   @Override
	   public void onComplete(String response, Object state) {
	    Log.d("Logout from Facebook", response);
	    if (Boolean.parseBoolean(response) == true) {
	     runOnUiThread(new Runnable() {
	 
	      @Override
	      public void run() {
	       // make Login button visible
	       btnFbLogin.setVisibility(View.VISIBLE);
	 
	       // making all remaining buttons invisible
	       btnFbGetProfile.setVisibility(View.INVISIBLE);
	       btnPostToWall.setVisibility(View.INVISIBLE);
	       btnShowAccessTokens.setVisibility(View.INVISIBLE);
	      }
	 
	     });
	 
	    }
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
	  });
	 }
	 
}
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.Toast;
//
//import com.facebook.android.DialogError;
//import com.facebook.android.Facebook;
//import com.facebook.android.Facebook.DialogListener;
//import com.facebook.android.FacebookError;
//
//public class ShareFacebook extends Activity{
//
//	private static final String APP_ID = "222137544641625";
//	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
//
//	private static final String TOKEN = "access_token";
//        private static final String EXPIRES = "expires_in";
//        private static final String KEY = "facebook-credentials";
//
//	private Facebook facebook;
//	private String messageToPost;
//
//	public boolean saveCredentials(Facebook facebook) {
//        	Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
//        	editor.putString(TOKEN, facebook.getAccessToken());
//        	editor.putLong(EXPIRES, facebook.getAccessExpires());
//        	return editor.commit();
//    	}
//
//    	public boolean restoreCredentials(Facebook facebook) {
//        	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
//        	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
//        	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
//        	return facebook.isSessionValid();
//    	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		facebook = new Facebook(APP_ID);
//		restoreCredentials(facebook);
//
//		share();
//
//		String facebookMessage = getIntent().getStringExtra("facebookMessage");
//		if (facebookMessage == null){
//			facebookMessage = "Test wall post";
//		}
//		messageToPost = facebookMessage;
//	}
//
//	public void doNotShare(View button){
//		finish();
//	}
//	public void share(){
//		if (! facebook.isSessionValid()) {
//			loginAndPostToWall();
//		}
//		else {
//			postToWall(messageToPost);
//		}
//	}
//
//	public void loginAndPostToWall(){
//		 facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
//	}
//
//	public void postToWall(String message){
//		Bundle parameters = new Bundle();
//                parameters.putString("message", message);
//                parameters.putString("description", "topic share");
//                try {
//        	        facebook.request("me");
//			String response = facebook.request("me/feed", parameters, "POST");
//			Log.d("Tests", "got response: " + response);
//			if (response == null || response.equals("") ||
//			        response.equals("false")) {
//				showToast("Blank response.");
//			}
//			else {
//				showToast("Message posted to your facebook wall!");
//			}
//			finish();
//		} catch (Exception e) {
//			showToast("Failed to post to wall!");
//			e.printStackTrace();
//			finish();
//		}
//	}
//
//	class LoginDialogListener implements DialogListener {
//	    public void onComplete(Bundle values) {
//	    	saveCredentials(facebook);
//	    	if (messageToPost != null){
//			postToWall(messageToPost);
//		}
//	    }
//	    public void onFacebookError(FacebookError error) {
//	    	showToast("Authentication with Facebook failed!");
//	        finish();
//	    }
//	    public void onError(DialogError error) {
//	    	showToast("Authentication with Facebook failed!");
//	        finish();
//	    }
//	    public void onCancel() {
//	    	showToast("Authentication with Facebook cancelled!");
//	        finish();
//	    }
//	}
//
//	private void showToast(String message){
//		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//	}
//}