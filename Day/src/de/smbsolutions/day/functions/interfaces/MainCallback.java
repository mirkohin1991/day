package de.smbsolutions.day.functions.interfaces;

import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RouteList;

public interface MainCallback {
	public void onItemSelected(int position);

	public void onOpenDialogNewRoute(RouteList routeList);

	public void onShowRoute(Route route);

	public void onNewRouteStarted(Route route);

	public void onLongItemSelected(RouteList routeList, int index);
	
	public void onStopPopup(RouteList routeList);

	public void onDeleteRoute();
	
	public void onStopRoute();
	
	public void onCamStart(Route route);
}
