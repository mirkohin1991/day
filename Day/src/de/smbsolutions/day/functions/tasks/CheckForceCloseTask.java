package de.smbsolutions.day.functions.tasks;

import org.focuser.sendmelogs.LogCollector;

import android.os.AsyncTask;
import android.util.Log;
import de.smbsolutions.day.functions.interfaces.MainCallback;

/**
 * 
 * Der CheckForceCloseTask überprüft im Hintergrund, ob die App zuletzt korrekt
 * beendet wurde. Ist dies nicht der Fall, wird ein Dialog aufgerufen, welche
 * den User fragt ob er den Log an die Entwickler senden möchte.
 * 
 */
public class CheckForceCloseTask extends AsyncTask<Void, Void, Boolean> {
	// LogCollector und Callback zur Activity
	private LogCollector mLogCollector;
	private MainCallback mCallback;

	public CheckForceCloseTask(LogCollector mLogCollector,
			MainCallback mCallback) {
		this.mLogCollector = mLogCollector;
		this.mCallback = mCallback;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		return mLogCollector.hasForceCloseHappened();
	}

	/**
	 * Sammelt alle Logs in einem neuen Hintergrundtask und öffnet dann den
	 * Dialog.
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		// Wurde die App richtig geschlossen?
		if (result) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					return mLogCollector.collect();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					// dismissDialog(DIALOG_PROGRESS_COLLECTING_LOG);
					if (result == true) {
						mCallback.onDumpDialogShow();
					}

				}

			}.execute();
		} else
			Log.d("ForceClose", "No force close detected.");
	}
}
