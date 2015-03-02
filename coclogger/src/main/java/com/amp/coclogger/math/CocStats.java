package com.amp.coclogger.math;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amp.coclogger.prefs.League;

public class CocStats {
	private Map<League, Map<Integer, CocStat>> stats;

	public CocStats(List<CocResult> results) {
		stats = new EnumMap<League, Map<Integer, CocStat>>(League.class);
		
		//prepopulate with known leagues and TH 1-10
		for (League league : League.values()) {
			stats.put(league, new HashMap<Integer, CocStat>());
			for(int i = 1 ; i <= 10; i++){
				stats.get(league).put(Integer.valueOf(i), new CocStat());
			}
		}
		
		for(CocResult result : results){
			add(result);
		}
	}
	
	public void add(CocResult result){
		CocStat stat = stats.get(result.getPlayerLeague()).get(Integer.valueOf(result.getPlayerTownhall()));
		stat.addElixir(result.getElixir());
		stat.addDarkElixir(result.getDarkElixir());
		stat.addGold(result.getGold());
	}

	public CocStat getStat(League league, int townHall){
		return stats.get(league).get(Integer.valueOf(townHall));
	}
	
	
}
