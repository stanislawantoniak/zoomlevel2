package com.mars.azure;

public class ZoomLevelResponse {

	private int defaultZoomLevel;
	private Double squareSideInMiles;
	private int count;
	private String debugInfo;
	
	public Double getSquareSideInMiles() {
		return squareSideInMiles;
	}
	public void setSquareSideInMiles(Double squareSideInMiles) {
		this.squareSideInMiles = squareSideInMiles;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getDefaultZoomLevel() {
		return defaultZoomLevel;
	}
	public void setDefaultZoomLevel(int defaultZoomLevel) {
		this.defaultZoomLevel = defaultZoomLevel;
	}
	public String getDebugInfo() {
		return debugInfo;
	}
	public void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
		
}
