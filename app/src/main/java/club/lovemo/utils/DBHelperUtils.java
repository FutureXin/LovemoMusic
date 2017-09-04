package club.lovemo.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import club.lovemo.entity.User;

public class DBHelperUtils {
	private static DBHelperUtils instance;
	public static DBHelperUtils getInstance(){
		if(instance==null){
			instance=new DBHelperUtils();
		}
		return instance;
	}
	private MyDBHelper helper;
	private SQLiteDatabase db;

	private DBHelperUtils(){
		helper=new MyDBHelper();
		db=helper.getReadableDatabase();
	}
	public void DBclose(){
		helper.close();
		db.close();
		instance=null;
	}
	public boolean insert(User user){
		ContentValues values=new ContentValues();
		values.put("username", user.getUsername());
		values.put("password", user.getUserpassword());
		values.put("encrypted", user.getEncrypted());
		values.put("answer", user.getAnswer());
		long rid=db.insert("Users", null, values);
		if(rid>0){
			return true;
		}
		return false;
	}
	public User getUser(String name){
		List<User> userarray=new ArrayList<User>();
		Cursor cur;
		cur=db.query("Users", null,"username=?", new String[]{name}, null, null, null);
		while (cur.moveToNext()) {
			User user=new User();
			int id=cur.getInt(cur.getColumnIndex("_id"));
			String username=cur.getString(cur.getColumnIndex("username"));
			String password=cur.getString(cur.getColumnIndex("password"));
			String encrypted=cur.getString(cur.getColumnIndex("encrypted"));
			String answer=cur.getString(cur.getColumnIndex("answer"));

			user.setId(id);
			user.setUsername(username);
			user.setUserpassword(password);
			user.setEncrypted(encrypted);
			user.setAnswer(answer);
			userarray.add(user);
		}
		return userarray.get(0);
	}
}
