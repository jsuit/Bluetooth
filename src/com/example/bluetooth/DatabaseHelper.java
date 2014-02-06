package com.example.bluetooth;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String KEY_COL_MEANX = "MEANX";
	public static final String KEY_COL_MEANY = "MEANY";
	public static final String KEY_COL_MEANZ = "MEANZ";
	public static final String KEY_COL_VARIANCEX = "VARIANCEX";
	public static final String KEY_COL_VARIANCEY = "VARIANCEY";
	public static final String KEY_COL_VARIANCEZ = "VARIANCEZ";
	public static final String KEY_COL_CORRX = "CORRX";
	public static final String KEY_COL_CORRY = "CORRY";
	public static final String KEY_COL_CORRZ = "CORRZ";
	public static final String DATABASE_NAME = "features";
	private static final int DATABASE_VERSION = 1;
	public static final String KEY_ROW_ID = "id";
	public static final String DATABASE_TABLE_USERS = "feature_table";
	public static final String DATABASE_TEST_FEATURES = "feature_test_table";
	public static final String DATABASE_COL_Activity = "Activity";
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ DATABASE_TABLE_USERS + "(" + KEY_ROW_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_COL_MEANX
			+ " REAL NOT NULL," + KEY_COL_MEANY + " REAL NOT NULL,"
			+ KEY_COL_MEANZ + " REAL NOT NULL," + KEY_COL_VARIANCEX
			+ " REAL NOT NULL," + KEY_COL_VARIANCEY + " REAL NOT NULL,"
			+ KEY_COL_VARIANCEZ + " REAL NOT NULL," + KEY_COL_CORRX
			+ " REAL NOT NULL," + KEY_COL_CORRY + " REAL NOT NULL,"
			+ KEY_COL_CORRZ + " REAL NOT NULL," + DATABASE_COL_Activity
			+ " TEXT NOT NULL);";
	private static final String CREATE_TEST_DATABASE = "CREATE TABLE "
			+ DATABASE_TEST_FEATURES + "(" + KEY_ROW_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_COL_MEANX
			+ " REAL NOT NULL," + KEY_COL_MEANY + " REAL NOT NULL,"
			+ KEY_COL_MEANZ + " REAL NOT NULL," + KEY_COL_VARIANCEX
			+ " REAL NOT NULL," + KEY_COL_VARIANCEY + " REAL NOT NULL,"
			+ KEY_COL_VARIANCEZ + " REAL NOT NULL," + KEY_COL_CORRX
			+ " REAL NOT NULL," + KEY_COL_CORRY + " REAL NOT NULL,"
			+ KEY_COL_CORRZ + " REAL NOT NULL," + DATABASE_COL_Activity
			+ " TEXT NOT NULL);";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("dataBasehelper called", "called");

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//db.execSQL(DATABASE_CREATE);
    	//db.execSQL(CREATE_TEST_DATABASE);
		//db.execSQL(DATABASE_CREATE);
		Log.i("Oncreate for datatabse helper called", "oncreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//db.execSQL(DATABASE_CREATE);
		//db.execSQL(CREATE_TEST_DATABASE);
	}

}
