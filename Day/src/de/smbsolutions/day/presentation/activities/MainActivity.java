package de.smbsolutions.day.presentation.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.services.TrackingService;
import de.smbsolutions.day.functions.services.TrackingService.ServiceBinder;
import de.smbsolutions.day.presentation.popups.RouteNameDialog;

public class MainActivity extends Activity {
	
	private static Handler trackingHandler;
	
	
	//KONKRETE VERBINDUNG ZUM SERVICE, ZUGRIFF ÜBER DIE DEFINIERTEN METHODEN DES BINDERS der TRACKINGSERVICE KLASSE.
	//DA WIR ABER NUR REGELMÄßIG die GPS DATEN SPEICHERN WOLLEN; BRAUCHEN WIR DIE VERBINDUNG ZUNÄCHST NICHT.
	//BUCH um S.180
//	private ServiceConnection serviceConn = new ServiceConnection() {
//		
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//		}
//		
//		@Override
//		public void onServiceConnected(ComponentName className, IBinder binder) {
//			// TODO Auto-generated method stub
//	
//			((ServiceBinder) binder).setAcitivityCallbackHandler ( trackingHandler);
//			
//		}
//	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Database.getInstance(this);
	
			
	}
	public void onButtonClick(View view){
		Button startButton = (Button) findViewById(R.id.button3);
		Button stopButton = (Button) findViewById(R.id.button4);
		switch (view.getId()) {
		case R.id.button1:
			startActivity(new Intent(this, MapActivity.class));
			break;
		case R.id.button2:
			startActivity(new Intent(this,KameraActivity.class));
			break;
			
		case R.id.button3:
			startService(new Intent(this, TrackingService.class));
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			
			break;
		case R.id.button4:
			
			stopService(new Intent(this, TrackingService.class));
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
//			Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL, 5000);
		
			break;
			
		case R.id.newRoute:
			
			RouteNameDialog dialog = new RouteNameDialog();
	        //Showing the popup / Second Parameter: Unique Name, that is used to identify the dialog
			dialog.show(getFragmentManager(), "NameDialog");
			
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
