package com.tmstudio.goon;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.Session;
import com.tmstudio.goon.database.GoonDB;
import com.tmstudio.goon.services.GoonService;
import com.tmstudio.goon.services.NetworkHelper;
import com.tmstudio.goon.services.UserSessionManager;

public class Report extends Activity {

	Calendar calen;
	LinearLayout todayWeightLayout;
	TextView todayWeightTextView;
	TextView showMonth;
	ListView reportlist;
	ArrayList<HashMap<String, String>> mylist, mylist_title;
	ListAdapter adapter_title, adapter;
	HashMap<String, String> map1, map2;
	ArrayList<String> listWeight, listDate;
	String[] date = {};
	String[] weight = {};
	TextView txtweight;
	String InputWeight;
	
	int pos;	
	Spinner chooseMonthSpinner;
	

	// for dialog method
	String showWeightString;
	float finalWeightFloat = 0;
	String finalWeightString;

	String w, updateDate;
	int index;

	// for database
	GoonDB goonDb;
	
	private AQuery aq;
	private String uid,email;
	private String CheckStatus;
	private String CheckMessage;
	final Context context = this;
	Dialog myDialog;
	
	// User Session Manager Class
	UserSessionManager sessionlogin;
	private String status_login_fb = "1";
	private String status_login_acc = "2";
	
	//check internet
	Boolean isInternetPresent = false;
	NetworkHelper cd;
	
	// BackPress
	private long backPressed = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		
		aq = new AQuery(this);
		aq.id(R.id.btn_sendemail).clicked(this,"SendEmailClicked");
		aq.id(R.id.logout).clicked(this,"LogoutButtonClicked");
		
		// Session class instance
		sessionlogin = new UserSessionManager(getApplicationContext());
		// get user data from session
		HashMap<String, String> user = sessionlogin.getUserDetails();
		// get name
		uid = user.get(UserSessionManager.KEY_UID);
		email = user.get(UserSessionManager.KEY_EMAIL);
	
		// Check Internet
		cd = new NetworkHelper(Report.this.getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        
		// set month spinner
		chooseMonthSpinner = (Spinner) findViewById(R.id.choosemonth);
		ArrayList<String> monthList = new ArrayList<String>();

		monthList.add(getResources().getString(R.string.jan));
		monthList.add(getResources().getString(R.string.feb));
		monthList.add(getResources().getString(R.string.mar));
		monthList.add(getResources().getString(R.string.april));
		monthList.add(getResources().getString(R.string.may));
		monthList.add(getResources().getString(R.string.june));
		monthList.add(getResources().getString(R.string.july));
		monthList.add(getResources().getString(R.string.aug));
		monthList.add(getResources().getString(R.string.sep));
		monthList.add(getResources().getString(R.string.oct));
		monthList.add(getResources().getString(R.string.nov));
		monthList.add(getResources().getString(R.string.dec));

		ArrayAdapter<String> monthAdaper = new ArrayAdapter<String>(this,
				R.layout.choosemonth_item, monthList);
		chooseMonthSpinner.setAdapter(monthAdaper);
		
		// get current month
		Calendar c = Calendar.getInstance();
		int currMonth = c.get(Calendar.MONTH);
		
		chooseMonthSpinner.setSelection(currMonth);

		chooseMonthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long arg3) {
				((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
				int pos = position + 1;
				showActivity(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		goonDb = new GoonDB(this);

		todayWeightLayout = (LinearLayout) findViewById(R.id.todayweightLayout);
		todayWeightTextView = (TextView) findViewById(R.id.todayweight);
		
		insertTestData(); //Test
		getLastWeight();
		reportlist = (ListView) findViewById(R.id.reportlist);
		showActivity(pos);

		reportlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView weightInList = (TextView) view
						.findViewById(R.id.weightinlist);
				TextView txtDate = (TextView) view.findViewById(R.id.date);
				String uDate = txtDate.getText().toString();
				w = weightInList.getText().toString();
				Dialog(-3, w, uDate);
				weightInList.setText(finalWeightFloat + "");

			}
		});
		todayWeightLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				w = todayWeightTextView.getText().toString();
				Dialog(-2, w, null);

			}
		});

	}

	private void getLastWeight() {
		int day, month, year;
		String fDate;
		String lastWeight;

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
		lastWeight = goonDb.getWeightByDate(fDate);
		todayWeightTextView.setText(lastWeight);
	}

	public void showActivity(int position) {
		mylist = new ArrayList<HashMap<String, String>>();
		mylist = goonDb.getWeightByMonth(position);
		try {
			adapter = new SimpleAdapter(this, mylist, R.layout.report_row,
					new String[] { "date", "weight" }, new int[] { R.id.date,
							R.id.weightinlist });
			reportlist.setAdapter(adapter);
		} catch (Exception e) {

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		showActivity(pos);
	}



	public String Dialog(final int num, String w, String date) {
		int day, month, year;
		final String fDate;
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
		updateDate = date;
		final Dialog dialog = new Dialog(Report.this);
		dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.weight_mesurement_dialog);
		dialog.setCanceledOnTouchOutside(true);

		finalWeightFloat = Float.parseFloat(w);
		showWeightString = finalWeightFloat + "";

		final TextView weight = (TextView) dialog.findViewById(R.id.weight);
		weight.setText(showWeightString); // init weight from profile

		ImageButton plus = (ImageButton) dialog.findViewById(R.id.plus);
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showWeightString = String.format("%.1f",
						(Float.parseFloat(showWeightString) + 0.1))
						+ "";
				finalWeightFloat = Float.parseFloat(showWeightString);
				weight.setText(showWeightString);
			}
		});

		ImageButton minus = (ImageButton) dialog.findViewById(R.id.minus);
		minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showWeightString = String.format("%.1f",
						(Float.parseFloat(showWeightString) - 0.1))
						+ "";
				finalWeightFloat = Float.parseFloat(showWeightString);
				weight.setText(showWeightString);
			}
		});

		Button ok = (Button) dialog.findViewById(R.id.weightchaged);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (num == -2) {
					updateWeight(fDate, weight.getText().toString());
					showActivity(pos);
					getLastWeight();
					dialog.dismiss();
				} else if (num == -3) {
					updateWeight(updateDate, weight.getText().toString());
					showActivity(pos);
					getLastWeight();
					dialog.dismiss();
				}
				
				//Insert Weight
				new InsertWeight().execute();

				//Insert to Service
				//new insertWeightThread(uid, InputWeight); */
			}
		});
		dialog.show();

		return finalWeightString;

	}

	/*
	//Insert Weight
	new InsertWeight().execute();

	//Insert to Service
	//new insertWeightThread(uid, InputWeight); */
	
	public void updateWeight(String date, String weight) {
		goonDb.UpdateWeightMeasure(date, weight);
		showActivity(pos);
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

		goonDb.insertWeightMeasure(fDate, "0.0");
	}
	
	
	// Send Email
	public void SendEmailClicked(View view) {
		 myDialog = new Dialog(Report.this);
		 myDialog.requestWindowFeature(myDialog.getWindow().FEATURE_NO_TITLE);
         myDialog.setContentView(R.layout.report_email_dialog);

         TextView finishbtn = (TextView) myDialog.findViewById(R.id.report_cancel);
         TextView txtemail = (TextView) myDialog.findViewById(R.id.report_email);
         Button sendbtn = (Button) myDialog.findViewById(R.id.btn_send);
         txtemail.setText(email.toString());
         finishbtn.setOnClickListener(new OnClickListener() {
              @Override
               public void onClick(View v) {
                     myDialog.cancel();
               }
         });
         sendbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new SendEmailThread(uid).execute();
				myDialog.cancel();
			}
		});
         
         myDialog.show();
      }

	// Check Logout
	public void LogoutButtonClicked(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("Do you want to exit ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// function logout
								logout();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	private void logout(){
		// clear session login user
		sessionlogin.logoutUser();
	    // find the active session which can only be facebook in my app
	    Session session = Session.getActiveSession();
	    // run the closeAndClearTokenInformation which does the following
	    // DOCS : Closes the local in-memory Session object and clears any persistent 
	    // cache related to the Session.
	    session.closeAndClearTokenInformation();
	    // return the user to the login screen
	    // clear the activity stack
	    Intent intent = new Intent(this, Signin.class);
	    intent.putExtra("finish", true); // if you are checking for this in your other Activities
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
	                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    finish();
	  
	}

	
	// Send Email Service
	private class SendEmailThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener{
		ProgressDialog dialog;
		String uid;
		
		SendEmailThread(String _uid) {
			uid = _uid;		
		}
		
		@Override
		protected void onPreExecute() {		
			if (isInternetPresent) {
				dialog = ProgressDialog.show(Report.this, "","Loading ...", true);
				dialog.setOnDismissListener(this);
				dialog.setCancelable(true);
			}else{
				Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Report.this);
	        	this.cancel(true);
			}
    	}
		
		@Override
		protected String doInBackground(Integer... params) {
			return GoonService.DefaultService().sendEmail(uid, Report.this);
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


		            if (CheckStatus.equals("0")) {
				        Utilities.ShowDialogNotify("Send Email Error !", CheckMessage.toString(), "Close", false, Report.this);
							
					} else if(CheckStatus.equals("1")) {
						Utilities.ShowDialogNotify("Send Email", "Send Email Complete !", "OK", false, Report.this);	
						// Insert Goon DB
					}
 
		         } catch (JSONException e) {         
		             e.printStackTrace();
		         }

			} else {
				Utilities.ShowDialogNotify("Sorry !!!", "Invalid Send Email", "Close", false, Report.this);
				this.cancel(true);
			}
    	}

		@Override
		public void onDismiss(DialogInterface dialog) {
			cancel(true);
		}
    }
	
	    // Insert Weight
		private class insertWeightThread extends AsyncTask<Integer, Integer, String>  implements OnDismissListener {
			ProgressDialog dialog;
			String uid, weight;
			
			insertWeightThread (String _uid, String _weight) {
				uid = _uid;
				weight = _weight;
			}
			
			@Override
			protected void onPreExecute() {		
				if (isInternetPresent) {
					dialog = ProgressDialog.show(Report.this, "","Loading ...", true);
					dialog.setOnDismissListener(this);
					dialog.setCancelable(true);
				} else {
					Utilities.ShowDialogNotify(GlobalVariable.connectlostTitle, GlobalVariable.connectlostMsg, "OK", false, Report.this);
		        	this.cancel(true);
				}
	    	}
			
			@Override
			protected String doInBackground(Integer... params) {
				return GoonService.DefaultService().insertWeight(uid, weight , Report.this);
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
			        	Toast.makeText(Report.this, "Insert Weight Error !",Toast.LENGTH_LONG).show();
						
					} else if(CheckStatus.equals("1")) {		
						Toast.makeText(Report.this, "Insert Weight Complete !",Toast.LENGTH_LONG).show();
						// Insert Goon DB
						//goonDB.insertDiaryTips(dirytip_id, dirytip_tip_text,dirytip_num_date, dirytip_modified);						
					}
	
				} else {
					Utilities.ShowDialogNotify("Sorry !!!", "Invalid", "Close", false, Report.this);
					this.cancel(true);
				}
	    	}
	
			@Override
			public void onDismiss(DialogInterface dialog) {
				cancel(true);
			}
	    }
	
			// Insert Weight Old
			public class InsertWeight extends AsyncTask<Void, Void, Void> {

				ProgressDialog progressDialog;
				boolean isSuccess = false;

				@Override
				protected void onPreExecute() {
					progressDialog = new ProgressDialog(Report.this);
					progressDialog.setIndeterminate(true);
					progressDialog.setCancelable(false);
					progressDialog.setInverseBackgroundForced(false);
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setTitle("กำลังบันทึกน้ำหนักคุณแม่");
					progressDialog.setMessage("Please wait ...");
					progressDialog.show();
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					
					// build json object
					JSONObject jObjectData2 = new JSONObject();	
					try {
				        JSONObject jObjectData = new JSONObject();	   
				        jObjectData.put("uid", uid);
				        jObjectData.put("weight", InputWeight);
				             
				        jObjectData2.put("jsonrpc",   "2.0");
				        jObjectData2.put("method",    "insertWeight");
				        jObjectData2.put("params",    jObjectData);
				        jObjectData2.put("id",        "808E6EEC-1335-431F-8BE7-958BD414F68B");
			
					    Log.i("jObjectData2", jObjectData2.toString());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				
					// send data request
					try {
						
						URL url = new URL("http://rid.in.th/service/service.php");
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost(url.toURI());

						// Prepare JSON to send by setting the entity
						httpPost.setEntity(new StringEntity(jObjectData2.toString(), "UTF-8"));

						// Set up the header types needed to properly transfer JSON
						httpPost.setHeader("Content-Type", "application/json");
						httpPost.setHeader("Accept-Encoding", "application/json");
						httpPost.setHeader("Accept-Language", "en-US");

						// Execute POST
						HttpResponse response = httpClient.execute(httpPost);			
						
						// response
						String responseText = EntityUtils.toString(response.getEntity());
						Log.i("responseText", responseText);
					
						//read response json
						JSONObject jsonResponse;	                     
				        try {	  
				        	
				             jsonResponse = new JSONObject(responseText);                     
				             JSONObject jsonMainNode = jsonResponse.optJSONObject("result");	
				           
				             CheckStatus = jsonMainNode.getString("status");
				             CheckMessage = jsonMainNode.getString("message");  
		 
				         } catch (JSONException e) {         
				             e.printStackTrace();
				         }
				        
					} catch (Exception e) {
						// handle exception here
						Log.e(e.getClass().getName(), e.getMessage());
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					progressDialog.dismiss();

					if (CheckStatus.equals("0")) {

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
						alertDialogBuilder.setTitle("Insert Weight Error !");
						alertDialogBuilder.setMessage(CheckMessage.toString());
						alertDialogBuilder.setPositiveButton("OK", null);
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
						
					} else if(CheckStatus.equals("1")) {	
						Utilities.ShowDialogNotify("บันทึกน้ำหนักคุณแม่", "บันทึกน้ำหนักคุณแม่เรียบร้อย.", "OK", false, Report.this);
					}
				}
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
