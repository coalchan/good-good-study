package com.luckypeng.study.loader.impl;

import com.luckypeng.study.loader.IMyServiceLoader;

/**
 * @author chenzhipeng
 * @date 2018/6/5 18:24
 */
public class MyServiceLoaderImplA implements IMyServiceLoader {
    @Override
    public void sayHello() {
        System.out.println("hello, A");;
    }

    @Override
    public String getName() {
        return "implA";
    }
}
