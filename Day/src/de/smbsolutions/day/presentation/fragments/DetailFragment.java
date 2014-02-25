package de.smbsolutions.day.presentation.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;
import de.smbsolutions.day.functions.tasks.BitmapWorkerTask;

public class DetailFragment extends android.support.v4.app.Fragment {

	private SupportMapFragment fragment;
	private View view;
	private GoogleMap map;
	private Configuration config;
	private Bundle data;
	private Route route;
	private int index;
	private MainCallback mCallback;
	private ImageButton imageButton;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private static String timeStamp;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mapPrepared = false;
	private static Activity context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		config = getResources().getConfiguration();

		view = inflater.inflate(R.layout.cr_fragment, container, false);
		data = getArguments();
		route = (Route) data.getParcelable("route");
		index = data.getInt("index");
		addPhotos2Gallery();
		return view;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (MainCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnButtonClick Interface");
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.cr_map);

		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().add(R.id.cr_map, fragment).commit();
		}
	}

	public void onResume() {
		super.onResume();
		
		
		context = getActivity();

		if (map == null) {
			map = fragment.getMap();
		}

		// checking device orientation for layout
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentLandscape();

		} else {

			if (map == null) {
				map = fragment.getMap();
			}

			initializeFragmentPortrait();

		}

	}

	public void initializeFragmentLandscape() {

		// map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		// LinearLayout linleaLayout = (LinearLayout) view
		// .findViewById(R.id.LinearLayoutcR);
		// linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
		// new OnGlobalLayoutListener() {
		//
		// @Override
		// public void onGlobalLayout() {
		//
		// map = routelist.getListRoutes().get(index).prepareMap(map,
		// getActivity(), false);
		//
		// }
		// });

	}

	public void initializeFragmentPortrait() {

		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		map.setPadding(0, 100, 0, 100);
		map.getUiSettings().setZoomControlsEnabled(false);
		LinearLayout linleaLayout = (LinearLayout) view
				.findViewById(R.id.LinearLayoutcR);
		imageButton = (ImageButton) view.findViewById(R.id.imagebutton1);
		
		
		//If a route doesn't have a picture point, the Picture Scrollbar is disabled
		if (route.hasPicturePoint() == false) {
			    
				LinearLayout linlayout = (LinearLayout) view.findViewById(R.id.LinearLayoutcR);
				linlayout.removeView(view.findViewById(R.id.RelativeHorizontalScrollViewLayout));
			}
		

		
        
		//Closed routes cannot generate a new picture
		if (route.getActive().equals("")) {
         imageButton.setVisibility(View.INVISIBLE);
		}

		linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (route != null) {
							if (mapPrepared == false) {
								map = route
										.prepareMap(map, getActivity(), false);
								mapPrepared = true;
								addButtonClickListener(imageButton);
						

							}

						}

					}

				});

	}

	public void addPhotos2Gallery() {
		LinearLayout myGallery = (LinearLayout) view
				.findViewById(R.id.LinearLayoutImage);

		myGallery.removeAllViews(); // bessere lösung, immer nur das neue bild
									// einfügen?
		BitmapWorkerTask task = new BitmapWorkerTask(myGallery, getActivity());
		task.execute(route); 
		
	

	}

	public void addButtonClickListener(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, false);

				// create intent with ACTION_IMAGE_CAPTURE action
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				// save the image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				// start camera activity
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

			}
		});

	}

	private static Uri getOutputMediaFileUri(int type, boolean small) {
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			GPSTracker gps = GPSTracker.getInstance(getActivity());
			if (gps.canGetLocation()) {
				
				
			File small =	getOutputMediaFile(MEDIA_TYPE_IMAGE, true);
			Uri uri = Uri.fromFile(small);
			
			
			Bitmap bitmap  = BitmapManager.decodeSampledBitmapFromUri(fileUri.getPath(), 220, 220);
				
		        
		   
		        FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(small);
					 bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					 fOut.flush();
				     fOut.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		       
		       
		       
//		        
//				String newUrl = 	MediaStore.Images.Media.insertImage(context.getContentResolver(),resizedBitmap ,small.getName(),small.getName());
//				Toast.makeText(getActivity(), newUrl, Toast.LENGTH_SHORT);
//		     
		        
		        
		       
		        

				// Getting the current timestamp
				Timestamp tsTemp = new Timestamp(System.currentTimeMillis());

				route.addRoutePointDB(new RoutePoint(route.getId(), tsTemp,
						fileUri.getPath(), small.getPath(), gps.getLatitude(), gps
								.getLongitude()));
				// route erneut anzeigen
				mCallback.onCamStart(route);

			} else {
				// route erneut anzeigen
				mCallback.onCamStart(route);
				
				Toast.makeText(getActivity(), "Keine Ortung möglich, bitte erneut versuchen", Toast.LENGTH_LONG);
				
			}

		}
	}
	
	
	
}