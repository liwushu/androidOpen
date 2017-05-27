package com.flying.test.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.flying.test.DaoMaster;
import com.flying.test.DaoSession;
import com.flying.test.Note;
import com.flying.test.NoteDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by liwu.shu on 2017/5/19.
 */

public class NodeDaoHelper implements IDaoHelperInterface {

    private NoteDao noteDao;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    //构造函数 参数是SQLiteDatabase 需要从OpenHelper（在该类的构造方法需要设置数据库名，版本，）得到一个可读写的数据源
    public NodeDaoHelper(SQLiteDatabase db) {
        if (db != null) {
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            noteDao = daoSession.getNoteDao();
        }
    }
    @Override
    public <T> void addData(T t) {
        if (noteDao != null && t != null)
            noteDao.insertOrReplace((Note) t);

    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    @Override
    public void deleteData(String id) {
        if (noteDao != null && !TextUtils.isEmpty(id))
            noteDao.deleteByKey(Long.valueOf(id));
    }

    @Override
    public <T> T getDataById(String id) {
        if (noteDao != null && !TextUtils.isEmpty(id))
            return (T) noteDao.load(Long.valueOf(id));
        return null;
    }

    @Override
    public List getAllData() {
        if (noteDao != null)
            return noteDao.loadAll();
        return null;
    }

    @Override
    public boolean hasKey(String id) {
        if (noteDao != null || TextUtils.isEmpty(id))
            return false;
        QueryBuilder<Note> qb = noteDao.queryBuilder();
        qb.where(NoteDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0 ? true : false;
    }

    @Override
    public long getTotalCount() {
        if (noteDao == null)
            return 0;
        QueryBuilder<Note> qb = noteDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if (noteDao != null)
            noteDao.deleteAll();
    }
}
