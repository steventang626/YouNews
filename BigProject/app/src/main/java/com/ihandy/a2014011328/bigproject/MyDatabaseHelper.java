package com.ihandy.a2014011328.bigproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_NEWS = "create table News ("
			+ "id bigint, "
			+ "image text, "
			+ "title text, "
			+ "origin text, "
			+ "source text, "
			+ "like integer, "
			+ "category text)";
	
//	public static final String CREATE_CATEGORY = "create table Category ("
//			+ "id integer primary key autoincrement, "
//			+ "category_name text, "
//			+ "category_code integer)";

	private Context mContext;

	public MyDatabaseHelper(Context context, String name,
							CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NEWS);
		Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
		Log.i("here","hhh");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
