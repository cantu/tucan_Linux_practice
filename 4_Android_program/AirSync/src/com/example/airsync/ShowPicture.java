package com.example.airsync;

import com.example.airsync.Main.PictureButtonListener;

import android.app.Activity;
import android.os.Bundle;
/* ����һ��gallery��ʾSD�������Ƭ*/
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.TextView;


public class ShowPicture extends Activity {
	private TextView tvShowPicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*����������ĸ����ؼ���ʽ*/
		setContentView(R.layout.activity_showpicture);
		/* ����һ��gallery��ʾSD�������Ƭ*/
		Gallery gallery = (Gallery)findViewById( R.id.my_gallery);
		gallery.setAdapter( new ImageAdapter( this, GetSDPicture()));
		gallery.setOnItemClickListener( new GalleryClickListener())
		
		tvShowPicture = (TextView) findViewById(R.id.show_picture_text);
		tvShowPicture.setText("�ðɣ��Ҷ��ˣ�������������");
		
	}
	

}

/*Ϊ��ť����������*/
class GalleryClickListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

/*��SD��ָ���ļ��е���Ƭ·������һ��List�У��������ؼ���ʾ
 * �����ƬĿ¼�Ǵ�aircard����������Ƭ�洢�ĵط�
 * ����ʵ���ʱ���ҷ��ڣ�/mnt/extsd/TestPhoto
 */
private List<String> GetSDPicture()
{
	;
}