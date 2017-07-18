/**
 * 
 */
package com.cc.wow.character;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Caleb.Cheng
 *
 */
public class WowBossMaster {

	private List<WowBoss> bosses;
	
	public static class WowBoss {
		
		private Long id;
		
		private String name;
		
		private String urlSlug;
		
		private String description;
		
		private Long zoneId;
		
		private String availableInNormalMode;
		
		private String availableInHeroicMode;
		
		private Long health;
		
		private Integer level;
		
		private Integer heroicLevel;
		
		private Integer heroicHealth;
		
		private Long journalId;
		
		private List<WowNpc> npcs;

		@JsonProperty("id")
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@JsonProperty("urlSlug")
		public String getUrlSlug() {
			return urlSlug;
		}

		public void setUrlSlug(String urlSlug) {
			this.urlSlug = urlSlug;
		}

		@JsonProperty("description")
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@JsonProperty("zoneId")
		public Long getZoneId() {
			return zoneId;
		}

		public void setZoneId(Long zoneId) {
			this.zoneId = zoneId;
		}

		@JsonProperty("availableInNormalMode")
		public String getAvailableInNormalMode() {
			return availableInNormalMode;
		}

		public void setAvailableInNormalMode(String availableInNormalMode) {
			this.availableInNormalMode = availableInNormalMode;
		}

		@JsonProperty("availableInHeroicMode")
		public String getAvailableInHeroicMode() {
			return availableInHeroicMode;
		}

		public void setAvailableInHeroicMode(String availableInHeroicMode) {
			this.availableInHeroicMode = availableInHeroicMode;
		}

		@JsonProperty("health")
		public Long getHealth() {
			return health;
		}

		public void setHealth(Long health) {
			this.health = health;
		}

		@JsonProperty("level")
		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		@JsonProperty("heroicLevel")
		public Integer getHeroicLevel() {
			return heroicLevel;
		}

		public void setHeroicLevel(Integer heroicLevel) {
			this.heroicLevel = heroicLevel;
		}

		@JsonProperty("journalId")
		public Long getJournalId() {
			return journalId;
		}

		public void setJournalId(Long journalId) {
			this.journalId = journalId;
		}

		@JsonProperty("npcs")
		public List<WowNpc> getNpcs() {
			return npcs;
		}

		public void setNpcs(List<WowNpc> npcs) {
			this.npcs = npcs;
		}
		
		@JsonProperty("heroicHealth")
		public Integer getHeroicHealth() {
			return heroicHealth;
		}

		public void setHeroicHealth(Integer heroicHealth) {
			this.heroicHealth = heroicHealth;
		}

		public static class WowNpc {
			
			private Long id;
			
			private String name;
			
			private String urlSlug;

			@JsonProperty("id")
			public Long getId() {
				return id;
			}

			public void setId(Long id) {
				this.id = id;
			}

			@JsonProperty("name")
			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			@JsonProperty("urlSlug")
			public String getUrlSlug() {
				return urlSlug;
			}

			public void setUrlSlug(String urlSlug) {
				this.urlSlug = urlSlug;
			}
		}
	}

	@JsonProperty("bosses")
	public List<WowBoss> getBosses() {
		return bosses;
	}

	public void setBosses(List<WowBoss> bosses) {
		this.bosses = bosses;
	}
}
