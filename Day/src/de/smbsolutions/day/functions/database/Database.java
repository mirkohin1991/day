package de.smbsolutions.day.functions.database;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.smbsolutions.day.functions.interfaces.DatabaseInterface;
import de.smbsolutions.day.functions.objects.Route;
import de.smbsolutions.day.functions.objects.RoutePoint;

public class Database implements DatabaseInterface {

	private static DatabaseManager mHelper;
	private static SQLiteDatabase mDatabase;
	private static int lastRouteID = 0;
	private static String currentRouteName;
	private static Database db_data = null;

	// In welchem Kontext???

	private Database(Context context) {

		mHelper = new DatabaseManager(context);

		// saving the current routeID
		lastRouteID = selectIDlastRoute();
		if (lastRouteID == 0) {

			Database.createDefaultRoutes();
		}
	}

	public static Database getInstance(Context context) {
		if (db_data == null)
			db_data = new Database(context);
		return db_data;
	}



	public static boolean addNewRoutePoint(RoutePoint point) {

		try {

			mDatabase = mHelper.getWritableDatabase();

			ContentValues route_values = new ContentValues();
			// Zum Testen erstmal alle dem gleichen Record
			route_values.put("_id", lastRouteID);
			route_values.put("timestamp", point.getTimestamp().toString());
			route_values.put("latitude", point.getLatitude()); // inserting a
			route_values.put("picture", point.getPicture()); 
			route_values.put("picture_preview", point.getPicturePreview()); 
			route_values.put("longitude", point.getLongitude());
			route_values.put("latitude", point.getLatitude());
			mDatabase.insert("route_points", null, route_values);
			mDatabase.close();

			// everything was ok
			return true;
		} catch (Exception e) {

			return false;

		}

	}
	
	public static boolean deleteRoute (Route route) {
		
		try {
			mDatabase = mHelper.getWritableDatabase();
			mDatabase.delete("route_points", "_id=?", new String[] { String.valueOf(route.getId()) });
			mDatabase.delete("route_info", "_id=?", new String[] { String.valueOf(route.getId()) });
			mDatabase.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
public static boolean deletePicturePath (RoutePoint routePoint) {

		try {
			mDatabase = mHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.putNull(Database.ROUTE_POINTS_PICTURE);
			values.putNull( Database.ROUTE_POINTS_PICTURE_PREVIEW);
			
			mDatabase.update("route_points", values, "timestamp=?", new String[] { String.valueOf(routePoint.getTimestamp()) });
			mDatabase.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static boolean createNewRoute(Route route) {

		// For a completely new route the general information, like
		// the name has to be stored

		try {
			mDatabase = mHelper.getWritableDatabase();
			ContentValues route_info = new ContentValues();
			route_info.put("_id", String.valueOf(route.getId()));
			route_info.put("name", route.getRouteName());
			route_info.put("date", route.getDate());
			route_info.put("active", "X");
			mDatabase.insert("route_info", null, route_info);

			lastRouteID = route.getId();
			mDatabase.close();

			// Everything was ok
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public static boolean closeRoute(int id) {

		try {

			mDatabase = mHelper.getWritableDatabase();
			ContentValues route_value = new ContentValues();
			route_value.put("active", "");

			mDatabase.update("route_info", route_value, "_id=?",
					new String[] { String.valueOf(id) }); // Which columns
			mDatabase.close();

			// Everything was ok
			return true;

		} catch (Exception e) {
			// TODO: handle exception

			return false;
		}

	}

	private static int selectIDlastRoute() {

		Cursor db_cursor;
		String[] db_columns = { "_ID" };
		int lastRecord;

		mDatabase = mHelper.getReadableDatabase();

		// Check if an entry exists. Ordered descending to get the latest route
		db_cursor = mDatabase.query("route_points", // table
				db_columns, // which column
				null, // select options
				null, // Using ? in the select options can be replaced here as
						// an array
				null, // Group by
				null, // Having
				"_id DESC");// order by DESCENDING

		if (db_cursor.getCount() != 0) {

			db_cursor.moveToFirst();

			lastRecord = db_cursor.getInt(db_cursor.getColumnIndex("_id"));

			// It is the first time a record is saved
		} else {

			lastRecord = 0;

		}

		db_cursor.close();
		mDatabase.close();
		return lastRecord;
	}

	// ////////////////////////////
	// / Method to get specific routes based on the count the method is called
	// / Count 0 --> All available routes
	// / Count 1-* --> The last X routes are returned
	// ////////////////////////////
	public static List<Route> getSpecificRoute(int count) {

		List<RoutePoint> allRoutes = new ArrayList<RoutePoint>();
		Cursor db_cursor;

		mDatabase = mHelper.getReadableDatabase();

		String condition;

		// count == 0 -> user wants all routes
		// count > id -> less routes available than required -> just show all
		if (count == 0 || count > lastRouteID) {
			condition = null;

		} else {
			condition = "_id > " + String.valueOf(lastRouteID - count)
					+ " AND _id <= " + String.valueOf(lastRouteID);

		}

		db_cursor = mDatabase.query("route_points", // table
				null, // which column
				condition, // select options ||
				null, // Using ? in the select options can be replaced here as
						// an
						// array
				null, // Group by ID --> Only the whole routes
				null, // Having
				null);// order by

		Route route = new Route();
		List<Route> route_list = new ArrayList();

		// Storing the id of the previous entry to compare it in the next while
		// loop
		// Initial value -1 signifies, that this is the first point
		int previous_id = -1;
		while (db_cursor.moveToNext()) {

			// Getting each field
			int cursor_id = db_cursor.getInt(db_cursor.getColumnIndex("_id"));
			String cursor_picture = db_cursor.getString(db_cursor
					.getColumnIndex("picture"));
			String cursor_picture_preview = db_cursor.getString(db_cursor
					.getColumnIndex("picture_preview"));
			
			double cursor_longitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("longitude"));
			double cursor_latitude = db_cursor.getDouble(db_cursor
					.getColumnIndex("latitude"));
			Timestamp cursor_time = Timestamp.valueOf(db_cursor
					.getString(db_cursor.getColumnIndex("timestamp")));

			// If it is a new route
			if (cursor_id != previous_id) {

				// additionally it is not the first point of th loop
				if (cursor_id != previous_id && previous_id != -1) {
					// add the complete route to the list and start a new route
					route_list.add(route);
					route = new Route();
				}

				// Because it is a new route, the new id has to be stored
				route.setId(cursor_id);
				// and additional information, like the name and date of the
				// route have to be collect one time.
				getRouteInfo(route);
			}

			// saving the id for the next loop
			previous_id = cursor_id;

			// Creating a new route point and adding it to the route.
			RoutePoint route_point = new RoutePoint(cursor_id, cursor_time,
					cursor_picture, cursor_picture_preview, cursor_latitude, cursor_longitude, cursor_latitude);
			route.addRoutePoint(route_point);

			// In case of the last entry, the collected route has to be added
			// (Because there is no next loop which could execute the common way
			// of adding it.
			if (db_cursor.isLast() == true) {
				route_list.add(route);
			}
		}

		db_cursor.close();
		mDatabase.close();

		return route_list;

	}

	public static RoutePoint getSingleRoutePoint(String timestamp) {

		RoutePoint route_point = null;

		Cursor db_cursor;

		mDatabase = mHelper.getReadableDatabase();

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
			String cursor_picture_preview = db_cursor.getString(db_cursor
					.getColumnIndex("picture_preview"));
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
					cursor_picture, cursor_picture_preview, cursor_latitude, cursor_longitude, cursor_latitude);

		}

		db_cursor.close();
		mDatabase.close();

		return route_point;
	}

	public static void getRouteInfo(Route route) {

		Cursor db_cursor;

		mDatabase = mHelper.getReadableDatabase();

		db_cursor = mDatabase.query("route_info", // table
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
		mDatabase.close();
	}

	public static int getlastRouteID() {
		return lastRouteID;
	}

	public static int getSettingValue(String name) {

		int value;

		Cursor db_cursor;

		mDatabase = mHelper.getReadableDatabase();

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
		mDatabase.close();

		return value;

	}

	public static void changeSettingValue(String setting, int value) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("value", value);
		mDatabase
				.update("settings", values, "name=?", new String[] { setting });
		
		mDatabase.close();

	}

	public static void createDefaultRoutes() {
		try {
			
			mDatabase = mHelper.getWritableDatabase();

			for (int i = 0; i <= 2; i++) {
				String ts = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date(i));
				Route route = new Route("Beispielroute" + String.valueOf(i));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9983322, 7.8018200, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.91913344, 7.8218310, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9313355, 7.8248580, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9513366, 7.8618999, 5454));
				route.addRoutePointDB(new RoutePoint(route.getId(),
						new Timestamp(System.currentTimeMillis()), null, null,
						47.9983322, 7.8018200, 5454));
				if (i < 2) {

					closeRoute(route.getId());
				}
				mDatabase.close();

			}

		} catch (Exception e) {
			// TODO: handle exception

		}
	}


	public static void addNewRoutePoint(double latitude, double longitude,
			Timestamp timestamp) {

		mDatabase = mHelper.getWritableDatabase();

		ContentValues route_values = new ContentValues();

		// Zum Testen erstmal alle dem gleichen Record
		route_values.put("_id", lastRouteID);
		route_values.put("timestamp", timestamp.toString()); // inserting an int
		route_values.put("latitude", latitude); // inserting a string
		route_values.put("longitude", longitude);

		// CurrentRoute greater than the latest one in the DB
		// --> Completely new route!
		if (lastRouteID > selectIDlastRoute()) {

			mDatabase.insert("route_points", null, route_values);

			// For a completely new route, also the general information, like
			// the name has to be stored
			ContentValues route_info = new ContentValues();
			route_info.put("_id", lastRouteID);
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
		route_values.put("_id", lastRouteID);
		route_values.put("timestamp", timestamp.toString()); // inserting an int
		route_values.put("picture", picture); // inserting an int
		route_values.put("latitude", latitude); // inserting a string
		route_values.put("longitude", longitude);

		// CurrentRoute greater than the latest one in the DB
		// --> Completely new route!
		if (lastRouteID > selectIDlastRoute()) {

			mDatabase.insert("route_points", null, route_values);

			// For a completely new route, also the general information, like
			// the name has to be stored
			ContentValues route_info = new ContentValues();
			route_info.put("_id", lastRouteID);
			route_info.put("name", currentRouteName);
			route_info.put("date", timestamp.getDate());
			route_info.put("active", "X");
			mDatabase.insert("route_info", null, route_info);

		} else {

			mDatabase.insert("route_points", null, route_values);

		}

	}


}
