package kr.co.toyappfactory.gosung.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by chiduk on 2016. 6. 8..
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user.db";
    public static final String INFO_TABLE_NAME = "info";
    public static final String INFO_COLUMN_ID = "id";
    public static final String INFO_COLUMN_NAME = "name";
    public static final String INFO_COLUMN_UNIQUE_ID = "uniqueId";
    public static final String INFO_COLUMN_EMAIL = "email";
    public static final String INFO_COLUMN_PASSWORD = "password";
    public static final String INFO_COLUMN_PHONE = "phone";
    public static final String INFO_COLUMN_LOGGEDIN = "loggedin";



    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table info (id integer primary key, name text, uniqueId text, email text, phone text, loggedin integer default 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS info");
        onCreate(db);
    }

    public boolean insertUser(String name, String uniqueId, String email, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("uniqueId", uniqueId);
        values.put("email", email);
        values.put("phone", phone);
        values.put("loggedin", 1);
        db.insert(INFO_TABLE_NAME, null, values);

        return true;
    }

    public boolean updateLoggedIn(int id, int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loggedin", value);
        db.update(INFO_TABLE_NAME, values, "id=?", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from info where id=" + id + "", null);
        return res;
    }

    public boolean updateInfo(Integer id, String name, String password, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("password", password);
        values.put("phone", phone);
        db.update(INFO_TABLE_NAME, values, "id=?", new String[] {Integer.toString(id)});
        return true;
    }

    public boolean updateInfo(Integer id, String name, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("password", password);
        db.update(INFO_TABLE_NAME, values, "id=?", new String[] {Integer.toString(id)});
        return true;
    }

    public boolean updateName(Integer id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);

        db.update(INFO_TABLE_NAME, values, "id=?", new String[] {Integer.toString(id)});
        return true;
    }

    public boolean updatePassword(Integer id, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("password", password);
        db.update(INFO_TABLE_NAME, values, "id=?", new String[] {Integer.toString(id)});
        return true;
    }

    public void setUserInfo(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM info WHERE id=?", new String[]{Integer.toString(id)});
        if(cursor.getCount() > 0 ){
            cursor.moveToFirst();
            JoinUserInfo.getInstance().setName(cursor.getString(cursor.getColumnIndex(INFO_COLUMN_NAME)));
            JoinUserInfo.getInstance().setEmail(cursor.getString(cursor.getColumnIndex(INFO_COLUMN_EMAIL)));
            JoinUserInfo.getInstance().setUniqueId(cursor.getString(cursor.getColumnIndex(INFO_COLUMN_UNIQUE_ID)));

        }
    }

    public Integer deleteUser(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INFO_TABLE_NAME, "id=?", new String[]{Integer.toString(id)});
    }

    public Integer deleteAllUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INFO_TABLE_NAME, null, null);

    }

    public Integer deleteUser(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INFO_TABLE_NAME, "email=?", new String[]{email});

    }
    public ArrayList<String> getAllUsers(){
        ArrayList<String> userList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from info", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            userList.add(res.getString(res.getColumnIndex(INFO_COLUMN_NAME)));
            res.moveToNext();
        }

        return userList;
    }

    public boolean isLoggedIn(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT loggedin FROM info WHERE id=?", new String[]{Integer.toString(1)});
        //Cursor res = db.rawQuery("select * from info", null);
        if ( res.getCount() > 0) {
            res.moveToFirst();
            int loggedin = res.getInt(res.getColumnIndex(INFO_COLUMN_LOGGEDIN));
            if(loggedin == 1){
                return true;
            }else{
                return false;
            }
        }else{

            return false;
        }
    }
}
