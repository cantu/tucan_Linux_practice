
#include <android/log.h>
#include <string.h>
//#include "cc_omusic_fingerprintjni_FingerprintWraper.h"
//#include "./fingerprint_ver_1/fingerprint.h"
#include <stdio.h>
#include <stdlib.h>
#include "./fingerprint_ver_1/fooid.h"
#include <jni.h>
#define  LOG_TAG    "And..F.p.c"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

jobject GetInstance( JNIEnv *env, jclass obj_class);

// in src dir use: javah cc.omusic.fingerprintjni.FingerprintWraper
// this string is gernerate from head file, as last step use javah oo.omusic.***.Fingerprrint
jbyteArray Java_cc_omusic_fingerprintjni_FingerprintWraper_fingerprint
//JNIEXPORT jbyteArray JNICALL Java_cc_omusic_fingerprintjni_FingerprintWraper_fingerprint
(JNIEnv *env, jobject thiz, jobject wavReaderObj, jint sampleRate, jint numChannels)

{	// jnit directly use in there

	//jobject wavObj;
	jclass  wavClass = 0;
	jmethodID getWavMethod = 0;

	int result= -1;
    int centiseconds = 0;
    int frames_read = 0;

	LOGD("sampleRate= %d, numChannels= %d \n", sampleRate, numChannels);
	/**
	 * native call java method, reference <core java V2> charpter 12
	 */
	//jniClass  = (*env)->FindClass( env, "Lcc/omusic/fingerprintjni/WavReader;");
	//jniObj    = GetInstance( env, jniClass );
	wavClass  = (*env)->GetObjectClass( env, wavReaderObj );
	getWavMethod = (*env)->GetMethodID( env, wavClass, "getWavData", "([S)I");
	//wavObj    = (*env)->NewObject( env, wavClass, jniMethod);


	//initial t_fooid struct, reasample wav file to 8000hz.
    t_fooid * fooid = fp_init(sampleRate, numChannels);

    //short * pcm = malloc(sizeof(short) * numChannels* sampleRate);
	int len = (int)(numChannels * sampleRate ); // every chip is 1s

	jshortArray pcmjArray = (*env)->NewShortArray( env, len  );
	short *pcm = NULL;
	LOGD("len= %d \n", len);

   //while (1)
    //for( int i=0; i<10; i++)
	do
    {
    	//return the number of  items read, every loop read 1s data from wav file
    	frames_read = (*env)->CallIntMethod( env, wavReaderObj, getWavMethod, pcmjArray);
    	//LOGD("Get pcmjArray, frames_read=%d, pcmjArray length=%d " ,
    	//		frames_read, (*env)->GetArrayLength( env, pcmjArray) );

        if (frames_read != 0)
        {
        	pcm =(short *)((*env)->GetShortArrayElements(env, pcmjArray, 0));
        	//(*env)->SetShortArrayRegion( env,pcmjArray, 0, len, (jbyte*)pcm );
        	//LOGD("get pcm form pcmjArray");

    		//1/100 seconds
            centiseconds += 100 * numChannels* sampleRate/ (frames_read* numChannels);
    		LOGD("centiseconds:%d, frames_read:%d \n", centiseconds, frames_read );

            result = fp_feed_short(fooid, pcm, frames_read * numChannels);

            if (result < 0)
            {
            	LOGE("fp_feed_short()Error!\n");
                break;
            }
    		//(*env)->ReleaseShortArrayElements(env, pcmjArray, pcm, 0);
        }
        else
        {
        	LOGE("frames_read = %d, error!\n",frames_read);
        	break;
        }

    }while( centiseconds < 10000); //get the head 100s music data

    (*env)->ReleaseShortArrayElements(env, pcmjArray, pcm, 0);
    /*
     do{
    	//return the number of  items read, every loop read 1s data from wav file
		(*env)->CallIntMethod( env, wavReaderObj, getWavMethod, pcmjArray);
		short *pcm =(short *)((*env)->GetShortArrayElements(env, pcmjArray, 0));
		result= fingerprint ( pcm, sampleRate, numChannels, fp);
		(*env)->ReleaseShortArrayElements(env, pcmjArray, pcm, 0);
	}while( result < 1 );
     */


	//fp_size == 424
	unsigned char * fp =  malloc(fp_getsize(fooid));


    //fp_size == 424
	result = fp_calculate(fooid, centiseconds, fp);
	/*
	if (result < 0)
	{
		LOGE("Failed to calculate fingerprint\n");
	}
	else
	{
		for ( int i = 0; i < fp_getsize(fooid); i++)
		{
			LOGD("%02x ", (int) fp[i]);
		}
     }
	*/


	jbyte *by = (jbyte*)fp;
	jbyteArray jarray =  (*env)->NewByteArray(env,424);
	(*env)->SetByteArrayRegion( env,jarray, 0, 424, (jbyte*)fp );


	free(fp);
	fp_free(fooid);
	LOGD("complete !!!\n");
	/*
	LOGD("begin into AndroidFingerprint () \n");
    // get the contents of the java array as native floats
	short *data = (short *)((*env)->GetShortArrayElements(env, pcmData, 0));
	LOGD("start fingerprint sub funtion in  jni lib\n");

    // release the native array as we're done with them
	(*env)->ReleaseShortArrayElements(env, pcmData, data, 0);
	*/
    return jarray;
}


/*
jobject GetInstance( JNIEnv *env, jclass obj_class)
{
	jmethodID construction_id = (*env)-> GetMethodID( env, obj_class,"<init>", "()V");

	return (*env)-> GetMethodID( env, obj_class, construction_id );
}
*/

