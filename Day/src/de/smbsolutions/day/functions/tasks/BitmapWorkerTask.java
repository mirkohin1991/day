package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.smbsolutions.day.functions.interfaces.MainCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class BitmapWorkerTask extends AsyncTask<Route, Void, List<ImageView>> {
	private WeakReference<LinearLayout> layoutReference;
	private Route route;
	private File bitmapFile;
	private List<ImageView> bitmapList;
	private Context context;
	private LinearLayout imageContainer;
	private boolean imageAvailable = false;
	private MainCallback mCallback;
	LinearLayout myGallery;

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	public BitmapWorkerTask(LinearLayout layout, Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		layoutReference = new WeakReference<LinearLayout>(layout);
		this.context = context;
		bitmapList = new ArrayList<ImageView>();
		imageContainer = new LinearLayout(context);
		imageContainer.setGravity(Gravity.CENTER);

		try {
			mCallback = (MainCallback) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnButtonClick Interface");
		}

	}

	// Decode image in background.
	@Override
	protected List<ImageView> doInBackground(Route... params) {

		route = params[0];

		int foreachindex = 0;
		Bitmap bm = null;
		for (RoutePoint point : route.getRoutePoints()) {
			if (point.getPicture() != null) {
				if (bm != null) {
					// Memoryusage????
					bm.recycle();
				
				}

				bitmapFile = new File(point.getPicturePreview());

				bm = BitmapManager.decodeSampledBitmapFromUri(
						bitmapFile.getPath(), 220, 220);// richtige größe?

				if (bm != null) {
					// Bilder sollten automatisch ins Layout passen
					ImageView imageView = new ImageView(context);

					imageView.setAdjustViewBounds(true);

					if (foreachindex == 0) {

						imageView.setPadding(0, 12, 0, 12);

					} else {

						// photo in portrait
						imageView.setPadding(5, 12, 0, 12);

					}

					imageView.setImageBitmap(bm);
					bm = null;
					// Saving the timestamp to identify the picture later on
					imageView.setTag(point.getTimestamp());

					bitmapList.add(imageView);

					imageAvailable = true;
					foreachindex++;
				}

			}

		}
		return bitmapList;

	}

	@Override
	protected void onPostExecute(List<ImageView> images) {
		if (layoutReference != null && bitmapList != null) {

			myGallery = layoutReference.get();
			if (myGallery != null) {
				for (ImageView image : images) {

					imageContainer.addView(image);

					// Handling events for the picture
					addPictureClickListener(image);

				}
				myGallery.addView(imageContainer);

			}

		}
		bitmapList.clear();
	
	}

	public boolean isImageAvailable() {
		return imageAvailable;
	}

	public void addPictureClickListener(ImageView image) {

		image.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				int i;

				i = 1;

			}
		});

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				for (RoutePoint point : route.getRoutePoints()) {

					// Timestamp of the clicked picture
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {

//						route.setZoomSpecificMarker(point);

					}

				}

			}
		});

		image.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				// Looping over the routelist to get the right picture
				for (RoutePoint point : route.getRoutePoints()) {

					// Timestamp of the clicked picture
					Timestamp tsClicked = (Timestamp) v.getTag();

					if (tsClicked == point.getTimestamp()) {

						// Call the Callback interface to execute the required
						// action
						mCallback.onDeletePictureClick(route, point);

						return true;

					}

				}
				return false;
			}
		});

	}

}
