package com.tcl.update;

import com.tcl.update.db.UpdateInfo;

/**
 * Created by fanyang.sz on 2017/2/13.
 */

public interface DownloadListener {

    void onCompleted(UpdateInfo info);

}
