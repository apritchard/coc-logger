package com.amp.coclogger.math;

/**
 * Represents the data returned from a resource snapshot on the combat screen
 * @author alex
 *
 */
public class ResourceData {
	private int elixir;
	private int gold;
	private int darkElixir;
	private int trophiesWon;
//	private int trophiesLost;
	
	@Override
	public String toString(){
		return String.format("Gold: %d Elixir: %d Dark Elixir: %d Trophies: %d", gold, elixir, darkElixir, trophiesWon);
	}
	public int getElixir() {
		return elixir;
	}
	public void setElixir(int elixir) {
		this.elixir = elixir;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getDarkElixir() {
		return darkElixir;
	}
	public void setDarkElixir(int darkElixir) {
		this.darkElixir = darkElixir;
	}
	public int getTrophiesWon() {
		return trophiesWon;
	}
	public void setTrophiesWon(int trophiesWon) {
		this.trophiesWon = trophiesWon;
	}
//	public int getTrophiesLost() {
//		return trophiesLost;
//	}
//	public void setTrophiesLost(int trophiesLost) {
//		this.trophiesLost = trophiesLost;
//	}

}
