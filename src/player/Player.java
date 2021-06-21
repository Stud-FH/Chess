package player;

import java.util.ArrayList;

import controller.Controller;
import main.Game;
import navigation.Grid;
import piece.*;

public enum Player {
	White(1,0), Black(6,7);		//only two players and two colors
	
	private final int pawnColumn, rearColumn;	//position coordinates for start piece formation
	private Controller controller;				//Controller is able to execute moves
	private final ArrayList<Piece> pieces;		//pieces owned by this player
	private final ArrayList<Piece> graveyard;	//died pieces owned by this player
	
	//Constructor
	Player(int pawnColumn, int rearColumn) {
		this.pawnColumn = pawnColumn;
		this.rearColumn = rearColumn;
		pieces = new ArrayList<Piece>();
		graveyard = new ArrayList<Piece>();
	}
	
	//set up starting piece formation
	public void init(Controller controller) {
		this.controller = controller;
		
		pieces.clear();
		graveyard.clear();
		
		pieces.add(new Rook		(this, Grid.getPosition(0, rearColumn), 'A'));
		pieces.add(new Knight	(this, Grid.getPosition(1, rearColumn), 'B'));
		pieces.add(new Bishop	(this, Grid.getPosition(2, rearColumn), 'C'));
		pieces.add(new Queen	(this, Grid.getPosition(3, rearColumn), 'D'));
		pieces.add(new King		(this, Grid.getPosition(4, rearColumn), 'E'));
		pieces.add(new Bishop	(this, Grid.getPosition(5, rearColumn), 'F'));
		pieces.add(new Knight	(this, Grid.getPosition(6, rearColumn), 'G'));
		pieces.add(new Rook		(this, Grid.getPosition(7, rearColumn), 'H'));
		pieces.add(new Pawn		(this, Grid.getPosition(0, pawnColumn), 'A'));
		pieces.add(new Pawn		(this, Grid.getPosition(1, pawnColumn), 'B'));
		pieces.add(new Pawn		(this, Grid.getPosition(2, pawnColumn), 'C'));
		pieces.add(new Pawn		(this, Grid.getPosition(3, pawnColumn), 'D'));
		pieces.add(new Pawn		(this, Grid.getPosition(4, pawnColumn), 'E'));
		pieces.add(new Pawn		(this, Grid.getPosition(5, pawnColumn), 'F'));
		pieces.add(new Pawn		(this, Grid.getPosition(6, pawnColumn), 'G'));
		pieces.add(new Pawn		(this, Grid.getPosition(7, pawnColumn), 'H'));
		
		getKing().initCastling();		//for initializing castling the player's pieces must be initialized first
	}
	
	//get other player
	public Player opponent() {
		return this == White? Black : White;
	}
	
	//remove died piece from list
	public void losePiece(Piece p) {
		pieces.remove(p);
		graveyard.add(p);
	}
	
	//let controller execute a move
	public void act() {
		controller.act();
	}
	
	//delete old information
	public void prepareCalculation() {
		for (Piece p : pieces) { 
			p.prepareCalculation();
		}
	}
	
	//scan possible movements
	public void calculate() {
		for (Piece p : pieces) { 
			p.calculate();
		}
	}
	
	//validate possible movements
	public boolean validateCalculation() {
		for (Piece p : pieces) { 
			if (!p.validateCalculation()) return false;
		}
		return true;
	}
	
	//getter
	public King getKing() {
		return (King) getPiece(initial() + "KE");
	}
	
	//king currently offended?
	public boolean isInCheck() {
		if (getKing().getPosition().getThreatFor(getKing()) > 0) {
			return true;
		}
		return false;
	}
	
	//able to move any piece?
	public boolean hasLegalMovement() {
		for (Piece p : pieces) {
			if (p.hasLegalMovement()) {
				return true;
			}
		}
		return false;
	}
	
	//get list of movable pieces
	public ArrayList<Piece> getMovablePieces() {
		ArrayList<Piece> movablePieces = new ArrayList<Piece>();
		
		for (Piece p : pieces) {
			if (p.hasLegalMovement()) {
				movablePieces.add(p);
			}
		}
		return movablePieces;
	}

	//getter
	public Piece getPiece(String code) {
		for (Piece p : pieces) {
			if (p.code.equals(code)) {
				return p;
			}
		}
		return null;
	}

	//getter
	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	//getter
	public ArrayList<Piece> getGraveyrd() {
		return graveyard;
	}
	
	//get color initial
	public char initial() {
		return name().charAt(0);
	}
	
	//currently allowed to move?
	public boolean isActive() {
		return Game.getActivePlayer() == this;
	}
}
