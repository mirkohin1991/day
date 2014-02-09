package de.smbsolutions.day.functions.database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database implements DatabaseInterface {

	RoutePoint TEST;

	private static DatabaseManager mHelper;
	private static SQLiteDatabase mDatabase;
	private static int currentRouteID = 1;
	private static String currentRouteName;

	private static Database db_data = null;

	// In welchem Kontext???

	private Database(Context context) {

		mHelper = new DatabaseManager(context);
	}

	public static Database getInstance(Context context) {
		if (db_data == null)
			db_data = new Database(context);
		return db_data;
	}

	public static void closeDBsession() {

		mDatabase.close();
	}

	public static void addNewRoutePoint(double latitude, double longitude,
			Timestamp timestamp) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues route_values = new ContentValues();

		// Zum Testen erstmal alle dem gleichen Record
		route_values.put("_id", currentRouteID);
		route_values.put("timestamp", timestamp.toString()); // inserting an int
		route_values.put("latitude", latitude); // inserting a string
		route_values.put("longitude", longitude);

		// CurrentRoute greater than the latest one in the DB
		// --> Completely new route!
		if (currentRouteID > getIDlastRoute()) {

			mDatabase.insert("route_points", null, route_values);

			// For a completely new route, also the general information, like
			// the name has to be stored
			ContentValues route_info = new ContentValues();
			route_info.put("_id", currentRouteID);
			route_info.put("name", currentRouteName);
			route_info.put("date", timestamp.getDate());
			route_info.put("active", "X");
			mDatabase.insert("route_info", null, route_info);

		} else {

			mDatabase.insert("route_points", null, route_values);

		}

	}

	public static void addNewRoutePoint(String picture, double latitude,
			double longitude, Timestamp timestamp) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues route_values = new ContentValues();

		// Zum Testen erstmal alle dem gleichen Record
		route_values.put("_id", currentRouteID);
		route_values.put("timestamp", timestamp.toString()); // inserting an int
		route_values.put("picture", picture); // inserting an int
		route_values.put("latitude", latitude); // inserting a string
		route_values.put("longitude", longitude);

		// CurrentRoute greater than the latest one in the DB
		// --> Completely new route!
		if (currentRouteID > getIDlastRoute()) {

			mDatabase.insert("route_points", null, route_values);

			// For a completely new route, also the general information, like
			// the name has to be stored
			ContentValues route_info = new ContentValues();
			route_info.put("_id", currentRouteID);
			route_info.put("name", currentRouteName);
			route_info.put("date", timestamp.getDate());
			route_info.put("active", "X");
			mDatabase.insert("route_info", null, route_info);

		} else {

			mDatabase.insert("route_points", null, route_values);

		}

	}

	public static void closeRoute(String id) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues route_value = new ContentValues();

		route_value.put("active", "");

		mDatabase.update("route_info", route_value, "_id=?",
				new String[] { id }); // Which columns

	}

	private static int getIDlastRoute() {

		Cursor db_cursor;
		String[] db_columns = { "_ID" };
		int lastRecord;

		mDatabase = mHelper.getWritableDatabase();

		// Check if an entry exists. Ordered descending to get the latest route
		db_cursor = mDatabase.query("route_points", // table
				db_columns, // which column
				null, // select options
				null, // Using ? in the select options can be replaced here as
						// an array
				null, // Group by
				null, // Having
				"_id DESC");// order by

		if (db_cursor.getCount() != 0) {

			db_cursor.moveToFirst();

			lastRecord = db_cursor.getInt(db_cursor.getColumnIndex("_id"));

			// It is the first time a record is saved
		} else {

			lastRecord = -1;

		}

		db_cursor.close();
		return lastRecord;
	}

	public static List<ArrayList<String>> getAllroutesGrouped() {
		List<ArrayList<String>> super2dArray = new ArrayList<ArrayList<String>>();

		List<RoutePoint> allRoutes = new ArrayList<RoutePoint>();
		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase
				.query("route_points INNER JOIN route_info ON route_points._id = route_info._id", // table
						new String[] { "route_points._id, route_info.name" }, // which
																				// column
						null, // select options
						null, // Using ? in the select options can be replaced
								// here as an array
						"route_points._id, route_info.name", // Group by ID -->
																// Only the
																// whole routes
						null, // Having
						null);// order by

		while (db_cursor.moveToNext()) {

			// Getting each field
			String cursor_id = db_cursor.getString(db_cursor
					.getColumnIndex("_id"));
			String name = db_cursor.getString(db_cursor.getColumnIndex("name"));

			ArrayList<String> test = new ArrayList<String>();
			test.add(cursor_id);
			test.add(name);

			super2dArray.add(test);

		}

		db_cursor.close();

		return super2dArray;

	}

	// Get last routes
	public static List<Route> getSpecificRoute(int count) {

		List<RoutePoint> allRoutes = new ArrayList<RoutePoint>();
		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		int id = getIDlastRoute();
		String[] ids = new String[count];

		for (int i = id; i > (id - count); i--) {

			ids[count - 1] = String.valueOf(id);

		}

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				"_id = ?", // select options
				ids, // Using ? in the select options can be replaced here as an
						// array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		Route route = new Route();
		List<Route> route_list = new ArrayList();
		int previous_id = -1;
		while (db_cursor.moveToNext()) {

			// Getting each field
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));

			String cursor_picture = db_cursor.getString(db_cursor
					.getColumnIndex("picture"));
			double cursor_longitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("longitude"));
			double cursor_latitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("latitude"));
			Timestamp cursor_time = Timestamp.valueOf(db_cursor
					.getString(db_cursor.getColumnIndex("timestamp")));

			if (cursor_id != previous_id && previous_id != -1) {
				route_list.add(route);
				route = new Route();
				route.setId(cursor_id);
				getRouteInfo(route);
			}

			previous_id = cursor_id;

			RoutePoint route_point = new RoutePoint(cursor_id, cursor_time, // Timestamp
																			// class
																			// helps
																			// us
																			// to
																			// get
																			// the
																			// value
																			// as
																			// timestamp
					cursor_picture, cursor_latitude, cursor_longitude);

			route.addRoutePoint(route_point);

		}

		db_cursor.close();
		return route_list;

	}

	// "1 - n routpoints can be selected"
	// Select needs the values as string and not int
	public static List<RoutePoint> getSpecificRoute(String[] ids) {

		List<RoutePoint> allRoutes = new ArrayList<RoutePoint>();
		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				"_id = ?", // select options
				ids, // Using ? in the select options can be replaced here as an
						// array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		while (db_cursor.moveToNext()) {

			// Getting each field
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
			String cursor_picture = db_cursor.getString(db_cursor
					.getColumnIndex("picture"));
			double cursor_longitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("longitude"));
			double cursor_latitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("latitude"));
			Timestamp cursor_time = Timestamp.valueOf(db_cursor
					.getString(db_cursor.getColumnIndex("timestamp")));

			RoutePoint route_point = new RoutePoint(cursor_id, cursor_time, // Timestamp
																			// class
																			// helps
																			// us
																			// to
																			// get
																			// the
																			// value
																			// as
																			// timestamp
					cursor_picture, cursor_latitude, cursor_longitude);

			allRoutes.add(route_point);

		}

		db_cursor.close();
		return allRoutes;

	}

	public static RoutePoint getSingleRoutePoint(String timestamp) {

		RoutePoint route_point = null;

		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				"timestamp = ?", // select options
				new String[] { timestamp }, // Using ? in the select options can
											// be replaced here as an array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		while (db_cursor.moveToNext()) {

			// Getting each field
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
			String cursor_picture = db_cursor.getString(db_cursor
					.getColumnIndex("picture"));
			int cursor_longitude = db_cursor.getInt(db_cursor
					.getColumnIndex("longitude"));
			int cursor_latitude = db_cursor.getInt(db_cursor
					.getColumnIndex("latitude"));
			Timestamp cursor_time = Timestamp.valueOf(db_cursor
					.getString(db_cursor.getColumnIndex("timestamp")));

			route_point = new RoutePoint(cursor_id, cursor_time, // Timestamp
																	// class
																	// helps us
																	// to get
																	// the value
																	// as
																	// timestamp
					cursor_picture, cursor_latitude, cursor_longitude);

		}

		db_cursor.close();

		return route_point;
	}

	public static boolean isOpenRoute() {

		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("route_info", // table
				null, // which column
				"active = X", // select options
				null, // Using ? in the select options can be replaced here as
						// an array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		if (db_cursor.getCount() == 0) {
			db_cursor.close();
			return false;

		} else {

			return true;

		}

	}

	public static void getRouteInfo(Route route) {

		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				"_id = " + String.valueOf(route.getId()), // select options
				null, // Using ? in the select options can be replaced here as
						// an array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		while (db_cursor.moveToNext()) {

			// Getting each field
			route.setRouteName(db_cursor.getString(db_cursor
					.getColumnIndex("name")));
			route.setDate(db_cursor.getString(db_cursor.getColumnIndex("date")));
			route.setActive(db_cursor.getString(db_cursor
					.getColumnIndex("active")));

			// route_point = new RoutePoint(cursor_id, cursor_name, cursor_date,
			// cursor_active);

		}

		db_cursor.close();
	}

	public static RoutePoint getOpenRouteInfo() {

		RoutePoint route_point = null;

		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				"active = X", // select options
				null, // Using ? in the select options can be replaced here as
						// an array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		while (db_cursor.moveToNext()) {

			// Getting each field
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
			String cursor_name = db_cursor.getString(db_cursor
					.getColumnIndex("name"));
			String cursor_date = db_cursor.getString(db_cursor
					.getColumnIndex("date"));
			String cursor_active = db_cursor.getString(db_cursor
					.getColumnIndex("active"));

			route_point = new RoutePoint(cursor_id, cursor_name, cursor_date,
					cursor_active);

		}

		db_cursor.close();

		return route_point;
	}

	public static int getCurrentRouteID() {
		return currentRouteID;
	}

	// EVERY TIME A NEW ROUTE IS STARTET THIS METHOD HAS TO BE CALLED
	public static void registerNewRoute(String name) {

		// -1 means first route ever tracked
		if (getIDlastRoute() != -1) {
			currentRouteID = getIDlastRoute() + 1;
			currentRouteName = name;

		} else {

			currentRouteID = 1;
			currentRouteName = name;

		}

	}

	public static int getSettingValue(String name) {

		int value;

		Cursor db_cursor;

		mDatabase = mHelper.getWritableDatabase();

		db_cursor = mDatabase.query("settings", // table
				null, // which column
				"name = ?", // select options
				new String[] { name }, // Using ? in the select options can be
										// replaced here as an array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		db_cursor.moveToFirst();

		value = db_cursor.getInt(db_cursor.getColumnIndex("value"));

		db_cursor.close();

		return value;

	}

	public static void changeSettingValue(String setting, int value) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("value", value);
		mDatabase
				.update("settings", values, "name=?", new String[] { setting });

	}

	// public static List<RoutePoint> getRoutePoint (int id){
	//
	// List<RoutePoint> route = new ArrayList <RoutePoint> () ;
	//
	// Cursor db_cursor;
	//
	// mDatabase = mHelper.getWritableDatabase();
	//
	//
	//
	// db_cursor = mDatabase.query("route_points", //table
	// null , //which column
	// "_id = ?" , // select options
	// new String [] { String.valueOf(id)}, // Using ? in the select options can
	// be replaced here as an array
	// null, // Group by ID --> Only the whole routes
	// null, //Having
	// null);// order by
	//
	//
	//
	// while (db_cursor.moveToNext()) {
	//
	// // Getting each field
	// int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
	// String cursor_picture =
	// db_cursor.getString(db_cursor.getColumnIndex("picture"));
	// int cursor_longitude =
	// db_cursor.getInt(db_cursor.getColumnIndex("longitude"));
	// int cursor_latitude =
	// db_cursor.getInt(db_cursor.getColumnIndex("latitude"));
	// Timestamp cursor_time =
	// Timestamp.valueOf(db_cursor.getString(db_cursor.getColumnIndex("timestamp")));
	//
	//
	// RoutePoint route_point = new RoutePoint(cursor_id,
	// cursor_time, //Timestamp class helps us to get the value as timestamp
	// cursor_picture,
	// cursor_latitude,
	// cursor_longitude);
	//
	// route.add(route_point);
	//
	//
	// }
	//
	// db_cursor.close();
	//
	//
	// return route;
	// }

}
