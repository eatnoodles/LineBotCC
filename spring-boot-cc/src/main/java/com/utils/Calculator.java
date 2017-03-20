/**
 * 
 */
package com.utils;

import java.math.BigDecimal;
import java.util.Stack;


public class Calculator{
    /**
     * 左括号
     */
    public final static char LEFT_BRACKET = '(';

    /**
     * 右括号
     */
    public final static char RIGHT_BRACKET = ')';

    /**
     * 中缀表达式中的空格，需要要忽略
     */
    public final static char BLANK = ' ';

    /**
     * 小数点符号
     */
    public final static char DECIMAL_POINT = '.';

    /**
     * 负号
     */
    public final static char NEGATIVE_SIGN = '-';

    /**
     * 正号
     */
    public final static char POSITIVE_SIGN = '+';

    /**
     * 后缀表达式的各段的分隔符
     */
    public final static char SEPARATOR = ' ';
    
    /**
     * 解析并计算表达式
     * 
     * @param expression
     * @return
     */
    public BigDecimal eval(String expression) {
        String str = infix2Suffix(expression);
        if (str == null) {
            throw new IllegalArgumentException("Infix Expression is null!");
        }
        return evalInfix(str);
    }

    /**
     * 对后缀表达式进行计算
     * 
     * @param expression
     * @return
     */
    private BigDecimal evalInfix(String expression) {
        String[] strs = expression.split("\\s+");
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < strs.length; i++) {
            if (!Operator.isOperator(strs[i])) {
                stack.push(strs[i]);
            } else {
                Operator op = Operator.getInstance(strs[i]);
                BigDecimal right =new BigDecimal(stack.pop());
                BigDecimal left =new BigDecimal(stack.pop());
                BigDecimal result = op.eval(left, right);
                stack.push(String.valueOf(result));
            }
        }
        return new BigDecimal(stack.pop());
    }

    /**
     * 将中缀表达式转换为后缀表达式<br>
     * 具体算法规则 81      * 1)计算机实现转换： 将中缀表达式转换为后缀表达式的算法思想： 
     *     开始扫描； 
     *         数字时，加入后缀表达式； 
     *         运算符： 
     *  a.若为 '('，入栈；
     *  b.若为 ')'，则依次把栈中的的运算符加入后缀表达式中，直到出现'('，从栈中删除'(' ； 
     *  c.若为 除括号外的其他运算符 ，当其优先级高于栈顶运算符时，直接入栈。否则从栈顶开始，依次弹出比当前处理的运算符优先级高和优先级相等的运算符，直到一个比它优先级低的或者遇到了一个左括号为止。
     *  ·当扫描的中缀表达式结束时，栈中的的所有运算符出栈；　
     * 
     * @param expression
     * @return
     */
    public String infix2Suffix(String expression) {
        if (expression == null) return null;

        Stack<Character> stack = new Stack<Character>();

        char[] chs = expression.toCharArray();
        StringBuilder sb = new StringBuilder(chs.length);

        boolean appendSeparator = false;
        boolean sign = true;
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            // 空白则跳过
            if (c == BLANK)continue;

            // Next line is used output stack information.
            // System.out.printf("%-20s %s%n", stack, sb.toString());

            // 添加后缀表达式分隔符
            if (appendSeparator) {
                sb.append(SEPARATOR);
                appendSeparator = false;
            }

            if (isSign(c) && sign) {
                sb.append(c);
            } else if (isNumber(c)) {
                sign = false;// 数字后面不是正号或负号，而是操作符+-
                sb.append(c);
            } else if (isLeftBracket(c)) {
                stack.push(c);
            } else if (isRightBracket(c)) {
                sign = false;

                // 如果为)，则弹出(上面的所有操作符，并添加到后缀表达式中，并弹出(
                while (stack.peek() != LEFT_BRACKET) {
                    sb.append(SEPARATOR).append(stack.pop());
                }
                stack.pop();
            } else {
                appendSeparator = true;
                if (Operator.isOperator(c)) {
                    sign = true;

                    // 若为(则入栈
                    if (stack.isEmpty() || stack.peek() == LEFT_BRACKET) {
                        stack.push(c);
                        continue;
                    }
                    int precedence = Operator.getPrority(c);
                    while (!stack.isEmpty() && Operator.getPrority(stack.peek()) >= precedence) {
                        sb.append(SEPARATOR).append(stack.pop());
                    }
                    stack.push(c);
                }
            }
        }
        while (!stack.isEmpty()) {
            sb.append(SEPARATOR).append(stack.pop());
        }
        return sb.toString();
    }

    /**
     * 判断某个字符是否是正号或者负号
     * 
     * @param c
     * @return
     */
    private boolean isSign(char c) {
        return (c == NEGATIVE_SIGN || c == POSITIVE_SIGN);
    }

    /**
     * 判断某个字符是否为数字或者小数点
     * 
     * @param c
     * @return
     */
    private boolean isNumber(char c) {
        return ((c >= '0' && c <= '9') || c == DECIMAL_POINT);
    }

    /**
     * 判断某个字符是否为左括号
     * 
     * @param c
     * @return
     */
    private boolean isLeftBracket(char c) {
        return c == LEFT_BRACKET;
    }

    /**
     * 判断某个字符是否为右括号
     * 
     * @param c
     * @return
     */
    private boolean isRightBracket(char c) {
        return c == RIGHT_BRACKET;
    }
}
