package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import gui.DesignManager;
import gui.GUI;

public abstract class Main {
	
	/*
	 * this is simply the main class containing the main method and being able to create/load/end any game
	 */
	
	//file information
	public static final String PATH = "data/save/";
	public static final String EXTENSION = ".ser";
	
	//Constructor
	public static void main(String[] args) {
		DesignManager.init();
		fileManager = new FileManager<Game.Properties>("data/save/", "Game No.");
		fileManager.modify(FileManager.SORT_BY_DATE);
		GUI.init();	//initializes GUI
	}
	
	private static FileManager<Game.Properties> fileManager;	//organizes old games
	
	//start new game
	public static void initGame(Game.Mode mode) {
		createGame(new Game.Properties(fileManager.generateName(), mode), 0);
	}
	
	//load game completely
	public static boolean loadGame(String name) {
		return loadGame(name, -1);
	}
	
	//load game to certain round
	public static boolean loadGame(String name, int round) {
		try {
			createGame(fileManager.open(name), round);
			return true;
		} catch (FileNotFoundException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//create game
	private static void createGame(Game.Properties properties, int round) {

		Game.init(properties, round);
		Game.start();
	}
	
	//save game as file
	public static void saveGame() {
		if (Game.hasInstance()) {
			try {
				fileManager.save(Game.getProperties(), Game.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//delete game by name
	public static void deleteGame(String name) {
		File file = new File(PATH + name + EXTENSION);
		
		if (!file.delete()) {
			file.deleteOnExit();
		}
	}
	
	//get list of games names
	public static ArrayList<String> getSavedNames() {
		return fileManager.getFilenames();
	}
	
}
