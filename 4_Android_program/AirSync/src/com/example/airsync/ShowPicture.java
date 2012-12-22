package com.example.airsync;

import com.example.airsync.Main.PictureButtonListener;

import android.app.Activity;
import android.os.Bundle;
/* 建立一个gallery显示SD卡里的照片*/
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.TextView;


public class ShowPicture extends Activity {
	private TextView tvShowPicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*载入主界面的各个控件格式*/
		setContentView(R.layout.activity_showpicture);
		/* 建立一个gallery显示SD卡里的照片*/
		Gallery gallery = (Gallery)findViewById( R.id.my_gallery);
		gallery.setAdapter( new ImageAdapter( this, GetSDPicture()));
		gallery.setOnItemClickListener( new GalleryClickListener())
		
		tvShowPicture = (TextView) findViewById(R.id.show_picture_text);
		tvShowPicture.setText("好吧，我饿了，就做到这里了");
		
	}
	

}

/*为按钮创建监听器*/
class GalleryClickListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

/*将SD卡指定文件夹的照片路径存在一个List中，好让相册控件显示
 * 这个照片目录是从aircard穿过来的照片存储的地方
 * 现在实验的时候我放在：/mnt/extsd/TestPhoto
 */
private List<String> GetSDPicture()
{
	;
}
