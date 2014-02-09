package de.smbsolutions.day.functions.database;

import java.util.ArrayList;
import java.util.List;

public class Route {

	private List<RoutePoint> routePoints;
	private String routeName;
	private String date;
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


	private String active;
	private int id;

	public Route() {

	}

	public Route(int id) {
		this.id = id;
		routePoints = Database.getSpecificRoute(new String[] { String
				.valueOf(id) });
		// routeName = Database.getOpenRouteInfo();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<RoutePoint> getRoutePoints() {
		return routePoints;
	}

	public void setRoutePoints(List<RoutePoint> routePoints) {
		this.routePoints = routePoints;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
	
	
	public void addRoutePoint(RoutePoint point){
		
		routePoints.add(point);
	}
}
