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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.initialization.Device;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.location.GPSTracker;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;
import de.smbsolutions.day.functions.tasks.BitmapWorkerTask;
import de.smbsolutions.day.functions.tasks.MarkerWorkerTask;

public class DetailFragment extends android.support.v4.app.Fragment  {

	private SupportMapFragment fragment;
	private View view;
	private GoogleMap map;
	private Configuration config;
	private Bundle data;
	private Route route;
	private MainCallback mCallback;
	private ImageButton imageButton;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mapPrepared = false;
	private static Activity context;
	private LinearLayout myGallery;
	private View removedView;

    private ViewGroup container;
    private  LayoutInflater inflater;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			
			Bundle savedInstanceState) {
		
		//Saved in order to access it in the onActivityResult method later on
		this.container = container;
		this.inflater = inflater;
		
		config = getResources().getConfiguration();

		data = getArguments();
		route = (Route) data.getParcelable("route");
		
		
		
		// If a route doesn't have a picture point, the Picture Scrollbar is
		// disabled
		if (route.hasPicturePoint() == false) {
			
			view = inflater.inflate(R.layout.fragment_detail_nopicture, container, false);
		}else {
			view = inflater.inflate(R.layout.fragment_detail, container, false);
			
			myGallery = (LinearLayout) view
					.findViewById(R.id.LinearLayoutImage);
			
			
			
			addPhotos2Gallery(myGallery);

//			LinearLayout linlayout = (LinearLayout) view
//					.findViewById(R.id.LinearLayoutcR);
//			
//			//Saving the removed view, to add it later on again, if a picture is taken
//			removedView = view
//					.findViewById(R.id.RelativeHorizontalScrollViewLayout);
//			//linlayout.removeView(removedView);
//			removedView.setVisibility(View.GONE);
		}
		
		
		
		
	

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

		map.setMapType(Device.getAPP_SETTINGS().getMAP_TYPE());
//		map.setPadding(0, 0, 99999, 0); // weg isses :D
		map.getUiSettings().setZoomControlsEnabled(false);
		LinearLayout linleaLayout = (LinearLayout) view
				.findViewById(R.id.LinearLayoutcR);
		imageButton = (ImageButton) view.findViewById(R.id.imagebutton1);



		// Closed routes cannot generate a new picture
		if (route.getActive().equals("")) {
			imageButton.setVisibility(View.INVISIBLE);
		}

		linleaLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (route != null) {
							if (mapPrepared == false) {
								map = route.prepareMapDetails(map, getActivity());
								mapPrepared = true;
								addButtonClickListener(imageButton);

							}

						}

					}

				});

	}

	public void addPhotos2Gallery( LinearLayout myGallery) {
		

		myGallery.removeAllViews(); // bessere lösung, immer nur das neue bild
									// einfügen?
		BitmapWorkerTask task = new BitmapWorkerTask(myGallery, getActivity());
		task.execute(route);

	}

	public void addButtonClickListener(ImageButton imageButton) {
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				fileUri = BitmapManager.getOutputMediaFileUri(MEDIA_TYPE_IMAGE,
						false);

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			GPSTracker gps = GPSTracker.getInstance(getActivity());
			if (gps.canGetLocation()) {

				// Getting the current timestamp
				Timestamp tsTemp = new Timestamp(System.currentTimeMillis());
				if (resultCode == -1) {

					File small_picture = BitmapManager
							.savePreviewBitmapToStorage(fileUri);

					if (small_picture != null) {

						Bitmap bitmap = BitmapManager
								.decodeSampledBitmapFromUri(fileUri.getPath(),
										250, 250);

						FileOutputStream fOut = null;
						try {
							fOut = new FileOutputStream(small_picture);
							bitmap.compress(Bitmap.CompressFormat.PNG, 100,
									fOut);
							fOut.flush();
							fOut.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						route.addRoutePointDB(new RoutePoint(route.getId(),
								tsTemp, fileUri.getPath(), small_picture
										.getPath(), gps.getLatitude(), gps
										.getLongitude()));

						// If no small picture could be created, NULL is stored
					} else {
						route.addRoutePointDB(new RoutePoint(route.getId(),
								tsTemp, fileUri.getPath(), null, gps
										.getLatitude(), gps.getLongitude()));

					}
		

				}
            
				//No Signal is available to detect the current location
			} else {
				

				Toast.makeText(getActivity(),
						"Keine Ortung möglich, bitte erneut versuchen",
						Toast.LENGTH_LONG);
			}
			
			
			
			 // now at least one picture was taken
			if(route.hasPicturePoint() == true) {

				//If it is the first picture ever taken the layout has to be changed to the one with picture scrollbar
				if (myGallery == null) {
					
					
					//Vielleicht gibt es noch eine bessere Lösung.
					mCallback.onShowRoute(route);
					
//					//Das hier wird nämlich leider nicht refresht
//					view = inflater.inflate(R.layout.fragment_detail, container, false);
//
//					myGallery = (LinearLayout) view
//							.findViewById(R.id.LinearLayoutImage);
				} else {
					//refresh the image view
					addPhotos2Gallery(myGallery);
					route.prepareMapDetails(map, getActivity());
				}
 				
			
				
				
			 } else {
				 route.prepareMapDetails(map, getActivity());
			 }
				
			

		}
	}




}
