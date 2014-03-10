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

public class GPSFragment extends android.support.v4.app.Fragment {

	public GPSFragment() {
	}

	private SeekBar seekBarFrequency;
	private Switch switchGPSOnOff;
	private TextView actSec;
	private TextView actMeter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings_gps, container,
				false);

		actSec = (TextView) rootView.findViewById(R.id.actSec);
		actMeter = (TextView) rootView.findViewById(R.id.actMeter);
		
		final int timeSec = Device.getAPP_SETTINGS().getTrackingFrequency() / 1000;
		final int meter = Device.getAPP_SETTINGS().getTrackingMeter();
		
		Switch switchGPSOnOff = (Switch) rootView.findViewById(R.id.switchGPSOnOff);
		final SeekBar seekBarFrequency = (SeekBar) rootView.findViewById(R.id.seekBarFrequency);
		final SeekBar seekBarFrequencyMeter = (SeekBar) rootView.findViewById(R.id.SeekBarFrequencyMeter);


		// Wert (On/Off) aus der Datenbank abrufen
		if (Device.getAPP_SETTINGS().getTrackingStatus() == 1) {

			switchGPSOnOff.setChecked(true);
			seekBarFrequency.setEnabled(true);
			seekBarFrequencyMeter.setEnabled(true);
			
			actMeter.setText("Aktuell: " + meter + " Meter");
			seekBarFrequencyMeter.setProgress(meter);

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

		// Wenn Wert geaendert wird, diesen in die Datenbank schreiben
		switchGPSOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Device.getAPP_SETTINGS().setTrackingStatus(1);
							
							final int timeSec = Device.getAPP_SETTINGS().getTrackingFrequency() / 1000;
							final int meter = Device.getAPP_SETTINGS().getTrackingMeter();
							
							seekBarFrequency.setEnabled(true);
							seekBarFrequencyMeter.setEnabled(true);
							
							actMeter.setText("Aktuell: " + meter + " Meter");


							if (timeSec >= 60) {
								int i = timeSec / 60;

								if (i == 1) {
									actSec.setText("Aktuell: 1 Minute");

								} 
								else {
									actSec.setText("Aktuell: " + i + " Minuten");
								}
							} else {
								actSec.setText("Aktuell: " + timeSec
										+ " Sekunden");

							}
						} else {
							Device.getAPP_SETTINGS().setTrackingStatus(0);
							seekBarFrequency.setEnabled(false);
							seekBarFrequencyMeter.setEnabled(false);
							actSec.setText("Aktuell: GPS ausgeschaltet");
							actMeter.setText("Aktuell: GPS ausgeschaltet");

						}
					}
				});
		
		
		seekBarFrequencyMeter.setProgress(meter - 5);
		
		if (timeSec >= 60) {
			int i = timeSec / 60;
			if (i == 1) {
				seekBarFrequency.setProgress(50);
			}
			else{
				seekBarFrequency.setProgress(50 + i);
			}
		} else{seekBarFrequency.setProgress(timeSec - 10);}
		

		seekBarFrequency.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int frequency = 0;

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						
						frequency = progress + 10;

						if (frequency >= 60) {
							int i = frequency - 60;
							if (i == 0) {
								actSec.setText("Aktuell: 1 Minute");
							}
							else{
								i = i + 1;
							actSec.setText("Aktuell: " + i + " Minuten");
							}
						} else{actSec.setText("Aktuell: " + frequency + " Sekunden");}

					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						
					
						
						if (frequency >= 60) {
							int i = (((frequency - 60) * 60000) + 60000);
							if(i == 0)
							{
								i = i + 60000;
								Device.getAPP_SETTINGS().setTrackingFrequency(i);
								//Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,(i));
							}
							else{
								Device.getAPP_SETTINGS().setTrackingFrequency(i);
								//Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,(i));
						
							}
						} else
							Device.getAPP_SETTINGS().setTrackingFrequency(frequency * 1000);

							//Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,f);
					}

				});
		
		

		seekBarFrequencyMeter.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int frequencyMeter = 0;

			public void onProgressChanged(SeekBar seekBarMeter,
					int progress, boolean fromUser) {
				
				frequencyMeter = progress + 5;

				actMeter.setText("Aktuell: " + frequencyMeter + " Meter");

			}

			public void onStartTrackingTouch(SeekBar seekBarMeter) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBarMeter) {
				
				//Database.changeSettingValue(Database.SETTINGS_TRACKING_METER, frequencyMeter);
				Device.getAPP_SETTINGS().setTrackingFrequency(frequencyMeter);
				
			}

		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		return rootView;

	}

}
