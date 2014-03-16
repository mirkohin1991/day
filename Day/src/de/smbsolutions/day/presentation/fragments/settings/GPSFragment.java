package de.smbsolutions.day.presentation.fragments.settings;

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
import de.smbsolutions.day.functions.interfaces.MainCallback;

public class GPSFragment extends android.support.v4.app.Fragment {

	private SeekBar seekBarFrequency;
	private SeekBar seekBarFrequencyMeter;
	private Switch switchGPSOnOff;
	private TextView actSec;
	private TextView actMeter;
	private MainCallback mCallback;
	private int timeSec = 0;
	private int meter = 0;
	private int result_time;
	private int result_meter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mCallback = (MainCallback) getActivity();

		View view = inflater.inflate(R.layout.fragment_settings_gps, container,
				false);

		actSec = (TextView) view.findViewById(R.id.actSec);
		actMeter = (TextView) view.findViewById(R.id.actMeter);

		// final int timeSec = Device.getAPP_SETTINGS().getTrackingFrequency() /
		// 1000;
		// final int meter = Device.getAPP_SETTINGS().getTrackingMeter();

		timeSec = Database.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 1000;
		meter = Database.getSettingValue(Database.SETTINGS_TRACKING_METER);

		switchGPSOnOff = (Switch) view.findViewById(R.id.switchGPSOnOff);
		seekBarFrequency = (SeekBar) view.findViewById(R.id.seekBarFrequency);
		seekBarFrequencyMeter = (SeekBar) view
				.findViewById(R.id.SeekBarFrequencyMeter);

		// Wert (On/Off) aus der Datenbank abrufen
		// if (Device.getAPP_SETTINGS().getTrackingStatus() == 1) {
		if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

			switchGPSOnOff.setChecked(true);
			seekBarFrequency.setEnabled(true);
			seekBarFrequencyMeter.setEnabled(true);

			actMeter.setText("Aktuell: " + meter + " Meter");

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
		} else {
			switchGPSOnOff.setChecked(false);
			seekBarFrequency.setEnabled(false);
			seekBarFrequencyMeter.setEnabled(false);
			actSec.setText("Aktuell: GPS ausgeschaltet");
			actMeter.setText("Aktuell: GPS ausgeschaltet");
		}
		view.post(new Runnable() {

			@Override
			public void run() {
				// Wenn Wert geaendert wird, diesen in die Datenbank schreiben
				switchGPSOnOff
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {

								if (isChecked) {
									// Device.getAPP_SETTINGS().setTrackingStatus(1);
									Database.changeSettingValue(
											Database.SETTINGS_TRACKING, 1);

									// final int timeSec =
									// Device.getAPP_SETTINGS().getTrackingFrequency()
									// /
									// 1000;
									// final int meter =
									// Device.getAPP_SETTINGS().getTrackingMeter();

									timeSec = Database
											.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 1000;
									meter = Database
											.getSettingValue(Database.SETTINGS_TRACKING_METER);

									seekBarFrequency.setEnabled(true);
									seekBarFrequencyMeter.setEnabled(true);

									actMeter.setText("Aktuell: " + meter
											+ " Meter");

									if (timeSec >= 60) {
										int i = timeSec / 60;

										if (i == 1) {
											actSec.setText("Aktuell: 1 Minute");

										} else {
											actSec.setText("Aktuell: " + i
													+ " Minuten");
										}
									} else {
										actSec.setText("Aktuell: " + timeSec
												+ " Sekunden");

									}
								} else {
									// Device.getAPP_SETTINGS().setTrackingStatus(0);
									Database.changeSettingValue(
											Database.SETTINGS_TRACKING, 0);
									seekBarFrequency.setEnabled(false);
									seekBarFrequencyMeter.setEnabled(false);
									actSec.setText("Aktuell: GPS ausgeschaltet");
									actMeter.setText("Aktuell: GPS ausgeschaltet");

								}

								// Notify service of changes
								mCallback.onTrackingTurnedOnOff();

							}
						});

				seekBarFrequencyMeter.setProgress(meter - 10);

				if (timeSec >= 60) {
					int i = timeSec / 60;
					if (i == 1) {
						seekBarFrequency.setProgress(50);
					} else {
						seekBarFrequency.setProgress(50 + i);
					}
				} else {
					seekBarFrequency.setProgress(timeSec - 10);
				}

				seekBarFrequency
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							int frequency = 0;

							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {

								frequency = progress + 10;

								if (frequency >= 60) {
									int i = frequency - 60;
									if (i == 0) {
										actSec.setText("Aktuell: 1 Minute");
									} else {
										i = i + 1;
										actSec.setText("Aktuell: " + i
												+ " Minuten");
									}
								} else {
									actSec.setText("Aktuell: " + frequency
											+ " Sekunden");
								}

							}

							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
							}

							public void onStopTrackingTouch(SeekBar seekBar) {

								if (frequency >= 60) {
									int frequency2save = (((frequency - 60) * 60000) + 60000);
									if (frequency2save == 0) {
										frequency2save = frequency2save + 60000;
										// Device.getAPP_SETTINGS().setTrackingFrequency(i);
										result_time = frequency2save;
									} else {
										// Device.getAPP_SETTINGS().setTrackingFrequency(i);
										result_time = frequency2save;
									}
								} else {
									// Device.getAPP_SETTINGS().setTrackingFrequency(frequency
									// * 1000);
									result_time = frequency * 1000;

								}

							}

						});

				seekBarFrequencyMeter
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							int frequencyMeter = 0;

							public void onProgressChanged(SeekBar seekBarMeter,
									int progress, boolean fromUser) {

								frequencyMeter = progress + 10;

								actMeter.setText("Aktuell: " + frequencyMeter
										+ " Meter");

							}

							public void onStartTrackingTouch(
									SeekBar seekBarMeter) {
								// TODO Auto-generated method stub
							}

							public void onStopTrackingTouch(SeekBar seekBarMeter) {

								result_meter = frequencyMeter;

							}

						});
			}
		});

		return view;

	}

	@Override
	public void onDestroy() {
		Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,
				result_time);

		Database.changeSettingValue(Database.SETTINGS_TRACKING_METER,
				result_meter);
		mCallback.onTrackingIntervalChanged();
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
