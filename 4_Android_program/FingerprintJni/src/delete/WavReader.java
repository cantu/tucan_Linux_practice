package delete;

import java.io.File;
import java.io.IOException;


import android.util.Log;

public class WavReader {

	String TAG = "wavreader";
	boolean isWavInit = false;
	int numChannels = 0;
	int sampleRate = 0;
	WavFile wavFile = null;
	int offset = 0;
	int numSamples = 0;
	int buffer[] = null;
	int framesRead = 0;
	
	public WavReader( File wav_file ){
		//inital wav file
		if( isWavInit == false ){
			//String file_path = "/storage/sdcard0/omusic/test.wav";
			//String file_path = "/storage/sdcard0/omusic/13819.wav";
			try {
				// Open the wav file specified as the first argument
					wavFile = WavFile.openWavFile( wav_file );
					Log.d(TAG,"initial WavReader class.");
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WavFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Get the number of audio channels in the wav file
			numChannels = wavFile.getNumChannels();
			sampleRate = (int) wavFile.getSampleRate();
			//Log.d(TAG,"sampleRate: " + sampleRate  );
			//Log.d(TAG,"numChannels: " + numChannels );
			
			numSamples = 1 * numChannels * sampleRate; //1 s
			buffer = new int[numSamples];
			offset = 0;
			isWavInit = true;
		}
	
	}
	

	// in native code to read a wav file in SD card.
	// now, every time is 1 second data.
	public  int getWavData( short[] data)
	{

		//short data[] = new short[numSamples];
		// Read frames into buffer
		//Log.d(TAG,"into getWavData(), numSamples= " + numSamples);
		framesRead = 0;
		
		try {
			//framesRead = wavFile.readFrames(buffer, offset, numSamples);
			framesRead = wavFile.readFrames(buffer, numSamples/numChannels);
			//Log.d(TAG,"framesRead= " + framesRead +", buffer length= " + buffer.length
			//			+ " remain= " + wavFile.getFramesRemaining());
			//offset += framesRead;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for( int i=0; i<numSamples; i++ )
		{
			data[i] = (short)buffer[i];
		}
		
		//buffer = null; //free memory
		
		return framesRead;
	}
	
	
	public int getChannels()
	{
		return numChannels;
	}
	
	public int getSampleRate()
	{
		return sampleRate;
	}

}
