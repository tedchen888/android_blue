package com.tedchen.play.rocker_ctrl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.tedchen.play.game.R;
import com.tedchen.play.bluetoothctrl.GlobalConfig;
import com.tedchen.play.common.VibratorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RockerSurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder sfh;
	private Paint paint;
	private Thread th;
	private boolean flag;
	private Canvas canvas;
	private int screenW, screenH;
	private static Bitmap carBmp[] = new Bitmap[10];
	GameSpirit spirit;
	Rocker rocker;
    CtrlButton btn_light; //灯光按钮
    CtrlButton btn_sound; //声音按钮

	private SoundPool soundPool;
	//Map<Integer,Integer> map = new HashMap<Integer, Integer>();
	List sound_list = new ArrayList();
	Random ran = new Random(System.currentTimeMillis());

	/**
	 * SurfaceView初始化函数
	 */
	public RockerSurfaceView(Context context) {
		super(context);
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		setFocusable(true);
    }

	//public RockerSurfaceView(Context context, AttributeSet attrs){
	//	super(context,attrs);
	//}

	/**
	 * SurfaceView视图创建，响应此函数
	 */
//	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//加载图片
		for (int i = 0; i < carBmp.length; i++) {
			carBmp[i] = BitmapFactory.decodeResource(this.getResources(), R.drawable.car0 + i);
		}
		//加载音效
		soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
		sound_list.add(soundPool.load(getContext(), R.raw.car_laba, 1));
		sound_list.add(soundPool.load(getContext(), R.raw.car_laba2, 1));

		//设置背景图
		//setBackgroundResource(R.drawable.background);

		spirit = new GameSpirit(this,getWidth()/2,this.getHeight()/2);
		screenW = this.getWidth();
		screenH = this.getHeight();
		rocker = new Rocker(screenW,screenH);
		flag = true;

		//精灵被按时发出声音
		spirit.setListener(new CtrlButtonListener() {
			@Override
			public void OnClickListener(Context context, float touchx, float touchy) {
				super.OnClickListener(context, touchx, touchy);
				int sound_id = ran.nextInt(sound_list.size());
				soundPool.play((int)sound_list.get(sound_id), 1, 1, 0, 0, 1);
			}
		});

        setupCtrButtons();

		//实例线程
		th = new Thread(this);
		//启动线程
		th.start();
	}

	void setupCtrButtons() {
        //各个控制按钮的位置
        float lightbtn_x = this.getWidth() - this.getWidth()*0.1f;
        float lightbtn_y = this.getHeight() - this.getHeight()*0.4f;
        btn_light = new CtrlButton(this, R.drawable.light, lightbtn_x, lightbtn_y);
        btn_light.setListener(new CtrlButtonListener() {
            @Override
            public void OnClickListener(Context context, float touchx, float touchy) {
                super.OnClickListener(context, touchx, touchy);
                //Toast.makeText(context, "on light click", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate((Activity)context, 100);
                GlobalConfig.SendBluetoothCmd(GlobalConfig.CMD_LIGHT);
            }
        });

        float sound_x = this.getWidth() - this.getWidth()*0.1f;
        float sound_y = this.getHeight() - this.getHeight()*0.2f;
        btn_sound = new CtrlButton(this, R.drawable.sound, sound_x, sound_y);
        btn_sound.setListener(new CtrlButtonListener() {
            @Override
            public void OnClickListener(Context context, float touchx, float touchy) {
                super.OnClickListener(context, touchx, touchy);
                //Toast.makeText(context, "on sound click", Toast.LENGTH_SHORT).show();
				VibratorUtil.Vibrate((Activity)context, 100);
                GlobalConfig.SendBluetoothCmd(GlobalConfig.CMD_SOUND);
            }
        });
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
				spirit.draw(this, canvas, carBmp, matrix, waterY);

                btn_light.draw(canvas);
                btn_sound.draw(canvas);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}

	String cmd = "";
	String last_cmd = "";
	/**
	 * 触屏事件监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//当用户手指抬起，应该恢复小圆到初始位置
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			rocker.reset();

			//发送结束命令
			GlobalConfig.SendBluetoothCmd(GlobalConfig.CMD_MOVE_STOP);

		} else {
			int pointX = (int) event.getX();
			int pointY = (int) event.getY();

			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if (rocker.begin(pointX,pointY)) { //在摇杆里面按下
                    //Toast.makeText(getContext(), "start rocker", Toast.LENGTH_SHORT).show();
                } else {
                    btn_light.OnLightBtnClick(getContext(), pointX, pointY);
                    btn_sound.OnLightBtnClick(getContext(), pointX, pointY);

					spirit.beCatched(getContext(), pointX, pointY);
                }
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE)
			{
				rocker.update(pointX,pointY);

				double d = rocker.degreesByNormalSystem;
				if ((d >= 0 && d < 25) || (d >= 360-25 && d < 360)) {
					cmd = "d0";
				} else if (d >= 25 && d < (90-25)) {
					cmd = "d45";
				} else if (d >= (90-25) && d < 90 + 25) {
					cmd = "d90";
				} else if (d >= (90+25) && d < (180-25)) {
					cmd = "d135";
				} else if (d >= (180-25) && d < 180+25) {
					cmd = "d180";
				} else if (d >= (180+25) && d < (270-25)) {
					cmd = "d225";
				} else if (d >= (270-25) && d < (270+25)) {
					cmd = "d270";
				} else if (d >= (270+25) && d < (360-25)) {
					cmd = "d315";
				}

				//根据控制发送命令
				if (cmd != last_cmd) {
                    GlobalConfig.SendBluetoothCmd(cmd);
					last_cmd = cmd;
				}
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
