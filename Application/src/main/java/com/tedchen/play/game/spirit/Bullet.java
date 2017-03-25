package com.tedchen.play.game.spirit;

import com.tedchen.play.game.base.BaseElement;

public abstract class Bullet extends BaseElement {
	protected int attack = 10;// 攻击力
	protected int speed = 60;// 移动速度
	public Bullet(String filepath) {
		super(filepath);
		// TODO Auto-generated constructor stub
	}
	public abstract void BaseAction();
	/**
	 * 移动
	 */
	public abstract void move();

	public int getAttack() {
		return attack;
	}
	
}
