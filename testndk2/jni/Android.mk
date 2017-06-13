LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := myTest
LOCAL_SRC_FILES := com_flying_testndk2_jni_TestJni.c

LOCAL_LDLIBS += -lm -llog

include $(BUILD_SHARED_LIBRARY)

