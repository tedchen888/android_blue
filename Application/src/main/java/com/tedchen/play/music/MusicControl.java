package com.tedchen.play.music;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class MusicControl {
	ArrayList<Music> music_list;
	MusicPlayerService mService;
	MusicPlayerServiceBinder mBinder;
	ServiceConnection mConnection;
	boolean mBound = false;
	
	List<String> filePaths = null;

    public void onCreate(Bundle savedInstanceState) {
		music_list = new ArrayList<Music>();
        final FileFlattener ff = new FileFlattener();

        createLibrary(ff);	// create the library by grabbing all the files
    }

	private synchronized void initQueue() {
		mService.addMusicToQueue(music_list);
		mService.playNext();
	}

	//查找mp3文件
	private void createLibrary(final FileFlattener ff) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				ff.flattenFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music", 6);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void params) {
				filePaths = ff.getFlattenedFiles();
				Log.d(this.toString(),"Done getting the files from the file flatenner");
				
				for (String filePath : filePaths) {
					music_list.add(new Music(filePath));
				}

				if (mBound)
					initQueue();
			}
        	
        }.execute();
	}

}
