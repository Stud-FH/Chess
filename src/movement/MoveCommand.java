package movement;

import java.io.Serializable;

import main.Game;
import movement.MovementPossibility.*;
import navigation.Grid;

public class MoveCommand implements Serializable{
	private static final long serialVersionUID = 1394144867938863356L;
	
	/*
	 * this is an easy serializable class that contains all information needed to execute a move
	 * information is saved by definite String codes
	 */

	//executes a MovementPossibility and hands created MoveCommand to history
	public static void execute(MovementPossibility move) {
		MoveCommand command;
		switch(move.getType()) {
		case EnPassant:		command = new EnPassantCommand((EnPassantPossibility) move);
							break;
		case Castling:		command = new CastlingCommand((CastlingPossibility) move);
							break;
		default:			command = new MoveCommand(move);
		}
		command.execute();
		Game.getHistory().expand(command);
		Game.nextMove();
	}

	protected final String pieceCode, targetCode;	//codes for piece and target
	
	//Constructor
	private MoveCommand(MovementPossibility move) {
		pieceCode = move.piece.code;
		targetCode = move.target.code;
	}
	
	//move piece to target
	public void execute() {
		Game.getPiece(pieceCode).moveTo(Grid.getPosition(targetCode));
	}
	
	//getter (only displayed name of move)
	public String getString() {
		return pieceCode.charAt(0) + targetCode;
	}
	
	/*
	 * specialized subclass for Pawn's en passant
	 */
	public static class EnPassantCommand extends MoveCommand {
		private static final long serialVersionUID = 7499164431559798660L;
		
		private final String victimCode;	//must kill a piece
		
		//Constructor
		private EnPassantCommand(EnPassantPossibility move) {
			super(move);
			victimCode = move.getContact().code;
		}
		
		@Override
		public void execute() {
			super.execute();
			Game.getPiece(victimCode).die();
		}
	}
	
	/*
	 * specialized subclass for King's castling
	 */
	public static class CastlingCommand extends MoveCommand {
		private static final long serialVersionUID = 1892151788358446987L;
		
		private final String rookCode, rookTargetCode;	//rook must be moved too
		
		private CastlingCommand(CastlingPossibility move) {
			super(move);
			rookCode = move.rook.code;
			rookTargetCode = move.rookTarget.code;
		}
		
		@Override
		public void execute() {
			super.execute();
			Game.getPiece(rookCode).moveTo(Grid.getPosition(rookTargetCode));
		}
		
		@Override
		public String getString() {
			return "O-O";
		}
	}
}
