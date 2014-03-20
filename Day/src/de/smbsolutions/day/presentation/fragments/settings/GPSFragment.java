package de.smbsolutions.day.presentation.fragments.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;

/**
 * Einstellungen für GPS Tracking, GPS Wiederholungsrate und minimale Entferung.
 * Berechnet die Werte für die Anzeige.
 * Speichert Werte für das GPS Tracking, GPS Wiederholungsrate und minimale Entferung in der Datenbank.
 */
public class GPSFragment extends android.support.v4.app.Fragment {
	private Switch switchGPSOnOff;
	private TextView actSec;
	private TextView actMeter;
	private MainCallback mCallback;
	private int timeSec;
	private int meter;
	private SeekBar seekBarFrequency;
	private SeekBar seekBarFrequencyMeter;

/**
 * Bei Aufruf werden die aktuellen Daten aus der Datenbank gelesen und in Variablen gespeichert.
 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mCallback = (MainCallback) getActivity();

		View rootView = inflater.inflate(R.layout.fragment_settings_gps,
				container, false);

//		Layoutelemente definiere
		actSec = (TextView) rootView.findViewById(R.id.actSec);
		actMeter = (TextView) rootView.findViewById(R.id.actMeter);
		
		Switch switchGPSOnOff = (Switch) rootView
				.findViewById(R.id.switchGPSOnOff);
		seekBarFrequency = (SeekBar) rootView
				.findViewById(R.id.seekBarFrequency);
		seekBarFrequencyMeter = (SeekBar) rootView
				.findViewById(R.id.SeekBarFrequencyMeter);
		
		// Werte für Zeit und Meter aus der Datenbank lesen
		timeSec = Database
				.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 1000;
		meter = Database
				.getSettingValue(Database.SETTINGS_TRACKING_METER);

		
//		  Wert für Tracking (On/Off) aus der Datenbank abrufen.
//		  Das Switch für das GPS Tracking und die Seekbars für Wiederholungsrate, und minimale Entfernung 
//		  auf die Entsprechenden Werte setzen.
		 
		if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

			// GPS Switch, Seekbar für Zeit, Seekbar für Meter aktivieren
			switchGPSOnOff.setChecked(true);
			seekBarFrequency.setEnabled(true);
			seekBarFrequencyMeter.setEnabled(true);

			// Im Textfeld actMeter die aktuelle Meter Zahl ausgeben
			actMeter.setText("Aktuell: " + meter + " Meter");

			// Berechnung ob die Zeit die aus der Datenbank kommt im
			// Sekundenbereich, oder im Minuten Bereich liegt.
			// Je nachdem wird die aktuelle Zeit in Minuten oder Sekunden
			// angezeigt
			if (timeSec >= 60) {
				int i = timeSec / 60;
				if (i == 1) {
					actSec.setText("Aktuell: " + i + " Minute");

				} else {
					actSec.setText("Aktuell: " + i + " Minuten");
				}
			} else {
				actSec.setText("Aktuell: " + timeSec + " Sekunden");
			}
		}
		// Falls das Tracking ausgeschaltet ist alle Regler auf der Seite
		// deaktivieren und die entsprechenden Texte anzeigen.
		else {
			switchGPSOnOff.setChecked(false);
			seekBarFrequency.setEnabled(false);
			seekBarFrequencyMeter.setEnabled(false);
			actSec.setText("Aktuell: GPS ausgeschaltet");
			actMeter.setText("Aktuell: GPS ausgeschaltet");
		}

		// Wenn Wert geaendert wird, diesen in die Datenbank schreiben
		switchGPSOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			/**
			 * Prüfen ob der Wert des GPS Tracking Switch geändert wurde.
			 */
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						// Wenn aktiv:
						if (isChecked) {
							Database.changeSettingValue(
									Database.SETTINGS_TRACKING, 1);

							// Die Werte für Zeit und Meter in die Datenbank
							// lesen.
							// Der Wert für die Zeit muss durch Milisekunden
							// geteilt werden ( /1000)
							timeSec = Database
									.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 1000;
							meter = Database
									.getSettingValue(Database.SETTINGS_TRACKING_METER);

							// Seekbar für Zeit und Meter aktivieren
							seekBarFrequency.setEnabled(true);
							seekBarFrequencyMeter.setEnabled(true);

							// Im Textfeld actMeter die aktuelle Meter Zahl
							// ausgeben
							actMeter.setText("Aktuell: " + meter + " Meter");

							// Berechnung ob die Zeit die aus der Datenbank
							// kommt im Sekundenbereich, oder im Minuten Bereich
							// liegt.
							// Je nachdem wird die aktuelle Zeit in Minuten oder
							// Sekunden angezeigt
							if (timeSec >= 60) {
								int i = timeSec / 60;

								if (i == 1) {
									actSec.setText("Aktuell: 1 Minute");

								} else {
									actSec.setText("Aktuell: " + i + " Minuten");
								}
							} else {
								actSec.setText("Aktuell: " + timeSec
										+ " Sekunden");

							}
						} else {
							// Falls das Tracking ausgeschaltet ist, den Wert 0
							// in die Datenbank schreiben und alle Regler auf
							// der Seite deaktivieren und die entsprechenden
							// Texte anzeigen.
							Database.changeSettingValue(
									Database.SETTINGS_TRACKING, 0);
							seekBarFrequency.setEnabled(false);
							seekBarFrequencyMeter.setEnabled(false);
							actSec.setText("Aktuell: GPS ausgeschaltet");
							actMeter.setText("Aktuell: GPS ausgeschaltet");
						}
						mCallback.onTrackingTurnedOnOff();
					}
				});
		// Der Wert der Seekbar muss um 5 substrahiert werden, um eine richtige
		// Anzeige zu gewährleisten.
		// Seekbars haben standardmaeßig einen Wertebereich von 0 - 100.
		// Das Maximum ist in den Layout Datein änderbar.
		// Minimum Werte können nicht direkt gesetzt werden, sondern müssen als
		// ganze Zahlen direkt substrahiert werden.
		seekBarFrequencyMeter.setProgress(meter - 5);

		// Anzeige der Zeit.
		// Prüfen ob es sich um Sekunden oder Minuten handelt, und den
		// Fortschritt anzeigen.
		if (timeSec >= 60) {
			int i = timeSec / 60;
			if (i == 1) {
				seekBarFrequency.setProgress(50);
			} else {
				seekBarFrequency.setProgress(50 + i);
			}
		} else {
			// Setzt den Wert der Seekbar um 10 zurück um den richtigen Wert der
			// in der Datenbank steht anzeigen.
			// Gleiche Problematik wie bei
			// "seekBarFrequencyMeter.setProgress(meter - 5);" weiter oben.
			seekBarFrequency.setProgress(timeSec - 10);
		}

		// Standardmethode einer Seekbar.
		// Sie wird automatisch aufgerufen wenn sich der Wert der Seekbar
		// ändert.
		seekBarFrequency
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int frequency = 0;

					/**
					 * Standardmethode einer Seekbar.
					 * Sie wird automatisch aufgerufen wenn der Nutzer die Seekbar betätigt
					 * Ändert den Wert der Anzeige zur Laufzeit
					 */
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						// frequency ist der Wert der in die Datenbank
						// geschrieben wird.
						// Er repraesentiert den aktuellen Wert der Seekbar. Da
						// dieser weiter oben geändert wird, muss hier der
						// gleiche Wert wieder addiert werden,
						// um die richtigen Informationen in die Datenbank zu
						// speichern.
						frequency = progress + 10;

						// Es wird geprüft, ob sich die Zeit im Sekundenbereich
						// oder Minutenbereich befindet.
						// Je nachdem wird der Wert in Minuten oder Sekunden
						// angezeigt
						if (frequency >= 60) {
							int i = frequency - 60;
							if (i == 0) {
								actSec.setText("Aktuell: 1 Minute");
							} else {
								i = i + 1;
								actSec.setText("Aktuell: " + i + " Minuten");
							}
						} else {
							actSec.setText("Aktuell: " + frequency
									+ " Sekunden");
						}

					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					/**
					 * Standardmethode einer Seekbar.
					 * Sie wird automatisch aufgerufen wenn der Nutzer die
					 * Seekbar "loslaesst".
					 */
					public void onStopTrackingTouch(SeekBar seekBar) {

						// Prüfen ob die Zeit größer gleich 60 ist.
						if (frequency >= 60) {
							// ja: 1. Von i werden 60 Sekunden substrahiert.
							// 2. Der Wert wird mit 60000 Milisekunden
							// multipliziert
							// 3. Es wird eine Minute in Millisekunden addiert.
							int i = (((frequency - 60) * 60000) + 60000);
							if (i == 0) {
								// Wenn i = 0 wahr ist, dann i den Wert von
								// 60000 Millisekunden zuweisen (1 Minute)
								i = i + 60000;
								// Diesen Wert in die Datenbank schreiben. Er
								// entspricht: 1 Minute = 60 Sekunden = 60000
								// Millisekunden
								Database.changeSettingValue(
										Database.SETTINGS_TRACKING_INTERVAL,
										(i));
							} else {
								// Andernfalls den oben berechneten Wert in die
								// Datenbank schreiben
								Database.changeSettingValue(
										Database.SETTINGS_TRACKING_INTERVAL,
										(i));
							}
						} else {
							// Falls die Zeit unterhalb von 60 Sekunden liegt
							// ist keine Umrechnung nötig.
							// Die Sekunden müssen lediglich noch in
							// Millisekunden umgerechnet werden. (* 1000)
							Database.changeSettingValue(
									Database.SETTINGS_TRACKING_INTERVAL,
									frequency * 1000);
						}

						mCallback.onTrackingIntervalChanged();
					}

				});

		seekBarFrequencyMeter
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int frequencyMeter = 0;

					/**
					 * Standardmethode einer Seekbar.
					 * Sie wird automatisch aufgerufen wenn der Nutzer die Seekbar betätigt.
					 * Aendert die Anzeige der Meteranzeige
					 */
					public void onProgressChanged(SeekBar seekBarMeter,
							int progress, boolean fromUser) {

						// FrequencyMeter spiegelt den Wert der Seekbar wieder.
						// Auf ihn müssen 5 addiert werden, da diese 5 weiter
						// oben abgezogen werden,
						// um den Wert der Anzeige (actMeter) richtig anzuzeigen
						frequencyMeter = progress + 5;

						actMeter.setText("Aktuell: " + frequencyMeter
								+ " Meter");

					}

					public void onStartTrackingTouch(SeekBar seekBarMeter) {
						// TODO Auto-generated method stub
					}

					/**
					 * Standardmethode einer Seekbar.
					 * Sie wird automatisch aufgerufen wenn der Nutzer die
					 * Seekbar "loslaesst".
					 * Speichert die aktuelle Meteranzahl in die Datenbank.
					 */
					public void onStopTrackingTouch(SeekBar seekBarMeter) {

						// Der Wert von frequencyMeter wird in die Datenbank
						// geschrieben
						Database.changeSettingValue(
								Database.SETTINGS_TRACKING_METER,
								frequencyMeter);
					}

				});

		return rootView;

	}

}
