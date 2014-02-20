package de.smbsolutions.day.functions.objects;

import java.io.Serializable;
import java.util.List;

import de.smbsolutions.day.functions.database.Database;

public class RouteList implements Serializable {

	/**
	 * 
	 */
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

		if (getlastRoute().getActive().equals("X")) {

			return true;

		} else {

			return false;
		}

	}

	public void addRoute(Route route) {
		listRoutes.add(route);
	}
	
	
	public void deleteRouteDB(int index) {
		
		
	 if (Database.deleteRoute(listRoutes.get(index)) == true ){
	 
	    listRoutes.remove(index);
	    
	 }
	 
	 
	 //VIEW MUSS NOCH REFRESHT WERDEN!
		
		
	}

}
