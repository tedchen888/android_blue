package com.tedchen.play.game.spirit.detail;

import com.tedchen.play.game.spirit.DefancePlant;
import com.tedchen.play.game.utils.CommonUtil;

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
		this.runAction(CommonUtil.getRepeatAnimation(shakeFrames, 11,
				"image/plant/nut/p_3_%02d.png"));
	}

}
