package com.tedchen.play.game.spirit;
/**
 * 防御型植物
 * @author Administrator
 *
 */
public abstract class DefancePlant extends Plant {

	public DefancePlant(String filepath) {
		super(filepath);
		life = 100;
	}

}
