package com.vimalcvs.calculator.utils;

import android.icu.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class Calculator {
    /**
     * 四个列表
     * 储存运算符（代号）, 包括三角函数指数函数等
     * 储存运算符的优先等级
     * 专门储存特殊运算符（代号）
     * 共有 + - × ÷ ln log sin cos tan cot asin acos atan acot ^ () exp
     */
    private static final List<String> CALC_LIST = Arrays.asList("l", "g", "i", "a", "n", "v", "s", "c", "t", "e", "o", "^", "×", "÷", "+", "-", "(", ")");
    private static final List<Integer> ORDER_LIST = Arrays.asList(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 3, 3, 2, 2, 1, 1);
    private static final List<String> SPECIAL_LIST = Arrays.asList("l", "g", "i", "a", "n", "v", "s", "c", "t", "e", "o");
    private static final List<String> OB_SPECIAL = Arrays.asList("ln", "log", "asin", "acos", "atan", "acot", "sin", "cos", "tan", "exp", "cot");

    private final boolean useRad;

    public Calculator(boolean rad) {
        useRad = rad;
    }

    public BigDecimal calc(String str) {
        return new BigDecimal(calculate(change(str), useRad));
    }

    /**
     * 储存特殊运算符的全名
     * 转化为后缀表达式
     * 创建change函数把中缀表达式转化为后缀表达式
     */
    public List<String> change(String func) {
        for (int i = 0; i < OB_SPECIAL.size(); i++) {
            //将特殊函数用单个字符替换
            func = func.replace(OB_SPECIAL.get(i), SPECIAL_LIST.get(i));
        }
        //栈S2
        List<String> numList = new ArrayList<>();
        //栈S1
        List<String> obList = new ArrayList<>();
        func = func.replace(" ", "");
        //去掉空格
        String temp;
        //遍历到的符号
        int i = 0;
        //遍历的位置
        while (i < func.length()) {
            temp = func.substring(i, i + 1);
            //遍历的符号
            //由于计算机是一位一位遍历, 下面的操作是为了提取出两位及以上的数
            StringBuilder num = new StringBuilder();
            //先表示为空字符串
            while (!CALC_LIST.contains(temp)) {
                //判断是否为数字
                num.append(temp);
                i = i + 1;
                try {
                    temp = func.substring(i, i + 1);
                } catch (Exception ex) {
                    break;
                }
            }
            if (!num.toString().isEmpty()) {
                numList.add(num.toString());
                //添加数字入栈S2
            }
            //若是运算符
            //先要处理负数, 在-前加上0, 否则有些情况会报错
            if (i == 0 && "-".equals(temp)) {
                numList.add("0");
            } else if (i != 0 && "-".equals(temp)) {
                if (func.charAt(i - 1) == '(') {
                    numList.add("0");
                }
            }
            if (CALC_LIST.contains(temp)) {
                if (obList.isEmpty()) {
                    //如果是S1空着, 直接入栈
                    obList.add(temp);
                } else {
                    if ("(".equals(temp)) {
                        //左括号直接入栈
                        obList.add(temp);
                    } else if (")".equals(temp)) {
                        //右括号, 则从栈顶开始依次弹出, 直到遇到左括号
                        String last = obList.get(obList.size() - 1);
                        while (!"(".equals(last)) {
                            numList.add(obList.get(obList.size() - 1));
                            obList.remove(obList.size() - 1);
                            last = obList.get(obList.size() - 1);
                        }
                        obList.remove(obList.size() - 1);
                        //如果左括号前是特殊函数, 直接压入S2
                        try {
                            if (SPECIAL_LIST.contains(obList.get(obList.size() - 1))) {
                                numList.add(obList.get(obList.size() - 1));
                                obList.remove(obList.size() - 1);
                            }
                        } catch (Exception ignored) {
                        }
                    } else {
                        //如果是运算符
                        //比较两个运算符的优先级
                        int order1 = ORDER_LIST.get(CALC_LIST.indexOf(obList.get(obList.size() - 1)));
                        int order2 = ORDER_LIST.get(CALC_LIST.indexOf(temp));
                        if (order2 > order1) {
                            obList.add(temp);
                            //优先级高, 直接压入S1
                        } else {
                            //否则将栈顶元素弹出至S2中
                            while (order2 <= order1) {
                                numList.add(obList.get(obList.size() - 1));
                                obList.remove(obList.size() - 1);
                                if (obList.isEmpty()) {
                                    break;
                                }
                                order1 = ORDER_LIST.get(CALC_LIST.indexOf(obList.get(obList.size() - 1)));
                            }
                            obList.add(temp);
                        }
                    }
                }
                i += 1;
            }
        }//遍历结束后, 把S1中剩余的一次弹入S2
        while (!obList.isEmpty()) {
            numList.add(obList.get(obList.size() - 1));
            obList.remove(obList.size() - 1);
        }
        return numList;
    }

    //运算符的定义
    public BigDecimal plus(BigDecimal i1, BigDecimal i2) {
        return i1.add(i2);
    }

    public BigDecimal reduce(BigDecimal i1, BigDecimal i2) {
        return i1.subtract(i2);
    }

    public BigDecimal multiply(BigDecimal i1, BigDecimal i2) {
        return i1.multiply(i2);
    }

    public BigDecimal division(BigDecimal i1, BigDecimal i2) {
        return i1.divide(i2, 15, BigDecimal.ROUND_HALF_UP);
    }

    public double cot(double i2) {
        return (1 / Math.tan(i2));
    }

    public double acot(double i2) {
        return (Math.PI / 2 - Math.atan(i2));
    }

    //计算后缀表达式
    public String calculate(List<String> list, boolean useDeg) {
        Deque<String> numStack = new LinkedList<>();

        for (String s : list) {
            if (CALC_LIST.contains(s)) {
                BigDecimal result;
                if (SPECIAL_LIST.contains(s)) {
                    result = BigDecimal.valueOf(calculateSpecialFunction(numStack.pop(), s, useDeg));
                } else {
                    result = calculateOperator(numStack.pop(), numStack.pop(), s);
                }
                numStack.push(result.toBigDecimal().toPlainString());
            } else {
                numStack.push(s);
            }
        }

        return numStack.pop();
    }

    private double calculateSpecialFunction(String number, String function, boolean useRad) {
        double num = Double.parseDouble(number);
        double numRad = num;

        if (useRad) {
            numRad = Math.toRadians(num);
        }

        switch (function) {
            case "s":
                return Math.sin(numRad);
            case "c":
                return Math.cos(numRad);
            case "t":
                return Math.tan(numRad);
            case "o":
                return cot(numRad);
            case "i":
                return Math.asin(numRad);
            case "a":
                return Math.acos(numRad);
            case "n":
                return Math.atan(numRad);
            case "v":
                return acot(numRad);
            case "e":
                return Math.exp(num);
            case "l":
                return Math.log(num);
            case "g":
                return Math.log10(num);
            default:
                return 0;
        }
    }

    private BigDecimal calculateOperator(String number1, String number2, String operator) {
        BigDecimal num2 = new BigDecimal(number1);
        BigDecimal num1 = new BigDecimal(number2);

        switch (operator) {
            case "+":
                return plus(num1, num2);
            case "-":
                return reduce(num1, num2);
            case "×":
                return multiply(num1, num2);
            case "÷":
                return division(num1, num2);
            case "^":
                return BigDecimal.valueOf(Math.pow(num1.doubleValue(), num2.doubleValue()));
            default:
                return BigDecimal.ZERO;
        }
    }
}
