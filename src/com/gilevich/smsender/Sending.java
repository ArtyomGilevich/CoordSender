package com.gilevich.smsender;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class Sending extends Service {
	int delay;
	String[] nms;
	double lat, lon;
	private static Timer timer;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		delay = Integer.valueOf(intent.getStringExtra("Delay"));
		nms = convertStringToArray(intent.getStringExtra("Numbers"));
		timer = new Timer();
		timer.scheduleAtFixedRate(new mainTask(), 0, delay * 60000);
		return super.onStartCommand(intent, flags, startId);
	}

	public static String[] convertStringToArray(String str) {
		String[] arr = str.split(",");
		return arr;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer.cancel();
	}

	private class mainTask extends TimerTask {
		public void run() {
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location loc = lm.getLastKnownLocation("network");
			Double lat, lon;
			lat = loc.getLatitude();
			lon = loc.getLongitude();
			Log.d("i1", String.valueOf(lat) + " " + String.valueOf(lon));
			SmsManager sms = SmsManager.getDefault();
			for (int i = 0; i < nms.length; i++) {
				sms.sendTextMessage(nms[i], null, String.valueOf(lat) + " "
						+ String.valueOf(lon), null, null);
			}
		}
	}

}
