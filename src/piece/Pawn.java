package piece;

import gui.GUI;
import movement.MovementScanner;
import movement.MovementScanner.Behaviour;
import navigation.*;
import player.Player;

public class Pawn extends Piece {
	
	public static final Type classType = Type.Pawn;

	private MovementScanner doubleMove;					//only allowed as first move
	private boolean doubleMoveFlag, allowsEnPassant;	//flags for special moves
	
	//Constructor
	public Pawn(Player player, Position position, char rowInitial){
		super(player, classType, position, calculateCode(player, classType, rowInitial));
	}
	
	@Override
	protected void initMovement() {
		MovementScanner movingLine, eastAttack, westAttack;
		
		if (player == Player.White) {
			doubleMove = new MovementScanner(this, Direction.North, Behaviour.DoubleMove);
			movingLine = new MovementScanner(this, Direction.North, Behaviour.PawnInoffensive);
			eastAttack = new MovementScanner(this, Direction.NorthEast, Behaviour.PawnOffensive);
			westAttack = new MovementScanner(this, Direction.NorthWest, Behaviour.PawnOffensive);
		} else {
			doubleMove = new MovementScanner(this, Direction.South, Behaviour.DoubleMove);
			movingLine = new MovementScanner(this, Direction.South, Behaviour.PawnInoffensive);
			eastAttack = new MovementScanner(this, Direction.SouthEast, Behaviour.PawnOffensive);
			westAttack = new MovementScanner(this, Direction.SouthWest, Behaviour.PawnOffensive);
		}

		movementScanners.add(doubleMove);
		movementScanners.add(movingLine);
		movementScanners.add(eastAttack);
		movementScanners.add(westAttack);
	}
	
	@Override
	public void moveTo(Position target) {
		if (Math.abs(position.y - target.y) == 2) {
			doubleMoveFlag = true;
		}
		super.moveTo(target);
		doubleMove.expire();
		if (promotable()) {
			promote();
		}
	}
	
	@Override
	public void prepareCalculation() {
		super.prepareCalculation();
		if (doubleMoveFlag){
			doubleMoveFlag = false;
			allowsEnPassant = true;
		} else {
			allowsEnPassant = false;
		}
	}
	
	//getter
	public boolean allowsEnPassant() {
		return allowsEnPassant;
	}
	
	//gatter
	private boolean promotable() {
		return getY() == 0 || getY() == 7;
	}
	
	//replace this Pawn by different piece
	private void promote() {
		Type type = GUI.selectPromotion();
		switch(type) {
		case Rook:				player.getPieces().add(new Rook(this));
								break;
		case Knight:			player.getPieces().add(new Knight(this));
								break;
		case Bishop:			player.getPieces().add(new Bishop(this));
								break;
		default:				player.getPieces().add(new Queen(this));
		}
		die();
	}
}

