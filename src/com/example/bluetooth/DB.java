package com.example.bluetooth;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {

	private DatabaseHelper my_helper;
	private static SQLiteDatabase database;
	private Cursor cursor;

	public DB(Context context) {
		my_helper = new DatabaseHelper(context);

	}

	public void open() {
		if (database == null || !database.isOpen())
			database = my_helper.getWritableDatabase();
	}

	public void close() {
		if (database != null && database.isOpen())
			my_helper.close();
	}

	public long save(List<Float> features, String activity) {
		ContentValues values = new ContentValues();
		// ORDER MATTERS: mean, variance, correlation (x,y,z)
		if (activity == null) {
			return -1;
		}
		String[] columns = { DatabaseHelper.KEY_COL_MEANX,
				DatabaseHelper.KEY_COL_MEANY, DatabaseHelper.KEY_COL_MEANZ,
				DatabaseHelper.KEY_COL_VARIANCEX,
				DatabaseHelper.KEY_COL_VARIANCEY,
				DatabaseHelper.KEY_COL_VARIANCEZ, DatabaseHelper.KEY_COL_CORRX,
				DatabaseHelper.KEY_COL_CORRY, DatabaseHelper.KEY_COL_CORRZ,
				DatabaseHelper.DATABASE_COL_Activity };

		int length = features.size();
		for (int i = 0; i < length; i++) {
			values.put(columns[i], features.get(i));
		}
		values.put(columns[columns.length - 1], activity);
		long id = database.insert(DatabaseHelper.DATABASE_TABLE_USERS, null,
				values);
		return id;
	}

	public Cursor retrieveRows(String table) {

		cursor = database.rawQuery("select * from " + table, null);
		if (cursor == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setGroupingUsed(false);
		formatter.setMinimumFractionDigits(7);

		if (cursor.moveToFirst()) {
			int i = cursor.getColumnCount();
			while (!cursor.isAfterLast()) {
				for (int j = 1; j < i - 1; j++) {
					buffer.append(formatter.format(cursor.getFloat(j)));
					buffer.append(",");
				} // this appends the classification
				buffer.append(cursor.getString(i - 1));
				buffer.append("\n");
				cursor.moveToNext();
			}
		}
		cursor.moveToFirst();
		return cursor;
	}

	public String getData(String table){
		
		cursor = database.rawQuery("select * from " + table, null);
		if (cursor == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setGroupingUsed(false);
		formatter.setMinimumFractionDigits(7);

		if (cursor.moveToFirst()) {
			int i = cursor.getColumnCount();
			while (!cursor.isAfterLast()) {
				for (int j = 1; j < i - 1; j++) {
					buffer.append(formatter.format(cursor.getFloat(j)));
					buffer.append(",");
				} // this appends the classification
				buffer.append(cursor.getString(i - 1));
				buffer.append("\n");
				cursor.moveToNext();
			}
		}
		
		return buffer.toString();
	}
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		cursor = database.rawQuery("select * from "
				+ DatabaseHelper.DATABASE_TABLE_USERS, null);
		return cursor;
	}

	public long save(List<Float> features, String activity,
			String databaseTestFeatures) {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();
		// ORDER MATTERS: mean, variance, correlation (x,y,z)

		String[] columns = { DatabaseHelper.KEY_COL_MEANX,
				DatabaseHelper.KEY_COL_MEANY, DatabaseHelper.KEY_COL_MEANZ,
				DatabaseHelper.KEY_COL_VARIANCEX,
				DatabaseHelper.KEY_COL_VARIANCEY,
				DatabaseHelper.KEY_COL_VARIANCEZ, DatabaseHelper.KEY_COL_CORRX,
				DatabaseHelper.KEY_COL_CORRY, DatabaseHelper.KEY_COL_CORRZ,
				DatabaseHelper.DATABASE_COL_Activity };

		int length = features.size();
		for (int i = 0; i < length; i++) {
			values.put(columns[i], features.get(i));
		}
		values.put(columns[columns.length - 1], activity);
		long id = database.insert(databaseTestFeatures, null, values);
		return id;
	}

}
