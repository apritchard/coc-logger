package com.amp.coclogger.math;

import com.amp.coclogger.prefs.League;

public class CocResult {
	private int gold;
	private int elixir;
	private int darkElixir;
	private League playerLeague;
	private int playerTownhall;
	
	public CocResult(int gold, int elixir, int darkElixir, League playerLeague, int playerTownhall){
		this.gold = gold;
		this.elixir = elixir;
		this.darkElixir = darkElixir;
		this.playerLeague = playerLeague;
		this.playerTownhall = playerTownhall;
	}
	
	public int getGold() {
		return gold;
	}
	public int getElixir() {
		return elixir;
	}
	public int getDarkElixir() {
		return darkElixir;
	}
	public League getPlayerLeague() {
		return playerLeague;
	}
	public int getPlayerTownhall() {
		return playerTownhall;
	}
}
