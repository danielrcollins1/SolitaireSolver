import java.io.*; 
import java.util.*;

//********************************************************************
//  ViewerText.java              @version 1.01
//    Text-based game state viewer (to file).
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class ViewerText implements ViewerInterface {

	//-----------------------------------------------------------------
	//  Fields
	//-----------------------------------------------------------------
	PrintWriter printer;

	//-----------------------------------------------------------------
	//  Open
	//-----------------------------------------------------------------
	public void open () throws IOException {
		printer = new PrintWriter(new File("output.txt"), "UTF-8");	
	}

	//-----------------------------------------------------------------
	//  Close
	//-----------------------------------------------------------------
	public void close () {
		printer.close();	
	}

	//-----------------------------------------------------------------
	//  Update
	//-----------------------------------------------------------------
	public void update (GameState gs) {

		// First row: top of deck, waste, foundations
		print(gs.deck());
		print(gs.waste());
		print("   ");
		for (int i = 0; i < 4; i++) {
			print(gs.found(i));
		}
		print("\r\n");
		
		// Find max length of tables
		int maxTable = 0;
		for (int i = 0; i < 7; i++) {
			int size = gs.table(i).size();
			if (size > maxTable) maxTable = size;
		}
		
		// Print a row for each
		for (int i = 0; i < maxTable; i++) {
			for (int j = 0; j < 7; j++) {
				if (i < gs.table(j).size())
					print(gs.table(j).get(i));
				else 
					print("   ");
			}
			print("\r\n");
		}
		print("\r\n");
	}

	//-----------------------------------------------------------------
	//  Print top of pile
	//-----------------------------------------------------------------
	void print (Pile pile) {
		if (pile.isEmpty()) {
			print("-- ");
		}
		else {
			print(pile.getTopCard());
		}
	}

	//-----------------------------------------------------------------
	//  Print one card
	//-----------------------------------------------------------------
	void print (Card card) {
		print(card.isFaceUp() ? card.toString() + " " : "[] ");
	}
	
	//-----------------------------------------------------------------
	//  Print shortcut
	//-----------------------------------------------------------------
	void print (String s) {
		printer.print(s);
   }
}

