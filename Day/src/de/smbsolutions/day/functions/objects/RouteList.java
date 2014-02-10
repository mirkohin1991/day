package de.smbsolutions.day.functions.objects;

import java.util.List;

import de.smbsolutions.day.functions.database.Database;

public class RouteList {

	private int count;
	private List<Route> listRoutes;

	public RouteList(int count) {
		this.count = count;
		listRoutes = Database.getSpecificRoute(count);

	}
	
	public RouteList() {
		listRoutes = Database.getSpecificRoute(count);
		
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

}
