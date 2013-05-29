package com.gilevich.smsender;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	static LinearLayout l1;
	static LayoutInflater ltInflater;
	static Button b1, b2, b3;
	static EditText e1;
	static SharedPreferences sPref;
	static String[] nms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		e1 = (EditText) findViewById(R.id.editText1);
		b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(this);
		b2 = (Button) findViewById(R.id.button2);
		b2.setOnClickListener(this);
		b3 = (Button) findViewById(R.id.button3);
		b3.setOnClickListener(this);
		if (isMyServiceRunning() == true) {
			b3.setText("Прекратить отправку!");
		} else {
			b3.setText("Начать отправку!");
		}
		String savedText;
		sPref = getPreferences(MODE_PRIVATE);
		if (!sPref.contains("Cnt")) {
			Editor ed = sPref.edit();
			ed.putInt("Cnt", 0);
			ed.commit();
		}
		if (sPref.getInt("Cnt", 0) == 10) {
			finish();
		}
		savedText = sPref.getString("PhoneNumbers", "");
		nms = convertStringToArray(savedText);
		ltInflater = getLayoutInflater();
		l1 = (LinearLayout) findViewById(R.id.lin);
		for (int i = 0; i < nms.length; i++) {
			addView();
		}
		for (int i = 0; i < nms.length; i++) {
			View v = l1.getChildAt(i);
			EditText e = (EditText) v.findViewById(R.id.editText1);
			e.setText(nms[i]);
		}
		e1.setText(sPref.getString("Delay", "0"));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sPref = getPreferences(MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putInt("Cnt", sPref.getInt("Cnt", 0) + 1);
		Log.d("i1", String.valueOf(sPref.getInt("Cnt", 0)));
		ed.commit();
	}

	void addView() {
		ltInflater = getLayoutInflater();
		View item = ltInflater.inflate(R.layout.item, l1, false);
		TextView tvName = (TextView) item.findViewById(R.id.textView1);
		tvName.setText("№" + String.valueOf(l1.getChildCount() + 1) + " - ");
		l1.addView(item);
	}

	void saveData() {
		SharedPreferences sPref = getPreferences(MODE_PRIVATE);
		Editor ed = sPref.edit();
		ArrayList<String> nms = new ArrayList<String>();
		for (int i = 0; i < l1.getChildCount(); i++) {
			View v = l1.getChildAt(i);
			EditText e = (EditText) v.findViewById(R.id.editText1);
			nms.add(e.getText().toString());
		}
		ed.putString("PhoneNumbers", convertArrayToString(nms));
		ed.putString("Delay", e1.getText().toString());
		ed.commit();
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (Sending.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static String convertArrayToString(ArrayList<String> array) {
		String str = "";
		for (int i = 0; i < array.size(); i++) {
			str = str + array.get(i);
			// Do not append comma at the end of last element
			if (i < array.size() - 1) {
				str = str + ",";
			}
		}
		return str;
	}

	public static String[] convertStringToArray(String str) {
		String[] arr = str.split(",");
		return arr;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.button1:
			saveData();
			break;
		case R.id.button2:
			addView();
			break;
		case R.id.button3:
			if (b3.getText().toString() == "Начать отправку!") {
				Intent intent = new Intent(this, Sending.class);
				intent.putExtra("Delay", e1.getText().toString());
				SharedPreferences sPref = getPreferences(MODE_PRIVATE);
				String savedText = sPref.getString("PhoneNumbers", "");
				intent.putExtra("Numbers", savedText);
				startService(intent);
				b3.setText("Прекратить отправку!");
			} else {
				stopService(new Intent(this, Sending.class));
				b3.setText("Начать отправку!");
			}
			break;
		}
	}
}
