package de.smbsolutions.day.presentation.fragments;


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

public class GPSFragment extends Fragment {
	
	public GPSFragment(){}
	private SeekBar seekBarFrequency;
	private Switch switchGPSOnOff;
    private TextView actSec;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_gps, container, false);
//        

//        int tracking = Database.getSettingValue(Database.SETTINGS_TRACKING);
//        Toast t1 = Toast.makeText(getActivity(),String.valueOf(tracking), Toast.LENGTH_SHORT);
//        t1.show();

        actSec = (TextView)rootView.findViewById(R.id.actSec);
      
       
        Switch switchGPSOnOff = (Switch)rootView.findViewById(R.id.switchGPSOnOff);
        final SeekBar seekBarFrequency = (SeekBar)rootView.findViewById(R.id.seekBarFrequency);
       
      
        
        //Wert (On/Off) aus der Datenbank abrufen
        if (Database.getSettingValue(Database.SETTINGS_TRACKING) == 1){
        	
        switchGPSOnOff.setChecked(true);
        seekBarFrequency.setEnabled(true);
    	actSec.setText("Aktuell: " + Database.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) / 100 + " Sekunden");
    	
        }
        else {
        	 switchGPSOnOff.setChecked(false);
        	 seekBarFrequency.setEnabled(false);
        	 actSec.setText("Aktuell: GPS ausgeschaltet");
        }
        
        
        //Wenn Wert geaendert wird, diesen in die Datenbank schreiben
        switchGPSOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	Database.changeSettingValue(Database.SETTINGS_TRACKING, 1);
                	seekBarFrequency.setEnabled(true);
                	actSec.setText("Aktuell: " + Database.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL) + " Sekunden");
                	
                } else {
                	Database.changeSettingValue(Database.SETTINGS_TRACKING, 0);
                	seekBarFrequency.setEnabled(false);
                	actSec.setText("Aktuell: GPS ausgeschaltet");
                	
                }
            }
        });
        
        
        //SeekBar
        seekBarFrequency.setProgress(Database.getSettingValue(Database.SETTINGS_TRACKING_INTERVAL));
        seekBarFrequency.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int frequency = 0;
 
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            	frequency = progress;
            	actSec.setText("Aktuell: " + progress + " Sekunden");
            	
            	
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
    
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getActivity(),"seek bar progress:"+frequency,
//                        Toast.LENGTH_SHORT).show();
            	Database.changeSettingValue(Database.SETTINGS_TRACKING_INTERVAL, frequency);
            }
           
            
        });

        return rootView;
        
        
        
          }
	
	
}
