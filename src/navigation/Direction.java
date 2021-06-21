package navigation;

public enum Direction {
	/*
	 * vector of movement
	 */
	North				(0, 1),
	NorthNorthEast		(1, 2),
	NorthEast			(1, 1),
	EastNorthEast		(2, 1),
	East				(1, 0),
	EastSouthEast		(2, -1),
	SouthEast			(1, -1),
	SouthSouthEast		(1, -2),
	South				(0, -1),
	SouthSouthWest		(-1, -2),
	SouthWest			(-1, -1),
	WestSouthWest		(-2, -1),
	West				(-1, 0),
	WestNorthWest		(-2, 1),
	NorthWest			(-1, 1),
	NorthNorthWest		(-1, 2);
	
	//Each Direction needs both an x- and a y-coordinate
	public final int x, y;
	
	//Constructor
	private Direction(int relativeX, int relativeY) {
		this.x = relativeX;
		this.y = relativeY;
	}
	
	//Returns the Direction that eventually leads from start to target when multiplied enough times
	public static Direction get(Position start, Position target) {
		return get(target.x - start.x, target.y - start.y);
	}
	
	//Returns the Direction that eventually fits the passed coordinates when multiplied enough times
	public static Direction get(int x, int y) {
		for (Direction direction : values()) {
			if (direction.fits(x, y)) return direction;
		}
		return null;
	}
	
	//Returns true if this Direction multiplied 1-8 times has got the same coordinates as passed
	private boolean fits(int x, int y) {
		for (int i = 1; i < 8; i++) {
			if (this.x * i == x && this.y * i == y) return true;
		}
		return false;
	}
}
