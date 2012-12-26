package com.example.airsync;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;



public class SDCard {
	
	
	/*��SD��ָ���ļ��е���Ƭ·������һ��List�У��������ؼ���ʾ
	 * �����ƬĿ¼�Ǵ�aircard����������Ƭ�洢�ĵط�
	 * ����ʵ���ʱ���ҷ��ڣ�/mnt/extsd/TestPhoto
	 */
	public   List<String> GetSDPicture()
	{
		List<String> it=new ArrayList<String>();
		/*�����ŵ���Ƭ��Ҫ̫�󣬲�Ȼ�ڴ治�����*/
		//File file_path = new File("/mnt/extsd/TestPhoto/");
		File file_path = new File("/mnt/sdcard/Pictures/Screenshots/");
		File[] files = file_path.listFiles();
		
		for( int i=0; i<files.length; i++ )
		{
			File file = files[i];
			if( getImageFile( file.getPath()))
				it.add( file.getPath());
		}
		return it;
	}
	
	/*�ж�ȡ����ͼƬ�ļ����ļ����ǲ���֧�ֵ����� */
	private boolean getImageFile( String Name )
	{
		boolean re;
		/*ȥ����չ��*/
		String end = Name.substring( Name.lastIndexOf(".")+1,
									 Name.length()).toLowerCase();
		if( end.equals("jpg") || end.equals("gif") || end.equals("png") ||
				end.equals("bmp") || end.equals("jpeg") )
		{
			re = true;
		}
		else
		{
			re = false;
		}
		return re;
	}

	/*ȡ��SD�����ڵ�����*/
	public String ShowSize()
	{
		if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			//ȡ��SD��·��
			File path = Environment.getExternalStorageDirectory();
			
			StatFs stat_fs = new StatFs( path.getPath() );
			
			long block_size = stat_fs.getBlockSize();
			long total_blocks = stat_fs.getBlockCount();
			long available_blocks = stat_fs.getAvailableBlocks();
			
			String total = FileSize( total_blocks * block_size );
			String available = FileSize( available_blocks * block_size );
			
			return available;
			//return total;
		
		}
		return null;
	}
	
	/*����SD���������ַ�������λMB*/
	private String FileSize( long size )
	{
		
		size = size/1024/1024;
		/*
			if( size / 1024 >= 0 )
			{
				
				size /= 1024;
			}
			else 
				size = 0;
		 */
		DecimalFormat formatter = new DecimalFormat();
		//3λ�ֱ��磬000
		formatter.setGroupingSize(3);
		return formatter.format(size);
	}
	
	
}