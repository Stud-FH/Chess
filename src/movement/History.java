package movement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

public class History implements Serializable {
	private static final long serialVersionUID = 4330780535694372412L;
	
	private ArrayList<Round> log;							//list of rounds executed
	private ArrayList<ActionListener> actionListeners;		//observer list
	private transient int motionCounter;					//index of current round
	private MoveCommand temp;								//first move of the current (unfinished) round

	public History() {
		log = new ArrayList<Round>();
		actionListeners = new ArrayList<ActionListener>();
	}
	
	/*
	 * adds a move to the history's log
	 * creates rounds automatically
	 */
	public void expand(MoveCommand movementPossibility) {
		
		clearUndone();
		
		if (temp == null) {
			temp = movementPossibility;
		} else {
			Round round = new Round(motionCounter, temp, movementPossibility);
			log.add(round);
			temp = null;
			motionCounter++;
			actionPerformed(null);
		}
	}
	
	//reenacts log to a given index
	public void goTo(int round) {
		if (round >= log.size() || round < 0) {
			round = log.size() - 1;
		}
		
		for (int i = 0; i <= round; i++) {
			log.get(i).execute();
		}
	}
	
	//returns index of last executed round
	public int getMotionCounter() {
		return motionCounter;
	}
	
	//returns size of full log (with undone)
	public int getLogSize() {
		return log.size();
	}
	
	//deletes all not-executed rounds contained in the log
	private void clearUndone() {
		while (motionCounter < log.size()) {
			log.remove(log.size() - 1);
		}
		actionPerformed(null);
	}
	
	//returns the chess-code of a specific round
	public String getRoundName(int index) {
		return log.get(index).getString();
	}
	
	//adds observer
	public void addActionListener(ActionListener a) {
		actionListeners.add(a);
	}
	
	//notifies observers of state change
	private void actionPerformed(ActionEvent e) {
		for (ActionListener a : actionListeners) {
			a.actionPerformed(e);
		}
	}
	
	/*
	 * contains white's move and black's response
	 * 
	 * The history saves rounds instead of moves in order to avoid bugs concerning the sequence of draws
	 */
	public class Round implements Serializable {
		private static final long serialVersionUID = 9052097169490996815L;
		
		private final int index;								//the round's number
		private final MoveCommand whiteMove, blackMove;			//the moves made in this round
		
		private Round(int index, MoveCommand whiteMove, MoveCommand blackMove) {
			this.index = index;
			this.whiteMove = whiteMove;
			this.blackMove = blackMove;
		}
		
		//reenacts this round
		private void execute() {
			whiteMove.execute();
			blackMove.execute();
		}
		
		//returns the chess code of this round, that is visible for users
		public String getString() {
			return (index + ". " + whiteMove.getString() + " - " + blackMove.getString());
		}
	}
	
	
	
}
