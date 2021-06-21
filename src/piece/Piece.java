package piece;

import java.util.ArrayList;

import movement.MovementScanner;
import movement.MovementPossibility;
import navigation.*;
import player.Player;


public abstract class Piece {
	
	/*
	 * pieces are also called chessmen
	 */
	
	/*
	 * the piece's type is used for identification
	 */
	public static enum Type {
		King,
		Queen,
		Rook,
		Knight,
		Bishop,
		Pawn;
		
		//get name initial
		public char initial() {
			return name().charAt(0);
		}
	}
	
	//generate identification code
	protected static String calculateCode(Player player, Type type, char rowInitial) {
		return "" + player.initial() + type.initial() + rowInitial;
	}
	
	public final Type type;											//type of piece
	public final String code;										//identification code
	public final Player player;										//owner of this piece
	protected final ArrayList<MovementScanner> movementScanners;	//kinds of movements executable
	protected int defaultValue;										//for automatic player
	protected int value;											//for automatic player
	protected Position position;									//current position
	protected ArrayList<Position> forcedTargets;					//only able to move to one of those targets
	
	//Constructor
	protected Piece(Player player,Type type, Position position, String code) {
		this.player = player;
		this.type = type;
		this.position = position;
		this.code = code;
		movementScanners = new ArrayList<MovementScanner>();
		
		initMovement();
	}
	
	//different kinds of movement abilities are initialized in subclasses
	protected abstract void initMovement();

	//delete old information
	public void prepareCalculation() {
		forcedTargets = null;
		for (MovementScanner l : movementScanners) {
			l.clean();
		}
		position.setPiece(this);
	}
	
	//scan possible movements
	public void calculate() {
		for (MovementScanner l : movementScanners) {
			l.scan();
		}
	}
	
	//validate possible movements
	public boolean validateCalculation() {
		for (MovementScanner l : movementScanners) {
			if (!l.validate()) {
				return false;
			}
		}
		if (filterMoves()) return false;
		return true;
	}
	
	//change position, perhaps kill piece
	public void moveTo(Position target) {
		if (target != null) {
			if (target.hasPiece()) {
				target.getPiece().die();
			}
			position = target;
		}
	}
	
	//getter
	public int getX() {
		return position.x;
	}

	//getter
	public int getY() {
		return position.y;
	}

	//getter
	public Position getPosition() {
		return position;
	}
	
	//dissapear from game
	public void die() {
		player.losePiece(this);
	}

	//is owned by the same player?
	public boolean hasSameColor(Piece piece) {
		return piece.player == player;
	}
	
	//delete movement ability
	public void removeScanner(MovementScanner movementScanner) {
		movementScanners.remove(movementScanner);
	}
	
	//how dangerous is this piece for the targeted one?
	public int getThreatFor(Piece victim) {
		if (victim != null && !hasSameColor(victim)) {
			
			//TODO specify for ai
			
			return 1;
		}
		return 0;
	}

	//getter
	public int getValue() {
		return value;
	}

	//getter
	public MovementPossibility getMoveTo(Position target) {
		for (MovementPossibility m : getLegalMoves()) {
			if (m.target == target) {
				return m;
			}
		}
		return null;
	}
	
	//able to move there?
	public boolean canMoveTo(Position target) {
		return getMoveTo(target) != null;
	}

	//getter
	public ArrayList<MovementPossibility> getLegalMoves() {
		
		ArrayList<MovementPossibility> legalMoves = new ArrayList<MovementPossibility>();
		
		for (MovementScanner l : movementScanners) {
			for (MovementPossibility m : l.getMoves()) {
				if (m.isLegal()) {
					legalMoves.add(m);
				}
			}
		}
		return legalMoves;
	}
	
	//find direction to target
	public Direction getDirection(Piece target) {
		return Direction.get(target.position.x - this.position.x, target.position.y - this.position.y);
	}
	
	//get opponent player
	public Player opponentPlayer() {
		return player.opponent();
	}
	
	//this piece is only allowed to move to one of those targets
	public void limitMovement(ArrayList<Position> safeTiles) {
		if (this.forcedTargets == null) {
			this.forcedTargets = safeTiles;
		} else {
			for (Position c : this.forcedTargets) {
				if (!safeTiles.contains(c)) {
					this.forcedTargets.remove(c);
				}
			}
		}
	}
	
	//illegalize movement possibilities to illegal targets
	protected boolean filterMoves() {
		if (forcedTargets != null) {
			for (MovementPossibility m : getLegalMoves()) {
				
				if (!forcedTargets.contains(m.target)) {
					m.block();
				}
			}
			forcedTargets = null;
			return true;
		}
		return false;
	}

	//able to move anywhere?
	public boolean hasLegalMovement() {
		return getLegalMoves().size() > 0;
	}
	
	//player is allowed to move?
	public boolean isActive() {
		return player.isActive();
	}
	
	
}
