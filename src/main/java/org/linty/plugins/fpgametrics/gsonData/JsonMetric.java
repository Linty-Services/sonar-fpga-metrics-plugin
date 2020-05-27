package org.linty.plugins.fpgametrics.gsonData;

public class JsonMetric {
	
	private String name;
	private String type;
	private String description;
	private String domain;
	private double worstValue;
	private double bestValue;
	private boolean qualitative;
	private int direction;
	private boolean optimizedBestValue;
	private boolean userManaged;
	private boolean enabled;
	private boolean hidden;
	private boolean deleteHistoricalData;
	private int decimalScale;
	  
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getDescription() {
		return description;
	}
	public boolean isQualitative() {
		return qualitative;
	}
	
	public String getDomain() {
		return domain;
	}

	public double getWorstValue() {
		return worstValue;
	}

	public double getBestValue() {
		return bestValue;
	}
	
	public boolean isOptimizedBestValue() {
		return optimizedBestValue;
	}
	public int getDirection() {
		return direction;
	}
	public boolean isUserManaged() {
		return userManaged;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isHidden() {
		return hidden;
	}
	public boolean isDeleteHistoricalData() {
		return deleteHistoricalData;
	}
	public int getDecimalScale() {
		return decimalScale;
	}

	  
}
