package cc.omusic.fingerprintver1lib;
import android.util.Log;


public class FingerprintVer1 {
	
	private final float normalizingValue = Short.MAX_VALUE;

	public native String fingerprint(float data[], int numSamples);
	static 
	{	
        try{
			//shared lib called libfingerprint-jin.so
        	Log.i("lib","try to load library");
        	System.loadLibrary("fingerprint-jin");
		}catch( UnsatisfiedLinkError use ){
			Log.e("lib", "could not load native library fingerprint-jni");
		}
    }
	
	
	
	
	/**
	 * Invoke the fingerprint native library and generate the fingerprint code.<br>
	 * REQUIRES PCM encoded audio with the following parameters:<br>
	 * Frequency: 44,100 Hz khz<br>
	 * Data: stereo - PCM enconded float array
	 * 
	 * @param data PCM encoded data as floats [-1, 1]
	 * @param numSamples number of PCM samples at 44,100 Hz
	 * @return The generated fingerprint 
	 */
	public String generate(float data[], int numSamples)
	{
		return fingerprint(data, numSamples);
	}
	
	
	/**
	 * Invoke the fingerprint native library and generate the fingerprint code.<br>
	 * Since echoprint requires the audio data to be an array of floats in the<br>
	 * range [-1, 1] this method will normalize the data array transforming the<br>
	 * 16 bit signed shorts into floats. 
	 * 
	 * @param data PCM encoded data as shorts
	 * @param numSamples number of PCM samples at 44,100 Hz
	 * @return The generated fingerprint 
	 */
	public String generate(short data[], int numSamples)
	{
		// echoprint expects data as floats, which is the native value for 
		// core audio data, and I guess ffmpeg
		// Android records data as 16 bit shorts, so we need to normalize the
		// data before sending it to echoprint
		float normalizeAudioData[] = new float[numSamples];
		for (int i = 0; i < numSamples - 1; i++) 
			normalizeAudioData[i] = data[i] / normalizingValue;
		
		return this.fingerprint(normalizeAudioData, numSamples);
	}
}
	