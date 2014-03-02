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
import android.widget.Toast;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;

public class GPSFragment extends android.support.v4.app.Fragment {

	public GPSFragment() {
	}

	private SeekBar seekBarFrequency;
	private Switch switchGPSOnOff;
	private TextView actSec;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings_gps, container,
				false);
		//

		// int tracking = Database.getSettingValue(Database.SETTINGS_TRACKING);
		// Toast t1 = Toast.makeText(getActivity(),String.valueOf(tracking),
		// Toast.LENGTH_SHORT);
		// t1.show();

		actSec = (TextView) rootView.findViewById(R.id.actSec);
		
		final int timeSec = Database.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 1000;

			
		Switch switchGPSOnOff = (Switch) rootView.findViewById(R.id.switchGPSOnOff);
		final SeekBar seekBarFrequency = (SeekBar) rootView.findViewById(R.id.seekBarFrequency);

		// Wert (On/Off) aus der Datenbank abrufen
		if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1) {

			switchGPSOnOff.setChecked(true);
			seekBarFrequency.setEnabled(true);

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
			actSec.setText("Aktuell: GPS ausgeschaltet");
		}

		// Wenn Wert geaendert wird, diesen in die Datenbank schreiben
		switchGPSOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Database.changeSettingValue(Database.SETTINGS_TRACKING, 1);
							seekBarFrequency.setEnabled(true);

							if (timeSec >= 60) {
								int i = timeSec - 60;

								if (i == 0) {
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
							Database.changeSettingValue(
							Database.SETTINGS_TRACKING, 0);
							seekBarFrequency.setEnabled(false);
							actSec.setText("Aktuell: GPS ausgeschaltet");

						}
					}
				});

		// SeekBar
//		seekBarFrequency.setProgress(timeSec / 60);
		
		
		
		
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
						// Toast.makeText(getActivity(),"seek bar progress:"+frequency,
						// Toast.LENGTH_SHORT).show();

						if (frequency >= 60) {
							int i = (((frequency - 60) * 60000) + 60000);
							if(i == 0)
							{
								i = i + 60000;
								Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,(i));
							}
							else{
							Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,(i));
							}
						} else
							Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL,frequency * 1000);
					}

				});

		return rootView;

	}

}
