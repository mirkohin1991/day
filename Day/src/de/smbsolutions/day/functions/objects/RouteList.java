package de.smbsolutions.day.functions.objects;

import java.io.Serializable;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import de.smbsolutions.day.functions.database.Database;

public class RouteList implements Parcelable {
	//RouteList object, that contains all Route objects
	private static final long serialVersionUID = 1L;
	private int count;
	private List<Route> listRoutes;

	public RouteList(int count) {
		this.count = count;
		listRoutes = Database.getSpecificRoute(count);

	}

	public RouteList() {
		listRoutes = Database.getSpecificRoute(0);

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Route> getListRoutes() {
		return listRoutes;
	}

	public void setListRoutes(List<Route> listRoutes) {
		this.listRoutes = listRoutes;
	}

	public Route getlastRoute() {
		Route route = listRoutes.get(listRoutes.size() - 1);
		return route;
	}

	public boolean isOpenRoute() {

		// It's enough to check the last route because the routes are sorted
		// chronologically
		if (getlastRoute().isActive()) {

			return true;

		} else {

			return false;
		}

	}

	public void addRoute(Route route) {
		listRoutes.add(route);
	}

	public void deleteRouteDB(int index) {

		if (Database.deleteRoute(listRoutes.get(index)) == true) {

			listRoutes.remove(index);

		}

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
