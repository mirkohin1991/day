package de.smbsolutions.day.functions.tasks;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.google.android.gms.internal.bi;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import de.smbsolutions.day.functions.interfaces.FragmentCallback;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class BmTask extends
		AsyncTask<Route, Void, LinkedHashMap<Bitmap, Timestamp>> {

	private FragmentCallback callback;
	private LinkedHashMap<Bitmap, Timestamp> bitmaps;

	public BmTask(LinkedHashMap<Bitmap, Timestamp> bitmaps,
			FragmentCallback callback) {
		this.bitmaps = bitmaps;
		this.callback = callback;

	}

	@Override
	protected LinkedHashMap<Bitmap, Timestamp> doInBackground(Route... params) {

		Route route = params[0];
		for (RoutePoint point : route.getRoutePoints()) {
			if (point.getPicture() != null) {

				File bitmapFile = new File(point.getPicturePreview());

				Bitmap bm = BitmapManager.decodeSampledBitmapFromUri(
						bitmapFile.getPath(), 220, 220);// richtige größe?

				bitmaps.put(bm, point.getTimestamp());
				bitmapFile = null;

				bm = null;
			}

		}

		// TODO Auto-generated method stub
		return bitmaps;
	}

	@Override
	protected void onPostExecute(LinkedHashMap<Bitmap, Timestamp> result) {
		// TODO Auto-generated method stub

		super.onPostExecute(result);
		callback.onTaskfinished(result);
	}

}
