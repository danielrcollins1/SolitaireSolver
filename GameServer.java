import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.io.*; 

//********************************************************************
//  GameServer.java              @version 1.08
//    Game server for the solitaire game.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class GameServer implements PlayerCallbacks {

   //--------------------------------------------------------------------------
   //  Constants
   //--------------------------------------------------------------------------

	// Maximal margin-of-error for given number of games:
	// 1K: 3%, 10K:1%; 100K: 0.3%; 1M: 0.1% (95% confidence)
	// Approx. 500 games/sec on P4 1.6Ghz

	final int NUM_GAMES = 100000;
	final int CRIT_MOVES = 995;
	final int MAX_MOVES = 1000;
	final int PROGBAR_SIZE = 50;
	final boolean VIEW_GAMES = false;
	final boolean SHOW_PROGBAR = true;

   //--------------------------------------------------------------------------
   //  Fields
   //--------------------------------------------------------------------------

	GameState game;
	ViewerInterface view;
	PlayerInterface player;

   //--------------------------------------------------------------------------
   //  Methods
   //--------------------------------------------------------------------------

	/**
	*  Run series of different game options.
	*/
	public void runGameSeries () {
		System.out.println("Number of games: " + NUM_GAMES);
		runManyGames(1, 1);
		runManyGames(3, 3);
		runManyGames(3, Integer.MAX_VALUE);
		runManyGames(1, Integer.MAX_VALUE);
	}

	/**
	*  Run many games.
	*/
	public void runManyGames (int cardsDrawn, int maxPasses) {
		int numWins = 0;
		int progBarCount = 0;
		int progBarInc = NUM_GAMES/PROGBAR_SIZE;
		if (SHOW_PROGBAR) {
	 		for (int i = 0; i < PROGBAR_SIZE; i++)
 				System.out.print(".");
			System.out.println();
		}
		for (int i = 0; i < NUM_GAMES; i++) {
			boolean won = runOneGame(cardsDrawn, maxPasses);
			if (won) numWins++;
			if (SHOW_PROGBAR) {
	 			progBarCount++;
 				if (progBarCount >= progBarInc) {
 					progBarCount = 0;
 					System.out.print("=");			
 				}
			}
		}
		if (SHOW_PROGBAR)
			System.out.println();
		double percent = (double) numWins/NUM_GAMES * 100;
		System.out.print("Draw " + cardsDrawn);
		System.out.print(", pass " + 
			(maxPasses < Integer.MAX_VALUE ? maxPasses : "inf"));
		DecimalFormat df = new DecimalFormat("#0.0");
		System.out.println(": won " + df.format(percent) + "%");
	}

	/**
	*  Run one game; return if game won.
	*/
	public boolean runOneGame (int cardsDrawn, int maxPasses) {
		game = new GameState(cardsDrawn, maxPasses);
		game.setupNewGame();
		view = new ViewerText();
		try {
			view.open();
		}
		catch (IOException exception) {
			System.err.println("Error: Could not open game viewer.");
			return false;		
		}
		player = new PlayerComputer(this);
		int numMoves = 0;
		while (!game.isOver()) {
			if (VIEW_GAMES) {
				view.update(game);
//				Thread.sleep(2000);
			}
			player.askNextMove();
			numMoves++;

			// Move cap warning/limit
			if (numMoves > CRIT_MOVES) {
				System.out.println();
				view.update(game);
			}
			if (numMoves > MAX_MOVES) {
				game.handleMoveSurrender(0, 0);
			}
		}
		view.close();
		return game.isGameWon();
	}

	/**
	*  Player view game: return scrubbed copy of game state.
	*/
	public GameState playerViewGame () {
		GameState scrubCopy = new GameState(game);
		scrubCopy.scrubHiddenData();
		return scrubCopy;
	}
	
	/**
	*  Player move callback: delegate to game object.
	*/
	public boolean playerMoveCall (int p1, int p2, int p3) {
		boolean retval = game.playerMoveCall(p1, p2, p3);
		if (!retval) {
			System.err.println("Warning: Rejected move call."); 
		}
		return retval;
	}	
	
	/**
	*  Main method.
	*/
	public static void main (String[] args) {
		GameServer server = new GameServer();	
		server.runGameSeries();
		Toolkit.getDefaultToolkit().beep();			
	}
}

