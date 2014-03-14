package de.smbsolutions.day.presentation.fragments.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.database.Database;
import de.smbsolutions.day.functions.initialization.Device;

public class CameraFragment extends android.support.v4.app.Fragment {
	
	public CameraFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_settings_camera, container, false);
        
        
        int show_in_gal = Database.getSettingValue(Database.SETTINGS_TRACKING);
        
        Switch switchShowInGal = (Switch)rootView.findViewById(R.id.switchShowInGal);
        
        
        //Wert (On/Off) aus der Datenbank abrufen
        if (Database.getSettingValue(Database.SETTINGS_SHOW_IN_GAL) == 1){
        	switchShowInGal.setChecked(true);
        }
        else {
        	switchShowInGal.setChecked(false);
        	
        }
        
        final File path = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Hike App");

        //Wenn Wert geaendert wird, diesen in die Datenbank schreiben
        switchShowInGal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	
                	Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL, 1);
                	
//                	Writes a new file to the MyCameraApp directory to hide pictures taken with this app from the standard gallery app
//                	Images should be hidden
                	String nomediaFile = path.toString()+"/.nomedia";
                	File file=new File(nomediaFile);
                	boolean deleted = file.delete();

                	
                } else {
         
                	Database.changeSettingValue(Database.SETTINGS_SHOW_IN_GAL, 0);

//                	Deletes the file wich indicates the availability in the gallery app.
//                	Images should be shown
//                	File path = new File(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_PICTURES), "MyCameraApp");
                	try {
						FileWriter fileWriter = new FileWriter(path + "/.nomedia");
					} catch (IOException e) {
						e.printStackTrace();
					}


               
                }
            }
        });

        return rootView;
    }
	

}
//	
	
	
	

