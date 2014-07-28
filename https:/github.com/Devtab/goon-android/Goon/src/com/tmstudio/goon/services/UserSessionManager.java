package com.tmstudio.goon.services;

import java.util.HashMap;

import com.tmstudio.goon.Signin;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSessionManager {

	// Shared Preferences reference
	SharedPreferences pref;

	// Editor reference for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREFER_NAME = "AndroidPref";

	// All Shared Preferences Keys
	private static final String IS_USER_LOGIN = "IsUserLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_UID = "uid";
	public static final String KEY_FID = "fid";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_CHILD_NAME = "child_name";
	public static final String KEY_CHILD_AGE= "child_age";
	public static final String KEY_AVATAR = "avatar";
	public static final String KEY_DAD_NAME = "dad_name";
	public static final String KEY_MOM_NAME = "mom_name";
	public static final String KEY_CHILD_SEX = "child_sex";
	public static final String KEY_BORN_DATE = "born_date";
	public static final String KEY_STATUS = "status";
	public static final String KEY_STATUS_LOGIN = "status_login";  //fb=1 acc=2
	public static final String KEY_CONTRY = "contry";

	// Constructor
	public UserSessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	// Create login session
	public void createUserLoginSession(String uid, String fid, String email, String password, String child_name, String child_age, String avatar, String dad_name, String mom_name, String child_sex, String born_date, String status, String status_login, String contry) {
		// Storing login value as TRUE
		editor.putBoolean(IS_USER_LOGIN, true);

		// Storing name in pref
		editor.putString(KEY_UID, uid);
		editor.putString(KEY_FID, fid);
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_PASSWORD, password);
		editor.putString(KEY_CHILD_NAME, child_name);
		editor.putString(KEY_CHILD_AGE, child_age);
		editor.putString(KEY_AVATAR, avatar);
		editor.putString(KEY_DAD_NAME, dad_name);
		editor.putString(KEY_MOM_NAME, mom_name);
		editor.putString(KEY_CHILD_SEX, child_sex);
		editor.putString(KEY_BORN_DATE, born_date);
		editor.putString(KEY_STATUS, status);
		editor.putString(KEY_STATUS_LOGIN, status_login);
		editor.putString(KEY_CONTRY, contry);
		
		// commit changes
		editor.commit();
	}

	/**
	 * Check login method will check user login status If false it will redirect
	 * user to login page Else do anything
	 * */
	public boolean checkLogin() {
		// Check login status
		if (!this.isUserLoggedIn()) {

			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, Signin.class);

			// Closing all the Activities from stack
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);

			return true;
		}
		return false;
	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {

		// Use hashmap to store user credentials
		HashMap<String, String> user = new HashMap<String, String>();
		
		// user name
		user.put(KEY_UID, pref.getString(KEY_UID, null));
		user.put(KEY_FID, pref.getString(KEY_FID, null));
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
		user.put(KEY_CHILD_NAME, pref.getString(KEY_CHILD_NAME, null));
		user.put(KEY_CHILD_AGE, pref.getString(KEY_CHILD_AGE, null));
		user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));
		user.put(KEY_DAD_NAME, pref.getString(KEY_DAD_NAME, null));
		user.put(KEY_MOM_NAME, pref.getString(KEY_MOM_NAME, null));
		user.put(KEY_CHILD_SEX, pref.getString(KEY_CHILD_SEX, null));
		user.put(KEY_BORN_DATE, pref.getString(KEY_BORN_DATE, null));
		user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
		user.put(KEY_STATUS_LOGIN, pref.getString(KEY_STATUS_LOGIN, null));
		user.put(KEY_CONTRY, pref.getString(KEY_CONTRY, null));
		
		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {

		// Clearing all user data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, Signin.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	// Check for login
	public boolean isUserLoggedIn() {
		return pref.getBoolean(IS_USER_LOGIN, false);
	}
}
