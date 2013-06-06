package cc.omusic.musicidentify;

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
import android.util.Log;

public class MusicRecorder {
	
	//audio recorder configure parameters
	private final int FREQUENCY = 11025;
	private final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	//audio recorder buffers
	private int minBufferSize;
	private static short audioData[];
	private static byte  audioByte[];
	private static int bufferSize;	
	private int read_size = 0;
	private volatile boolean  isRecording = false;
	
	private static int secondsToRecord = 20;
	AudioRecord mRecordInstance = null;
	private String fingerprint = "";
	
	private final String TAG = "MusicRecorder";
	
	private long record_time;
	private long fingerprint_time;
	private long query_time;
	private SDRecord SDRecorder;
	
	private File RecordMusicRawFile = null;
	private File RecordMusicDir = null;
	private File RecordMusicWavFile = null;
	private String file_prefix="";
	
	byte[] header = new byte[44];
	
	
	public MusicRecorder(  ){
		this.secondsToRecord = 20;
		SDRecorder = new SDRecord();
		
	}
	
	public MusicRecorder( int seconds ){
		this.secondsToRecord = seconds;
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

	//start to record music
	public void startRecorder(){
		Log.d(TAG,"start audio recorder");
		try 
		{			

			Log.d(TAG,"start audio recorder");
			mRecordInstance.startRecording();
			isRecording = true;
			
			//start audio record thread to write data to sd file.
			new Thread( new AudioRecorderThread()).start();
			Log.d(TAG,"start a thread for audio recorder");
			
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	// stop recorder at app quite
	public void stopRecorder(){
		//Log.d(TAG,"in stopRecorder");
		if( mRecordInstance != null ){
			isRecording = false;
			Log.d(TAG,"mRecordInstance.stop();");
			mRecordInstance.stop();
			//Log.d(TAG,"mRecordInstance.release();");
			//mRecordInstance.release();
			//mRecordInstance = null;
			Log.d(TAG,"complete stop audio recorder");
			
			
		}
	}
	
	// new thread 
	class AudioRecorderThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			fingerprint = "";
			
			//recordMusic();
			
			//generateMusicFp();
		
			recordAudioToRawFile();
			Log.d(TAG,"ready to wav file, RecordMusicRawFile: " + RecordMusicRawFile.getPath());
			copyRawToWaveFile( RecordMusicRawFile.getPath() );
			
		}
			
		
	}
	
	public void recordMusic(){
		
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
				Log.d(TAG,"read_size: " + read_size);
			} 
			while (read_size < bufferSize && isRecording );	
			isRecording = false;
		}
		record_time = System.currentTimeMillis() - record_time;
		Log.d(TAG,"music record time: " + record_time + "ms" );	
		
	}
	
	public String generateMusicFp(){
		fingerprint_time = System.currentTimeMillis();
		
		//audioData.length == read_size
		if( audioData.length != 0 ) {
			//need to modify the codegen lib
			String fingerprint = "";
			fingerprint_time = System.currentTimeMillis() - fingerprint_time;
			Log.d(TAG,"fingerprint generate time: " + fingerprint_time + "ms" );
			return fingerprint;

		}
		else{
			Log.e(TAG,"fingerprint generate  error, audio data is not invaled" );
			return null;
		}
			

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
		
		/*
			//audioData.length == read_size
			if( audioData.length != 0 ) {
				Log.d(TAG,"write data to ***.raw");
				DataOutputStream dos = new DataOutputStream(
											new BufferedOutputStream( fos ));
				Log.d(TAG,"new dos");
				for( int i=0; i<read_size; i++ ){
					try {
						dos.writeShort(audioData[i]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d(TAG,"success write data to dos");
				try{
					dos.close();
					Log.d(TAG,"success close dos");
					fos.close();
					Log.d(TAG,"success close fos");
				}catch( IOException e){
					e.printStackTrace();
				}
			}
			else
				Log.e(TAG,"audio data is empty!");
		}
	*/
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
        
		if( RecordMusicDir != null )
		{
			//File file = new File( file_prefix +".wav" );
			RecordMusicWavFile = new File(RecordMusicDir, file_prefix+".wav");
			if( RecordMusicWavFile.exists())
				RecordMusicWavFile.delete();
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
  
    
	 
	//��ȡʵʱʱ��
	private String GetTimeNow() {
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyyMMdd_HHmmss_SS");
	   	Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
	   	String TimerStr    =    formatter.format(curDate);   
	   	return TimerStr;
			
	}
	
	public String getRecordMusicWavFileStr(){
		return RecordMusicWavFile.getName();
	}

}




	
	