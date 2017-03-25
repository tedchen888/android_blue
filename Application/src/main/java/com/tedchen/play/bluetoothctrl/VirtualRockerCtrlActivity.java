package com.tedchen.play.bluetoothctrl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tedchen.play.music.FileFlattener;
import com.tedchen.play.music.Music;
import com.tedchen.play.music.MusicPlayerService;
import com.tedchen.play.music.MusicPlayerServiceBinder;
import com.tedchen.play.music.SeekBarTextCallback;
import com.tedchen.play.rocker_ctrl.RockerSurfaceView;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
