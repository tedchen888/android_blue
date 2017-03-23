package com.example.android.music;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileFlattener {
	private static List<String> allFiles = new ArrayList<String>();
	
	public void flattenFolder(String path, int level){
		File origin = new File(path);
		File[] files = origin.listFiles();
		
		if (files == null) return; 
		
		for (File file : files) {
			if (file.isDirectory() && !file.isHidden() && file.canRead() && level > 0) {
				flattenFolder(path + "/" + file.getName(), level - 1);
			}
			else if (file.isFile() && file.canRead() && file.getName().endsWith("mp3")) {
				allFiles.add(file.getAbsolutePath());
				Log.d("Music", "find mp3: " + toString());
			}
	    }
	}

	public static void getAssetsMusics(AssetManager am, String parent) {
		try {
			String fileNames[] = am.list(parent);
			if (fileNames.length > 0) {//如果是目录则递归
				for (String fileName : fileNames) {
					getAssetsMusics(am, fileName);
				}
			}
			else { //如果是文件
				allFiles.add(parent);
				Log.i("Music", "find mp3: " + parent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getFlattenedFiles(){
		return allFiles;
	}
}
