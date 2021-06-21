package piece;

import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class King extends Piece{

	public static final Type classType = Type.King;
	
	private MovementScanner castling1, castling2;	//castling into 2 directions
	
	//Constructor
	public King(Player player, Position position, char rowInitial) {
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	//You can't make a King out of a Pawn

	@Override
	protected void initMovement() {
		movementScanners.add(new MovementScanner(this, Direction.North, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.NorthEast, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.East, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.SouthEast, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.South, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.SouthWest, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.West, Behaviour.ShortSensitive));
		movementScanners.add(new MovementScanner(this, Direction.NorthWest, Behaviour.ShortSensitive));
	}
	
	@Override
	protected boolean filterMoves() {
		return false;	//Moves are filtered automatically
	}
	
	@Override
	public void moveTo(Position target) {
		super.moveTo(target);
		if (castling1 != castling2) {	//!= null
			castling1.expire();
			castling1 = null;
			castling2.expire();
			castling2 = null;
		}
	}
	
	//init castling scanners
	public void initCastling() {
		Rook rook1 = (Rook) player.getPiece(player.initial() + "RA");
		castling1 = new MovementScanner(this, Direction.East, Behaviour.Castling);
		movementScanners.add(castling1);
		rook1.setCastling(castling1);
		
		Rook rook2 = (Rook) player.getPiece(player.initial() + "RH");
		castling2 = new MovementScanner(this, Direction.West, Behaviour.Castling);
		movementScanners.add(castling2);
		rook2.setCastling(castling2);
	}
	
}
