package de.smbsolutions.day.presentation.fragments.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;

public class CameraFragment extends android.support.v4.app.Fragment {
	
	public CameraFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_settings_camera, container, false);
        
        
        int show_in_gal = Database.getSettingValue(Database.SETTINGS_TRACKING);
        Toast t1 = Toast.makeText(getActivity(),String.valueOf(show_in_gal), Toast.LENGTH_SHORT);
        t1.show();
        
        Switch switchShowInGal = (Switch)rootView.findViewById(R.id.switchShowInGal);
        
        
        //Wert (On/Off) aus der Datenbank abrufen
        if (Database.getSettingValue(Database.SETTINGS_SHOW_IN_GAL) == 1){
        	switchShowInGal.setChecked(true);

        }
        else {
        	switchShowInGal.setChecked(false);
        	
        }

        //Wenn Wert geaendert wird, diesen in die Datenbank schreiben
        switchShowInGal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL, 1);
                	
                	File path = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "MyCameraApp");
                	File from = new File(path,"nomedia");
                	File to = new File(path,".nomedia");
                	from.renameTo(to);
                	

                	
                } else {
//             
                	Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL, 0);
                	File path = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "MyCameraApp");
                	File from = new File(path,".nomedia");
                	File to = new File(path,"nomedia");
                	from.renameTo(to);
               
                }
            }
        });

        return rootView;
    }
	

}
//	
	
	
	

