package com.tcl.update.db;

import java.util.List;

public interface IDataProvider<T> {

    /**
     * 获取当前所有数据
     * 
     * @return
     */
    public List<T> getAllData();

    /**
     * 根据ID查找记录
     * 
     * @param id
     * @return
     */
    public T getDataById(String id);

    /**
     * 保存
     * 
     * @param dataList
     */
    public void saveOrUpdateAll(List<T> dataList);

    /**
     * 保存
     * 
     * @param dataList
     */
    public void saveOrUpdate(T dataList);

    /**
     * 删除全部
     * 
     */
    public void deleteAll();

    /**
     * 根据id删除
     * 
     * @param id
     */
    public void deleteById(String id);
}
