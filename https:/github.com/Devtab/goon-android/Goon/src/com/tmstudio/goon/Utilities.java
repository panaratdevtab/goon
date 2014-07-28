package com.tmstudio.goon;
import com.tmstudio.goon.services.GoonService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

public class Utilities extends Activity {

	public static void ShowDialogNotify(String title,String msg,String okButton,final boolean isExit,final Activity activity)
    {
    	final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(activity);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(title);
        dlgAlert.setCancelable(true);
        dlgAlert.setNeutralButton(okButton, new DialogInterface.OnClickListener()
        {  
            public void onClick(DialogInterface arg0, int arg1)
            {
            	if(isExit)
            	{
            		activity.finish();
            	}
            }  
        });
        dlgAlert.create().show();
    }
	
	 public static void showDialogExit(final Activity ac)
		{
		 final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ac);
	        dlgAlert.setMessage("Are you sure to exit ???");
	        dlgAlert.setTitle("Confirm exit?");
	        dlgAlert.setCancelable(true);
	        dlgAlert.setNegativeButton("Cancel", null);
	        dlgAlert.setNeutralButton("Exit", new DialogInterface.OnClickListener()
	        {  
	            public void onClick(DialogInterface arg0, int arg1)
	            {
	            	ac.finish();
	            }  
	        });
	        dlgAlert.create().show();
		}

}
