package com.tedchen.play.rocker_ctrl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class CtrlButton {

	float m_x, m_y;  //按钮左上角坐标
	Bitmap bmp_light;

    private  CtrlButtonListener m_listener;
    public void setListener(CtrlButtonListener listener) {
        m_listener = listener;
    }

	public CtrlButton(RockerSurfaceView mySurfaceView, int bmp_resid, float x, float y) {
        bmp_light = BitmapFactory.decodeResource(mySurfaceView.getResources(), bmp_resid);
        //screenwidth - bmp_light.width;
        //lightbtn_x = mySurfaceView.getWidth() - bmp_light.getWidth() - mySurfaceView.getWidth()*0.01f;
        //lightbtn_y = mySurfaceView.getHeight() - bmp_light.getHeight() - mySurfaceView.getHeight()*0.1f;
        m_x = x;
        m_y = y;
    }

	void draw(Canvas canvas){
		//draw light button
		canvas.drawBitmap(bmp_light, m_x, m_y, new Paint());

	}

	public void OnLightBtnClick(Context context, float touchx, float touchy) {
		//判断是否按到了按钮
		Rect rect = new Rect((int)m_x,(int)m_y,
                (int)(m_x+bmp_light.getWidth()),(int)(m_y + bmp_light.getHeight()));
		if (rect.contains((int)touchx,(int)touchy)) {
            //Toast.makeText(context, "on light click", Toast.LENGTH_SHORT).show();
            //GlobalConfig.SendBluetoothCmd(GlobalConfig.CMD_LIGHT);
            m_listener.OnClickListener(context, touchx, touchy);
        }

	}
}
