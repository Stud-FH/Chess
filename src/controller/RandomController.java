package controller;

import java.util.ArrayList;

import movement.MovementPossibility;
import player.Player;

public class RandomController extends Controller {

	/*
	 * This subclass of Controller automatically executes a random legal movement for a player
	 */
	
	//Constructor
	public RandomController(Player player) {
		super(player);
	}

	@Override
	public void act() {
		setEnabled(true);
		
		ArrayList<MovementPossibility> movementPossibilities;
		
		do {
			movementPossibilities = player.getPieces().get((int) (Math.random() * player.getPieces().size() - 1)).getLegalMoves();
		} while (movementPossibilities.size() == 0);

		if (execute(movementPossibilities.get((int) Math.random() * (movementPossibilities.size() - 1)))) {
			setEnabled(false);
			return;
		} else {
			act();
		}
	}

}
