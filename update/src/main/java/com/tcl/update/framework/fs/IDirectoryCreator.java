package com.tcl.update.framework.fs;

import java.io.IOException;

/**
 * Created by wenbiao.xie on 2015/9/15.
 */
public interface IDirectoryCreator {
    /**
     * 创建指定目录，并根据要求清理过期缓存
     * 
     * @param directory 待创建目录， 如果已存在，不会重新创建
     * @param cleancache 是否清理过期缓存标识
     * @return 创建是否成功
     */
    boolean createDirectory(com.tcl.update.framework.fs.Directory directory, boolean cleancache) throws IOException;
}
