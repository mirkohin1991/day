package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class BitmapWorkerTask extends AsyncTask<Route, Void, List<ImageView>> {
	private final WeakReference<LinearLayout> layoutReference;
	private Route route;
	private File targetDirector;
	private List<ImageView> bml;
	private Context context;
	private LinearLayout layout;
	private boolean imageAvailable = false;
	LinearLayout myGallery;

	public BitmapWorkerTask(LinearLayout layout, Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		layoutReference = new WeakReference<LinearLayout>(layout);
		this.context = context;
		bml = new ArrayList<ImageView>();

	}

	// Decode image in background.
	@Override
	protected List<ImageView> doInBackground(Route... params) {

		route = params[0];
		layout = new LinearLayout(context);
		// layout.setLayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT);
		layout.setGravity(Gravity.CENTER);
		int foreachindex = 0;
		for (RoutePoint point : route.getRoutePoints()) {
			if (point.getPicture() != null) {

				targetDirector = new File(point.getPicture());
				Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
						targetDirector.getPath(),220, 220);// richtige größe?

				if (bm != null) {

					ImageView imageView = new ImageView(context);
					imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					imageView.setAdjustViewBounds(true);

					imageView.setLayoutParams(new LayoutParams(android.support.v4.view.ViewPager.LayoutParams.MATCH_PARENT,
							android.support.v4.view.ViewPager.LayoutParams.MATCH_PARENT));
				
					if (foreachindex == 0) {
						imageView.setPadding(0, 0, 0, 0);
					} else
						imageView.setPadding(1, 0, 0, 0);

				
					imageView.setImageBitmap(bm);

					bml.add(imageView);
					imageAvailable = true;
					foreachindex++;
				}

			}
		}
		return bml;

	}

	@Override
	protected void onPostExecute(List<ImageView> images) {
		if (layoutReference != null && bml != null) {

			myGallery = layoutReference.get();
			if (myGallery != null) {
				for (ImageView image : images) {
					layout.addView(image);
				}
				myGallery.addView(layout);

			}

		}
	}

	public boolean isImageAvailable() {
		return imageAvailable;
	}

}