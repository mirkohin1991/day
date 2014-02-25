package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class BitmapManager {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static String timeStamp;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public static int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}
	
	

	
	public static File savePreviewBitmapToStorage (Uri big_image_file) {
		
		File small_picutre_file =	getOutputMediaFile(MEDIA_TYPE_IMAGE, true);
//		Uri uri = Uri.fromFile(small_picutre);
		
		
		Bitmap bitmap  = BitmapManager.decodeSampledBitmapFromUri(big_image_file.getPath(), 220, 220);
	   
	        FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(small_picutre_file);
				 bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
				 fOut.flush();
			     fOut.close();
			     return  small_picutre_file;
			} catch (FileNotFoundException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		
	}
	
	public static Uri getOutputMediaFileUri(int type, boolean small) {
		return Uri.fromFile(getOutputMediaFile(type, small));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type, boolean small) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name

		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		


		return mediaFile;
	}
}
