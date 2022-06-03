package com.example.smartlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQLiteQueries extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "SmartLog.db";
    private static final int DATABASE_VERSION = 1;

    //for table Server
    private static final String TABLE_NAME_SERVER = "ServerTable";
    private static final String COLUMN_SERVER_ID = "Server_ID";
    private static final String COLUMN_SERVER_NAME = "Server_Name";
    private static final String COLUMN_SERVER_ADDRESS = "Server_mcAddress";


    public SQLiteQueries(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Query = "CREATE TABLE "+ TABLE_NAME_SERVER +
                " ("+COLUMN_SERVER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_SERVER_NAME + " TEXT, "+
                COLUMN_SERVER_ADDRESS + " TEXT)";
        db.execSQL(Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_SERVER);
        onCreate(db);
    }

    public void addServer(String Name, String Address){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        CV.put(COLUMN_SERVER_NAME, Name);
        CV.put(COLUMN_SERVER_ADDRESS, Address);

        long result = DB.insert(TABLE_NAME_SERVER, null, CV);
        if(result == -1){
            Toast.makeText(context, "Server failed to save.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Server Saved.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean serverNotSEt(){
        SQLiteDatabase db = this.getReadableDatabase();
        long NoOfRows = DatabaseUtils.queryNumEntries(db, TABLE_NAME_SERVER);
        return NoOfRows == 0;
    }

    public String[] serverInfo(){
        String query = "SELECT * FROM " + TABLE_NAME_SERVER;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] info = new String[2];

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                info[0] = cursor.getString(1);
                info[1] = cursor.getString(2);
            }while(cursor.moveToNext());
        }
        return info;
    }
}
