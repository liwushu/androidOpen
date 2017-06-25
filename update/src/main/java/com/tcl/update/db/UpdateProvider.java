package com.tcl.update.db;

import android.content.Context;

import com.tcl.framework.db.EntityManager;
import com.tcl.framework.db.EntityManagerFactory;

import java.util.List;

/**
 * Created by yancai.liu on 2016/10/11.
 */

public class UpdateProvider extends BaseProvider<UpdateInfo> {

    private static final String TABLE = "t_update";

    public UpdateProvider(Context context) {
        super(context);
    }

    @Override
    protected EntityManager<UpdateInfo> entityManagerFactory(Context context) {
        dbMananger =
                EntityManagerFactory.getInstance(context, BaseProvider.DB_VERSION, BaseProvider.DB_ACCOUNT, null, null)
                        .getEntityManager(UpdateInfo.class, TABLE);
        return dbMananger;
    }

    @Override
    public List<UpdateInfo> getAllData() {
        return dbMananger.findAll();
    }

    @Override
    public UpdateInfo getDataById(String id) {
        return dbMananger.findById(id);
    }

    @Override
    public void saveOrUpdateAll(List<UpdateInfo> dataList) {
        dbMananger.saveOrUpdateAll(dataList);
    }

    @Override
    public void saveOrUpdate(UpdateInfo dataList) {
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
