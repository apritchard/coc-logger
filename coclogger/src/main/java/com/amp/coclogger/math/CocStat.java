package com.amp.coclogger.math;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class CocStat {
	DescriptiveStatistics elixirStats;
	DescriptiveStatistics goldStats;
	DescriptiveStatistics darkElixirStats;
	DescriptiveStatistics totalLoot;

	public CocStat() {
		elixirStats = new DescriptiveStatistics();
		goldStats = new DescriptiveStatistics();
		darkElixirStats = new DescriptiveStatistics();
		totalLoot = new DescriptiveStatistics();
	}
	
	public long getEntries(){
		return goldStats.getN();
	}

	public void addElixir(int elixir) {
		elixirStats.addValue((double) elixir);
		totalLoot.addValue((double) elixir);
	}

	public void addGold(int gold) {
		goldStats.addValue((double) gold);
		totalLoot.addValue((double) gold);
	}

	public void addDarkElixir(int darkElixir) {
		darkElixirStats.addValue((double) darkElixir);
		totalLoot.addValue((double) (darkElixir*100));
	}

	public DescriptiveStatistics getElixirStats() {
		return elixirStats;
	}

	public DescriptiveStatistics getGoldStats() {
		return goldStats;
	}

	public DescriptiveStatistics getDarkElixirStats() {
		return darkElixirStats;
	}
	
	public DescriptiveStatistics getTotalLoot(){
		return totalLoot;
	}

	public String displayString() {
		return String.format(
				"Elixir: %s%n"
				+ "Gold: %s%n"
				+ "DE: %s%n"
				+ "Total: %s",
				statsToString(goldStats),
				statsToString(elixirStats),
				statsToString(darkElixirStats),
				statsToString(totalLoot)
		);

	}

	private static String statsToString(DescriptiveStatistics ds) {
		if (ds.getN() == 0) {
			return "no results";
		}
		int min = (int) ds.getMin();
		int max = (int) ds.getMax();
		int mean = (int) Math.round(ds.getMean());
		int q1 = (int) Math.round(ds.getPercentile(25));
		int median = (int) Math.round(ds.getPercentile(50));
		int q3 = (int) Math.round(ds.getPercentile(75));
		int stdDev = (int) Math.round(ds.getVariance());

		return String.format("Min(%d)Max(%d)Mean(%d)Q(%d/%d/%d)Var(%d)", min,
				max, mean, q1, median, q3, stdDev);
	}

}
