package com.tcl.update;

/**
 * Created by fanyang.sz on 2017/2/13.
 */

public interface InstallListener {

    void onSuccess();

    void onFailed(int code, String message);

}
