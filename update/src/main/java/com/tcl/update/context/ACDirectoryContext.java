package com.tcl.update.context;

import android.content.Context;

import com.tcl.update.framework.fs.Directory;
import com.tcl.update.framework.fs.DirectoryContext;
import com.tcl.update.framework.util.TimeConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wenbiao.xie on 2015/9/15.
 */
public class ACDirectoryContext extends DirectoryContext {


    public ACDirectoryContext(Context context, String externalPath) {
        super(context, externalPath);
    }

    @Override
    protected Collection<Directory> initDirectories() {
        List<Directory> children = new ArrayList<Directory>();

        for (DirType dirType : DirType.values()) {
            Directory dir = newDirectory(dirType);
            children.add(dir);
        }

        return children;
    }

    private Directory newDirectory(DirType type) {
        Directory child = new Directory(type.toString(), null);
        child.setType(type.value());
        if (type.equals(DirType.cache)) {
            child.setForCache(true);
            child.setExpiredTime(TimeConstants.ONE_DAY_MS);
        }

        return child;
    }
}
