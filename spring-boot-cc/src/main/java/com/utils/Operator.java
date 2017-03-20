/**
 * 
 */
package com.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class Operator {
    /**
     * 运算符
     */
    private char operator;

    /**
     * 运算符的优先级别，数字越大，优先级别越高
     */
    private int priority;

    private static Map<Character, Operator> operators = new HashMap<Character, Operator>();

    private Operator(char operator, int priority) {
        setOperator(operator);
        setPriority(priority);
        register(this);
    }

    private void register(Operator operator) {
        operators.put(operator.getOperator(), operator);
    }

    /**
     * 加法运算
     */
    public final static Operator ADITION = new Operator('+', 100) {
        public BigDecimal eval(BigDecimal left, BigDecimal right) {
            return left.add(right);
        }
    };

    /**
     * 减法运算
     */
    public final static Operator SUBTRATION = new Operator('-', 100) {
        public BigDecimal eval(BigDecimal left, BigDecimal right) {
            return left.subtract(right);
        }
    };

    /**
     * 乘法运算
     */
    public final static Operator MULTIPLICATION = new Operator('*', 200) {
        public BigDecimal eval(BigDecimal left, BigDecimal right) {
            return left.multiply(right);
        }
    };

    /**
     * 除法运算
     */
    public final static Operator DIVITION = new Operator('/', 200) {
        public BigDecimal eval(BigDecimal left, BigDecimal right) {
            return left.divide(right);
        }
    };

    /**
     * 冪运算
     */
    public final static Operator EXPONENT = new Operator('^', 300) {
        public BigDecimal eval(BigDecimal left, BigDecimal right) {
            return left.pow(right.intValue());
        }
    };

    public char getOperator() {
        return operator;
    }

    private void setOperator(char operator) {
        this.operator = operator;
    }

    public int getPriority() {
        return priority;
    }

    private void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 根据某个运算符获得该运算符的优先级别
     * 
     * @param c
     * @return 运算符的优先级别
     */
    public static int getPrority(char c) {
        Operator op = operators.get(c);
        return op != null ? op.getPriority() : 0;
    }

    /**
     * 工具方法，判断某个字符是否是运算符
     * 
     * @param c
     * @return 是运算符返回 true，否则返回 false
     */
    public static boolean isOperator(char c) {
        return getInstance(c) != null;
    }

    public static boolean isOperator(String str) {
        return str.length() > 1 ? false : isOperator(str.charAt(0));
    }

    /**
     * 根据运算符获得 Operator 实例
     * 
     * @param c
     * @return 从注册中的 Operator 返回实例，尚未注册返回 null
     */
    public static Operator getInstance(char c) {
        return operators.get(c);
    }

    public static Operator getInstance(String str) {
        return str.length() > 1 ? null : getInstance(str.charAt(0));
    }

    /**
     * 根据操作数进行计算
     * 
     * @param left
     *            左操作数
     * @param right
     *            右操作数
     * @return 计算结果
     */
    public abstract BigDecimal eval(BigDecimal left, BigDecimal right);
}
