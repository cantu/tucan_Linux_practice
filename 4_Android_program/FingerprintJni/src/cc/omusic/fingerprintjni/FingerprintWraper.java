package cc.omusic.fingerprintjni;
import java.io.File;

import cc.omusic.decorder.*;

import android.util.Log;

public class FingerprintWraper {
	
	String TAG = "FingerprintWraper";
	Decoder decoder = null;

	
	
	/**
	 * Invoke the fingerprint native library and generate the fingerprint code.<br>
	 * Since echoprint requires the audio data to be an array of floats in the<br>
	 * range [-1, 1] this method will normalize the data array transforming the<br>
	 * 16 bit signed shorts into floats. 
	 * 
	 * @param data PCM encoded data as shorts
	 * @param numSamples number of PCM samples at 44,100 Hz, 2 channel.
	 * @return The generated fingerprint 
	 */
	/**
	 * @param in_object: wav data provider, call object's getWavData() method.
	 * @param sampleRate
	 * @param numChannels
	 * @return
	 */
	
	
	public native byte[] fingerprint( Decoder in_object, int sampleRate, int numChannels);

	
    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
	
	static 
	{	
        try{
			//shared lib called libfingerprint-jin.so
        	Log.i("lib","try to load library : AndroidFingerprint ");
        	//System.loadLibrary("fingerprint-jin");
        	System.loadLibrary("AndroidFingerprint");
		}catch( UnsatisfiedLinkError use ){
			Log.e("lib", "could not load native library AndroidFingerprint");
		}
    }
	
	
	
	public byte[] generate( File music_file )
	{
		if( music_file != null && music_file.exists()){

			Log.d(TAG, "select music file:" + music_file.getAbsolutePath());
			String music_end = music_file.getName().substring( music_file.getName().lastIndexOf(".") + 1,
					music_file.getName().length()).toLowerCase();
			if( music_end.equals("mp3")){
				decoder = new Mpg123Decoder( music_file );
				Log.d( TAG, "music file is mp3, initial a Mpg123Decoder class");
			}
			else if( music_end.equals("ogg") ){
				decoder = new VorbisDecoder( music_file );
				Log.d( TAG, "music file is ogg, initial a VorbisDecoder class");
			}
			else if( music_end.equals("wav")){
				decoder = new WavDecoder( music_file );
				Log.d( TAG, "music file is wav, initial a WavDecoder class");
			}
			else
			{
				Log.e(TAG,"music file is not mp3/ogg/wav, can not support!");
			}
			
			Log.d( TAG, "channels:" + decoder.getChannels() );
			Log.d( TAG, "rate:" + decoder.getRate() );
			Log.d( TAG, "length:" + decoder.getLength() );
			

			byte[] fingerprint = fingerprint( decoder, decoder.getRate(), decoder.getChannels());
			decoder.dispose();
			Log.d( TAG, "dispose Decoder class");
			
			return fingerprint;
			
		}else{
			Log.e(TAG,"select file is invalid");
			return null;
		}

		
		
		
		
/*		
		//Construct a wav reader to jni lib.
		WavReader wav_object = new WavReader( music );
		return fingerprint( wav_object,
						    wav_object.getSampleRate(),
							wav_object.getChannels()   );
		
*/	
		
	}

	
	
	/*
	public byte[] generate(int sampleRate, int numChannels)
	{
		if ( sampleRate == 44100  && numChannels == 2){
			return fingerprint( sampleRate, numChannels);
		}
		else{
			Log.e(TAG, " input pcm invalide");
			return null;
		}
	}
	*/

	/*
	public byte[] generate(short data[], int numSamples)
	{
		return fingerprint(data, numSamples);
	}
	
	
	public byte[] generate(int data[], int numSamples)
	{
		// echoprint expects data as floats, which is the native value for 
		// core audio data, and I guess ffmpeg
		// Android records data as 16 bit shorts, so we need to normalize the
		// data before sending it to echoprint
		short buffer[] = new short[numSamples];
		for( int i=0; i<numSamples; i++ )
		{
			buffer[i] = (short)data[i];
		}

		return fingerprint(buffer, numSamples);
	}
	*/

	
	
}
