package piece;

import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class Queen extends Piece{

	public static final Type classType = Type.Queen;
	
	//default Constructor
	public Queen(Player player, Position position, char rowInitial) {
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	//promotion Constructor
	Queen(Pawn pawn) {
		super(pawn.player, classType, pawn.position, pawn.code);
	}
	
	@Override
	protected void initMovement() {
		movementScanners.add(new MovementScanner(this, Direction.North, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.NorthEast, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.East, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.SouthEast, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.South, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.SouthWest, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.West, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.NorthWest, Behaviour.Line));
	}
	
	
}

