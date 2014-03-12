package de.smbsolutions.day.functions.interfaces;

import java.io.File;

import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;

public interface MainCallback {
	// Dialog for opening a new route
	public void onOpenDialogNewRoute(RouteList routeList);

	// Open DetailFragment for selected Route
	public void onShowRoute(Route route);

	// Open DetailFragment for new Route
	public void onNewRouteStarted(Route route);

	// Opens Dialog for deleting routes
	public void onLongItemSelected(RouteList routeList, int index);

	// Opens Dialog for stopping routes
	public void onStopPopup(RouteList routeList);

	// Deletes Route
	public void onDeleteRoute();

	// Methods regarding the PictureScrollbar
	// Opens Dialog for deleting picture
	public void onDeletePictureClick(Route route, RoutePoint point);

	// Opens PictureFragment
	public void onPictureClick(Route route, RoutePoint point);

	public void onDeletePicture(Route route);

	public void onShowFullPicture(Route route);

	public void onStopRoute();

	public void onCamStart(Route route);

	public void onSliderClick(Fragment fragment);

	public void onRefreshMap();

	// SERVICE START / STOP

	public void onStartTrackingService(RouteList routeList, Route route);

	// SERVICE HAS TO STORE PICTURE
	public void onPictureTaken(Route route, Uri fileUri, File small_picture);

	// SERVICE HAS TO BE STOPPED AGAIN WHEN NO ROUTE WAS CREATED
	public void onDialogCreateCanceled();

}
