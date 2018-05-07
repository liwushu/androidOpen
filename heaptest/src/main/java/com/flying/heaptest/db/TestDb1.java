package com.flying.heaptest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TestDb1 extends SQLiteOpenHelper{

    public TestDb1(Context mc,String dbName) {
        super(mc,dbName,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists test0 (id integer,context vchar, name vachar,age integer,sex integer) ");
        db.execSQL("create table if not exists test1 (id integer,context vchar,name vachar,age integer,sex integer)");
        db.execSQL("create table if not exists test2 (id integer,context vchar,name vachar,age integer,sex integer)");
        db.execSQL("create table if not exists test3 (id integer,context vchar,name vachar,age integer,sex integer)");
        db.execSQL("create table if not exists test4 (id integer,context vchar,name vachar,age integer,sex integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SQLiteDatabase db,int count) {
        for(int i=0;i<5;i++) {
            String tableName = "test"+i;
            for(int j=0;j<count;j++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id",j);
                contentValues.put("context","context"+j);
                db.insert(tableName,null,contentValues);
            }
        }
    }

    public void query(SQLiteDatabase db) {
        for(int i=0;i<5;i++) {
            String tableName = "test"+i;
            Cursor cursor = db.query(tableName,null ,null,null,null,null,null);
            if(cursor == null) {
                return ;
            }

            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String context = cursor.getString(cursor.getColumnIndex("context"));
                Log.e("TestDb1","id: "+id+"   context: "+context);
            }
            cursor.close();
        }
    }
}
