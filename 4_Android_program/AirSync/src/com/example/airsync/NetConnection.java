package com.example.airsync;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetConnection {
	
	/*访问网络上的图片文件*/
	public Bitmap getURLToBitmap( String pic_url )
	{
	
	
		URL image_Url = null;
		Bitmap bit_map = null;
		
		try 
		{
			//image_Url = new URL( pic_url );
			image_Url = new URL( test_url );
		}
		catch ( MalformedURLException e )
		{
			e.printStackTrace();
		}
		
		try
		{
			//
			HttpURLConnection connection = (HttpURLConnection) image_Url.openConnection();
			connection.connect();
		
			InputStream input_stream = connection.getInputStream();
			bit_map = BitmapFactory.decodeStream( input_stream );
			input_stream.close();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return bit_map;
	}
		
	private String test_url = "http://www.baidu.com/img/baidu_sylogo1.gif";
}
