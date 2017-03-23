package com.example.android.music;

import java.io.File;
import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;

public class Music {
    private static final int FILE_TYPE_ABSOOLUE = 0;
    private static final int FILE_TYPE_ASSETS = 1;

	private final String UNKNOWN = "Unknown";
	private String file_name;
    private AssetFileDescriptor musicFileDescriptor = null;
    private int file_type;
	private String name = UNKNOWN;
	private String artist = UNKNOWN;
	private String album = UNKNOWN;
	private int timesPlayed = 0;
	private String duration;
	private File albumCover;
	private int time;

	public Music(String filePath) {
        file_name = filePath;
        populateMusicData();
	}

	public Music(AssetManager am, String filePath) {
        file_name = filePath;
        file_type = FILE_TYPE_ASSETS;
        try {
            musicFileDescriptor = am.openFd(filePath);
        } catch (Exception e) {
            Log.d("Music", "open assets file error: " + filePath + " e:" + e.toString());
        }
        populateMusicData();
	}
	public Music(Music music) {
        file_name = music.file_name;
        musicFileDescriptor = music.musicFileDescriptor;
		name = music.name;
		artist = music.artist;
		album = music.album;
		timesPlayed = music.timesPlayed;
		duration = music.duration;
		albumCover = music.albumCover;
		
		Log.d("Music", "Done making the music object with following data: " + toString());
	}
	
	private void populateMusicData() {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        if (file_type == FILE_TYPE_ASSETS) {
            if (null == musicFileDescriptor) {
                Log.e("Music", "asset file not found!");
                return;
            }
            mmr.setDataSource(musicFileDescriptor.getFileDescriptor(),
                    musicFileDescriptor.getStartOffset(),
                    musicFileDescriptor.getLength());
        } else if (!file_name.isEmpty()) {
            File file = new File(file_name);
            if (file.exists()) {
                mmr.setDataSource(file.getAbsolutePath());
            }
        }
		
		String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		
		if (name != null && !name.equals("")) this.name = name;
		if (artist != null && !artist.equals("")) this.artist= artist;
		if (album != null && !album.equals("")) this.album = album;
		if (duration != null && !duration.equals("")){
			this.duration = duration;
			this.time = Integer.parseInt(this.duration)/1000;
		}
	}
	
    public void setMediaPlayerSource(MediaPlayer mMediaPlayer) {
        try {
            if (null != musicFileDescriptor) {
                mMediaPlayer.setDataSource(musicFileDescriptor.getFileDescriptor(),
                        musicFileDescriptor.getStartOffset(),
                        musicFileDescriptor.getLength());
            } else if (!file_name.isEmpty()) {
                File file = new File(file_name);
                if (file.exists()) {
                    mMediaPlayer.setDataSource(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            Log.d("Music", "setDataSource failed. " + file_name + " e:" + e.toString());
        }
    }
	//public String getMusicLocation() {
	//	return file_name;
	//}
	
	@Override
	public String toString() {
		return "Music [file=" + file_name + ", name=" + name + ", artist=" + artist + ", album=" + album + ", timesPlayed=" + timesPlayed + "]";
	}

	public String getFileName() {
		return file_name;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public int getTimesPlayed() {
		return timesPlayed;
	}

	public File getAlbumCover() {
		return albumCover;
	}

//	public String getPlayableFilePath() {
//		return file.getAbsolutePath();
//	}
	
	public String getDuration() {
		return duration;
	}
	
	public int getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((file_name == null) ? 0 : file_name.hashCode());
        result = prime * result + ((musicFileDescriptor == null) ? 0 : musicFileDescriptor.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Music other = (Music) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (file_name == null) {
			if (other.file_name != null)
				return false;
		} else if (!file_name.equals(other.file_name))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
