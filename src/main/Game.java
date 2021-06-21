package main;

import java.io.Serializable;

import controller.RandomController;
import controller.VisualController;
import gui.GUI;
import movement.History;
import navigation.Grid;
import piece.Piece;
import player.Player;

public class Game {
	
	/*
	 * this is the center of the game's model
	 */
	
	public static final String defaultPrefix = "Game No. ";		//default filename
	
	private static Game runningGame;							//no singleton but only one Game at a time
	
	//getter
	public static Game getInstance() {
		return runningGame;
	}
	
	//is a game running?
	public static boolean hasInstance() {
		return runningGame != null;
	}
	
	//create new game
	static void init(Properties properties, int preferredRound) {
		runningGame = new Game(properties, preferredRound);
	}

	//create new game
	static void init(Properties properties) {
		runningGame = new Game(properties, -1);
	}
	
	//start game
	public static void start() {
		runningGame.activePlayer = Player.White;
		runningGame.calculate();
	}
	
	//next player's move
	public static void nextMove() {
		runningGame.save();
		runningGame.togglePlayers();
		runningGame.calculate();
	}
	
	//getter
	public static Player getActivePlayer() {
		return runningGame.activePlayer;
	}

	//getter
	public static History getHistory() {
		return runningGame.properties.history;
	}

	//getter
	public static String getName() {
		return runningGame.properties.name;
	}

	//getter
	public static Properties getProperties() {
		return runningGame.properties;
	}

	//getter
	public static Piece getPiece(String code) {
		return code.charAt(0) == Player.White.initial()? Player.White.getPiece(code) : Player.Black.getPiece(code);
	}
	
	
	private final Properties properties;	//game information
	private Player activePlayer;			//player allowed to act
	
	//Constructor
	private Game(Properties properties, int preferredRound){
		this.properties = properties;
		properties.initPlayers();
		properties.history.goTo(preferredRound);
	}
	
	//change active player
	private void togglePlayers() {
		activePlayer = activePlayer.opponent();
	}
	
	//calculate new situation
	private void calculate() {
		
		clean();
		
		scan();
		
		validate();
		
		if (activePlayer.hasLegalMovement()) {
			activePlayer.act();
		} else {
			GUI.gameoverMessage(gameoverText());
		}
	}
	
	//delete old information
	private void clean() {
		Grid.reset();
		
		for (Player player : Player.values()) {
			player.prepareCalculation();
		}
	}
	
	//register movement possibilities
	private void scan() {
		for (Player player : Player.values()) {
			player.calculate();	
		}
	}
	
	/*
	 * This method will repeat itself as long as any validating method from Player, Piece or MovementScanner returns false.
	 * MovementScanner should return false if any MovementPossibilities are manipulated.
	 */
	private void validate() {
		for (Player player : Player.values()) {
			if (!player.validateCalculation()) {
				validate();
				break;
			}
		}
	}
	
	//save game as file
	private void save() {
		Main.saveGame();
	}
	
	//generate gameover message
	private String gameoverText() {
		if (activePlayer.isInCheck()) {
			return activePlayer.opponent().name() + " wins by checkmate. Leave game?";
		} else {
			return "Remis. Leave game?";
		}
	}
	
	/*
	 * an object of this class contains all relevant information to start a new game or replay/load an old one
	 */
	static class Properties implements Serializable {
		private static final long serialVersionUID = 6274167573692523677L;
		
		private final Mode mode;		//against human or computer
		private final History history;	//history is saved
		private String name;			//name as identification
		
		//Constructor
		Properties(String name, Mode mode) {
			this.name = name;
			this.mode = mode;
			history = new History();
		}
		
		//activate Players
		void initPlayers() {
			switch(mode) {
			case WhiteUser:		Player.White.init(new VisualController(Player.White));
								Player.Black.init(new RandomController(Player.Black));
								break;
			case BlackUser:		Player.White.init(new RandomController(Player.White));
								Player.Black.init(new VisualController(Player.Black));
								break;
			default:			Player.White.init(new VisualController(Player.White));
								Player.Black.init(new VisualController(Player.Black));
								break;
			}
		}
	}

	//different game modes available
	public enum Mode {
		Hotseat, WhiteUser, BlackUser;
	}
}
