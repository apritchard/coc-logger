package com.amp.coclogger.math;

import java.io.Serializable;

import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.Townhall;

public class CocResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3266970224016024336L;
	private int gold;
	private int elixir;
	private int darkElixir;
	private League playerLeague;
	private Townhall playerTownhall;
	
	public CocResult(int gold, int elixir, int darkElixir, League playerLeague, Townhall playerTownhall){
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
	public Townhall getPlayerTownhall() {
		return playerTownhall;
	}
}
