package de.smbsolutions.day.functions.database;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "routes.db";
	private static final int DB_VERSION = 1;
	private static final String ROUTES_CREATE = 
			"CREATE TABLE route_points ( _id INTEGER NOT NULL, timestamp NOT NULL, picture TEXT , longitude DOUBLE, latitude DOUBLE, PRIMARY KEY (_id, timestamp) ) ";
	
	private static final String ROUTE_INFO_CREATE = "CREATE TABLE route_info ( _id INTEGER NOT NULL, name TEXT NOT NULL, date TEXT NOT NULL, PRIMARY KEY (_id) ) ";
	
//	"CREATE TABLE routes (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "link TEXT NOT NULL, " +
 //   "geo_location INTEGER, " + "timestamp TIMESTAMP" + ")";
	
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
