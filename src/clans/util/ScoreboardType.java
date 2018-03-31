package clans.util;

import clans.ClansMain;

public enum ScoreboardType {
	SIDEBOARD(ClansMain.getInstance().getNetworkName() + " Season " + String.valueOf(ClansMain.getInstance().getSeason())),
	UNDER_NAME("War Points");
	
	
	private String objectiveName;

	private ScoreboardType(String objectiveName) {
		this.objectiveName = objectiveName;
	}
	
	public String getObjectiveName() {
		return this.objectiveName;
	}
	
}
