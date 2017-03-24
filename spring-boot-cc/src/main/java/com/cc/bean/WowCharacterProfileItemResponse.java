/**
 * 
 */
package com.cc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Caleb.Cheng
 *
 */
public class WowCharacterProfileItemResponse extends WowCharacterProfileResponse {

	private Items items;
	
	public static class Items {
		
		/**
		 * 背包裝備等級
		 */
		private Integer averageItemLevel;
		
		/**
		 * 已裝備等級
		 */
		private Integer averageItemLevelEquipped;
		
		@JsonProperty("averageItemLevel")
		public Integer getAverageItemLevel() {
			return averageItemLevel;
		}
		
		public void setAverageItemLevel(Integer averageItemLevel) {
			this.averageItemLevel = averageItemLevel;
		}
		
		@JsonProperty("averageItemLevelEquipped")
		public Integer getAverageItemLevelEquipped() {
			return averageItemLevelEquipped;
		}
		
		public void setAverageItemLevelEquipped(Integer averageItemLevelEquipped) {
			this.averageItemLevelEquipped = averageItemLevelEquipped;
		}
	}
	
	@JsonProperty("items")
	public Items getItems() {
		return items;
	}

	public void setItems(Items items) {
		this.items = items;
	}
	
}
