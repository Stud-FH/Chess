package piece;

import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class Bishop extends Piece{

	public static final Type classType = Type.Bishop;
	
	//default Constructor
	public Bishop(Player player, Position position, char rowInitial) {
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	//promotion Constructor
	Bishop(Pawn pawn) {
		super(pawn.player, classType, pawn.position, pawn.code);
	}
	
	@Override
	protected void initMovement() {
		movementScanners.add(new MovementScanner(this, Direction.NorthEast, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.SouthEast, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.SouthWest, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.NorthWest, Behaviour.Line));
	}
	
	
}

