package de.smbsolutions.day.presentation.fragments;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import de.smbsolutions.day.R;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;
import de.smbsolutions.day.functions.tasks.BitmapManager;

public class PictureFragment extends android.support.v4.app.Fragment {

	private Bundle data;
	private View view;

	private MainCallback mCallback;

	private Route route;
	private RoutePoint routePoint;
	private ImageView pictureView;
	private ImageButton btn_sharePicture;
	private ImageButton btn_deletePicture;
	private File pictureFile;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		data = getArguments();
		route = (Route) data.getParcelable("route");
		routePoint = (RoutePoint) data.getParcelable("point");

		view = inflater.inflate(R.layout.fragment_picture, container, false);
		pictureView = (ImageView) view.findViewById(R.id.imageViewFullPicture);
		btn_sharePicture = (ImageButton) view.findViewById(R.id.pictureShareButton);
		btn_deletePicture = (ImageButton) view.findViewById(R.id.pitctureDeleteButton);
		
		addButtonClickListener();

		return view;

	}

	private void addButtonClickListener() {
		btn_sharePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Opening dialog to share the picture
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_TEXT, "Aufgenommen mit 'Hike', der interaktiven Wander-App!");
				sharingIntent.putExtra(Intent.EXTRA_STREAM,  Uri.fromFile(pictureFile));
				startActivity(Intent.createChooser(sharingIntent, "Bild teilen mit"));
			}
		});
		
		btn_deletePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				mCallback.onDeletePictureClick(route, routePoint);
			}
		});
	
		
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
	public void onResume() {
		super.onResume();
		 
		initializeFragmentPortrait();

	}
	


	public void initializeFragmentPortrait() {
		

		pictureFile = new File(routePoint.getPicturePreview());
		Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
				pictureFile.getPath(), 220, 220);// richtige größe?

		if (bm != null) {
			pictureView.setImageBitmap(bm);
		}

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	 
	}

	@Override
	public void onDestroy() {
	
		super.onDestroy();
		
		
		data = null;
		 view = null;
		mCallback = null;
		 route = null;
		routePoint = null;
		 pictureView = null;
		btn_sharePicture = null;
		btn_deletePicture = null;
		 pictureFile = null;
		
		
	}
	
	


	

}
