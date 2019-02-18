package com.luckypeng.study.loader;

import java.util.ServiceLoader;

/**
 * @author chenzhipeng
 * @date 2018/6/5 18:25
 */
public class TestMyServiceLoader {
    public static void main(String[] argus){
        ServiceLoader<IMyServiceLoader> serviceLoader = ServiceLoader.load(IMyServiceLoader.class);
        for (IMyServiceLoader myServiceLoader : serviceLoader){
            myServiceLoader.sayHello();
            System.out.println(myServiceLoader.getName());
        }
    }
}
