LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := fingerprint_decoder_jni
LOCAL_CFLAGS := 
LOCAL_SRC_FILES := test.c

#LOCAL_SHARED_LIBRARIES := libavcodec libavformat libavutil 
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/../ffmpeg/build/ffmpeg/$(TARGET_ARCH_ABI)/include 
LOCAL_LDLIBS := -llog -lm -lz

include $(BUILD_SHARED_LIBRARY)