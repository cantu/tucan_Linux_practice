#get current work directory
LOCAL_PATH:= $(call my-dir)

#clear all the variabre of make file register
include $(CLEAR_VARS)

#module name to be generate
LOCAL_MODULE := hello
#libs/armlib/libhello.so

LOCAL_CFLAGS := 
#LOCAL_CFLAGES := -I$(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/lib

#source files
LOCAL_SRC_FILES := main.c
					
#LOCAL_SHARED_LIBRARIES := libavcodec libavformat libavutil 
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/include

#LOCAL_LDLIBS := -L$(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/lib
LOCAL_LDLIBS += -llog
#LOCAL_LDLIBS += -lm
#LOCAL_LDLIBS += -lz

#include $(PREBUILD_SHARED_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
