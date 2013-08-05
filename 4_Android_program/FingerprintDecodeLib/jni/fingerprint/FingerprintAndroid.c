#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>


#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>





//#include "cc_omusic_Fingerprint_decoder_jni_FingerprintWraper.h"

#include <android/log.h>
#define  LOG_TAG    "test.c"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

//jbyteArray Java_cc_omusic_fingerprintjni_FingerprintWraper_fingerprint
//JNIEXPORT jbyteArray JNICALL Java_cc_omusic_fingerprintjni_FingerprintWraper_fingerprint
//(JNIEnv *env, jobject thiz, jobject DecoderObj, jint sampleRate, jint numChannels)


/*
 * Class:     cc_omusic_Fingerprint_decoder_jni_FingerprintWraper
 * Method:    getVersionOfDecoder
 * Signature: (V)Ljava/lang/String;
 */

jstring Java_cc_omusic_FingerprintDecoderLib_FingerprintWraper_getVersionOfDecoder
(JNIEnv *env, jobject thiz)
{

	//printf(" this ffmpeg version: %d \n", avcodec_version() );
	LOGD("go into jni function\n");
	char str[25];
	//sprintf(str, "%d", avcodec_version());
	//return (*env)->NewStringUTF(env,str);
	return (*env)->NewStringUTF(env,"hello from jni");
}