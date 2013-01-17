package com.example.airsync;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
  private SurfaceHolder mSurfaceHolder;
  private Bitmap[] bmp=new Bitmap[]{null,null,null,null,null};
  private Context mContext;
  private int width=0;
  private int height=0;
  private int position=0;
  private boolean fingerDown=false;
  private float distance=0f;
  private float drawPoint1=0f;
  private float drawPoint2=0f;
  private float drawPoint3=0f;
  private ProgressDialog dialog;
  private String Tag = "MySurface";
  private String path="http://picasaweb.google.com/data/feed/base/all?alt=rss&kind=photo&access=public&filter=1&q=%E7%BE%8E%E5%A5%B3&hl=zh_TW";
 // private String path="http://192.168.1.1";
  private List<String> photos=new ArrayList<String>();
  
  /* Constructor */
  public MySurfaceView(Context context,int w,int h)
  {
    super(context);
    
    width=w;
    height=h;
    /* SurfaceHolder初始化 */
    mSurfaceHolder = this.getHolder();
    mSurfaceHolder.addCallback(this);
    this.setFocusable(true);
    this.mContext = context;
    /* 程序一启动显示加载中ProgressDialog */
    dialog=new ProgressDialog(mContext);
    dialog.setMessage("载入中...");
    dialog.show();
  }
  
  @Override
  public void surfaceCreated(SurfaceHolder holder)
  {
	  /* 启动Thread取得相片List与初始Bitmap */
    Thread thread = new Thread(new surfaceViewInit());
    thread.start();
  }
  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
  {
  }
  @Override
  public void surfaceDestroyed(SurfaceHolder arg0)
  {
  }
  
  /* 拖拉照片时触发事件 */
  public void handleScroll(float dis)
  {
    if(fingerDown){
      distance=dis;
      /* 第一张照片 */
      if(position==0)
      {
    	/* 向左拉 */
        if(distance<0)
        {
        /* 设置左中右三张照片的起始点 */
          drawPoint1=0;
          drawPoint2=distance;
          drawPoint3=width+distance;
          /* 绘制照片 */
          doDraw();
        }
      }/* 最后一张照片 */
      else if(position==photos.size()-1)
      {
        /* 向右拉 */
        if(distance>0)
        {
          drawPoint1=distance-width;
          drawPoint2=distance;
          drawPoint3=0;
          doDraw();
        }
      }
      else
      {
        /* 向右拉 */
        if(distance>0)
        {
          drawPoint1=distance-width;
          drawPoint2=distance;
          drawPoint3=0;
        }/* 向左拉 */
        else if(distance<0)
        {
          drawPoint1=0;
          drawPoint2=distance;
          drawPoint3=width+distance;
        }
        doDraw();
      }
    }
  }
  /* 手指按下时触发 */
  public void onDown()
  {
    fingerDown=true;
  }
  /* 手指离开时触发 */
  public void onUp()
  {
    fingerDown=false;
    /* 如拖拉距离超过照片一半时，将照片显示为下一张 */
    if(Math.abs(distance)>width/2)
    {      
    	/* 向右拉 */
      if(distance>0)
      {
        if(position!=0)
        {
          position--;
          /*预先在bmp数组中缓存了5张照片*/
          /* 重组Bitmap内容 */
          bmp[4]=bmp[3];
          bmp[3]=bmp[2];
          bmp[2]=bmp[1];
          bmp[1]=bmp[0];
          /* 启动Thread后台读取左边的照片 bmp[0]= getBitmap(position-2); */
          Thread thread = new Thread(new loadBitmap(1));
          thread.start();
          drawPoint1=0;
          drawPoint2=0;
          drawPoint3=0;
          doDraw();
        }
      }
      else
      {		/* 向左拉 */
        if(position!=photos.size()-1)
        {
          position++;
          /*预先在bmp数组中缓存了5张照片*/
          /* 重组Bitmap内容 */
          bmp[0]=bmp[1];
          bmp[1]=bmp[2];
          bmp[2]=bmp[3];
          bmp[3]=bmp[4];
          /* 启动Thread后台读取右边的照片 bmp[4]= getBitmap(position+2); */
          Thread thread = new Thread(new loadBitmap(2));
          thread.start();
          drawPoint1=0;
          drawPoint2=0;
          drawPoint3=0;
          doDraw();
        }
      }
    }
    else
    {
    	/*拖拉距离未过半时显示原来照片*/
      drawPoint1=0;
      drawPoint2=0;
      drawPoint3=0;
      doDraw();
    }
  }
  
  /* 绘制照片 */
  public void doDraw()
  {
    Canvas canvas = null;
    try
    {
    	canvas = mSurfaceHolder.lockCanvas(null);
        synchronized (mSurfaceHolder)
        {
				Paint p = new Paint();
				/* 上白色底色 */
				canvas.drawColor(mContext.getResources().getColor(R.drawable.white));
				/* 画左边的照片 */
				if (drawPoint1 != 0 && bmp[1] != null) {
					canvas.drawBitmap(bmp[1], drawPoint1,
							height / 2 - bmp[1].getHeight() / 2, p);
				}
				/* 画中间的照片 */
        if(drawPoint1==0&&drawPoint2==0&&drawPoint3==0)
          canvas.drawBitmap(bmp[2],0,height/2-bmp[2].getHeight()/2,p);
       }
    }
    finally
    {
      if (canvas != null)
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }
  }

  /* 通过网络联机取得Bitmap对象的method */
  public Bitmap getBitmap(int indx)
  {
    Bitmap bm=null;
    try
    {
      if(indx>=0&&indx<=photos.size()-1)
      {
        URL url = new URL(photos.get(indx).toString());
        URLConnection conn = url.openConnection(); 
        conn.connect();
        Bitmap tmp=BitmapFactory.decodeStream(conn.getInputStream());
        /* 通过Matrix将Bitmap对象缩放至屏幕大小 */
        float scale=((float)width)/tmp.getWidth();
        Matrix matrix=new Matrix();
        matrix.postScale(scale,scale);
        bm=Bitmap.createBitmap(tmp,0,0,tmp.getWidth(),tmp.getHeight(),matrix,true);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return bm;
  }
  /* 后台取得相片Bitmap */
  public class loadBitmap implements Runnable
  {
    int type=0;
    public loadBitmap(int t)
    {
      type=t;
    }
    
    @Override
    public void run()
    {
      if(type==1)
      {
        bmp[0]=MySurfaceView.this.getBitmap(position-2);
      }
      else if(type==2)
      {
        bmp[4]=MySurfaceView.this.getBitmap(position+2);
      }
      handler.sendEmptyMessage(0);
    }
  }
  
  public class surfaceViewInit implements Runnable
  {
    @Override
    public void run()
    {
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	/* 取得相片信息 */
//      photos=MySurfaceView.this.getPhotoList();
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	//photos.add("http://www.baidu.com/img/baidu_sylogo1.gif");
    	//photos.add("http://www.cma.gov.cn/tqyb/images/forecast/products/yuntu/l/sevp_nsmc_wxcl_asc_fff_achn_ffff_py_201212271200fffff.jpg");
    	//photos.add("http://i.weather.com.cn/i/product/pic/m/sevp_nsmc_wxcl_asc_e99_achn_lno_py_20121227140000000.jpg");
    	//photos.add("http://imgsrc.baidu.com/forum/pic/item/7318deaa8992a8730ff4778e.jpg");
    	String ImageWeb = "http://www.huxiu.com";
    	//String ImageWeb = "http://image.baidu.com/i?tn=list&word=liulan#%E6%91%84%E5%BD%B1|%E5%85%A8%E9%83%A8|0";
    	NetConnection mNet = new NetConnection();
	    try 
	    {
			photos = mNet.HttpPraseImage(ImageWeb);
		} 
	    catch (Exception e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(Tag, "Error in network call",e);
		}
	    	
      /* 初始Bitmap物件 */
      bmp[2]=MySurfaceView.this.getBitmap(position);
      bmp[3]=MySurfaceView.this.getBitmap(position+1);
      bmp[4]=MySurfaceView.this.getBitmap(position+2);
      
      doDraw();
      handler.sendEmptyMessage(0);
    }
  }
  
  private Handler handler=new Handler(){

    @Override
    public void handleMessage(Message msg)
    {
      /* 关闭ProgressDialog */
      if(dialog.isShowing())
          dialog.dismiss();
      super.handleMessage(msg);
    }
  };
  
  /* 解析XML取得相片信息的method */
  private List<String> getPhotoList()
  {
    List<String> photos=new ArrayList<String>();
    URL url = null;
    try
    {  
      url = new URL(path);
      /* 以自定义的PhotoHandler作为解析XML的Handler */
      PhotoHandler handler = new PhotoHandler();
      Xml.parse(url.openConnection().getInputStream(),
                Xml.Encoding.UTF_8,handler);
      photos =handler.getPhotos();
    }
    catch (Exception e)
    { 
      e.printStackTrace();
    }
    return photos;
  }
}

