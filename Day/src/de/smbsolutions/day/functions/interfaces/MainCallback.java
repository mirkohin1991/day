package de.smbsolutions.day.functions.interfaces;

import android.support.v4.app.Fragment;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;

public interface MainCallback {
	// Item in MainFragment is selected
	public void onItemSelected(int position);

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
	public void onLongPictureClick(Route route, RoutePoint point);

	// Opens PictureFragment
	public void onPictureClick(Route route, RoutePoint point);

	public void onDeletePicture(Route route);

	public void onShowFullPicture(Route route);

	public void onStopRoute();

	public void onCamStart(Route route);

	public void onSliderClick(Fragment fragment);

	public void onRefreshMap();

}
