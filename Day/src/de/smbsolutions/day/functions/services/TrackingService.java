package de.smbsolutions.day.functions.services;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.location.LocationTracker;

public class TrackingService extends Service {

	private LocationTracker tracker;
	private ServiceBinder mBinder = new ServiceBinder();
	private Timer mTimer;
	private Long mStartzeit;
	private Handler locationHandler;
	private Database db = Database.getInstance(this);

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {

			if (tracker.enoughDistance()) {

				// Getting the current timestamp
				Timestamp tsTemp = new Timestamp(System.currentTimeMillis());

//				 db.addNewRoutePoint(tracker.getLatitude(),
//						tracker.getLongitude(), tsTemp);
				

			}

		}
	};

	public class ServiceBinder extends Binder {

		// Schnittstellenmethoden für Service

		// AUS DEM BUCH
		// public void setAcitivityCallbackHandler ( final Handler callback) {
		// locationHandler = callback;
		// }

	}

	@Override
	// Binder ist klar definierte Schnittstelle die Kommunikation mit dem
	// Service regeelt
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public boolean onUnbind(Intent intent) {

		locationHandler = null;

		return super.onUnbind(intent);

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		tracker = LocationTracker.getInstance(this);

		mTimer = new Timer();

		mStartzeit = System.currentTimeMillis();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTimer.cancel();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		// Start timer
		mTimer.scheduleAtFixedRate(mTimerTask, 0, // Verzögerung
				Device.getAPP_SETTINGS().getTrackingFrequency()); // Intervall

		return super.onStartCommand(intent, flags, startId);
	}

}
