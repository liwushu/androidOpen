LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := JNIThreads
LOCAL_SRC_FILES := JNI_Threads.c
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)