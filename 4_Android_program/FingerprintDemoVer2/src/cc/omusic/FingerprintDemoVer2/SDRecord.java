package cc.omusic.FingerprintDemoVer2;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import android.os.Environment;


public class SDRecord {
	
	//Contractor
	public SDRecord( ){
		sdCardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED );	
	}
	
	//check sd card is onmount
	public boolean  checkSD( ){
		//check SD card is  insert?
		return  Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED );	
	}
	//if( sdExit )
	//SDPath = Environment.getExternalStorageDirectory() +"/OmusicRecordVoice/";	
	
	//create directory in SD card
	 public File createSDDir(  String DirName){
		 String SDPath = null;
		 File dir = null;
		 
		 if( !this.sdCardExit ){
			 return null;
		 }
		 SDPath = Environment.getExternalStorageDirectory()+"";
		 dir = new File(  SDPath + File.separator + DirName);
		 //Log.i("sd","dir = "+dir.getPath());
		 if(!dir.exists()){
			 try{
				 //如果需要，会先创建上层目录.
				 dir.mkdirs();
				// Log.i("sd","create dir  "+dir.getPath());
			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 return dir;	 
	 }
	 
	 //create files in SD card
	 public  File createSDFile(String  FilePath) {
		 if( ! this.sdCardExit )
			 return null;
		 
		 File file = new File( FilePath );
		 if(!file.exists()){
			 try{
				 file.createNewFile();
			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		
		 return file;
	 }
	 
	 //write data to sd card.
	 public File WriteToSDCard( String FilePath, String FileName, InputStream data){
		 File file = null;
		 OutputStream output = null;
		 String SDPath = null;
		 //String FilePath = null;
		 if( ! this.sdCardExit )
			 return null;
		 
		 try{
			 createSDDir(FilePath);
			 file = createSDFile( FilePath + FileName);
			 output = new FileOutputStream( file);
			 
			 byte buffer[]= new byte[4*1024];
			 while((data.read(buffer) != -1)){
				 output.write(buffer);
			 }
			 output.flush(); 
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 finally{
			 try{
				 output.close();
			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
		 }
		 return file;
	 }
	 
	 
	 
	 public String getMIMEType( File f)
		{
			String end = f.getName().substring( f.getName().lastIndexOf(".") + 1,
						f.getName().length()).toLowerCase();
			String type = "";
			if( end.equals("mp3") || end.equals("aac") || end.equals("amr")
					|| end.equals("mpeg") || end.equals("wav") || end.equals("ogg") ){
				type = "audio";
			}
			else if( end.equals("jpg") || end.equals("gif")||
					end.equals("png") || end.equals("jpeg") ) {
				type = "image";
			}
			else{
				type = "";
			}
			type += "/*";
			return type;
		}
		

	 
	 
	 
	 
	//获取实时时间
	public String GetTimeNow() {
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyyMMdd_HHmmss_SS");
    	Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
    	String TimerStr    =    formatter.format(curDate);   
    	return TimerStr;
		
	}
	
	private  boolean sdCardExit = false;

}
