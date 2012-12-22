package com.example.airsync;


import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;

/* TextView 控件需要的库*/
import android.widget.TextView;
/*按钮控件需要库*/
import android.widget.Button;
/*探测设备的分辨率时需要*/
import android.util.DisplayMetrics;
/*按钮监听器*/
import android.view.View.OnClickListener;
/*多个Activity需要新建intent对象 */
import android.content.DialogInterface;
import android.content.Intent;



public class Main extends Activity {
	/*用于给用户显示这个程序运行前需要的必要硬件连接 */
	private TextView tvTopinfo;
	private TextView tvMetrics;
	private Button   btPicture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*载入主界面的各个控件格式*/
		setContentView(R.layout.activity_main);
		
		/*判断当前设备的android版本，
		 * APK支持的最低版本在projec.properties target=android-8
		 */
		if( getApplicationInfo().targetSdkVersion <=  Build.VERSION_CODES.FROYO )
		{
			//给出一个提示框
			;
		}
		
		/*用于给用户显示这个程序运行前需要的必要硬件连接信息，要保证他们在一个局域网内 */
		tvTopinfo = (TextView) findViewById(R.id.MainTextView1);
		tvMetrics = (TextView) findViewById(R.id.MainTextView2);
		btPicture = (Button) findViewById(R.id.show_picture_button);
				
		CharSequence info_str = getString( R.string.start_info );
		tvTopinfo.setText( info_str );
	
		/*提示用户设备的分辨率,为现实照片做准备，也许照片可以压缩为这个分辨率再显示*/
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm); 
		String metrics_str ="这个设备的分辨率为： "+ dm.widthPixels +
							" * " + dm.heightPixels;
		tvMetrics.setText( metrics_str );
		
		/*为按钮创建监听器*/
		btPicture.setOnClickListener( new PictureButtonListener() );
		
		/*提示用户WIFI要连到aircard */
		ShowAlertDialog( );
		

	}
	

	/*为按钮创建监听器*/
    class PictureButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
 			tvMetrics.setText( "算了，还是别看这个数字了，太乱~");
 			Intent intent = new Intent();
 			intent.setClass( Main.this, ShowPicture.class);
 			/*调用新的Activity*/
 			Main.this.startActivity(intent);
 			/*关闭现在这个Activity，按返回按钮时就退不回来了*/
 			//Main.this.finish();
			
		}
 	}
    
    
	/*在启动的时候启动一个警告框。
	 *用于给用户显示这个程序运行前需要的必要硬件连接信息，要保证他们在一个局域网内 
	 *！！！但是设备旋转，activity重新画后，这个警告框会再次出现，这个需要改善。
	 */
   private void ShowAlertDialog(  )
    {
	    new AlertDialog.Builder( Main.this )
		.setTitle(R.string.alert_title)
		.setMessage(R.string.alert_message)
		//如果确认连接了，下一步。。。
		.setPositiveButton(
							R.string.alert_ok,
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}
							)
		//如果没有连接呢？
		.setNegativeButton(
							R.string.alert_exit,
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}
							)
		.show();
    }
    
    /*menu菜单下的一些选项*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	


}
