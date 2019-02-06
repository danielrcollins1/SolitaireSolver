//********************************************************************
//  PlayerCallbacks.java              @version 1.00
//    Interface for player callbacks handled by game server.
//  Copyright (c) 2012 Daniel R. Collins. All rights reserved.
//  See the bottom of this file for any licensing information.
//********************************************************************

public interface PlayerCallbacks {
	public GameState playerViewGame ();	
	public boolean playerMoveCall (int param1, int param2, int param3);
}

