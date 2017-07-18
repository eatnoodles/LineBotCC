/**
 * 
 */
package com.cc.wow.character;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class CharacterItemsResponse {

	/**
	 * 背包裝備等級
	 */
	private final Integer averageItemLevel;
	
	/**
	 * 已裝備等級
	 */
	private final Integer averageItemLevelEquipped;
	
	/**
	 * 頭部
	 */
	private final ItemParts head;
	
	/**
	 * 項鍊
	 */
	private final ItemParts neck;
	
	/**
	 * 肩膀
	 */
	private final ItemParts shoulder;
	
	/**
	 * 披風
	 */
	private final ItemParts back;
	
	/**
	 * 胸部
	 */
	private final ItemParts chest;
	
	/**
	 * 護腕
	 */
	private final ItemParts wrist;
	
	/**
	 * 手套
	 */
	private final ItemParts hands;
	
	/**
	 * 腰帶
	 */
	private final ItemParts waist;
	
	/**
	 * 護腿
	 */
	private final ItemParts legs;
	
	/**
	 * 靴子
	 */
	private final ItemParts feet;
	
	/**
	 * 戒指1
	 */
	private final ItemParts finger1;
	
	/**
	 * 戒指2
	 */
	private final ItemParts finger2;
	
	/**
	 * 飾品1
	 */
	private final ItemParts trinket1;
	
	/**
	 * 飾品2
	 */
	private final ItemParts trinket2;
	
	/**
	 * 主手
	 */
	private final ItemParts mainHand;
	
	/**
	 * 副手
	 */
	private final ItemParts offHand;
	
	public CharacterItemsResponse(
            @JsonProperty("averageItemLevel") Integer averageItemLevel,
            @JsonProperty("averageItemLevelEquipped") Integer averageItemLevelEquipped,
            @JsonProperty("head") ItemParts head,
            @JsonProperty("neck") ItemParts neck,
            @JsonProperty("shoulder") ItemParts shoulder,
            @JsonProperty("back") ItemParts back,
            @JsonProperty("chest") ItemParts chest,
            @JsonProperty("wrist") ItemParts wrist,
            @JsonProperty("hands") ItemParts hands,
            @JsonProperty("waist") ItemParts waist,
            @JsonProperty("legs") ItemParts legs,
            @JsonProperty("feet") ItemParts feet,
            @JsonProperty("finger1") ItemParts finger1,
            @JsonProperty("finger2") ItemParts finger2,
            @JsonProperty("trinket1") ItemParts trinket1,
            @JsonProperty("trinket2") ItemParts trinket2,
            @JsonProperty("mainHand") ItemParts mainHand,
            @JsonProperty("offHand") ItemParts offHand
            ) {
        this.averageItemLevel = averageItemLevel;
        this.averageItemLevelEquipped = averageItemLevelEquipped;
        this.head = head;
        this.neck = neck;
        this.shoulder = shoulder;
        this.back = back;
        this.chest = chest;
        this.wrist = wrist;
        this.hands = hands;
        this.waist = waist;
        this.legs = legs;
        this.feet = feet;
        this.finger1 = finger1;
        this.finger2 = finger2;
        this.trinket1 = trinket1;
        this.trinket2 = trinket2;
        this.mainHand = mainHand;
        this.offHand = offHand;
    }
	
}
