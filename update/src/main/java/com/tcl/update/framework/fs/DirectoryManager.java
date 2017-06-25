/*
 * Copyright (c) 1998-2012 TENCENT Inc. All Rights Reserved.
 * 
 * FileName: DirectoryManager.java
 * 
 * Description: 目录管理类文件
 * 
 * History: 1.0 devilxie 2012-09-05 Create
 */
package com.tcl.update.framework.fs;

import android.util.SparseArray;

import com.tcl.update.framework.util.CollectionUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;

/**
 * 目录管理，负责检索每 一个目录，同时负责校验与创建。对需要清理的目录进行相关清理
 *
 * @author devilxie
 * @version 1.0
 */
public final class DirectoryManager implements IDirectoryCreator {

    /**
     * 目录上下文
     */
    private com.tcl.update.framework.fs.DirectoryContext context;
    /**
     * 目录管理集合
     */
    private SparseArray<File> dirArray;

    public DirectoryManager(com.tcl.update.framework.fs.DirectoryContext context) {
        this.context = context;
        this.context.setDirectoryCreator(this);
        this.dirArray = new SparseArray<File>(10);
    }

    /**
     * 校验并创建文件系统所有相关的目录，同时清理需要清除的缓存
     *
     * @return 返回操作结果。
     */
    public boolean buildAndClean() {
        return context.buildAllAndClean();
    }

    /**
     * 获取指定类型的目录
     *
     * @param type 目录类型，可自由定义。必须是大于0的整数
     * @return 返回代表指定目录的文件实例，null表示没有找到
     */
    public File getDir(int type) {
        if (type < 0 || dirArray.size() == 0) return null;
        File file = dirArray.get(type);
        return file;
    }

    /**
     * 获取指定类型的目录的完整路径
     *
     * @param type 目录类型，可自由定义。必须是大于0的整数
     * @return 返回代表指定目录的绝对路径。找不到时返回空字符串"".
     */
    public String getDirPath(int type) {
        File file = getDir(type);
        if (file == null) return null;

        return file.getAbsolutePath();
    }

    private void cleanCache(File dir, final long expired) {
        File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()) {
                    return CacheChecker.expired(pathname, expired);
                }
                return false;
            }
        });

        if (CollectionUtils.isEmpty(files)) return;

        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    public boolean createDirectory(com.tcl.update.framework.fs.Directory directory, boolean cleancache)
            throws IOException {
        boolean ret = true;
        String path = null;
        com.tcl.update.framework.fs.Directory parent = directory.getParent();
        // 这是一个根目录
        if (parent == null) {
            path = directory.getPath();
        } else {
            File file = getDir(parent.getType());
            path = file.getAbsolutePath() + File.separator + directory.getPath();
        }

        // 先检测当前目前是否存在
        File file = new File(path);
        if (!file.exists()) {
            ret = file.mkdirs();
        } else if (cleancache && directory.isForCache()) {
            cleanCache(file, directory.getExpiredTime());
        }

        if (!ret) {
            return false;
        }

        dirArray.put(directory.getType(), file);
        // 再检测各子目录是否存在
        Collection<com.tcl.update.framework.fs.Directory> children = directory.getChildren();
        if (children != null) {
            for (Directory dir : children) {
                if (!createDirectory(dir, true)) {
                    return false;
                }
            }
        }

        return ret;
    }

}
