package de.smbsolutions.day.functions.interfaces;

import android.support.v4.app.Fragment;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;
import de.smbsolutions.day.functions.objects.RoutePoint;

public interface MainCallback {
	public void onItemSelected(int position);

	public void onOpenDialogNewRoute(RouteList routeList);

	public void onShowRoute(Route route);

	public void onNewRouteStarted(Route route);

	public void onLongItemSelected(RouteList routeList, int index);

	public void onStopPopup(RouteList routeList);

	public void onDeleteRoute();
	
	public void onDeletePicture(Route route);
	
	public void onLongPictureClick(Route route, RoutePoint point);

	public void onStopRoute();

	public void onCamStart(Route route);

	public void onSliderClick(Fragment fragment);
}
