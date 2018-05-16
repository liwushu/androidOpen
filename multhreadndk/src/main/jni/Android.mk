LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := JNIThreads
LOCAL_SRC_FILES := thread/JNI_Threads.c
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_LDLIBS    := -llog
LOCAL_MODULE    := Injectso
LOCAL_SRC_FILES := Injectso/Injectso.cpp
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := inject
LOCAL_SRC_FILES := Inject/inject.c
LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE
include $(BUILD_EXECUTABLE)