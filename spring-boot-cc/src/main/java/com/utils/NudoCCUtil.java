/**
 * 
 */
package com.utils;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import com.cc.enums.WowItemPartsEnum;

/**
 * @author Caleb.Cheng
 *
 */
public class NudoCCUtil {

	public static final String SLASH = "/";
	
	public static final String QUESTION_MARK = "?";
	
	public static final String DEFAULT_SERVER = "阿薩斯";
	
	public static final String PATTERN_EN = "^[a-zA-Z]+$";
	
	public static final String PATTERN_CH = "^[\u4e00-\u9fa5]+$";
	
	public static final String[] REALMS = new String[]{ "阿薩斯", "地獄吼", "狂熱之刃", "水晶之刺", "世界之樹", "聖光之願"};
	
	public static final String[] ALL_REALMS = new String[]{ "世界之樹", "亞雷戈斯", "冰霜之刺",	"冰風崗哨", "地獄吼", "夜空之歌",	  	  	  	  
														"天空之牆", "寒冰皇冠", "尖石", "屠魔山谷", "巨龍之喉",	 "憤怒使者",	  	  	  	  
														"日落沼澤", "暗影之月", "水晶之刺",	"狂熱之刃", "眾星之子", "米奈希爾",	  	  	  	  
														"聖光之願", "血之谷", "語風", "銀翼要塞", "阿薩斯", "雲蛟衛", "雷鱗" };
	
	public static final String WOW_IMG_BASE_PATH = "https://render-tw.worldofwarcraft.com/character/";
	
	public static final String WOW_COMMAND = "-wow ";
	
	public static final String WOW_COMMAND_IMG = "-img ";
	
	public static final String WOW_COMMAND_TEST = "-test ";
	
	public static final String WOW_COMMAND_ITEM = "-i ";
	
	public static final String WOW_COMMAND_CHECK_ENCHANTS = "-ec ";
	
	public static final String WOW_COMMAND_HELP = "-help";
	
	public static final String WOW_NAME_ERROR_MSG = "角色名稱的格式有誤哦~";
	
	public static final String WOW_ITEM_PARAM_ERROR_MSG = "取得裝備資訊參數有誤哦~";
	
	public static final String WOW_ENCHANTS_PARAM_ERROR_MSG = "取得附魔資訊參數有誤哦~";
	
	public static final String ROLL_COMMAND = "/roll";
	
	public static final String NS_COMMAND = "/ns";
	
	public static final String GET_USER_ID_COMMAND = "/id";
	
	public static final String RUN_TIMER_COMMAND = "/timer";
	
	public static final String ROLL_SUB_COMMAND_A = "-a";
	
	public static final WowItemPartsEnum[] enchantsParts = {WowItemPartsEnum.NECK, WowItemPartsEnum.SHOULDER,
															WowItemPartsEnum.FINGER1, WowItemPartsEnum.FINGER2,
															WowItemPartsEnum.BACK
															};
	
	/**
	 * 根據sie zip 機率 (y = 1/x^2)
	 * 
	 * @param populationSize
	 * @return
	 */
	public static double[] zipfDistribution(int populationSize) {
		double[] ratio = new double[populationSize];
		for (int i = 1; i < populationSize; ++i){
			ratio[i-1] = 1.0 / (double) i;
		}
		double sum = 0.0;
		for (int i = 0; i < ratio.length; ++i)
			sum += ratio[i];
		for (int i = 0; i < ratio.length; ++i)
			ratio[i] /= sum;
		return ratio;
	}
	
	/**
	 * 根據機率取得int array
	 * 
	 * @param numsToGenerate :產生的int基底
	 * @param discreteProbabilities :權重array
	 * @param numSamples :產生筆數
	 * @return
	 */
	public static int[] getIntegerDistribution(int[] numsToGenerate, double[] discreteProbabilities, int numSamples) {
		EnumeratedIntegerDistribution distribution = 
				new EnumeratedIntegerDistribution(numsToGenerate, discreteProbabilities);

		return distribution.sample(numSamples);
	}
}
