package navigation;

import java.util.ArrayList;

import movement.MovementPossibility;
import piece.Piece;
import player.Player;

public class Position {
	
	/*
	 * this class represents one field of the chess board
	 * don't confuse this with Square, which is a visible field displayed on GUI
	 */
	
	public final int x, y;											//coordinates
	public final String code;										//identification code
	private final ArrayList<MovementPossibility> incomingMoves;		//MovementPossibilities comig to this position
	private Piece piece;											//piece standing on this position
	
	//Constructor
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		code = getCode(x, y);
		incomingMoves = new ArrayList<MovementPossibility>();
	}
	
	//delete old information
	public void reset() {
		piece = null;
		incomingMoves.clear();
	}
	
	//is occupied?
	public boolean hasPiece() {
		return piece != null;
	}
	
	//has piece of certain player?
	public boolean hasPieceOf(Player player) {
		return hasPiece() && piece.player == player;
	}

	//getter
	public Piece getPiece() {
		return piece;
	}

	//setter
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	//how dangerous is this position for given piece?
	public int getThreatFor(Piece piece) {
		int threat = 0;
		for (MovementPossibility m : incomingMoves) {
			threat += m.getThreatFor(piece);
		}
		return threat;
	}
	
	//add incoming MovementPossibility
	public void addIncomingMove(MovementPossibility movementPossibility) {
		incomingMoves.add(movementPossibility);
	}
	
	//get relative position
	public Position getNeighbor(Direction direction) {
		return relativePosition(direction, 1);
	}

	//get relative position
	public Position relativePosition(Direction direction, int distance) {
		return Grid.getPosition(this.x + (distance * direction.x), this.y + (distance * direction.y));
	}
	
	//how many steps are needed to get to given position?
	public int getDistanceTo(Position target) {
		
		int distanceX = Math.abs(x - target.x);
		int distanceY = Math.abs(y - target.y);
		
		if (distanceX != distanceY) {
			return 1;		//must be a knight, can only jump once
		} else if (distanceX > distanceY) {
			return distanceX;
		}
		return distanceY;
		
	}

	//getter
	public String getString() {
		return code;
	}

	//generate code
	private String getCode(int x, int y) {
		return Grid.withinBorders(x, y)? (char) ('a'+ x) + Integer.toString(y + 1) : null;
	}
}
