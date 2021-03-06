/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class cc_omusic_decorder_VorbisDecoder */

#ifndef _Included_cc_omusic_decorder_VorbisDecoder
#define _Included_cc_omusic_decorder_VorbisDecoder
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    openFile
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_cc_omusic_decorder_VorbisDecoder_openFile
  (JNIEnv *, jclass, jstring);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    getNumChannels
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_getNumChannels
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    getRate
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_getRate
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    getLength
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_cc_omusic_decorder_VorbisDecoder_getLength
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    readSamples
 * Signature: (J[SII)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_readSamples
  (JNIEnv *, jclass, jlong, jshortArray, jint, jint);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    skipSamples
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_skipSamples
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    closeFile
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_cc_omusic_decorder_VorbisDecoder_closeFile
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    seekable
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_seekable
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    tellTime
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_cc_omusic_decorder_VorbisDecoder_tellTime
  (JNIEnv *, jclass, jlong);

/*
 * Class:     cc_omusic_decorder_VorbisDecoder
 * Method:    timeSeek
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_cc_omusic_decorder_VorbisDecoder_timeSeek
  (JNIEnv *, jclass, jlong, jfloat);

#ifdef __cplusplus
}
#endif
#endif
