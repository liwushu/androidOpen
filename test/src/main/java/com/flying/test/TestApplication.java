package com.flying.test;


import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.Test;

/**
 * Created by liwu.shu on 2017/4/24.
 */

public class TestApplication extends Application {
    public DaoSession daoSession;
    private static TestApplication application;
    static Context mc;
    @Override
    public void onCreate(){
        super.onCreate();
        mc =  TestApplication.this;
        application = this;
        setupDatabase();
    }

    public static Context getContext(){
        return mc;
    }

    public static TestApplication getTestApplication(){
        return application;
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "example-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
