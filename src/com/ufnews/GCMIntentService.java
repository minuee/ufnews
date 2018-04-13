package com.ufnews;

import android.app.Notification;  
import android.app.NotificationManager; 
import android.app.PendingIntent; 
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;
import android.telephony.TelephonyManager;

import static com.ufnews.CommonUtilities.SENDER_ID;

import com.google.android.gcm.GCMBaseIntentService;
import com.ufnews.*;

public class GCMIntentService extends GCMBaseIntentService {

	String c2dm_msg = null;
	String tmDevice = null;
	String tmPhoneNumber = null;
	
	public ActivityTwo ActivityTwo;
	
	public GCMIntentService() {
		super(SENDER_ID);
	}

	public static final String TAG = "===GCMIntentService===";
	

	public void onReceive(Context context, Intent intent) {
   
	}
	

	@Override
	public void onRegistered(Context arg0, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		
		TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        tmDevice = "" + tm.getDeviceId();
        tmPhoneNumber = "" + tm.getLine1Number();
        
		GCM3rdPartyRequest registration_red_id = new GCM3rdPartyRequest();
		registration_red_id.Setting("http://m.ufnews.co.kr/GCMRegistration_Id.php", tmDevice , tmPhoneNumber , registrationId, null);


		registration_red_id.start();

		try {
			registration_red_id.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "unregistered = "+arg1);
	}


	@Override
	public void onMessage(Context context, Intent intent) {
	 handleMessage(context, intent);
	 }
	

	public void handleMessage(Context context, Intent intent) {
		
		ActivityTwo = new ActivityTwo();

		Utils.notiMsg = intent.getStringExtra("msg");
		Utils.notiTitle = intent.getStringExtra("title");
		Utils.notiContent = intent.getStringExtra("content");
		Utils.notiType = intent.getStringExtra("type");
		Utils.notiUrl = intent.getStringExtra("url");
		int icon = R.drawable.ic_launcher; // icon from resources
		CharSequence tickerText = Utils.notiTitle;//intent.getStringExtra("me"); // ticker-text
		long when = System.currentTimeMillis(); // notification time
		CharSequence contentTitle = ""+Utils.notiMsg; //intent.getStringExtra("me"); // message title
		CharSequence contentText = ""+Utils.notiContent; //intent.getStringExtra("me"); // message title
		NotificationManager notificationManager =
		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, tickerText, when);
		Intent notificationIntent = new Intent(context, ActivityTwo.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		notificationIntent.putExtra("reurl", intent.getStringExtra("url"));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
		notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags = Notification.FLAG_INSISTENT;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND;
		/*notification.vibrate=new long[] {100L, 100L, 200L, 500L};*/
		notificationManager.notify(1, notification);
		Utils.notificationReceived=true;
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
		wl.acquire();
	 }


	public void GET_GCM() { 
    
		Thread thread = new Thread(new Runnable() { 
        	public void run() { 
        	
        	handler.sendEmptyMessage(0); 
        	} 
    	}); 
    	thread.start(); 
	}



	public Handler handler = new Handler() { 
		public void handleMessage(Message msg) { 
                    	
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, c2dm_msg, duration);
			toast.show(); 
			c2dm_msg = null;
		} 
	};

	public void showMsg(String msg, int option) { 
		Toast.makeText(this, msg, option).show(); 
	}

	@Override
	public void onError(Context arg0, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	public boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
}


