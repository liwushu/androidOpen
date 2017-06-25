package com.tcl.update.context;

public enum DirType {
    root, log, image, apps, cache, crash, app_self;

    public int value() {
        return ordinal() + 1;
    }
}
