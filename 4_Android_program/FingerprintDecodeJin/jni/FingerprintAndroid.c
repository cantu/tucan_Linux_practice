#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#include "./libavcodec/avcodec.h"
#include "./libavformat/avformat.h"
#include "./libavutil/fifo.h"
//#include "./libavutil/avutil.h"
//#include "./libswscale/swscale.h"
#include "./libfooid/fooid.h"

#include <jni.h>
#include <android/log.h>
#define  LOG_TAG    "FingprintAndroid.c"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/*
 * Class:     cc_omusic_FingerprintDecodeJin_FingerprintWraper
 * Method:    getFingerprint
 * Signature: ()[B
 */
//JNIEXPORT jbyteArray JNICALL Java_cc_omusic_FingerprintDecodeJin_FingerprintWraper_getFingerprint
// (JNIEnv *, jobject);
jbyteArray Java_cc_omusic_FingerprintDecodeJin_FingerprintWraper_getFingerprint
(JNIEnv *env, jobject thiz)
{
	LOGD("go into jni function\n");
	//char * filename = "/storage/sdcard0/omusic/1490278.mp3";
	//char * filename = "/storage/sdcard0/omusic/1490278.wav";

	//char * filename = "/mnt/sdcard/omusic/test.mp3";
	//char * filename = "/storage/sdcard0/omusic/test.mp3";
	char * filename = "/storage/sdcard0/omusic/test.wav";
	LOGD("open file: %s \n", filename );
	//register encoders&decoders
	av_register_all();
	AVFormatContext * pFormatCtx = avformat_alloc_context();   // file's continer of contex.

	//open input file
	if (avformat_open_input(&pFormatCtx, filename, 0, NULL) != 0)
	{
		LOGD("could not open file");
		exit(1);
	}
	if (av_find_stream_info(pFormatCtx) < 0) // 检查在文件中的流的信息
	{
		LOGD("error! can not find the stream's info \n");
		exit(1);
	}

	av_dump_format(pFormatCtx, 0, filename, 0); // 显示pfmtctx->streams里的信息


	// find the first audio stream
	int audioStream = -1;
	for (int i = 0; i < pFormatCtx->nb_streams; ++i)  //找到音频、视频对应的stream
	{
		if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO)
		{
			audioStream = i;
			break;
		}
	}
	if (audioStream == -1) //if could not find any audio.
	{
		LOGD("could not find any  audio stream in input file\n");
		exit(1);
	}

	// get audio decoder's context.
	AVCodecContext * pCodecCtx = NULL;
	pCodecCtx= pFormatCtx->streams[audioStream]->codec;

	// find correct decoder base on the context.
	AVCodec * pCodec = NULL ;
	pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
	if (pCodec == NULL)
	{
		LOGD("error! no Codec found \n" );
		exit(1);
	}

	// Inform the codec that we can handle truncated bitstreams
	// bitstreams where frame boundaries can fall in the middle of packets
	if (pCodec->capabilities & CODEC_CAP_TRUNCATED)
	{
		pCodecCtx->flags |= CODEC_CAP_TRUNCATED;
	}

	// 打开解码器
	if (avcodec_open(pCodecCtx, pCodec) < 0)
	{
		LOGD("error! avcodec_open failed. \n ");
		exit(1);
	}

	LOGD(" 	[bit_rate]    = %d \n", pCodecCtx->bit_rate);
	LOGD(" 	[sample_rate] = %d \n", pCodecCtx->sample_rate);
	LOGD(" 	[channels]    = %d \n", pCodecCtx->channels);
	LOGD(" 	[code_name]   = %s \n", pCodecCtx->codec->name);
	LOGD(" 	[block_align] = %d \n",pCodecCtx->block_align);
	LOGD("  [sample_fmt]  = %d \n", pCodecCtx->sample_fmt );

	//
	AVPacket packet;
	AVFrame * pFrame = NULL;
	int frame_num = 2 * pCodecCtx->sample_rate * pCodecCtx->channels;
	//short inbuf [frame_num];
	int count =0;
	int read_seconds = 0; //fooid need data in 1 second chips.
	int gotFrame = 0;
	int out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE;
	//int buf_size = AVCODEC_MAX_AUDIO_FRAME_SIZE;
	//uint8_t inbuf [frame_num];   //typedef unsigned char uin8_t
	uint8_t *inbuf;   //typedef unsigned char uin8_t
	inbuf = (uint8_t * )malloc( frame_num * sizeof( uint8_t ) );
	//LOGD("size of short: %d, size of uint8_t:%d \n", sizeof(short), sizeof(uint8_t) );
	AVFifoBuffer *fifo;
//	fifo = av_fifo_alloc( AVCODEC_MAX_AUDIO_FRAME_SIZE * 2);
//	fifo = av_fifo_alloc( frame_num * 2 );
	fifo = av_fifo_alloc( frame_num );
	av_init_packet( &packet );

	LOGD(" before decode, out_size=%d,frame_num=%d,  MAX_FRAME_SIZE=%d\n",\
			out_size, frame_num, AVCODEC_MAX_AUDIO_FRAME_SIZE);

	// initial for fingerprint libfooid
	t_fooid *fooid = fp_init( pCodecCtx->sample_rate, pCodecCtx->channels );
	//LOGE("fooid address %0x \n", fooid);
	int result = -1;

	/**
	* test a mp3 file,
	*/
	if( !pFrame )
	{
		if( ! ( pFrame = avcodec_alloc_frame() ) )
			{
				LOGE("while allocate memory for Frame.\n");
				exit(1);
			}
	} //avcodec_get_frame_defaults(pFrame ); //alredy add in avcodec_alloc_frame()

	while(av_read_frame(pFormatCtx, &packet) >= 0) //pFormatCtx中调用对应格式的packet获取函数
	{
		if (packet.stream_index == audioStream) //Detect read packet is audio stream?
		{
			out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE ;
			while ( packet.size > 0)
			{  	// decode audio
				//int len = avcodec_decode_audio3(pCodecCtx, (short *)inbuf, &out_size, &packet);
				int len = avcodec_decode_audio4(pCodecCtx, pFrame, &gotFrame, &packet);
				if( len < 0 )
				{
					LOGE( "Error while decoding\n");
					exit(1);
				}
			   if ( gotFrame )
				{  //if a frame has been decoded, output it
					out_size = av_samples_get_buffer_size( NULL, pCodecCtx->channels,
																pFrame->nb_samples,
																pCodecCtx->sample_fmt, 1 );
					av_fifo_generic_write( fifo, pFrame->data[0], out_size, NULL );
				}

				while( av_fifo_size( fifo ) >= frame_num )
				{
					read_seconds++ ;
				//	LOGD("  seconds=%d, fifo_size=%d\n", read_seconds, av_fifo_size(fifo) );

					av_fifo_generic_read( fifo, inbuf, frame_num, NULL );
					//fwrite(inbuf, 1, frame_num, fp);  //write pcm to file: ***.pcm
					result = fp_feed_short( fooid,(short * ) inbuf, (frame_num/2) );
					if( result < 0 )
					{
						LOGE(" error in calculate fingerprint process!\n" );
					}
				}
				packet.size -= len;
				packet.data += len;
				packet.dts =
				packet.pts = AV_NOPTS_VALUE;
			}
		}
		if( read_seconds >= 100 )
		{
			break;
		}
	}

	free( inbuf );
//	fclose(fp);
	avcodec_close(pCodecCtx);
	avformat_close_input(&pFormatCtx);

	unsigned char * fingerprint = malloc( fp_getsize (fooid ) );
	//result = fp_calculate( fooid, read_seconds*100, fingerprint );
	result = fp_calculate( fooid, read_seconds*100, fingerprint );
	if( result < 0 )
	{
		LOGE("error, failed to calculate fingerprint!\n" );
	}

	jbyte *by = (jbyte*)fingerprint;
	jbyteArray jarray =  (*env)->NewByteArray(env,424);
	(*env)->SetByteArrayRegion( env,jarray, 0, 424, (jbyte*)fingerprint );
	free(fingerprint);
	fp_free(fooid);

    return jarray;

}
