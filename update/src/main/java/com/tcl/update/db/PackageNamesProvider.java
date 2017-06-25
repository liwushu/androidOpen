package com.tcl.update.db;

import android.content.Context;

import com.tcl.framework.db.EntityManager;
import com.tcl.framework.db.EntityManagerFactory;

import java.util.List;

/**
 * Created by yancai.liu on 2016/10/11.
 */

public class PackageNamesProvider extends BaseProvider<PackageNames> {

    private static final String TABLE = "t_package_names";

    public PackageNamesProvider(Context context) {
        super(context);
    }

    @Override
    protected EntityManager<PackageNames> entityManagerFactory(Context context) {
        dbMananger =
                EntityManagerFactory.getInstance(context, BaseProvider.DB_VERSION, BaseProvider.DB_ACCOUNT, null, null)
                        .getEntityManager(PackageNames.class, TABLE);
        return dbMananger;
    }

    @Override
    public List<PackageNames> getAllData() {
        return dbMananger.findAll();
    }

    public PackageNames getData() {
        List<PackageNames> list = dbMananger.findAll();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;

    }

    @Override
    public PackageNames getDataById(String id) {
        return dbMananger.findById(id);
    }

    @Override
    public void saveOrUpdateAll(List<PackageNames> dataList) {
        dbMananger.saveOrUpdateAll(dataList);
    }

    @Override
    public void saveOrUpdate(PackageNames dataList) {
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
