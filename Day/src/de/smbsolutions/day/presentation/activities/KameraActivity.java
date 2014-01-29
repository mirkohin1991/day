package de.smbsolutions.day.presentation.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.smbsolutions.day.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;

public class KameraActivity extends Activity {
	
	
	
	
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private static String timeStamp;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.);
	        
	       
	        
	      //  db_data = new DatabaseInteraction(this);

	     // create Intent to take a picture and return control to the calling application
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

	        // start the image capture Intent
	        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	      
	    }
	    /** Create a file Uri for saving an image or video */
	    private static Uri getOutputMediaFileUri(int type){
	          return Uri.fromFile(getOutputMediaFile(type));
	    }

	    /** Create a File for saving an image or video */
	    private static File getOutputMediaFile(int type){
	        // To be safe, you should check that the SDCard is mounted
	        // using Environment.getExternalStorageState() before doing this.

	        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
	        // This location works best if you want the created images to be shared
	        // between applications and persist after your app has been uninstalled.

	        // Create the storage directory if it does not exist
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                Log.d("MyCameraApp", "failed to create directory");
	                return null;
	            }
	        }

	        // Create a media file name
	        
	        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE){
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	            "IMG_"+ timeStamp + ".jpg");
	        } else if(type == MEDIA_TYPE_VIDEO) {
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	            "VID_"+ timeStamp + ".mp4");
	        } else {
	            return null;
	        }

	        return mediaFile;
	    }
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				GPSTracker gps = new GPSTracker(KameraActivity.this);
			      if(gps.canGetLocation()){
			            Database.addNewRoutePoint(fileUri.toString(), gps.getLatitude() + 0.2, gps.getLongitude(), "Wanderung");
			    		ImageSpot neuesBild = new ImageSpot(gps.getLongitude(), gps.getLatitude(), fileUri.toString(), timeStamp);
						Intent mapIntent = new Intent(this, MapActivity.class);
						mapIntent.putExtra("ImgLatitude", neuesBild.getLatitude());
						mapIntent.putExtra("ImgLongitude", neuesBild.getLongitude());
						mapIntent.putExtra("ImgUri", neuesBild.getImgUri());
						mapIntent.putExtra("TimeStamp", neuesBild.getTimestamp());
						startActivity(mapIntent);
						
					
			          
			      }
			
			}
		}
	    
	    
	
	
}
