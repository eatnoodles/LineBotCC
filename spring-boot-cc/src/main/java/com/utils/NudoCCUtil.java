package com.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cc.Application;
import com.cc.enums.WoWItemPartsEnum;

/**
 * @author Caleb.Cheng
 *
 */
public class NudoCCUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(NudoCCUtil.class);
	
	private static Properties messageProperties = new Properties();
	private static Properties systemProperties = new Properties();
	
	static {
		try {
			messageProperties.load(Application.class.getResourceAsStream("/message.properties"));
			systemProperties.load(Application.class.getResourceAsStream("/system.properties"));
		} catch (IOException e) {
			LOG.error("properties init error!");
		}
	}
	
	public static final String NEW_LINE = "\r\n";

	public static final String SLASH = "/";
	
	public static final String QUESTION_MARK = "?";
	
	public static final String DEFAULT_SERVER = systemProperties.getProperty("wow.default.server");
	
	public static final String PATTERN_EN = "^[a-zA-Z]+$";
	
	public static final String PATTERN_CH = "^[\u4e00-\u9fa5]+$";
	
	public static final String[] LOCATIONS = systemProperties.getProperty("wow.locations").split(",");
	
	public static final String[] METRICS = new String[]{ "dps", "hps", "bossdps", "tankhps","playerspeed" };
	
	public static final String[] REALMS = systemProperties.getProperty("wow.default.realms").split(",");
	
	public static final String[] ALL_REALMS = systemProperties.getProperty("wow.realms").split(",");
	
	public static final String WOW_IMG_BASE_PATH = "https://render-tw.worldofwarcraft.com/character/";
	
	public static final String WOW_COMMAND = "-wow ";
	
	public static final String WOW_COMMAND_IMG = "-img ";
	
	public static final String WOW_COMMAND_TEST = "-test ";
	
	public static final String WOW_COMMAND_ITEM = "-i ";
	
	public static final String WOW_COMMAND_CHECK_ENCHANTS = "-ec ";
	
	public static final String WOW_COMMAND_WCL = "-wcl ";
	
	public static final String WOW_COMMAND_SAVE = "-save ";
	
	public static final String WOW_COMMAND_HELP = "-help";
	
	public static final String IROL_COMMAND = "-irol ";
	
	public static final String IROL_COMMAND_FIGHT = "-fight ";
	
	public static final String IROL_COMMAND_SKILL = "-skill ";
	
	public static final String ROLL_COMMAND = "/roll";
	
	public static final String NS_COMMAND = "/ns";
	
	public static final String GET_USER_ID_COMMAND = "/id";
	
	public static final String RUN_TIMER_COMMAND = "/timer";
	
	public static final String STOP_TIMER_COMMAND = "/stoptimer";
	
	public static final String REG_TIMER_COMMAND = "/dd";
	
	public static final String UNREG_TIMER_COMMAND = "/rmdd";
	
	public static final String ROLL_SUB_COMMAND_A = "-a";
	
	public static final String LEAVE_COMMAND = systemProperties.getProperty("wow.command.leave");
	
	public static final String WHOAMI_COMMAND = systemProperties.getProperty("wow.command.whoami");
	
	public static final String SAD_COMMAND = systemProperties.getProperty("wow.command.sad");
	
	public static final String IMG1_COMMAND = "><";
	
	public static final String OPEN_COMMAND = "open";
	
	public static final String BATTLE_COMMAND = "battle";
	
	public static final String EMOJI_COMMAND = systemProperties.getProperty("other.command.emoji");
	
	public static final String USER_ROLL_START_COMMAND = systemProperties.getProperty("roll.command.start");
	
	public static final String USER_ROLL_END_COMMAND = systemProperties.getProperty("roll.command.end");
	
	public static final String WCL_USER_COMMANDS = "[mhn]{1}[的]{1}(dps|hps|bossdps|tankhps|playerspeed){1}"; 
	
	public static final WoWItemPartsEnum[] enchantsParts = { WoWItemPartsEnum.NECK, WoWItemPartsEnum.SHOULDER,
															 WoWItemPartsEnum.FINGER1, WoWItemPartsEnum.FINGER2,
															 WoWItemPartsEnum.BACK };

	
	
	/**
	 * 根據sie zip 機率 (權重  y = 1/(x+1)^2 )
	 * 
	 * @param populationSize
	 * @return
	 */
	public static double[] zipfDistribution(int populationSize) {
		double[] ratio = new double[populationSize];
		for (int x = 0; x < populationSize; ++x){
			ratio[x] = 1.0 / Math.pow((x + 2), 2);
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

	/**
	 * 以code 取得 訊息
	 * 
	 * @param code
	 * @param args
	 * @return
	 */
	public static String codeMessage(String code, Object... args) {
		return MessageFormat.format(messageProperties.getProperty(code), args);
	}
}
