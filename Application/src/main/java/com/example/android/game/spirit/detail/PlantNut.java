package com.example.android.game.spirit.detail;

import com.example.android.game.spirit.DefancePlant;

import org.cocos2d.nodes.CCSpriteFrame;

import java.util.ArrayList;


public class PlantNut extends DefancePlant {
	private static ArrayList<CCSpriteFrame> shakeFrames;// 摇晃序列帧

	public PlantNut() {
		super("image/plant/nut/p_3_01.png");
		// TODO Auto-generated constructor stub

	}

	@Override
	public void BaseAction() {
		// TODO Auto-generated method stub
		this.runAction(com.example.android.game.utils.CommonUtil.getRepeatAnimation(shakeFrames, 11,
				"image/plant/nut/p_3_%02d.png"));
	}

}
