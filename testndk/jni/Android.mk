LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := mytest
LOCAL_SRC_FILES := com_flying_testndk_MainActivity.c

LOCAL_LDLIBS += -lm -llog

LOCAL_PROGUARD_ENABLED:= disabled

include $(BUILD_SHARED_LIBRARY)
