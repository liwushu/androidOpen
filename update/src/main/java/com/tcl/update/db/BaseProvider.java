package com.tcl.update.db;

import android.content.Context;

import com.tcl.framework.db.EntityManager;


public abstract class BaseProvider<T> implements IDataProvider<T> {

    public static final int DB_VERSION = 1;

    public static final String DB_ACCOUNT = "updateSdk";

    protected EntityManager<T> dbMananger;

    public BaseProvider(Context context) {
        dbMananger = entityManagerFactory(context);
    }

    protected abstract EntityManager<T> entityManagerFactory(Context context);

}
