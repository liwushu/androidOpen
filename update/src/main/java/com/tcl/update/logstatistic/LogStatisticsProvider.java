package com.tcl.update.logstatistic;

import android.content.Context;

import com.tcl.framework.db.EntityManager;
import com.tcl.framework.db.EntityManagerFactory;
import com.tcl.update.db.BaseProvider;

import java.util.List;

/**
 * Created by fanyang.sz on 2016/12/9.
 */

public class LogStatisticsProvider extends BaseProvider<LogStatisticsItem> {

    private static final String TABLE = "operLog_table";

    public LogStatisticsProvider(Context context) {
        super(context);
    }

    @Override
    protected EntityManager<LogStatisticsItem> entityManagerFactory(Context context) {
        dbMananger =
                EntityManagerFactory.getInstance(context, BaseProvider.DB_VERSION, BaseProvider.DB_ACCOUNT, null, null)
                        .getEntityManager(LogStatisticsItem.class, TABLE);
        return dbMananger;
    }

    @Override
    public List<LogStatisticsItem> getAllData() {
        return dbMananger.findAll();
    }

    @Override
    public LogStatisticsItem getDataById(String id) {
        return dbMananger.findById(id);
    }

    @Override
    public void saveOrUpdateAll(List<LogStatisticsItem> dataList) {
        dbMananger.saveOrUpdateAll(dataList);
    }

    @Override
    public void saveOrUpdate(LogStatisticsItem dataList) {
        dbMananger.saveOrUpdate(dataList);
    }

    @Override
    public void deleteAll() {
        dbMananger.deleteAll();
    }

    @Override
    public void deleteById(String id) {
        dbMananger.deleteById(id);
    }
}
