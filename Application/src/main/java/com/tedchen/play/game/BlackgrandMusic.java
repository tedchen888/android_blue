package com.tedchen.play.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.tedchen.play.music.FileFlattener;
import com.tedchen.play.music.Music;
import com.tedchen.play.music.MusicPlayerService;
import com.tedchen.play.music.MusicPlayerServiceBinder;
import com.tedchen.play.music.SeekBarTextCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedchen on 2017/3/25.
 */

public class BlackgrandMusic {
    private Context m_context;

    ArrayList<Music> music_library;
    List<String> filePaths = null;

    MusicPlayerService mService;
    MusicPlayerServiceBinder mBinder;
    ServiceConnection mConnection;
    boolean mBound = false;

    public void initMusic(Context context) {
        m_context = context;
        //连接音乐服务，加载音乐，随机播放
        defineServiceConnection();
        context.bindService(new Intent(m_context, MusicPlayerService.class),
                mConnection, Context.BIND_AUTO_CREATE);
        music_library = new ArrayList<Music>();
        final FileFlattener ff = new FileFlattener();
        createLibrary(ff);
    }

    public void fini() {
        m_context.stopService(new Intent(m_context, MusicPlayerService.class));
        m_context.unbindService(mConnection);
        mBound = false;
    }

    //查找mp3文件
    private void createLibrary(final FileFlattener ff) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //ff.flattenFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music", 6);
                ff.getAssetsMusics(m_context.getAssets(), "mp3");
                return null;
            }

            @Override
            protected void onPostExecute(Void params) {
                filePaths = ff.getFlattenedFiles();
                Log.d(this.toString(),"Done getting the files from the file flatenner");

                for (String filePath : filePaths) {
                    music_library.add(new Music(m_context.getAssets(),"mp3/" + filePath));
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
                m_context.startService(new Intent(m_context, MusicPlayerService.class));
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
                initQueue();
                //Log.d(getApplicationContext().toString(),"Service is connected and good to go");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                //Log.d(getApplicationContext().toString(),"Service is disconnected and good to go");
            }
        };
    }

    private synchronized void initQueue() {
        mService.addMusicToQueue(music_library);
        mService.playNext();
    }
}
