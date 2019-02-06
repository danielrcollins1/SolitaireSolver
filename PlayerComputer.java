//********************************************************************
//  PlayerComputer.java              @version 1.07
//    A computer player for solitaire.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class PlayerComputer implements PlayerInterface {
	GameState game;               // Scrubbed game state
	PlayerCallbacks callbacks;    // Callbacks to server
	ViewerInterface view;         // View the game if needed
	
	boolean movedThisTick, movedThisPass;

	//-----------------------------------------------------------------
	//  Constructor
	//-----------------------------------------------------------------
	public PlayerComputer (PlayerCallbacks pc) {
		callbacks = pc;
		movedThisPass = false;
		view = new ViewerText();
	}

	//-----------------------------------------------------------------
	//  Ask me for my next move (AI strategy)
	//-----------------------------------------------------------------
	public void askNextMove () {
		movedThisTick = false;		
		game = callbacks.playerViewGame();

		checkFlipTableTop();
		checkMoveToTable();
		checkMoveToFound();
		checkMoveFromFound();
		checkMoveSubpile();
		checkDrawFromDeck();
		checkStartNewPass();
		checkSurrenderGame();
	}

	//-----------------------------------------------------------------
	//  Call move shortcut
	//-----------------------------------------------------------------
	boolean callMove (int p1, int p2, int p3) {
		movedThisTick = true;
		if (!(p1==0 && p2==1)) 
			// not counting draws from deck
			movedThisPass = true;
		return callbacks.playerMoveCall(p1, p2, p3);
	}

	//-----------------------------------------------------------------
	//  Surrender the game
	//-----------------------------------------------------------------
	void checkSurrenderGame () {
		if (movedThisTick) return;
		callMove(0, 0, 0);
	}

	//-----------------------------------------------------------------
	//  Check to start a new pass
	//-----------------------------------------------------------------
	void checkStartNewPass () {
		if (movedThisTick) return;
		if (game.deck().isEmpty() && !game.waste().isEmpty() 
				&& game.getPass() < game.getMaxPasses() && movedThisPass) {
			callMove(1, 0, 0);
			movedThisPass = false;
		}
	}

	//-----------------------------------------------------------------
	//  Check for more cards to flip from deck
	//-----------------------------------------------------------------
	void checkDrawFromDeck () {
		if (movedThisTick) return;
		if (!game.deck().isEmpty()) {
			callMove(0, 1, 0);		
		}
	}
	
	//-----------------------------------------------------------------
	//  Check to flip a table pile top face-up
	//-----------------------------------------------------------------
	void checkFlipTableTop () {
		if (movedThisTick) return;
		for (int i = 6; i <= 12; i++) {
			Pile table = game.getPile(i);
			if (!table.isEmpty() && !table.getTopCard().isFaceUp()) {
				callMove(i, i, 0);
				return;
			}
		}
	}
	
	//-----------------------------------------------------------------
	//  Check for any moves to foundation
	//-----------------------------------------------------------------
	void checkMoveToFound () {
		if (movedThisTick) return;
		for (int i = 6; i <= 12; i++) {
			checkMoveToFoundFromPile(i);		
		}
		checkMoveToFoundFromPile(1); // waste
	}

	//-----------------------------------------------------------------
	//  Check for move from given pile to foundation (one card)
	//-----------------------------------------------------------------
	void checkMoveToFoundFromPile (int srcIdx) {
		if (movedThisTick) return;
		Pile srcPile = game.getPile(srcIdx);
		if (srcPile.isEmpty()) return;
		Card card = srcPile.getTopCard();

		for (int j = 2; j <= 5; j++) {
			Pile dstPile = game.getPile(j);

			// Ace to empty foundation
			if (dstPile.isEmpty()) {
				if (card.getRank() == 1) {
					callMove(srcIdx, j, 0);
					return;
				}
				else continue;
			}			
				
			// Same suit, one more rank
			Card dstCard = dstPile.getTopCard();
			if ((card.getSuit() == dstCard.getSuit())
					&& (card.getRank() == dstCard.getRank()+1)) {
				callMove(srcIdx, j, 0);
				return;
			}
		}
	}
	
	//-----------------------------------------------------------------
	//  Check for any moves to table
	//    Searches down tables by count of face-down cards
	//    (Makes ~1% improvement over simple search right-to-left)
	//-----------------------------------------------------------------
	void checkMoveToTable () {
		if (movedThisTick) return;

		// Record cards down in each table
		int numTables = game.NUM_PILES - game.IDX_TABLE;
		int[] cardsDown = new int[numTables];
		int maxDown = 0;
		for (int i = 0; i < numTables; i++) {
			int down = numFaceDownCards(
				game.getPile(i+game.IDX_TABLE));
			cardsDown[i] = down;
			if (down > maxDown) 
				maxDown = down;
		}
		
		// Search down by count of face-downs
		for (int down = maxDown; down >= 0; down--) {
			for (int i = numTables-1; i >= 0; i--) {
				if (cardsDown[i] == down)
					checkMoveToTableFromPile(i+game.IDX_TABLE);
			}
		}
		
		// Also check for move from waste
		checkMoveToTableFromPile(1); // waste
	}
	
	//-----------------------------------------------------------------
	//  Check given pile-to-table moves (possibly whole subpile)
	//-----------------------------------------------------------------
	void checkMoveToTableFromPile (int srcIdx) {
		if (movedThisTick) return;
		Pile srcPile = game.getPile(srcIdx);
		if (srcPile.isEmpty()) return;
		int faceIdx = (srcIdx == 1? // waste top only
			srcPile.size()-1: idxFirstFaceUpCard(srcPile));
		Card card = srcPile.get(faceIdx);

		for (int j = 6; j <= 12; j++) {
			Pile dstPile = game.getPile(j);

			// King to empty tableaux
			if (dstPile.isEmpty()) {
				if (card.getRank() == 13 
					// don't leave a table empty for this
						&& (srcIdx == 1 || faceIdx > 0)) {
					callMove(srcIdx, j, faceIdx);
					return;
				}
				else continue;
			}			
			
			// Switch color, one less rank
			Card dstCard = dstPile.getTopCard();
			if ((card.isBlack() != dstCard.isBlack())
					&& (card.getRank() == dstCard.getRank()-1)) {
				callMove(srcIdx, j, faceIdx);
				return;
			}
		}
	}

	//-----------------------------------------------------------------
	//  Check if moving a subpile can free up a card for foundation
	//-----------------------------------------------------------------
	void checkMoveSubpile () {
		if (movedThisTick) return;
		for (int i = 2; i <= 5; i++) {
			if (game.getPile(i).isEmpty()) continue;
			Card topFoundCard = game.getPile(i).getTopCard();

			// Search table top for counterfeit (next rank, same color)
			int counterPile = -1, counterIdx = -1;
			for (int j = 6; j <= 12; j++) {
				if (game.getPile(j).isEmpty()) continue;
				Card topCard = game.getPile(j).getTopCard();
				if ((topCard.getSuit() != topFoundCard.getSuit())
						&& (topCard.isBlack() == topFoundCard.isBlack())
						&& (topCard.getRank() == topFoundCard.getRank()+1)) {
					counterIdx = game.getPile(j).size()-1;
					counterPile = j;
					break;
				}
			}
			if (counterPile == -1) continue;
			
			// Search tables for actual buried match 
			int matchPile = -1, matchIdx = -1;
			for (int j = 6; j <= 12; j++) {
				if (game.getPile(j).isEmpty()) continue;
				for (int k = 0; k < game.getPile(j).size()-1; k++) {
					Card nextCard = game.getPile(j).get(k);
					if (nextCard.isFaceUp()
							&& (nextCard != game.getPile(j).getTopCard())
							&& (nextCard.getSuit() == topFoundCard.getSuit())
							&& (nextCard.getRank() == topFoundCard.getRank()+1)) {
						matchPile = j;
						matchIdx = k;
						break;
					}
				}
			}
			if (matchPile == -1) continue;
				
			// Make the subpile switch
			callMove(matchPile, counterPile, matchIdx+1);
			return;
		}				
	}

	//-----------------------------------------------------------------
	//  Check if foundation card can join table subpiles
	//-----------------------------------------------------------------
	void checkMoveFromFound () {
		if (movedThisTick) return;
		for (int i = 2; i <= 5; i++) {
			if (game.getPile(i).isEmpty()) continue;
			Card topFoundCard = game.getPile(i).getTopCard();

			// Search table top one rank higher
			int startPile = -1, startIdx = -1;
			for (int j = 6; j <= 12; j++) {
				if (game.getPile(j).isEmpty()) continue;
				Card topCard = game.getPile(j).getTopCard();
				if ((topCard.isBlack() != topFoundCard.isBlack())
						&& (topCard.getRank() == topFoundCard.getRank()+1)) {
					startIdx = game.getPile(j).size()-1;
					startPile = j;
					break;
				}
			}
			if (startPile == -1) continue;
			
			// Search tables for subpile to be joined
			int endPile = -1, endIdx = -1;
			for (int jprime = 12; jprime >= 5; jprime--) {
				int j = (jprime==5? 1: jprime);  // waste last
				Pile srcPile = game.getPile(j);
				if (srcPile.isEmpty()) continue;
				int faceIdx = (j == 1? srcPile.size()-1 // waste top only
					: idxFirstFaceUpCard(srcPile));
				Card nextCard = game.getPile(j).get(faceIdx);
				if ((nextCard.isBlack() != topFoundCard.isBlack())
						&& (nextCard.getRank() == topFoundCard.getRank()-1)) {
					endIdx = faceIdx;
					endPile = j;
					break;
				}
			}
			if (endPile == -1) continue;
			
			// Make foundation card to table
			callMove(i, startPile, 0);
			return;
		}
	}
	
	//-----------------------------------------------------------------
	//  Get index of first face-up card in a pile
	//-----------------------------------------------------------------
	int idxFirstFaceUpCard (Pile p) {
		assert(!p.isEmpty());
		assert(p.getTopCard().isFaceUp());
		for (int i = 0; i < p.size(); i++) {
			if (p.get(i).isFaceUp()) return i;
		}
		return -1;
	}

	//-----------------------------------------------------------------
	//  Get number of face-down cards in a pile
	//-----------------------------------------------------------------
	int numFaceDownCards (Pile p) {
		if (p.isEmpty()) return 0;
		if (!p.getTopCard().isFaceUp()) return p.size();
		return idxFirstFaceUpCard(p);	
	}
	
	//-----------------------------------------------------------------
	//  Is there a king ready for an empty table?
	//-----------------------------------------------------------------
	boolean isKingAvailable () {
		for (int iprime = 6; iprime <= 13; iprime++) {
			int i = (iprime==13? 1: iprime);  // waste last
			Pile srcPile = game.getPile(i);
			if (srcPile.isEmpty()) continue;
			int faceIdx = (i == 1?  // waste top only
				srcPile.size()-1 : idxFirstFaceUpCard(srcPile));
			Card card = srcPile.get(faceIdx);
			if (card.getRank() == 13) return true;
		}	
		return false;
	}
}

