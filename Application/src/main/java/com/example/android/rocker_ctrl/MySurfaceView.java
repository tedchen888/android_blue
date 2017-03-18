package com.example.android.rocker_ctrl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.android.bluetoothchat.R;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder sfh;
	private Paint paint;
	private Thread th;
	private boolean flag;
	private Canvas canvas;
	private int screenW, screenH;
	private static Bitmap fishBmp[] = new Bitmap[10];
	Fish fish;
	Rocker rocker;
	/**
	 * SurfaceView初始化函数
	 */
	public MySurfaceView(Context context) {
		super(context);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		setFocusable(true);
	}

	/**
	 * SurfaceView视图创建，响应此函数
	 */
//	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		for (int i = 0; i < fishBmp.length; i++) {
			fishBmp[i] = BitmapFactory.decodeResource(this.getResources(), R.drawable.fish0 + i);
		}
		fish = new Fish(this,getWidth()/2,this.getHeight()/2);
		screenW = this.getWidth();
		screenH = this.getHeight();
		rocker = new Rocker(screenW,screenH);
		flag = true;
		//实例线程
		th = new Thread(this);
		//启动线程
		th.start();
	}

	Matrix matrix = new Matrix();
	float waterY = 0;
	/**
	 * 游戏绘图
	 */
	public void myDraw() {
		try {
			canvas = sfh.lockCanvas();
			if (canvas != null) {
				canvas.drawColor(Color.WHITE);
				//绘制大圆
				rocker.draw(canvas);
				fish.draw(this, canvas, fishBmp,   matrix, waterY);

			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 触屏事件监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//当用户手指抬起，应该恢复小圆到初始位置
		if (event.getAction() == MotionEvent.ACTION_UP) {
			rocker.reset();
		} else{
			int pointX = (int) event.getX();
			int pointY = (int) event.getY();
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				rocker.begin(pointX,pointY);
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				rocker.update(pointX,pointY);
			}
		}
		return true;
	}

	/**
	 * 按键事件监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 游戏逻辑
	 */
	private void logic() {
	}

	public void run() {
		while (flag) {
			long start = System.currentTimeMillis();
			myDraw();
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * SurfaceView视图状态发生改变，响应此函数
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/**
	 * SurfaceView视图消亡时，响应此函数
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}
}
