package piece;

import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class Knight extends Piece {

	public static final Type classType = Type.Knight;
	
	//default Constructor
	public Knight(Player player, Position position, char rowInitial) {
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	//promotion Constructor
	Knight(Pawn pawn) {
		super(pawn.player, classType, pawn.position, pawn.code);
	}
	
	@Override
	protected void initMovement() {
		movementScanners.add(new MovementScanner(this, Direction.NorthNorthEast, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.EastNorthEast, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.EastSouthEast, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.SouthSouthEast, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.SouthSouthWest, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.WestSouthWest, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.WestNorthWest, Behaviour.Short));
		movementScanners.add(new MovementScanner(this, Direction.NorthNorthWest, Behaviour.Short));
	}
}
