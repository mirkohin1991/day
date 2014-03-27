package de.smbsolutions.hike.functions.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.smbsolutions.hike.functions.initialization.Device;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * Der Bitmapmanager stellt verschiedene Methoden zur Verfügung, mit denen man
 * Bitmaps erstellen und bearbeiten kann.
 * 
 */
public class BitmapManager {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static String timeStamp;

	/**
	 * Läd ein Bild von der Festplatte und gibt es als Bitmap zurück.
	 * 
	 */
	public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		/*
		 * Dekodiert die Bitmap mit "inJustDecodeBounds" um das Bild mit den
		 * richtigen Dimensionen zu laden. --> Spart Memory
		 */
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		/*
		 * Berechnet "inSampleSize" um das Bild in der gewünschten Größe(Pixel)
		 * zu laden. --> Spart Memory.
		 */
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		/*
		 * Dekodiert die Bitmap erneut und gibts dieses zurück.
		 */
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	/**
	 * Berechnet die "inSampleSize" zur gewünschten Größe der Bitmap
	 */
	public static int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw Höhe und Breite
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

	/**
	 * Wird ausgeführt wenn mit der Kamera ein Bild geschossen wurde und ein
	 * Vorschaubild gespeichert wird.
	 * 
	 */
	public static File savePreviewBitmapToStorage(Uri big_image_file) {

		File small_picutre_file = getOutputMediaFile(MEDIA_TYPE_IMAGE, true);

		Bitmap bitmap = BitmapManager.decodeSampledBitmapFromUri(
				big_image_file.getPath(), Device.getPictureScrollbarDensity(),
				Device.getPictureScrollbarDensity());

		FileOutputStream fOut = null;
		try {
			// Speichert komprimiertes Vorschaubild
			fOut = new FileOutputStream(small_picutre_file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
			fOut.flush();
			fOut.close();
			bitmap.recycle();
			return small_picutre_file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Gibt Uri einer Datei zurück.
	 */
	public static Uri getOutputMediaFileUri(int type, boolean small) {
		return Uri.fromFile(getOutputMediaFile(type, small));
	}

	/**
	 * Gibt eine Datei zurück und überprüft ob eine SD-Karte vorhanden ist.
	 * Außerdem wird überprüft ob Speicherorte für die Bitmapdatei bereits
	 * ertellt wurden.
	 */
	private static File getOutputMediaFile(int type, boolean small) {

		File picturepath;

		// mist das Bild ein PreviewBild?
		if (small) {
			// Pfad des Bildes
			picturepath = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"HikePreview");

			// Erstellt den Ordner falls er noch nicht vorhanden ist.
			if (!picturepath.exists()) {
				picturepath.mkdirs();
				try {
					String path = picturepath.getPath();
					FileWriter fileWriter = new FileWriter(path + "/.nomedia");
					fileWriter.close();
					fileWriter = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (!picturepath.exists()) {
					Log.d("HikePreview", "failed to create directory");
					return null;
				}
			}

		} else {
			// Pfad des Bildes
			picturepath = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"Hike");
			// Erstellt den Ordner falls er noch nicht vorhanden ist.
			if (!picturepath.exists()) {
				if (!picturepath.mkdirs()) {
					Log.d("Hike", "failed to create directory");
					return null;
				}
			}
		}
		// Erstellt die Media-Datei und hängt ein Timestamp ans Ende des
		// Dateinamens.
		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(picturepath.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(picturepath.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

}
