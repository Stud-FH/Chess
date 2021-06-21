package controller;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import gui.GUI;
import gui.ImageFactory;
import gui.Square;
import movement.MovementPossibility;
import navigation.*;
import piece.Piece;
import player.Player;

public class VisualController extends Controller {
	
	/*
	 * this subclass of Controller creates a visible and interactive chess board for the user to give commands
	 */
	
	private final JPanel board;			//chess board containing 64 Squares
	private final Square[] squares;		//64 Squares (interactive JButtons)
	
	private Piece selection;			//currently selected Piece to move

	//Constructor
	public VisualController(Player player) {
		super(player);
		board = new JPanel();
		board.setLayout(new GridLayout(8, 8));
		board.setPreferredSize(GUI.dimension);
		
		squares = new Square[64];
		for (int i = 0; i < 64; i++) {
			Square s = new Square(Grid.getPosition(i % 8, i / 8));
			s.addMouseListener(focusListener());
			squares[i] = s;
			board.add(s);
		}
	}
	
	//getter
	public JPanel getBoard() {
		return board;
	}

	@Override
	public void act() {
		GUI.setController(this);
		setEnabled(true);
	}
	
	//sets a mark at the currently regarded Square
	private void inspect(Square square) {
		square.setInspected(true);
		
		if (selection == null) {
			drawMovementPossibilities(square.coord.getPiece());
		}
	}
	
	//removes inspection mark form Square
	private void unfocus(Square square) {
		square.setInspected(false);
		
		if (selection == null) {
			clearDrawing();
		}
	}
	
	//user clicked onto this Square
	private void select(Square square) {
		clearDrawing();											//no need to emphasize any Squares any longer
		
		if (selection != null && selection.canMoveTo(square.coord)) {
			
			execute(selection.getMoveTo(square.coord));			//try to move selected Piece to selected target
			
		} else {
			doSelection(square);								//try to select new Piece
		}
	}
	
	//tries to select Piece on pointed Square
	private void doSelection(Square square) {
		
		if (square.coord.hasPieceOf(player)) {
			selection = square.coord.getPiece();
			square.drawImage(ImageFactory.Selected);
			drawMovementPossibilities(selection);
		} else {
			selection = null;
		}
	}
	
	//initializes emphasis of Squares the currently regarded Piece is allowed to move to
	private void drawMovementPossibilities(Piece piece) {
		if (piece != null) {
			for (MovementPossibility m : piece.getLegalMoves()) {
				getSquare(m.target).drawImage(m.isAggressive()? ImageFactory.AttackingPossibillity : ImageFactory.MovementPossibillity);
			}
		}
	}
	
	//removes all additional drawing marks from all Squares
	private void clearDrawing() {
		for (Square s : squares) {
			s.drawImage(null);		//removes all tags of this square
		}
	}
	
	//getter
	private Square getSquare(Position coord) {
		for (Square s : squares) {
			if (s.coord == coord) {
				return s;
			}
		}
		return null;
	}
	
	//returns MouseListener that notifies this VisualController of every mouse interaction with a jComponent (meant for Squares)
	private MouseListener focusListener() {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (isEnabled()) {
					select((Square) e.getSource());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				inspect((Square) e.getSource());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				unfocus((Square) e.getSource());
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		};
	}
}
