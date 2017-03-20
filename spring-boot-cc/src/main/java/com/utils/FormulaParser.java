/**
 * 
 */
package com.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaParser {
    /**
     * 匹配变量占位符的正则表达式
     */
    private static Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");

    /**
     * 解析公式，并执行公式计算
     * 
     * @param formula
     * @param formulas
     * @param values
     * @return
     */
    public static BigDecimal parse(String formula, Map<String, String> formulas, Map<String, BigDecimal> values) {
        if (formulas == null)formulas = Collections.emptyMap();
        if (values == null)values = Collections.emptyMap();
        String expression = finalExpression(formula, formulas, values);
        return new Calculator().eval(expression);
    }

    /**
     * 解析公式，并执行公式计算
     * 
     * @param formula
     * @param values
     * @return
     */
    public static BigDecimal parse(String formula, Map<String, BigDecimal> values) {
        if (values == null)values = Collections.emptyMap();
        return parse(formula, Collections.<String, String> emptyMap(), values);
    }

    /**
     * 解析公式，并执行公式计算
     * 
     * @param formula
     * @return
     */
    public static BigDecimal parse(String formula) {
        return parse(formula, Collections.<String, String> emptyMap(), Collections.<String, BigDecimal> emptyMap());
    }

    /**
     * 将所有中间变量都替换成基础数据
     * 
     * @param expression
     * @param formulas
     * @param values
     * @return
     */
    private static String finalExpression(String expression, Map<String, String> formulas, Map<String, BigDecimal> values) {
        Matcher m = pattern.matcher(expression);
        if (!m.find())return expression;

        m.reset();

        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            String group = m.group(1);
            if (formulas != null && formulas.containsKey(group)) {
                String formula = formulas.get(group);
                m.appendReplacement(buffer, '(' + formula + ')');
            } else if (values != null && values.containsKey(group)) {
                BigDecimal value = values.get(group);
                m.appendReplacement(buffer,value.toPlainString());
            }else{
                throw new IllegalArgumentException("expression '"+expression+"' has a illegal variable:"+m.group()+",cause veriable '"+group+"' not being found in formulas or in values.");
            }
        }
        m.appendTail(buffer);
        return finalExpression(buffer.toString(), formulas, values);
    }
}
