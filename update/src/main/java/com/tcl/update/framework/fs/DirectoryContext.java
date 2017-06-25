/*
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 * 
 * FileName: DirectroyContext.java
 * 
 * Description: 目录上下文类文件
 * 
 * History: 1.0 devilxie 2012-09-05 Create
 */
package com.tcl.update.framework.fs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.tcl.update.framework.log.NLog;
import com.tcl.update.framework.util.AndroidNewApi;
import com.tcl.update.framework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * 目录上下文类，主要提供上层进行具体目录管理集合的定义的扩展
 * 
 * @author devilxie
 * @version 1.0
 */
public abstract class DirectoryContext {
    private final static String BASE_DATA_DIRECTORY = "app_dir_root";
    public static final int APP_ROOT_DIR = 0;
    private IDirectoryCreator creator;
    private Context mContext;
    private String mExternalRootPath;

    public DirectoryContext(Context context, String externalPath) {
        this.mContext = context.getApplicationContext();
        this.mExternalRootPath = externalPath;
    }

    public void setDirectoryCreator(IDirectoryCreator creator) {
        this.creator = creator;
    }

    protected abstract Collection<com.tcl.update.framework.fs.Directory> initDirectories();

    private boolean isExternalSDMounted() {
        return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }

    private long getExternalSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = AndroidNewApi.getBlockSizeLong(stat);
        long availableBlocks = AndroidNewApi.getAvailableBlocks(stat);;
        long availableSize = availableBlocks * blockSize;
        return availableSize;
    }

    private boolean hasEnoughSpace() {
        long size = getExternalSDFreeSize();
        if (size <= 0) return false;

        size >>= 20;
        return size >= 5;
    }

    boolean buildAllAndClean() {
        final IDirectoryCreator directoryCreator = creator;
        if (directoryCreator == null)
            throw new IllegalStateException("cannot build all directories, lack of directory creator");

        boolean ret = false;
        Collection<Directory> children = initDirectories();
        Directory directory = null;
        do {
            if (!isExternalSDMounted() || !hasEnoughSpace()) {
                break;
            }
            String rootPath = null;
            int permission = mContext.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.os.Process.myPid(), android.os.Process.myUid());
            if (permission == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < 23) {
                rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                        + mExternalRootPath;
            } else {
                // File[] files = ContextCompat.getExternalFilesDirs(mContext, mExternalRootPath);
                // if (files == null || files.length == 0) break;

                File path = mContext.getExternalFilesDir(mExternalRootPath);
                if (path != null && path.exists()) {
                    rootPath = path.getAbsolutePath();
                } else {
                    rootPath = mContext.getFilesDir().getAbsolutePath();
                }
            }

            directory = new Directory(rootPath, null);
            directory.setType(APP_ROOT_DIR);

            if (!CollectionUtils.isEmpty(children)) {
                directory.addChildren(children);
            }

            try {
                ret = directoryCreator.createDirectory(directory, true);
                return ret;
            } catch (IOException e) {
                NLog.printStackTrace(e);
            }

        } while (false);

        File file = mContext.getDir(BASE_DATA_DIRECTORY, Context.MODE_PRIVATE);
        directory = new Directory(file.getAbsolutePath(), null);
        directory.setType(APP_ROOT_DIR);

        if (!CollectionUtils.isEmpty(children)) {
            directory.addChildren(children);
        }

        try {
            ret = directoryCreator.createDirectory(directory, true);
            return ret;
        } catch (IOException e) {
            NLog.printStackTrace(e);
            directory.removeAll();
            return false;
        }
    }
}
