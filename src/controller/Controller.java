package controller;

import movement.MoveCommand;
import movement.MovementPossibility;
import player.Player;

public abstract class Controller {
	
	/*
	 * subclasses of this class are the operating interface of every Player.
	 */
	
	protected final Player player;		//owner of this Controller
	protected boolean enabled;			//input allowed?
	
	//Constructor
	protected Controller(Player player) {
		this.player = player;
	}
	
	//this method is supposed to initialize the full process of a player's action.
	public abstract void act();
	
	//setter
	public void setEnabled(boolean b) {
		enabled = b;
	}
	
	//getter
	public boolean isEnabled() {
		return enabled;
	}
	
	//executes a MovementPossibility for the Controller's owner (may fail)
	protected boolean execute(MovementPossibility move) {
		if (enabled && move.executableBy(player)) {
			MoveCommand.execute(move);
			return true;
		}
		return false;
	}
}
