#get current work directory
LOCAL_PATH:= $(call my-dir)

#clear all the variabre of make file register
include $(CLEAR_VARS)

#module name to be generate
LOCAL_MODULE := Fingerprint

LOCAL_CFLAGS := 
LOCAL_CFLAGES := -I$(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/lib

#source files
LOCAL_SRC_FILES := FingerprintAndroid.c

LOCAL_SHARED_LIBRARIES := libavcodec libavformat libavutil
#LOCAL_STATIC_LIBRARIES := libavcodec libavformat libavutil 
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/include

LOCAL_LDLIBS := -L/home/tusion/android-ndk/android-ndk-r8e/platforms/android-14/arch-arm/usr/lib/ \
				-L$(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/lib \
				-lavcodec \
				-lavformat \
				-lavutil \
				-llog \
				-lz \
				-ldl \
				-lgcc 
#LOCAL_LDLIBS += -llog -lm -lz -ldl -lgcc -lbz2
#include $(PREBUILD_SHARED_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
