package piece;

import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class Rook extends Piece {

	public static final Type classType = Type.Rook;
	
	private MovementScanner castling;
	
	//default Constructor
	public Rook(Player player, Position position, char rowInitial) {
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	//promotion Constructor
	Rook(Pawn pawn) {
		super(pawn.player, classType, pawn.position, pawn.code);
	}

	@Override
	protected void initMovement() {
		movementScanners.add(new MovementScanner(this, Direction.North, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.East, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.South, Behaviour.Line));
		movementScanners.add(new MovementScanner(this, Direction.West, Behaviour.Line));
	}
	
	@Override
	public void moveTo(Position target) {
		super.moveTo(target);
		if (castling != null) {
			castling.expire();
			castling = null;
		}
	}
	
	@Override
	public void die() {
		super.die();
		if (castling != null) castling.expire();
	}
	
	void setCastling(MovementScanner castling) {
		this.castling = castling;
	}
}
