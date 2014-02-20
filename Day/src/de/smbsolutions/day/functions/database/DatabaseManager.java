package de.smbsolutions.day.functions.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

	private static final String DB_NAME = "routes.db";
	private static final int DB_VERSION = 1;
	private static final String ROUTES_CREATE = 
			"CREATE TABLE route_points ( _id INTEGER NOT NULL, timestamp NOT NULL, picture TEXT , longitude DOUBLE, latitude DOUBLE, PRIMARY KEY (_id, timestamp) ) ";
	
	private static final String ROUTE_INFO_CREATE = "CREATE TABLE route_info ( _id INTEGER NOT NULL, name TEXT NOT NULL, date TEXT NOT NULL, active TEXT, PRIMARY KEY (_id) ) ";
	private static final String SETTINGS_CREATE = "CREATE TABLE settings (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, value INTEGER NOT NULL ) ";
//	"CREATE TABLE routes (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "link TEXT NOT NULL, " +
 //   "geo_location INTEGER, " + "timestamp TIMESTAMP" + ")";
	
	private static final String INITIALSETTINGS_INSERT = 
			" INSERT INTO settings (name, value) VALUES " +
			"  ('tracking', 0 ), " +
			"  ('tracking_interval', 10000)";
			
	private static final String CLASS_DROP = "DROP TABLE IF EXISTS routes";
	
	public DatabaseManager (Context context) {
		
		super( context , DB_NAME, null, DB_VERSION);

	
	}
	
	



	//Wird aufgerufen, wenn DB noch nicht vorhanden
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	
		db.execSQL(ROUTES_CREATE);
		db.execSQL(ROUTE_INFO_CREATE);
		db.execSQL(SETTINGS_CREATE);
		db.execSQL(INITIALSETTINGS_INSERT);
	
	
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		db.execSQL(CLASS_DROP);
		onCreate(db);

		
	}

// wird angesprungen, wenn version größer als die aktuelle ist
//	@Override
//	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		// TODO Auto-generated method stub
//		// super.onDowngrade(db, oldVersion, newVersion);
//		
//		db.execSQL(CLASS_DROP);
//		onCreate(db);
//	}
//		

}
