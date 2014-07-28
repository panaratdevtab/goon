package com.tmstudio.goon.services;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GoonService {

	private static MyJSONRPC myJSONRPC;
	private static String url = "http://rid.in.th/service/service.php";

	
private static GoonService instance = null;
	
	public GoonService() {
		// Exists only to defeat instantiation.
	}
	
	public static GoonService DefaultService() {

		if (instance == null) {
			instance = new GoonService();
			myJSONRPC = new MyJSONRPC(url);  
		}
		return instance;
	}
	
	
	public boolean getAccessByVersionCode(String device,Activity activity)
	{
		try 
		{
		JSONObject jsonParameter = new JSONObject();
		
		jsonParameter.put("device", device);
		jsonParameter.put("version_code", activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode);
		
		JSONObject inputJSON = null;
		inputJSON = myJSONRPC.doRequest("getAccessByVersionCode", jsonParameter);
		
			if (inputJSON != null)
			{
				JSONObject result  = inputJSON.getJSONObject("result");
			
				if(result.getString("status").equals("ok"))
				return true;
			}
		}
		catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			
		return false;
	}
	
	public boolean updateStatus(Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("device_id", IMEI);
			jsonParameter.put("type", "Android");
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("updateStatus", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");
				
				SharedPreferences pref = activity.getSharedPreferences("GOON_ONLINE",activity.MODE_PRIVATE); 
				pref.edit().putString("GOON_ONLINE_USER", result).commit(); 
				return true;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		return false;
	}
	
	public JSONObject getPopup()
	{
		try 
		{
			JSONObject result = null;
			result = myJSONRPC.doRequest("getPopup", null);
			
			if (result != null)
			{
				JSONObject _result = result.getJSONObject("result");
				return _result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public JSONArray getCampaign()
	{
		try 
		{
			JSONObject result = null;
			result = myJSONRPC.doRequest("getCampaign", null);
			
			if (result != null)
			{
				JSONArray _result = result.getJSONArray("result");
				return _result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public JSONArray getContent(String device)
	{
		try 
		{
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("device", device);
			
			JSONObject result = null;
			result = myJSONRPC.doRequest("getContent", jsonParameter);
			
			if (result != null)
			{
				JSONArray _result = result.getJSONArray("result");
				return _result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String createAccount(String fid,String email,String password,Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("fid", fid);
			jsonParameter.put("email", email);
			jsonParameter.put("password", password);
			jsonParameter.put("child_name", "Test");
			jsonParameter.put("child_age", "11");
			jsonParameter.put("avatar", "00");
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("insertUser", jsonParameter);
			Log.i("jsonParameter", jsonParameter.toString());
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String updateUser(String uid, String fid, String email, String password, String child_name, String child_age ,String avatar ,String dad_name ,String mom_name,String child_sex, String born_date, String status,Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("fid", fid);
			jsonParameter.put("email", email);
			jsonParameter.put("password", password);
			jsonParameter.put("child_name", child_name);
			jsonParameter.put("child_age", child_age);
			jsonParameter.put("avatar", avatar);
			jsonParameter.put("dad_name", dad_name);
			jsonParameter.put("mom_name", mom_name);
			jsonParameter.put("child_sex", child_sex);
			jsonParameter.put("born_date", born_date);
			jsonParameter.put("status", status);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("updateUser", jsonParameter);
			Log.i("updateUser", jsonParameter.toString());
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String getUserByLogin(String fid,String email,String password,Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("fid", fid);
			jsonParameter.put("email", email);
			jsonParameter.put("password", password);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getUserByLogin", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String forgotPassword(String email,Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("email", email);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("forgotPassword", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String getDiary(String uid, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getDiaryByUserID", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String insertDiary(String uid, String date, String text, String image, String weight, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("date", date);
			jsonParameter.put("text", text);
			jsonParameter.put("image", "00");
			jsonParameter.put("weight", weight);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("insertDiary", jsonParameter);
			Log.i("insert jsonParameter", jsonParameter.toString());
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String updateDiary(String id, String date, String text, String image, String weight, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("id", id);
			jsonParameter.put("date", date);
			jsonParameter.put("text", text);
			jsonParameter.put("image", "00");
			jsonParameter.put("weight", weight);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("updateDiary", jsonParameter);
			Log.i("update jsonParameter", jsonParameter.toString());
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String getDiaryTip(String lastTime,Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("lastTime", lastTime);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getDiaryTip", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String getCategoryTip(String lastTime, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("lastTime", lastTime);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getCategoryTip", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String getTip(String lastTime, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("lastTime", lastTime);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getTip", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String sendEmail(String uid, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("sendPDF", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	public String insertWeight(String uid,String weight, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("weight", weight);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("insertWeight", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	
	
	public String getWeight(String uid, Activity activity)
	{
		try 
		{
			String IMEI = getIMEI(activity);
			
			JSONObject jsonParameter = new JSONObject();
			
			jsonParameter.put("uid", uid);
			jsonParameter.put("id", IMEI);
			
			JSONObject inputJSON = null;
			inputJSON = myJSONRPC.doRequest("getWeightByUserID", jsonParameter);
			
			if (inputJSON != null)
			{
				String result  = inputJSON.getString("result");

				return result;
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}
	

	private String getIMEI(Activity activity)
	{
		TelephonyManager telephonyManager =((TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE));
		return telephonyManager.getDeviceId();
	}
}
