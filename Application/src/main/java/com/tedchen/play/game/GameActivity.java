package com.tedchen.play.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.tedchen.play.game.layer.WelComeLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;


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
