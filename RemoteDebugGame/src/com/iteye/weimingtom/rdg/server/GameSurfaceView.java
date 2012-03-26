package com.iteye.weimingtom.rdg.server;

import com.iteye.weimingtom.rdg.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
	private static final boolean D = false;
	private static final String TAG = "GameSurfaceView";
	
	private boolean isRunning;
	private Bitmap image;
    private SurfaceHolder holder;
    private Thread thread;
    private Paint paint;
    
    private int px = 0;
    private int py = 0;
    
    private Object dataLock = new Object();
    private String text = ""; // not thread-safe
    
	private CommonDataQueue queue;
	
	public GameSurfaceView(Context context) {
		super(context);
        Resources r = getResources();
        image = BitmapFactory.decodeResource(r, R.drawable.nagato);  
        holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(getWidth(), getHeight());
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(24);
        paint.setColor(Color.RED);
	}
	
	public void setCommonDataQueue(CommonDataQueue queue) {
		this.queue = queue;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        setRunning(true);
        thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		setRunning(false);
        try {
            thread.join(1000);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

    public void setRunning(boolean b) {
        isRunning = b;
    }
	
	@Override
	public void run() {
        while (isRunning) {
        	doDraw();
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
	}
	
	private void doDraw() {
		Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        synchronized (dataLock) {
        	canvas.drawBitmap(image, px - 57, py - 57, null);
	        if (text != null && text.length() >= 1) {
	        	Rect bounds = new Rect();
	        	paint.getTextBounds(text, 0, text.length(), bounds);
	        	canvas.drawText(text, 0, bounds.height(), paint);
	        }
        }
        holder.unlockCanvasAndPost(canvas);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	//text = "down : " + x + ", " + y;
            	doDraw();
            	if (queue != null) {
            		queue.touchDown(x, y);
            	}
            	break;
                
            case MotionEvent.ACTION_UP:
            	//text = "up : " + x + ", " + y;
            	doDraw();
            	if (queue != null) {
            		queue.touchUp(x, y);
            	}
            	break;
                
            case MotionEvent.ACTION_MOVE:
            	//text = "move : " + x + ", " + y;
            	doDraw();
            	if (queue != null) {
            		queue.touchMove(x, y);
            	}
                break;
        }
        return true;
    }
	
	public void echoText(String text) {
		synchronized (dataLock) {
			this.text = text;
		}
	}
	
	public void moveTo(int px, int py) {
		synchronized (dataLock) {
			this.px = px;
			this.py = py;
		}
	}
}
