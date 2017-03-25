package com.tedchen.play.music;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;
//import java.util.Random;


public class Queue {
	private List<Music> queue = new ArrayList<Music>();
	private int current = 0;
	private boolean random = false;
	private List<Music> random_queue = new ArrayList<Music>();
//	private Random rnd = new Random();
	
	public Music getCurrentlyPlaying() {
		return queue.get(current);
	}
	
	public void addMusicToQueue(Music music) {
		if (!queue.contains(music)){
			queue.add(music);
//			random_queue.add(rnd.nextInt(random_queue.size()), music);
			Log.d(this.toString(), "added " + music.toString());
		}
	}
	
	public void removeMusicFromQueue(Music music) {
		queue.remove(music);
		random_queue.remove(music);
	}
	
	public void addMusicToQueue(List<Music> list, boolean is_random) {
		if (!is_random) {
			for (Music music : list){
				addMusicToQueue(music);
			}
		} else {
            Random ran = new Random(System.currentTimeMillis());
			//创建一个连续的数组，每次随机一个数字作为下标，从数组取出
			List<Integer> seq_list = new ArrayList<Integer>(list.size());
			for (int i=0; i<list.size(); ++i) {
				seq_list.add(i);
			}
            //随机取musiclist里的元素
			for (int i = 0; i < list.size(); ++i){
                int rand_seq = ran.nextInt(seq_list.size());
                int rand_idx = seq_list.get(rand_seq);
                seq_list.remove(rand_seq);
				addMusicToQueue(list.get(rand_idx));
			}
		}
	}
	
	//public void addMusicToQueue(Queue queue) {
	//	addMusicToQueue(queue.queue);
	//}
	
	public void addMusicToQueue(Music music, int index) {
		queue.add(index, music);
	}
	
	public int getSizeOfQueue() {
		return queue.size();
	}
	
	public Music next() {
		current = queue.size()==0 ? queue.size() : ++current % queue.size();
		if (queue.size() >= 1 && random) return random_queue.get(current%random_queue.size());
		else if (queue.size() >= 1) return queue.get(current%queue.size());
		else return null;
	}
	
	public Music last() {
		current = --current % queue.size();
		if (queue.size() >= 1 && random) {
			return random_queue.get(current%random_queue.size());
		}
		else if (queue.size() >= 1) {
			return queue.get(current%queue.size());
		}
		else return null;
	}
	
	public Music playGet(int position) {
		current = position;
		return queue.get(position);
	}
	
	public void clearQueue() {
		queue.clear();
		current = 0;
	}


}
