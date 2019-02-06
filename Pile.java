//********************************************************************
//  Pile.java              @version 1.00
//    A pile of cards; as ArrayList, index 0 is at bottom.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

import java.util.ArrayList;

public class Pile {
	private ArrayList<Card> cardList;

	//-----------------------------------------------------------------
	//  Constructor (empty)
	//-----------------------------------------------------------------
   public Pile () {
		cardList = new ArrayList<Card>();
   }

	//-----------------------------------------------------------------
	//  Constructor (fresh deck of 52)
	//-----------------------------------------------------------------
   public Pile (boolean freshDeck) {
		cardList = new ArrayList<Card>();
		if (freshDeck) {
			for (char suit = 1; suit <= 4; suit++) {
				for (int rank = 1; rank <= 13; rank++) {
					add(new Card(rank, suit, false));
				}
			}
		}
   }

	//-----------------------------------------------------------------
	//  Constructor (copy)
	//-----------------------------------------------------------------
   public Pile (Pile old) {
		cardList = new ArrayList<Card>();
		for (int i = 0; i < old.size(); i++) {
			add(new Card(old.get(i)));					
		}
   }

	//-----------------------------------------------------------------
	//  Shuffle the pile in a simple fashion
	//-----------------------------------------------------------------
	public void shuffle () {
		ArrayList<Card> newList = new ArrayList<Card>();
		while (size() > 0) {
			int random = (int)(Math.random() * size());
			newList.add( cardList.remove(random) );
		}
		cardList = newList;
	}

	//-----------------------------------------------------------------
	//  ArrayList-style methods
	//-----------------------------------------------------------------
	public void add (Card card) { cardList.add(card); }
	public Card get (int i) { return cardList.get(i); }
	public int size () { return cardList.size(); }
	public boolean isEmpty () { return cardList.isEmpty(); }

	//-----------------------------------------------------------------
	//  Top-card accessors
	//-----------------------------------------------------------------
	public Card getTopCard () { return cardList.get(size()-1); }
	public Card removeTopCard () { return cardList.remove(size()-1); }

	//-----------------------------------------------------------------
	//  Draw top card out to another pile
	//-----------------------------------------------------------------
	public void drawToPile (Pile dest) {
		dest.add(removeTopCard());
	}

	//-----------------------------------------------------------------
	//  Flip the whole pile face down to another pile
	//-----------------------------------------------------------------
	public void flipWholePileFaceDown (Pile dest) {
		assert(this != dest);
		while (size() > 0) {
			drawToPile(dest);
			dest.getTopCard().setFaceDown();
		}
	}
	
	//-----------------------------------------------------------------
	//  Move pile subsection in order from index
	//-----------------------------------------------------------------
	public void moveSubpileToPile (int index, Pile dest) {
		assert(this != dest);
		assert(0 <= index && index < size());	
		while (size() > index) {
			dest.add(cardList.remove(index));
		}	
	}
}

