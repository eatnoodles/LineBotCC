/**
 * 
 */
package com.cc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Caleb.Cheng
 *
 */
public class WowCharacterProfileResponse {
	
	/**
	 * 最後更新時間
	 */
	private Long lastModified;
	
	/**
	 * 名稱
	 */
	private String name;
	
	/**
	 * server
	 */
	private String realm;

	/**
	 * group
	 */
	private String battlegroup;
	
	/**
	 * 職業
	 */
	private Integer clz;
	
	/**
	 * 種族
	 */
	private Integer race;
	
	private Integer gender;
	
	/**
	 * 等級
	 */
	private Integer level;
	
	/**
	 * 成就點數
	 */
	private Long achievementPoints;
	
	private String thumbnail;
	
	/**
	 * 天賦(?)
	 */
	private String calcClass;
	
	private Integer faction;
	
	/**
	 * 榮譽擊殺
	 */
	private Long totalHonorableKills;
	
	
	@JsonProperty("lastModified")
	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("realm")
	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	@JsonProperty("battlegroup")
	public String getBattlegroup() {
		return battlegroup;
	}

	public void setBattlegroup(String battlegroup) {
		this.battlegroup = battlegroup;
	}

	@JsonProperty("class")
	public Integer getClz() {
		return clz;
	}

	public void setClz(Integer clz) {
		this.clz = clz;
	}

	@JsonProperty("race")
	public Integer getRace() {
		return race;
	}

	public void setRace(Integer race) {
		this.race = race;
	}

	@JsonProperty("gender")
	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	@JsonProperty("level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@JsonProperty("achievementPoints")
	public Long getAchievementPoints() {
		return achievementPoints;
	}

	public void setAchievementPoints(Long achievementPoints) {
		this.achievementPoints = achievementPoints;
	}

	@JsonProperty("thumbnail")
	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	@JsonProperty("calcClass")
	public String getCalcClass() {
		return calcClass;
	}

	public void setCalcClass(String calcClass) {
		this.calcClass = calcClass;
	}

	@JsonProperty("faction")
	public Integer getFaction() {
		return faction;
	}

	public void setFaction(Integer faction) {
		this.faction = faction;
	}

	@JsonProperty("totalHonorableKills")
	public Long getTotalHonorableKills() {
		return totalHonorableKills;
	}

	public void setTotalHonorableKills(Long totalHonorableKills) {
		this.totalHonorableKills = totalHonorableKills;
	}
}
