package club.lovemo.utils;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper{

	public MyDBHelper() {
		super(LovemoApplication.getContext(), "LovemoMusicManager.db", null, AppConstants.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String Sql="create table Users(_id integer primary key autoincrement,username,password,encrypted,answer)";
		db.execSQL(Sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
