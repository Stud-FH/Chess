package navigation;

public class Grid {
	/*
	 * coordinate system of chess game
	 */
	
	//Singleton
	private static final Grid instance = new Grid();
	
	////////// STATIC METHODS //////////
	
	//Getters
	public static Position getPosition(int x, int y) {return instance.getCoordinate(x, y);}
	public static Position getPosition(String code) {return instance.getCoordinate(code);}
	
	//validation
	static boolean withinBorders(int x, int y) {
		return x >= 0 && x < 8 && y >= 0 && y < 8;
	}
	
	//Reset Coordinate System
	//Must be called at the beginning of each round, before calculating
	public static void reset() {
		for (Position p : instance.position) {
			p.reset();
		}
	}
	
	////////// CONSTRUCTOR //////////
	
	//Coordinate System
	private final Position[] position;
	
	public Grid() {
		
		position = new Position[64];
		for (int i = 0; i < position.length; i++) {
			position[i] = new Position(getX(i), getY(i));
		}
	}
	
	////////// PRIVATE METHODS //////////
	
	//Get Position by coordinates
	private Position getCoordinate(int x, int y){
		return withinBorders(x, y)? position[getIndex(x, y)]: null;
	}
	
	//Get Position by code
	private Position getCoordinate(String code) {
		for (Position c : position) {
			if (c.code.equals(code)) {
				return c;
			}
		}
		return null;
	}

	//conversion
	private int getIndex(int x, int y) {return x + (8 * y);}
	private int getX(int index) {return index % 8;}
	private int getY(int index) {return index / 8;}
	
}
