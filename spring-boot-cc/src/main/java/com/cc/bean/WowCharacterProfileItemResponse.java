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
		
		/**
		 * 頭部
		 */
		private ItemParts head;
		
		/**
		 * 項鍊
		 */
		private ItemParts neck;
		
		/**
		 * 肩膀
		 */
		private ItemParts shoulder;
		
		/**
		 * 披風
		 */
		private ItemParts back;
		
		/**
		 * 胸部
		 */
		private ItemParts chest;
		
		/**
		 * 護腕
		 */
		private ItemParts wrist;
		
		/**
		 * 手套
		 */
		private ItemParts hands;
		
		/**
		 * 腰帶
		 */
		private ItemParts waist;
		
		/**
		 * 護腿
		 */
		private ItemParts legs;
		
		/**
		 * 靴子
		 */
		private ItemParts feet;
		
		/**
		 * 戒指1
		 */
		private ItemParts finger1;
		
		/**
		 * 戒指2
		 */
		private ItemParts finger2;
		
		/**
		 * 飾品1
		 */
		private ItemParts trinket1;
		
		/**
		 * 飾品2
		 */
		private ItemParts trinket2;
		
		/**
		 * 主手
		 */
		private ItemParts mainHand;
		
		/**
		 * 副手
		 */
		private ItemParts offHand;
		
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
		
		@JsonProperty("head")
		public ItemParts getHead() {
			return head;
		}

		public void setHead(ItemParts head) {
			this.head = head;
		}
		
		@JsonProperty("neck")
		public ItemParts getNeck() {
			return neck;
		}

		public void setNeck(ItemParts neck) {
			this.neck = neck;
		}

		@JsonProperty("shoulder")
		public ItemParts getShoulder() {
			return shoulder;
		}

		public void setShoulder(ItemParts shoulder) {
			this.shoulder = shoulder;
		}

		@JsonProperty("back")
		public ItemParts getBack() {
			return back;
		}

		public void setBack(ItemParts back) {
			this.back = back;
		}

		@JsonProperty("chest")
		public ItemParts getChest() {
			return chest;
		}

		public void setChest(ItemParts chest) {
			this.chest = chest;
		}

		@JsonProperty("wrist")
		public ItemParts getWrist() {
			return wrist;
		}

		public void setWrist(ItemParts wrist) {
			this.wrist = wrist;
		}

		@JsonProperty("hands")
		public ItemParts getHands() {
			return hands;
		}

		public void setHands(ItemParts hands) {
			this.hands = hands;
		}

		@JsonProperty("waist")
		public ItemParts getWaist() {
			return waist;
		}

		public void setWaist(ItemParts waist) {
			this.waist = waist;
		}

		@JsonProperty("legs")
		public ItemParts getLegs() {
			return legs;
		}

		public void setLegs(ItemParts legs) {
			this.legs = legs;
		}

		@JsonProperty("feet")
		public ItemParts getFeet() {
			return feet;
		}

		public void setFeet(ItemParts feet) {
			this.feet = feet;
		}

		@JsonProperty("finger1")
		public ItemParts getFinger1() {
			return finger1;
		}

		public void setFinger1(ItemParts finger1) {
			this.finger1 = finger1;
		}

		@JsonProperty("finger2")
		public ItemParts getFinger2() {
			return finger2;
		}

		public void setFinger2(ItemParts finger2) {
			this.finger2 = finger2;
		}

		@JsonProperty("trinket1")
		public ItemParts getTrinket1() {
			return trinket1;
		}

		public void setTrinket1(ItemParts trinket1) {
			this.trinket1 = trinket1;
		}

		@JsonProperty("trinket2")
		public ItemParts getTrinket2() {
			return trinket2;
		}

		public void setTrinket2(ItemParts trinket2) {
			this.trinket2 = trinket2;
		}

		@JsonProperty("mainHand")
		public ItemParts getMainHand() {
			return mainHand;
		}

		public void setMainHand(ItemParts mainHand) {
			this.mainHand = mainHand;
		}

		@JsonProperty("offHand")
		public ItemParts getOffHand() {
			return offHand;
		}

		public void setOffHand(ItemParts offHand) {
			this.offHand = offHand;
		}
		
	}
	
	public static class ItemParts {
		
		private String name;
		
		private Integer itemLevel;

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@JsonProperty("itemLevel")
		public Integer getItemLevel() {
			return itemLevel;
		}

		public void setItemLevel(Integer itemLevel) {
			this.itemLevel = itemLevel;
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
