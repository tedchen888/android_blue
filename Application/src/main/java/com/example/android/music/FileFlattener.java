package com.example.android.music;
import android.content.res.AssetManager;
import android.text.TextUtils;

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
			}
	    }
	}

	public static void printAssetsFiles(AssetManager am, String parent, String current, String indent) {
		if (!TextUtils.isEmpty(current)) {
			System.out.println(indent + current);
			indent += "\t";
		}

		// 列出子文件
		String[] files;
		String currentParent;
		try {
			if (TextUtils.isEmpty(parent)) {
				currentParent = current;
			} else {
				currentParent = parent + "/" + current;
			}

			files = am.list(currentParent);
		} catch (IOException e1) {
			return;
		}

		if (files != null && files.length > 0) {
			for (String f : files) {
				printAssetsFiles(am, currentParent, f, indent);
			}
		}

	}

	public List<String> getFlattenedFiles(){
		return allFiles;
	}
}
