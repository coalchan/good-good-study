package com.luckypeng.study.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.luckypeng.study.guice.inject.MyModule;
import com.luckypeng.study.guice.service.PersonService;

/**
 * @author chenzhipeng
 * @date 2019/1/3 11:23
 */
public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyModule());
        PersonService personService = injector.getInstance(PersonService.class);
        System.out.println(personService.get(1));
    }
}
