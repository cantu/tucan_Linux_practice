package cc.omusic.musicidentify;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import cc.omusic.musicidentify.SDRecord;




public class MainActivity extends Activity {
	
	private ToggleButton startButton;
	private Button queryButton;
	private Button playButton;
	private Button deleteButton;
	private TextView infoText;
	private ListView musicList;
	private ArrayList<String> recordFiles;
	private ArrayAdapter<String> adapter;
	
	//private File RecordMusicFile;
	private File RecordMusicDir;
	private File SelectedFile;
//	private MediaRecorder mMediaRecorder;
	private boolean isStopRecord;
	private SDRecord SDRecorder;

	
	private Handler handler = new Handler();
	private int  count = 0;
	
	private MusicRecorder musicRecorder = null;
	
	private final String TAG = "Main";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startButton = (ToggleButton) findViewById( R.id.start_button);
		queryButton = (Button) findViewById( R.id.query_button);
		playButton = (Button) findViewById( R.id.play_button);
		deleteButton = (Button) findViewById( R.id.delete_button);
		
		infoText = (TextView) findViewById( R.id.info_text);
		musicList = (ListView) findViewById( R.id.music_List);
		
		SDRecorder = new SDRecord();
		RecordMusicDir = SDRecorder.createSDDir( "omusic" );
		//list all  media (.amr) files
		getRecordFiles();
		// use a ArrarAdapter to contain a ListView
		adapter = new ArrayAdapter<String>(this, R.layout.my_simple_list_item, recordFiles );
		musicList.setAdapter(adapter);
		
		//set every button's click event
		startButton.setOnCheckedChangeListener( new startButtonListener()); 
		playButton.setOnClickListener( new playButtonListener() );
		deleteButton.setOnClickListener( new deleteButtonListener() );
		musicList.setOnItemClickListener(new musicListClickListener());
		queryButton.setOnClickListener(new queryButtonListener());
		
		//initial audio recorder
		musicRecorder =  new MusicRecorder(5);
		musicRecorder.creatRecorder();
	}
	
	Runnable runnable = new Runnable(){
		public void run(){
			count++;
			infoText.setText("record time: " + count + "s");
			handler.postDelayed(this, 1000);
		}
	};
	
	
	
	//query a wav file in sd card
	//input a stream -> short[] audio data -> String fingerprint -> query server 
	class queryButtonListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			infoText.setText("want to query? it's not ready.");
			
			
			
			//Codegen codegen = new Codegen();
			//String fingerprint = codege.generate( audioData, read_size );	
		}
	}
	
	
	
	//start record button
	public class startButtonListener implements OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			// TODO Auto-generated method stub
			// press to start record
			if(isChecked){ 
	
				if( !SDRecorder.checkSD() ){
					Toast.makeText(MainActivity.this, "please check SD card", Toast.LENGTH_SHORT).show();
					infoText.setText("SD card is not insert.");
					return;
				}
				else{
					Toast.makeText(MainActivity.this, "Start to record voice.", Toast.LENGTH_LONG).show(); 
					queryButton.setEnabled(false);
					playButton.setEnabled(false);
					deleteButton.setEnabled(false);
					isStopRecord = false;
					
					musicRecorder.startRecorder();
					
					//Display info
					infoText.setText("recording ...");
					//start timer after 1 second, count record time
					handler.postDelayed(runnable, 1000);
					
				}
			}
			//press to stop record
			else{			
				//adapter.add(RecordMusicFile.getName());
				//SelectedFile = RecordMusicFile;
				//infoText.setText("new music ..."+ RecordMusicFile.getName());
				musicRecorder.stopRecorder();
				
				Toast.makeText(MainActivity.this, 
					"record voice stopped.", Toast.LENGTH_SHORT).show();
				Log.i(TAG,"press to stop");
				queryButton.setEnabled(true);
				playButton.setEnabled(true);
				deleteButton.setEnabled(true);
				//infoText.setText("recorder stopped. file:" + RecordMusicFile.getName());
				Log.i(TAG,"in close process");
				adapter.add(musicRecorder.getRecordMusicWavFileStr());
				
				isStopRecord = true;
				//stop timer and reverse count
				handler.removeCallbacks(runnable);
				count =  0;				
			}
		}
	}
	
	//select a music file from list
	class musicListClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			playButton.setEnabled(true);
			deleteButton.setEnabled(true);
			queryButton.setEnabled(true);
			
			SelectedFile = new File( RecordMusicDir.getAbsolutePath()
									+ File.separator 
									+ ( (CheckedTextView) arg1).getText());
			infoText.setText("you choose: "
							+ RecordMusicDir.getAbsolutePath()
							+ File.separator 
							+ ( (CheckedTextView) arg1).getText());	
		}
		
	}
	
	//play a music that selected
	class playButtonListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if( SelectedFile != null && SelectedFile.exists() ){
				//openFile( SelectedFile );
				Intent intent = new Intent();
				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction( android.content.Intent.ACTION_VIEW);
				String type = SDRecorder.getMIMEType( SelectedFile );
				intent.setDataAndType(Uri.fromFile( SelectedFile ), type);
				startActivity( intent );	
			}
		
			
			
		}
		
	}

	
	//delete the selected music file
	class deleteButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if( SelectedFile != null){
				//remove file name from adapter
				Log.d("tusion","success remove file name"+SelectedFile.getName());
				adapter.remove(SelectedFile.getName());
				//delete file data in sd card
				if( SelectedFile.exists() )
					SelectedFile.delete();
				infoText.setText("success delete. ");		
			}
		}
		
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void getRecordFiles( ){
		recordFiles = new ArrayList<String>();
		 if( SDRecorder.checkSD() ){
			 File files[] = RecordMusicDir.listFiles();
			 if(files != null ){
				 //Log.d("tusion"," get files!");
				 for( int i=0; i<files.length; i++ ){
					 if( files[i].getName().indexOf(".") >= 0 )
					 {
						 //read all .amr files
						 String file_str = files[i].getName().substring(
								 	files[i].getName().indexOf("."));
						 //if( file_str.toLowerCase().equals(".wav"))
							 recordFiles.add( files[i].getName());
					 }
				 }
			 } 
		 } 
	 }
    
	/*
	@Override  
    protected void onDestroy() {  
		musicRecorder.stopRecorder();  
        super.onDestroy();  
    }  
	*/

	
}
	

	

		
	

	



		
