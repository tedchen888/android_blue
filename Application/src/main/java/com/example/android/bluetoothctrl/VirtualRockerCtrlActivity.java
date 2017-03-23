package com.example.android.bluetoothctrl;

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

import com.example.android.music.FileFlattener;
import com.example.android.music.Music;
import com.example.android.music.MusicPlayerService;
import com.example.android.music.MusicPlayerServiceBinder;
import com.example.android.music.SeekBarTextCallback;
import com.example.android.rocker_ctrl.RockerSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedchen on 2017/3/18.
 */

public class VirtualRockerCtrlActivity extends Activity {
    ArrayList<Music> music_library;
    List<String> filePaths = null;

    MusicPlayerService mService;
    MusicPlayerServiceBinder mBinder;
    ServiceConnection mConnection;
    boolean mBound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //显示自定义的SurfaceView视图
        setContentView(new RockerSurfaceView(this));

        //连接音乐服务，加载音乐，随机播放
        defineServiceConnection();
        bindService(new Intent(this, MusicPlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
        music_library = new ArrayList<Music>();
        final FileFlattener ff = new FileFlattener();
        createLibrary(ff);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(VirtualRockerCtrlActivity.this, MusicPlayerService.class));
        unbindService(mConnection);
        mBound = false;
    }

    //查找mp3文件
    private void createLibrary(final FileFlattener ff) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //ff.flattenFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music", 6);
                ff.getAssetsMusics(VirtualRockerCtrlActivity.this.getAssets(), "mp3");
                return null;
            }

            @Override
            protected void onPostExecute(Void params) {
                filePaths = ff.getFlattenedFiles();
                Log.d(this.toString(),"Done getting the files from the file flatenner");

                for (String filePath : filePaths) {
                    music_library.add(new Music(getAssets(),"mp3/" + filePath));
                }

                if (mBound)
                    initQueue();
            }

        }.execute();
    }

    private void defineServiceConnection() {
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                startService(new Intent(VirtualRockerCtrlActivity.this, MusicPlayerService.class));
                mBinder = (MusicPlayerServiceBinder) service;
                mService = mBinder.getService(new SeekBarTextCallback() {

                    @Override
                    public void setTotalTime(String time) {
                        //得到总时间
                    }

                    @Override
                    public void setCurrentTime(String time) {
                        //得到已播放时间
                    }
                });
                mBound = true;
/*
                state = mService.getState();
                setPlayPauseOnClickListener();
                mService.registerSeekBar(mSeekBar);


                if (filePaths != null)
                    initQueue();
*/
                initQueue();
                Log.d(getApplicationContext().toString(),"Service is connected and good to go");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                Log.d(getApplicationContext().toString(),"Service is disconnected and good to go");
            }
        };
    }

    private synchronized void initQueue() {
        mService.addMusicToQueue(music_library);
        mService.playNext();
    }
}
