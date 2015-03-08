package com.amp.coclogger.math;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.amp.coclogger.prefs.League;
import com.amp.coclogger.prefs.Townhall;

public class CocStats {
	private Map<League, Map<Townhall, CocStat>> stats;

	public CocStats(List<CocResult> results) {
		stats = new EnumMap<League, Map<Townhall, CocStat>>(League.class);

		// prepopulate with known leagues and TH 1-10
		for (League league : League.values()) {
			stats.put(league, new EnumMap<Townhall, CocStat>(Townhall.class));
			for (Townhall townhall : Townhall.values()) {
				stats.get(league).put(townhall, new CocStat());
			}
		}

		for (CocResult result : results) {
			add(result);
		}
	}

	public void add(CocResult result) {
		if (result == null) {
			return;
		}
		// TODO fix total
		CocStat stat = stats.get(result.getPlayerLeague()).get(result.getPlayerTownhall());
		stat.addElixir(result.getElixir());
		stat.addDarkElixir(result.getDarkElixir());
		stat.addGold(result.getGold());
	}

	public CocStat getStat(League league, int townHall) {
		return stats.get(league).get(Integer.valueOf(townHall));
	}

}
