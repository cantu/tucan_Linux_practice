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
    /* SurfaceHolder��ʼ�� */
    mSurfaceHolder = this.getHolder();
    mSurfaceHolder.addCallback(this);
    this.setFocusable(true);
    this.mContext = context;
    /* ����һ������ʾ������ProgressDialog */
    dialog=new ProgressDialog(mContext);
    dialog.setMessage("������...");
    dialog.show();
  }
  
  @Override
  public void surfaceCreated(SurfaceHolder holder)
  {
	  /* ����Threadȡ����ƬList���ʼBitmap */
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
  
  /* ������Ƭʱ�����¼� */
  public void handleScroll(float dis)
  {
    if(fingerDown){
      distance=dis;
      /* ��һ����Ƭ */
      if(position==0)
      {
    	/* ������ */
        if(distance<0)
        {
        /* ����������������Ƭ����ʼ�� */
          drawPoint1=0;
          drawPoint2=distance;
          drawPoint3=width+distance;
          /* ������Ƭ */
          doDraw();
        }
      }/* ���һ����Ƭ */
      else if(position==photos.size()-1)
      {
        /* ������ */
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
        /* ������ */
        if(distance>0)
        {
          drawPoint1=distance-width;
          drawPoint2=distance;
          drawPoint3=0;
        }/* ������ */
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
  /* ��ָ����ʱ���� */
  public void onDown()
  {
    fingerDown=true;
  }
  /* ��ָ�뿪ʱ���� */
  public void onUp()
  {
    fingerDown=false;
    /* ���������볬����Ƭһ��ʱ������Ƭ��ʾΪ��һ�� */
    if(Math.abs(distance)>width/2)
    {      
    	/* ������ */
      if(distance>0)
      {
        if(position!=0)
        {
          position--;
          /*Ԥ����bmp�����л�����5����Ƭ*/
          /* ����Bitmap���� */
          bmp[4]=bmp[3];
          bmp[3]=bmp[2];
          bmp[2]=bmp[1];
          bmp[1]=bmp[0];
          /* ����Thread��̨��ȡ��ߵ���Ƭ bmp[0]= getBitmap(position-2); */
          Thread thread = new Thread(new loadBitmap(1));
          thread.start();
          drawPoint1=0;
          drawPoint2=0;
          drawPoint3=0;
          doDraw();
        }
      }
      else
      {		/* ������ */
        if(position!=photos.size()-1)
        {
          position++;
          /*Ԥ����bmp�����л�����5����Ƭ*/
          /* ����Bitmap���� */
          bmp[0]=bmp[1];
          bmp[1]=bmp[2];
          bmp[2]=bmp[3];
          bmp[3]=bmp[4];
          /* ����Thread��̨��ȡ�ұߵ���Ƭ bmp[4]= getBitmap(position+2); */
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
    	/*��������δ����ʱ��ʾԭ����Ƭ*/
      drawPoint1=0;
      drawPoint2=0;
      drawPoint3=0;
      doDraw();
    }
  }
  
  /* ������Ƭ */
  public void doDraw()
  {
    Canvas canvas = null;
    try
    {
    	canvas = mSurfaceHolder.lockCanvas(null);
        synchronized (mSurfaceHolder)
        {
				Paint p = new Paint();
				/* �ϰ�ɫ��ɫ */
				canvas.drawColor(mContext.getResources().getColor(R.drawable.white));
				/* ����ߵ���Ƭ */
				if (drawPoint1 != 0 && bmp[1] != null) {
					canvas.drawBitmap(bmp[1], drawPoint1,
							height / 2 - bmp[1].getHeight() / 2, p);
				}
				/* ���м����Ƭ */
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

  /* ͨ����������ȡ��Bitmap�����method */
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
        /* ͨ��Matrix��Bitmap������������Ļ��С */
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
  /* ��̨ȡ����ƬBitmap */
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
    	/* ȡ����Ƭ��Ϣ */
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
	    	
      /* ��ʼBitmap��� */
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
      /* �ر�ProgressDialog */
      if(dialog.isShowing())
          dialog.dismiss();
      super.handleMessage(msg);
    }
  };
  
  /* ����XMLȡ����Ƭ��Ϣ��method */
  private List<String> getPhotoList()
  {
    List<String> photos=new ArrayList<String>();
    URL url = null;
    try
    {  
      url = new URL(path);
      /* ���Զ����PhotoHandler��Ϊ����XML��Handler */
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
