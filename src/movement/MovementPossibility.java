package movement;

import navigation.*;
import piece.Pawn;
import piece.Piece;
import piece.Rook;
import player.Player;

public class MovementPossibility {
	
	protected Quality quality;			//contains information: dangerous? legal?
	public final Piece piece;			//piece moved
	protected final Piece contact;		//offended or protected piece
	public final Position target;		//target position
	
	//Constructor
	public MovementPossibility(Piece piece, Position target) {
		this.piece = piece;
		this.target = target;
		contact = target.getPiece();
		quality = Quality.get(this);
		
		target.addIncomingMove(this);
	}
	
	//getter
	public Type getType() {
		return Type.Default;
	}

	//getter
	public boolean isLegal() {
		return quality.legal;
	}

	//is movement of this player?
	public boolean executableBy(Player player) {
		return isLegal() && player == piece.player;
	}
	
	//illegalize, isn't dangerous
	public void block() {
		quality = Quality.Blocked;
	}
	
	//illegalize if no attack
	public void forceAttack() {
		quality = Quality.getOffensive(this);
	}
	
	//illegalize if attack
	public void suppressAttack() {
		quality = Quality.getPieceful(this);
	}
	
	//illegalize if running into dangerous target
	public boolean preventDanger() {
		if (isLegal() && target.getThreatFor(piece) > 0) {
			block();
			return true;
		}
		return false;
	}

	//getter
	public int getThreatFor(Piece victim) {
		return quality.dangerous? piece.getThreatFor(victim) : 0;
	}

	//EnPassant returns a diferent piece
	public Piece getContact() {
		return contact;
	}

	//getter
	public boolean hasContact() {
		return getContact() != null;
	}

	//getter
	public boolean isAggressive() {
		return quality == Quality.Attack;
	}
	
	//target is part of given list?
	public boolean arrivesWithin(Position[] tileArray) {
		for (Position t : tileArray) {
			if (t == target) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * movement types existing
	 */
	public enum Type {
		Default, EnPassant, Castling;
	}
	
	/*
	 * contains information: legal? dangerous?
	 */
	private enum Quality {
		Movement(true, true),
		Attack(true, true),
		Cover(false, true),
		Blocked(false, false),
		Inoffensive(true, false),
		Threat(false, true);
		
		final boolean legal, dangerous;			//information needed by MovementPossibility
		
		//Constructor
		Quality(boolean legal, boolean dangerous) {
			this.legal = legal;
			this.dangerous = dangerous;
		}

		//get generated Quality
		static Quality get(MovementPossibility movementPossibility) {
			if (movementPossibility.hasContact()) {
				return movementPossibility.getContact().hasSameColor(movementPossibility.piece)? Cover : Attack;
			}
			return Movement;
		}

		//get generated Quality without peaceful ones
		static Quality getOffensive(MovementPossibility movementPossibility) {
			if (movementPossibility.hasContact()) {
				return movementPossibility.getContact().hasSameColor(movementPossibility.piece)? Cover : Attack;
			}
			return Threat;
		}
		
		//get generated Quality without offensive ones
		static Quality getPieceful(MovementPossibility movementPossibility) {
			return movementPossibility.hasContact()? Blocked : Inoffensive;
		}
	}
	
	/*
	 * subclass for special Pawn move "en passant"
	 */
	public static class EnPassantPossibility extends MovementPossibility {
		
		private Pawn victim;		//must kill a piece
		
		public EnPassantPossibility(Piece pawn, Position target) {
			super(pawn, target);
			
			try {
				victim = (Pawn) pawn.getPosition().getNeighbor(Direction.get(target.x - pawn.getPosition().x, 0)).getPiece();
				if (!victim.allowsEnPassant()) {
					block();
				}
			} catch (Exception e) {
				victim = null;
				block();
			}
		}
		
		@Override
		public Piece getContact() {
			return victim;
		}
		
		@Override
		public Type getType() {
			return Type.EnPassant;
		}
	}
	
	/*
	 * subclass for special King move "castling"
	 */
	public static class CastlingPossibility extends MovementPossibility {

		public final Rook rook;				//must know rook
		public final Position rookTarget;	//rook will be moved too
		
		//Constructor
		public CastlingPossibility(Piece king, Position target) {
			super(king, target);
			
			rook = initRook();
			rookTarget = initRookTarget();
			
			if (king.type != Piece.Type.King) {
				block();
			}
		}
		
		//find rook
		private Rook initRook() {
			String code = piece.player.initial() + "R" + (target.x < 4? "A" : "H");
			return (Rook) piece.player.getPiece(code);
		}
		
		//find rook target
		private Position initRookTarget() {
			return piece.getPosition().getNeighbor(piece.getDirection(rook));
		}
		
		@Override
		public Type getType() {
			return Type.Castling;
		}
		
	}

}
