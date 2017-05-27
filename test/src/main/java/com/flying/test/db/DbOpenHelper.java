package com.flying.test.db;

import android.content.Context;

import com.flying.test.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * Created by liwu.shu on 2017/5/19.
 */

public class DbOpenHelper  extends DaoMaster.DevOpenHelper{

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                break;
            case 2:
                break;
        }
    }
}
