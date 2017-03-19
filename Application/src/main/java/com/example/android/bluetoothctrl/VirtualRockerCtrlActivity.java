package com.example.android.bluetoothctrl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.rocker_ctrl.RockerSurfaceView;

/**
 * Created by tedchen on 2017/3/18.
 */

public class VirtualRockerCtrlActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //显示自定义的SurfaceView视图
        setContentView(new RockerSurfaceView(this));
    }

}
