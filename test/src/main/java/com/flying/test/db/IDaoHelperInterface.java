package com.flying.test.db;

import java.util.List;

/**
 * Created by liwu.shu on 2017/5/19.
 */

public interface IDaoHelperInterface {
    public <T> void addData(T t);
    public void deleteData(String id);
    public <T> T getDataById(String id);
    public List getAllData();
    public boolean hasKey(String id);
    public long getTotalCount();
    public void deleteAll();
}
