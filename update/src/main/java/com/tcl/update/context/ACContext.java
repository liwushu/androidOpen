package com.tcl.update.context;

import android.content.Context;

import com.tcl.update.framework.fs.DirectoryManager;
import com.tcl.update.utils.PathDirName;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ACContext {

    private final static String GC_ROOT_FOLDER = "updateSdk";
    private static ACContext _instance = null;
    private static Context mContext;
    private static String ua = null;

    public static boolean initInstance(Context context, boolean must) {
        if (_instance == null) {
            ACContext gcContext = new ACContext(context);

            _instance = gcContext;
            mContext = context;
            return gcContext.init();
        } else if (must) {
            _instance = null;
            ACContext gcContext = new ACContext(context);
            _instance = gcContext;
            mContext = context;
            return gcContext.init();
        }

        return true;
    }

    private Map<String, Object> objsMap;
    DirectoryManager mDirectoryManager = null;

    public ACContext(Context context) {
        objsMap = new HashMap<String, Object>();
    }

    private boolean init() {
        DirectoryManager dm = new DirectoryManager(new ACDirectoryContext(getApplicationContext(), GC_ROOT_FOLDER));
        boolean ret = dm.buildAndClean();
        if (!ret) {
            return false;
        }

        registerSystemObject(PathDirName.DIR_MANAGER, dm);
        mDirectoryManager = dm;

        return ret;
    }

    public static DirectoryManager getDirectoryManager() {
        if (_instance == null) return null;

        return ((ACContext) _instance).mDirectoryManager;
    }

    public static File getDirectory(DirType type) {
        DirectoryManager manager = getDirectoryManager();
        if (manager == null) return null;

        return manager.getDir(type.value());
    }

    public static String getDirectoryPath(DirType type) {
        File file = getDirectory(type);
        if (file == null) {
            File dir = ACContext.getContext().getExternalFilesDir(type.name());
            if (dir != null) {
                return dir.getAbsolutePath();
            } else {
                return ACContext.getContext().getFilesDir().getAbsolutePath();
            }
        }
        return file.getAbsolutePath();
    }

    public void registerSystemObject(String name, Object obj) {
        objsMap.put(name, obj);
    }

    public Object getSystemObject(String name) {
        return objsMap.get(name);
    }

    public static ACContext getInstance() {
        return _instance;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public final static String getPackageName() {
        return mContext.getPackageName();
    }

    private static Context getContext() {
        return mContext;
    }

}
