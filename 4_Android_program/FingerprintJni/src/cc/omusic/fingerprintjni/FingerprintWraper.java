package cc.omusic.fingerprintjni;
import android.util.Log;



public class FingerprintWraper {
	
	String TAG = "FingerprintWraper";

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
	
	
	
	public native byte[] fingerprint( WavReader in_object, int sampleRate, int numChannels);

	
    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
	
	static 
	{	
        try{
			//shared lib called libfingerprint-jin.so
        	Log.i("lib","try to load library");
        	//System.loadLibrary("fingerprint-jin");
        	System.loadLibrary("AndroidFingerprint");
		}catch( UnsatisfiedLinkError use ){
			Log.e("lib", "could not load native library AndroidFingerprint");
		}
    }
	
	
	public byte[] generate( )
	{
		//Construct a wav reader to jni lib.
		WavReader wav_object = new WavReader();
		return fingerprint( wav_object,
						    wav_object.getSampleRate(),
							wav_object.getChannels()   );
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
