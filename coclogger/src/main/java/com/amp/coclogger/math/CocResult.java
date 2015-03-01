package com.amp.coclogger.math;

import com.amp.coclogger.prefs.League;

public class CocResult {
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getElixir() {
		return elixir;
	}
	public void setElixir(int elixir) {
		this.elixir = elixir;
	}
	public int getDarkElixir() {
		return darkElixir;
	}
	public void setDarkElixir(int darkElixir) {
		this.darkElixir = darkElixir;
	}
	public League getPlayerLeague() {
		return playerLeague;
	}
	public void setPlayerLeague(League playerLeague) {
		this.playerLeague = playerLeague;
	}
	public int getPlayerTownhall() {
		return playerTownhall;
	}
	public void setPlayerTownhall(int playerTownhall) {
		this.playerTownhall = playerTownhall;
	}
	private int gold;
	private int elixir;
	private int darkElixir;
	private League playerLeague;
	private int playerTownhall;
}
