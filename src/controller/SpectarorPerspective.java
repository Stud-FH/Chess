package controller;

import player.Player;

public class SpectarorPerspective extends VisualController {
	
	/*
	 * This Subclass of VisualController creates a visible chess board for a user that isn't meant to interact with the game
	 */

	//Constructor
	public SpectarorPerspective() {
		super(Player.White);
		super.setEnabled(false);
	}
	
	@Override
	public void setEnabled(boolean b) {
		//Not meant to be enabled
	}

}
