package movement;

import java.util.ArrayList;

import movement.MovementPossibility.*;
import navigation.*;
import piece.Piece;

public class MovementScanner {
	
	private final Piece piece;												//owner of this scanner
	private final Direction direction;										//movement direction to scan
	private final ArrayList<MovementPossibility> movementPossibilities;		//possible movements scanned
	private final ArrayList<Piece> contacts;								//pieces found while scanning
	private final Behaviour behaviour;										//modification for individual scanning
	
	//Constructor
	public MovementScanner(Piece piece, Direction direction, Behaviour behaviour) {
		this.piece = piece;
		this.direction = direction;
		this.behaviour = behaviour;
		movementPossibilities = new ArrayList<MovementPossibility>();;
		contacts = new ArrayList<Piece>();
	}
	
	//delete old information
	public void clean() {
		movementPossibilities.clear();
		contacts.clear();
	}
	
	//starts process of scanning
	public void scan() {
		behaviour.scan(this);
		behaviour.modify(this);
	}
	
	//might modify some MovementPossibilities
	public boolean validate() {
		return behaviour.validate(this);
	}
	
	//tests if any check was found while scanning
	private void scanCheck() {
		Piece king = piece.opponentPlayer().getKing();
		if (contacts.size() > 0 && contacts.get(0) == king) {
			check();
		} else if (contacts.size() > 1 && contacts.get(1) == king) {
			blockedCheck();
		}
	}
	
	//call expire method for this scanner to be deleted
	public void expire() {
		piece.removeScanner(this);
	}
	
	//returns scanned possibilities
	public ArrayList<MovementPossibility> getMoves() {
		return movementPossibilities;
	}
	
	//creates new MovementPossibility
	private MovementPossibility createMove(Position target) {
		if (target != null) {
			MovementPossibility m = new MovementPossibility(piece, target);
			if (isBlocked()) {
				m.block();
			}
			return m;
		}
		return null;
	}
	
	//if there is any piece on the pointed position, this method adds it to contacts
	private void scanContact(Position p) {
		if (p.hasPiece()) {
			contacts.add(p.getPiece());
		}
	}
	
	//returns whether there is an obstacle found on this line yet
	private boolean isBlocked() {
		return contacts.size() > 1 || (hasContact() && contacts.get(0) != piece.opponentPlayer().getKing());
	}
	
	//initializes consequences of check
	private void check() {
		ArrayList<Position> threatZone = getLine(piece.getPosition(), 0);
		for (Piece p : piece.opponentPlayer().getPieces()) {
			p.limitMovement(threatZone);
		}
	}
	
	//initiallizes consequences of a blocked check
	private void blockedCheck() {
		ArrayList<Position> threatZone = getLine(piece.getPosition(), 1);
		Piece defender = contacts.get(0);
		if (!defender.hasSameColor(piece)) defender.limitMovement(threatZone);
	}
	
	//returns a list of positions that are allowed to enter for the opponent's pieces
	private ArrayList<Position> getLine(Position start, int maxContacts) {
		int contacts = 0;
		ArrayList<Position> line = new ArrayList<Position>();
		Position coord = start;
		
		do {
			line.add(coord);
			coord = coord.getNeighbor(direction);
			
			if (coord == null) {
				contacts = maxContacts + 1;
			} else if (coord.hasPiece()) {
				contacts++;
			}
			
		} while (contacts <= maxContacts);
		
		return line;
	}
	
	private boolean hasContact() {
		return contacts.size() > 0;
	}
	
	//modifiers for special movements
	public enum Behaviour {
		Line(lineScanning(), noModification(), noValidation()),
		Short(shortScanning(), noModification(), noValidation()),
		ShortSensitive(shortScanning(), preventDanger(), sensitiveValidation()),
		PawnInoffensive(shortScanning(), suppressAttack(), noValidation()),
		PawnOffensive(pawnAttackScanning(), forceAttack(), noValidation()),
		DoubleMove(doubleMoveScanning(), suppressAttack(), noValidation()),
		Castling(castlingScanning(), preventDanger(), castlingValidation())
		;

		private final ScanningMethod scanning;
		private final ModificationMethod modification;
		private final ValidationMethod validation;
		
		//Constructor
		Behaviour(ScanningMethod scanning, ModificationMethod modification, ValidationMethod validation) {
			this.scanning = scanning;
			this.modification = modification;
			this.validation = validation;
		}
		
		void scan(MovementScanner s) {scanning.scan(s);}						//the Behaviour defines the scanning method
		void modify(MovementScanner s) {modification.modify(s);}				//the Behaviour decides whether there are any modifications made
		boolean validate(MovementScanner s) {return validation.validate(s);}	//the Behaviour decides how to validate
	}
	
	private interface ValidationMethod {boolean validate(MovementScanner s);}
	private interface ModificationMethod {void modify(MovementScanner s);}
	private interface ScanningMethod {void scan(MovementScanner s);}
	
	/*
	 * some different scanning methods following
	 */
	
	//lines are used by: Queen, Rook, Bishop
	private static ScanningMethod lineScanning() {
		return new ScanningMethod() {
			
			@Override
			public void scan(MovementScanner l) {
				Position pointer = l.piece.getPosition().getNeighbor(l.direction);
				while (pointer != null) {
					l.movementPossibilities.add(l.createMove(pointer));
					l.scanContact(pointer);
					pointer = pointer.getNeighbor(l.direction);
				}
				l.scanCheck();
			}
		};
	}
	
	//short scanning is used by: Knight, King, Pawn
	private static ScanningMethod shortScanning() {
		return new ScanningMethod() {
			
			@Override
			public void scan(MovementScanner l) {
				Position target = l.piece.getPosition().getNeighbor(l.direction);
				if (target != null) {
					l.movementPossibilities.add(l.createMove(target));
					l.scanContact(target);
				}
				l.scanCheck();
			}
		};
	}
	
	//pawn attack is used by: Pawn
	private static ScanningMethod pawnAttackScanning() {
		return new ScanningMethod() {
			
			@Override
			public void scan(MovementScanner l) {
				Position target = l.piece.getPosition().getNeighbor(l.direction);
				if (target != null) {
					l.movementPossibilities.add(l.createMove(target));
					l.movementPossibilities.add(new EnPassantPossibility(l.piece, target));
					l.scanContact(target);
				}
				l.scanCheck();
			}
		};
	}
	//double move is used by: Pawn
	private static ScanningMethod doubleMoveScanning() {
		return new ScanningMethod() {
			
			@Override
			public void scan(MovementScanner l) {
				Position pointer = l.piece.getPosition().getNeighbor(l.direction);
				if (pointer != null && !pointer.hasPiece()) {
					pointer = pointer.getNeighbor(l.direction);
					if (pointer != null) {
						l.movementPossibilities.add(l.createMove(pointer));
					}
				}
			}
		};
	}
	
	//castling is used by: King
	private static ScanningMethod castlingScanning() {
		return new ScanningMethod() {
			
			@Override
			public void scan(MovementScanner l) {
				Position target = l.piece.getPosition().relativePosition(l.direction, 2);
				l.movementPossibilities.add(new CastlingPossibility(l.piece, target));
			}
		};
	}
	
	//most scanners don't need any modification
	private static ModificationMethod noModification() {
		return new ModificationMethod() {

			@Override
			public void modify(MovementScanner s) {}
		};
	}
	
	//the Pawn's default movement may not be aggressive
	private static ModificationMethod suppressAttack() {
		return new ModificationMethod() {

			@Override
			public void modify(MovementScanner s) {
				for (MovementPossibility m : s.movementPossibilities) {
					m.suppressAttack();
				}
			}
		};
	}
	
	//the Pawn's aggressive movement must kill a piece
	private static ModificationMethod forceAttack() {
		return new ModificationMethod() {
			@Override
			public void modify(MovementScanner s) {
				for (MovementPossibility m : s.movementPossibilities) {
					m.forceAttack();
				}
			}
		};
	}
	
	//the King may not move to any dangerous position
	private static ModificationMethod preventDanger() {
		return new ModificationMethod() {
			@Override
			public void modify(MovementScanner s) {
				for (MovementPossibility m : s.movementPossibilities) {
					m.preventDanger();
				}
			}
		};
	}
	
	//most scanners don't need to validate
	private static ValidationMethod noValidation() {
		return new ValidationMethod() {

			@Override
			public boolean validate(MovementScanner s) { return true;}
		};
	}
	
	//decides whether the King's castling is really executable
	private static ValidationMethod castlingValidation() {
		return new ValidationMethod() {

			@Override
			public boolean validate(MovementScanner l) {
				MovementPossibility castling = l.movementPossibilities.get(0);
				if (!castling.isLegal()) return true;
				
				Position pointer = l.piece.getPosition();
				for (int i = 0; i < 3; i++) {
					if ((pointer.hasPiece() && pointer.getPiece() != l.piece) || pointer.getThreatFor(l.piece) > 0) {
						castling.block();
					}
					pointer = pointer.getNeighbor(l.direction);
				}
				return true;
			}
		};
	}
	
	//decides whether the King's target really isn't dangerous
	private static ValidationMethod sensitiveValidation() {
		return new ValidationMethod() {

			@Override
			public boolean validate(MovementScanner s) {
				for (MovementPossibility m : s.movementPossibilities) {
					if (m.preventDanger()) return false;
				}
				return true;
			}
		};
	}
	
}
