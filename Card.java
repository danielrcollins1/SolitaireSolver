//********************************************************************
//  Card.java              @version 1.01
//    One standard playing card.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public class Card {

	//-----------------------------------------------------------------
	//  Fields
	//-----------------------------------------------------------------

	private int rank;         // Unknown = 0, A=1... J=11, Q=12, K=13
	private char suit;		  // Unknown = 0, S=1, H=2, D=3, C=4
	private boolean faceUp;   // Is this card face up?

	static private final boolean VIEW_IN_DOS = false;

	//-----------------------------------------------------------------
	//  Constructors
	//-----------------------------------------------------------------

	/**
	*  Constructor (known)
	*/
	public Card (int rank, char suit, boolean faceUp) {
		assert(0 <= rank && rank <= 13);
		assert(0 <= suit && suit <= 4);

		this.rank = rank;
		this.suit = suit;
		this.faceUp = faceUp;
	}

	/**
	*  Constructor (unknown)
	*/
   public Card () {
		rank = 0;
		suit = 0;
		faceUp = false;
   }

	/**
	*  Constructor (copy)
	*/
   public Card (Card old) {
		rank = old.rank;
		suit = old.suit;
		faceUp = old.faceUp;
   }
	
	//-----------------------------------------------------------------
	//  Methods
	//-----------------------------------------------------------------
	public int getRank () { return rank; }
	public char getSuit () { return suit; }
	public boolean isFaceUp () { return faceUp; }
	public boolean isBlack () { return (suit == 1 || suit == 4); }
	public void setFaceUp () { faceUp = true; }
	public void setFaceDown () { faceUp = false; }
	
	/**
	*   Srring representation.
	*/
   public String toString () {
		String s;
		switch (rank) {
			default: s = Integer.toString(rank); break;
			case 10: s = "T"; break;
			case 11: s = "J"; break;
			case 12: s = "Q"; break;
			case 13: s = "K"; break;
			case 1:  s = "A"; break;
		}
		s += (char)(suit + (VIEW_IN_DOS ? 2 : 0x265F));
		return s;
   }
	
	/**
	*  Scrub data for player view.
	*/
	public void scrubData () {
		rank = 0;
		suit = 0;	
	}
}

