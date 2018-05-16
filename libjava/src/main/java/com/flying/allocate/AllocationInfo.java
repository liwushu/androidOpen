package com.example.allocate;

public class AllocationInfo {
    public long totalSize;
    public long size;
    public long memorySize;
    public String beginStackSoFullName;

    public void reset() {
        beginStackSoFullName = null;
        totalSize = 0;
        memorySize = 0;
        size = 0;
    }
}
