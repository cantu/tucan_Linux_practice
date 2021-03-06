package cc.omusic.FingerprintDemoVer2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;




import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class MusicRecorder {
	
	//audio recorder configure parameters
	private final int FREQUENCY = 11025;
	private final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	//audio recorder buffers
	private int minBufferSize;
	private static byte  audioByte[];
	private static int bufferSize;	
	private int read_size = 0;
	//Muti-thread sync between main thread and recorder thread.
	private volatile boolean  isRecording = false;
	
	private static int secondsToRecord = 20;
	AudioRecord mRecordInstance = null;
	private String fingerprint = "";
	
	private long fingerprint_time=0;
	private long query_time=0;
	
	private final String TAG = "MusicRecorder";
	
	private long record_time;
	private SDRecord SDRecorder;
	
	private File RecordMusicRawFile = null;
	private File RecordMusicDir = null;
	private File RecordMusicWavFile = null;
	private String file_prefix="";
	
	byte[] header = new byte[44];
	//int offset = 50000;
	int offset = 44;
	
	
	
	private String jsonstr = "";


	private Handler RecorderHandler = null;
	
	short[] music_data = null;
	
	
	public void setHandler(Handler handler) {
		this.RecorderHandler = handler;
	}

	public MusicRecorder(  ){
		MusicRecorder.secondsToRecord = 20;
		SDRecorder = new SDRecord();
		
	}
	
	public MusicRecorder( int seconds ){
		MusicRecorder.secondsToRecord = seconds;
		SDRecorder = new SDRecord();
	}
	
	//initial audio recorder
	public void creatRecorder(){
		// create the audio buffer
		// get the minimum buffer size
		Log.d(TAG,"creatAudioRecorder()");
		minBufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL, ENCODING);
		// start recorder
		mRecordInstance = new AudioRecord(	MediaRecorder.AudioSource.MIC,
											FREQUENCY, CHANNEL, 
											ENCODING, minBufferSize);
	}
	
	




	
	// stop recorder at app quite
	public void stopRecorder(){
		//Log.d(TAG,"in stopRecorder");
		if( mRecordInstance != null ){
			isRecording = false;
			Log.d(TAG,"mRecordInstance.stop();");
			mRecordInstance.stop();
			Log.d(TAG,"complete stop audio recorder");		
		}
	}
	
	
	public String getQueryResult(){
		return jsonstr;
	}
	
	public File getRecordMusicWavFile(){
		return RecordMusicWavFile;
	}
	
	public int getWavFileTime( File f ){
		int wav_time = 0;
		String end = f.getName().substring( f.getName().lastIndexOf(".") + 1,
				f.getName().length()).toLowerCase();
		String type = "";
		if( end.equals("wav") ){
			wav_time =(int) ( (f.length() - this.offset) / this.FREQUENCY / 2 );
		}
		
		return wav_time;
		
	}
	
	public long getQueryTime(){
		return 	query_time;
	}
	
	public long getFingerprintTime(){
		return 	fingerprint_time;
	}

	
	// continuos record 20 seconds to short data array
	public short [] recordAudioToShortArray(){
		short audioData[];
		// buffer size for the audio recorder: frequency * seconds to record.
		bufferSize = Math.max(minBufferSize, this.FREQUENCY * secondsToRecord);	
		//data buffer for music fingerprint input data
		audioData = new short[bufferSize];
		
		while( isRecording ){
			// fill audio buffer with mic data.
			read_size = 0;
			record_time = System.currentTimeMillis();
			do 
			{					
				read_size += mRecordInstance.read(audioData, read_size, bufferSize - read_size);
				if(mRecordInstance.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED)
					break;
				//Log.d(TAG,"read_size: " + read_size);
			} 
			while (read_size < bufferSize && isRecording );	
			isRecording = false;
		}
		record_time = System.currentTimeMillis() - record_time;
		Log.i(TAG,"music record time: " + record_time + "ms" );
		return audioData;	
		
	}
	
	//store record audio to ***.raw file
	public  void recordAudioToRawFile(){	
		
		audioByte = new byte[minBufferSize];
		FileOutputStream fos = null ;
		
		//initial *.raw file in sd card.
		file_prefix = this.GetTimeNow();
		try{
			RecordMusicDir = SDRecorder.createSDDir( "omusic" );
			//Log.d(TAG,"RecordMusicDir: "+ RecordMusicDir.getPath());
			//Log.d(TAG,"RecordMusicRawFile: "+ RecordMusicDir.getPath()+ file_prefix+".raw");
			RecordMusicRawFile = new File(RecordMusicDir, file_prefix+".raw");
			//Log.d(TAG,"RecordMusicRawFile: "+ RecordMusicRawFile.getPath());
			
			if( RecordMusicRawFile.exists())
				RecordMusicRawFile.delete();
			fos = new FileOutputStream( RecordMusicRawFile );
		} catch( Exception e){
			e.printStackTrace();
		}
		
		//record audio continuously.
		read_size = 0;
		record_time = System.currentTimeMillis();
		while( isRecording ){
			read_size += mRecordInstance.read(audioByte, 0, minBufferSize);	
			if(AudioRecord.ERROR_INVALID_OPERATION != read_size ){
				try{
					/*
					 * audio recorder's read() method use a buffer byte[] audioByte
					 * at stop point, the buffer maybe not enough.
					 * so read_size <= RecordMusicRawFile.length().
					 * in codegen( ***, length), length use RecordMusicRawFile.length().
					 * */
					fos.write(audioByte);
				}catch( IOException e){
					e.printStackTrace();
				}
			}
		}
		
		try{
			fos.flush();
			fos.close();
			Log.d(TAG,"success close fos");
		}catch( IOException e){
			e.printStackTrace();
		}
		
		Log.d(TAG,"read_size:" + read_size);
		record_time = System.currentTimeMillis() - record_time;
		Log.d(TAG,"music record time: " + record_time + "ms" );
		Log.d(TAG,"RecordMusicRawFile length:" + RecordMusicRawFile.length() );
		
	}

	
    //copy a raw file to wav file
    private void copyRawToWaveFile(String inFilename ) {  
        FileInputStream in = null;  
        FileOutputStream out = null;  
        long totalAudioLen = 0;  
        long totalDataLen = totalAudioLen + 36;  
        long longSampleRate = FREQUENCY;  
        int channels = 1;  
        long byteRate = 16 * FREQUENCY * channels / 8;  
        byte[] data = new byte[minBufferSize];  
        long rawfile2wav_time = 0;
        
        rawfile2wav_time = System.currentTimeMillis();
        
		if( RecordMusicDir != null )
		{
			//File file = new File( file_prefix +".wav" );
			RecordMusicWavFile = new File(RecordMusicDir, file_prefix+".wav");
			if( RecordMusicWavFile.exists())
				RecordMusicWavFile.delete();
			Log.d(TAG,"wav file:" + RecordMusicWavFile.getPath());
		}
        
        
        try {  
            in = new FileInputStream(inFilename);  
            out = new FileOutputStream(RecordMusicWavFile.getPath());  
            totalAudioLen = in.getChannel().size();  
            totalDataLen = totalAudioLen + 36; 
            
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,  
                    			longSampleRate, channels, byteRate);  
            
            while (in.read(data) != -1) {  
                out.write(data);  
            }  
            in.close();  
            out.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        rawfile2wav_time =  System.currentTimeMillis()- rawfile2wav_time ;
        Log.i(TAG,"copy raw file to wav file time: " + rawfile2wav_time + "ms" );
    }  
  
    //store wav file, 44 char header + ***.raw
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,  
            long totalDataLen, long longSampleRate, int channels, long byteRate)  
            throws IOException {    
        header[0] = 'R'; // RIFF/WAVE header  
        header[1] = 'I';  
        header[2] = 'F';  
        header[3] = 'F';  
        header[4] = (byte) (totalDataLen & 0xff);  
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);  
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);  
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);  
        //Log.d(TAG," chunksize:  " + totalDataLen);
        header[8] = 'W';  
        header[9] = 'A';  
        header[10] = 'V';  
        header[11] = 'E';  
        header[12] = 'f'; // 'fmt ' chunk  
        header[13] = 'm';  
        header[14] = 't';  
        header[15] = ' ';  
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk  
        header[17] = 0;  
        header[18] = 0;  
        header[19] = 0;  
        header[20] = 1; // format = 1  
        header[21] = 0;  
        header[22] = (byte) channels;  
        //Log.d(TAG," channels:  " + channels);
        header[23] = 0;  
        header[24] = (byte) (longSampleRate & 0xff);  
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);  
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);  
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);  
        //Log.d(TAG," SampleRate:  " + longSampleRate);
        header[28] = (byte) (byteRate & 0xff);  
        header[29] = (byte) ((byteRate >> 8) & 0xff);  
        header[30] = (byte) ((byteRate >> 16) & 0xff);  
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        //Log.d(TAG," byteRate:  " + byteRate);
        header[32] = (byte) (channels* 16 / 8); // block align  //channels * BitsPerSample/8 
        header[33] = 0;  
        header[34] = 16; // bits per sample  
        header[35] = 0;  
        header[36] = 'd';  
        header[37] = 'a';  
        header[38] = 't';  
        header[39] = 'a';  
        header[40] = (byte) (totalAudioLen & 0xff);  //NumSamples * channels * BitPerSample /8
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);  
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);  
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff); 
        //Log.d(TAG," subchunk size:  " + totalAudioLen);
        out.write(header, 0, 44);  
    }  
  
    
	 
	//获取实时时间
	private String GetTimeNow() {
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyyMMdd_HHmmss_SS");
	   	Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
	   	String TimerStr    =    formatter.format(curDate);   
	   	return TimerStr;
			
	}

	//strill have problem, should test !!!!!!!!!!!!
	public short[] readWavFileToShortArray( File WavFile,  int numSample  ) {
		FileInputStream fin = null;
		//FileOutputStream fout = null;
		DataInputStream din = null;
		long audioLen = 0;
		//short music_data[] = null;
		
		Log.i(TAG, "readWavFileToStream input file: " + WavFile.getPath() );
		if( WavFile.isFile() && WavFile.exists() ){
			try {
				fin  = new FileInputStream( WavFile );
				audioLen = (WavFile.length() - offset);
			    Log.i(TAG, "audioLen: " + audioLen );
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			din = new DataInputStream(
					new BufferedInputStream( fin) 
							//new FileInputStream( filename ) )
					);
			//short length 16bits, Byte length 8bits
		    try {
				//audioLen = fin.getChannel().size() /  2 ;
			   // Log.i(TAG, "audioLen" + audioLen );
		    	
		    	//music_data = new short[ numSample ];
		    	music_data = new short[ numSample ];
				
				//skip wav file's header
				din.skipBytes(offset);
				
				for(int i=0; i<music_data.length; i++ )
				{
	                music_data[i] = din.readShort();
	                
				} 
		    }catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			try {
				if( din != null ){
					din.close();
					din = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return music_data;
		}
		else{
			Log.e( TAG, "input file is not validate");
			return null;		
		}		
	}
	
	//strill have problem, should test !!!!!!!!!!!!
	public float[] readWavFileToFloatArray( File WavFile, int numSample ) {
		
		final float MAX_VALUE = 1.0f / Short.MAX_VALUE;
		FileInputStream fin = null;
		//FileOutputStream fout = null;
		DataInputStream din = null;
		int audioLen = 0;
		//short music_data[] = null;
		float data[] = null;
		
		Log.i(TAG, "readWavFileToStream input file: " + WavFile.getPath() );
		if( WavFile.isFile() && WavFile.exists() ){
			try {
				fin  = new FileInputStream( WavFile );
				audioLen = (int) ((WavFile.length() - offset)/( Float.SIZE / Byte.SIZE ));
			    Log.i(TAG, "audioLen: " + audioLen );
			    //Log.i(TAG,"read_size:" + read_size);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			din = new DataInputStream(
					new BufferedInputStream( fin) 
							//new FileInputStream( filename ) )
					);
			//short length 16bits, Byte length 8bits
		    try {
				//audioLen = fin.getChannel().size() /  2 ;
			   // Log.i(TAG, "audioLen" + audioLen );
		    	//data = new float[(int) audioLen];
		    	data = new float[numSample];
				
				//skip wav file's header
				din.skipBytes(offset);
				
				for(int i=0; i<data.length; i++ ){
					
	                int result = din.readUnsignedByte();
	                result |= din.readUnsignedByte() << 8;
	                result |= din.readUnsignedByte() << 16;
	                result |= din.readUnsignedByte() << 24;
	                data[i] = result  * MAX_VALUE;
	                
				} 
		    }catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			try {
				if( din != null ){
					din.close();
					din = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return data;
		}
		else{
			Log.e( TAG, "input file is not validate");
			return null;		
		}		
	}
	
	public boolean checkMusicdata( File raw_file, short music_data[] ){
		
		for( int i=0; i<music_data.length; i++ ){
			//raw_file.
		}
		
		return false;
	}

}




	
	
