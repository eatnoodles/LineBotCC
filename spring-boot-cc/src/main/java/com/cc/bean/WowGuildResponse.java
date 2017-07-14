package com.cc.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WowGuildResponse extends BaseWOWResponse {
	
	public class New {
		private String type;
		
		private String character;
		
		private Long timestamp;
		
		private String itemId;

		@JsonProperty("type")
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("character")
		public String getCharacter() {
			return character;
		}

		public void setCharacter(String character) {
			this.character = character;
		}

		@JsonProperty("timestamp")
		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		@JsonProperty("itemId")
		public String getItemId() {
			return itemId;
		}

		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
	}
	
	private List<New> news;

	@JsonProperty("news")
	public List<New> getNews() {
		return news;
	}

	public void setNews(List<New> news) {
		this.news = news;
	}

}
