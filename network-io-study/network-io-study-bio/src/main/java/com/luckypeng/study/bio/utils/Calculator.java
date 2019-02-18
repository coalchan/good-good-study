package com.luckypeng.study.bio.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author coalchan
 * created at: 2018/2/24 14:36
 * Description: 表达式计算工具类
 */
public class Calculator {
    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * 计算表达式
     * @param expression
     * @return
     * @throws ScriptException
     */
    public static Object cal(String expression) throws ScriptException {
        return jse.eval(expression);
    }
}
