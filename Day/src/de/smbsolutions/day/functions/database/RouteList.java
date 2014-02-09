package de.smbsolutions.day.functions.database;

import java.util.List;

public class RouteList {

	private int count;
	private int sequence;
	private List<Route> listRoutes;

	public RouteList(int count) {
		this.count = count;

		listRoutes = Database.getSpecificRoute(count);

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<Route> getListRoutes() {
		return listRoutes;
	}

	public void setListRoutes(List<Route> listRoutes) {
		this.listRoutes = listRoutes;
	}

}
