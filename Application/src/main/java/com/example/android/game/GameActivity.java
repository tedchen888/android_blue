package com.example.android.game;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.example.android.game.layer.WelComeLayer;
import com.example.android.music.FileFlattener;
import com.example.android.music.Music;
import com.example.android.music.MusicPlayerService;
import com.example.android.music.MusicPlayerServiceBinder;
import com.example.android.music.SeekBarTextCallback;
import com.example.android.rocker_ctrl.RockerSurfaceView;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedchen on 2017/3/18.
 */

public class GameActivity extends Activity {
    private CCDirector director;
    //private BlackgrandMusic bg_music = new BlackgrandMusic();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //显示自定义的SurfaceView视图
        CCGLSurfaceView surfaceView = new CCGLSurfaceView(this);
        setContentView(surfaceView);

        //导演管理场景
        director = CCDirector.sharedDirector();
        director.attachInView(surfaceView);//开线程
        director.setScreenSize(480, 320);
        director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);//横屏
        director.setDisplayFPS(false);//不显示帧率
        CCScene scene = CCScene.node();
        scene.addChild(new WelComeLayer());
        director.runWithScene(scene);

        //连接音乐服务，加载音乐，随机播放
        //bg_music.initMusic(this);
    }

    @Override
    protected void onResume() {
        director.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        director.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //bg_music.fini();
        director.end();
    }

}
